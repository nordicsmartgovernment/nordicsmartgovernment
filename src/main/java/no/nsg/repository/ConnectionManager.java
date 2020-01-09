package no.nsg.repository;

import no.nsg.repository.dbo.AccountDbo;
import no.nsg.repository.dbo.CurrencyDbo;
import no.nsg.repository.document.DocumentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Component
public class ConnectionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

	public static final String DB        = "postgres";
	public static final String DB_SCHEMA = "nsg";

	@Value("${postgres.nsg.db_url}")
	private String postgresDbUrl;

	@Value("${postgres.nsg.dbo_user}")
	private String postgresDboUser;

	@Value("${postgres.nsg.dbo_password}")
	private String postgresDboPassword;

	@Value("${postgres.nsg.user}")
	private String postgresUser;

	@Value("${postgres.nsg.password}")
	private String postgresPassword;

	//For synthetic data
	@Autowired
	private DocumentManager documentManager;

	enum SyntheticDataStatus {
		UNINITIALIZED,
		IMPORTING,
		IMPORTED
	}
	private static final long LOCAL_BUILD_SYNTHETICDATA_LENGTH_LIMIT = 512*1024; //Skip files larger than 0.5MB when building locally

	private SyntheticDataStatus syntheticDataIsImported = SyntheticDataStatus.UNINITIALIZED;
	private final Object syntheticDataIsImportedLock = new Object();

	private boolean databaseIsReady = false;
	private final Object databaseIsReadyLock = new Object();


	public Connection getConnection() throws SQLException {
		return getConnection(false);
	}

	public Connection getConnection(final boolean requireDboPermissions) throws SQLException {
		try {
			synchronized (this.databaseIsReadyLock) {
				while (!this.databaseIsReady && !requireDboPermissions) {
					try {
						this.databaseIsReadyLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			String username = null;
			String password = null;
			if (requireDboPermissions) {
				username = postgresDboUser;
				password = postgresDboPassword;
			}
			if (username==null) {
				username = postgresUser;
				password = postgresPassword;
			}

			if (postgresDbUrl==null || username==null || password==null) {
				throw new RuntimeException("System environment variable NSG_POSTGRES_DB_URL, NSG_POSTGRES_DBO_USER/NSG_POSTGRES_USER and NSG_POSTGRES_DBO_PASSWORD/NSG_POSTGRES_PASSWORD not set correctly.");
			}

			if (requireDboPermissions) { //This happens only at application startup. Do some extra logging
				LOGGER.info("postgres.nsg.db_url  : " + postgresDbUrl);
				LOGGER.info("postgres.nsg.dbo_user: " + postgresDboUser);
				LOGGER.info("postgres.nsg.user    : " + postgresUser);
			}

			Connection connection = DriverManager.getConnection(postgresDbUrl, username, password);
			connection.setAutoCommit(false);

			if (requireDboPermissions) {
				try (Statement stmt = connection.createStatement()) {
					LOGGER.info("Creating schema " + DB_SCHEMA + " if not exists");
					stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + DB_SCHEMA);
					connection.commit();
				} catch (Exception e) {
					throw e;
				}
			}

			return connection;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public void createRegularUser(final Connection connection) throws SQLException {
		try {
			// Is the regular user created?
			int user_count = 1;
			try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(1) FROM pg_user WHERE pg_user.usename=?")) {
				stmt.setString(1, postgresUser);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					user_count = rs.getInt(1);
				}
			} catch (Exception e) {
				throw e;
			}

			// If not created, create it now
			if (user_count < 1) {
				try (Statement stmt = connection.createStatement()) {
					final String safeUser = StringUtils.replace(postgresUser, "'", "''");
					final String safePassword = StringUtils.replace(postgresPassword, "'", "''");

					LOGGER.info("Creating regular user " + safeUser);
					stmt.executeUpdate("CREATE USER " +	safeUser + " WITH PASSWORD '" + safePassword + "'");
					stmt.executeUpdate("GRANT CONNECT ON DATABASE " + DB + " TO " + safeUser);
					stmt.executeUpdate("GRANT USAGE ON SCHEMA " + DB_SCHEMA + " TO " + safeUser);
					stmt.executeUpdate("GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA " + DB_SCHEMA + " TO " + safeUser);
					stmt.executeUpdate("GRANT USAGE ON ALL SEQUENCES IN SCHEMA " + DB_SCHEMA + " TO " + safeUser);
				} catch (Exception e) {
					throw e;
				}
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public void initializeCaches(final Connection connection) throws SQLException {
		AccountDbo.initializeAccountCache(connection);
		CurrencyDbo.initializeCurrencyCache(connection);
	}

	public void setDatabaseIsReady() {
		synchronized (this.databaseIsReadyLock) {
			if (!this.databaseIsReady) {
				this.databaseIsReady = true;
				threadedImportSyntheticData();
			}
			this.databaseIsReadyLock.notifyAll();
		}
	}

	private void threadedImportSyntheticData() {
		new Thread(() -> {
			//Return if we are already importing/imported. If not, set status to importing and import
			synchronized (this.syntheticDataIsImportedLock) {
				if (this.syntheticDataIsImported != SyntheticDataStatus.UNINITIALIZED) {
					return;
				}
				this.syntheticDataIsImported = SyntheticDataStatus.IMPORTING;
			}

			//Import synthetic data. Set status to imported when done
			try (Connection connection = getConnection()) {
				try {
					importSyntheticData(connection);
					connection.commit();
					LOGGER.info("Synthetic data imported OK.");
				} catch (SQLException | IOException | URISyntaxException | RuntimeException e) {
					try {
						LOGGER.error("Importing synthetic data failed: " + e.getMessage());
						connection.rollback();
						throw new SQLException(e);
					} catch (SQLException e2) {
						LOGGER.error("Rollback after fail failed: " + e2.getMessage());
						throw new SQLException(e2);
					}
				}
			} catch (SQLException e) {
				LOGGER.error("Getting connection for synthetic data import failed: " + e.getMessage());
			} finally {
				synchronized (this.syntheticDataIsImportedLock) {
					this.syntheticDataIsImported = SyntheticDataStatus.IMPORTED;
					this.syntheticDataIsImportedLock.notifyAll();
				}
			}
		}).start();
	}

	public void waitUntilSyntheticDataIsImported() {
		synchronized (this.syntheticDataIsImportedLock) {
			while (this.syntheticDataIsImported != SyntheticDataStatus.IMPORTED) {
				try {
					this.syntheticDataIsImportedLock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void importSyntheticData(final Connection connection) throws URISyntaxException, IOException, SQLException {
		LOGGER.info("Deleting old synthetic data");
		final String sql = "DELETE FROM nsg.businessdocument WHERE issynthetic=true";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.executeUpdate();
		}

		URI uri = ConnectionManager.class.getResource("/").toURI();
		if (uri.getScheme().equals("jar")) {
			try (java.nio.file.FileSystem fileSystem = java.nio.file.FileSystems.newFileSystem(uri, Collections.emptyMap());
				 DirectoryStream<Path> fileStream = Files.newDirectoryStream(fileSystem.getPath("/BOOT-INF/classes/SyntheticData/"))) {
				fileStream.forEach(x -> {
					try {
						importSyntheticData(new File(x.toString()).getName(), connection);
					} catch (IOException|URISyntaxException e) {
						throw new RuntimeException(e);
					}
				});
			}
		} else {
			try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(Paths.get("target/classes/SyntheticData/"))) {
				fileStream.forEach(x -> {
					try {
						importSyntheticData(x.toFile().getName(), connection);
					} catch (IOException|URISyntaxException e) {
						throw new RuntimeException(e);
					}
				});
            }
		}
	}

	private void importSyntheticData(final String filename, final Connection connection) throws IOException, URISyntaxException {
		if (filename==null || (filename.length()<=".zip".length()) || !filename.endsWith(".zip")) {
			return;
		}

		LOGGER.info("Importing synthetic data "+filename);
		Instant start = Instant.now();
		Instant lastLogged = start;
		long importCount = 0;

		String companyId = filename.substring(0, filename.length()-".zip".length());

		URI uri = ConnectionManager.class.getResource("/").toURI();
		boolean isLocalBuild = !uri.getScheme().equals("jar");

		ClassLoader loader = ConnectionManager.class.getClassLoader();
		try (InputStream is = loader.getResourceAsStream("SyntheticData/"+filename);
			 ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is))) {
			if (isLocalBuild) {
				long length = loader.getResource("SyntheticData/"+filename).openConnection().getContentLengthLong();
				if (length > LOCAL_BUILD_SYNTHETICDATA_LENGTH_LIMIT) {
					LOGGER.info("Skipping " + filename + " because of size. " + length + ">" + LOCAL_BUILD_SYNTHETICDATA_LENGTH_LIMIT);
					return;
				}
			}

			ZipEntry ze;

			String xmlDocument;
			byte[] buffer = new byte[10*1024];
			int bytesRead;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory() || ze.getSize()<=0) {
					continue;
				}

				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					while ((bytesRead = zis.read(buffer)) >= 0) {
						baos.write(buffer, 0, bytesRead);
					}
					xmlDocument = baos.toString();

					if (ze.getName().startsWith("purchase_invoices/")) {
						documentManager.createDocument(companyId, null, DocumentType.Type.PURCHASE_INVOICE, xmlDocument, true, connection);
					} else if (ze.getName().startsWith("sales_invoices/")) {
						documentManager.createDocument(companyId, null, DocumentType.Type.SALES_INVOICE, xmlDocument, true, connection);
					} else if (ze.getName().startsWith("bank_statements/")) {
						documentManager.createDocument(companyId, null, DocumentType.Type.BANK_STATEMENT, xmlDocument, true, connection);
					} else if (ze.getName().startsWith("orders/")) {
						documentManager.createDocument(companyId, null, DocumentType.Type.PURCHASE_ORDER, xmlDocument, true, connection);
					} else if (ze.getName().startsWith("receipts/")) {
						documentManager.createDocument(companyId, null, DocumentType.Type.RECEIPT, xmlDocument, true, connection);
					} else if (ze.getName().startsWith("other/")) {
						documentManager.createDocument(companyId, null, DocumentType.Type.OTHER, xmlDocument, true, connection);
					}

					importCount++;
				} catch (Exception e) {
					LOGGER.info("Not importing document " + ze.getName() + " : " + e.getMessage());
				}

				Instant now = Instant.now();
				if (ChronoUnit.SECONDS.between(lastLogged, now) > 10) {
					LOGGER.info("Imported " + importCount + " files in " + ChronoUnit.SECONDS.between(start, now) + " seconds");
					lastLogged = now;
				}
			}
		}

		LOGGER.info("Finished importing " + importCount + " files from " + filename + " in " + (ChronoUnit.MILLIS.between(start, Instant.now()) / 1000.0) + " seconds");
	}

}
