package no.nsg.repository;

import no.nsg.repository.dbo.AccountDbo;
import no.nsg.repository.dbo.CurrencyDbo;
import no.nsg.repository.invoice.InvoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Component
public class ConnectionManager {

	private static Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

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
	ResourcePatternResolver resourceResolver;
	@Autowired
	private InvoiceManager invoiceManager;


	public Connection getConnection() throws SQLException {
		return getConnection(false);
	}

	public Connection getConnection(final boolean requireDboPermissions) throws SQLException {
		try {
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

	public void importSyntheticData(final Connection connection) throws SQLException, IOException, SAXException {
		ClassLoader loader = ConnectionManager.class.getClassLoader();
		try (InputStream is = loader.getResourceAsStream("SyntheticData/");
			 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String filename;
			while ((filename = br.readLine()) != null) {
				importSyntheticData(filename, connection);
			}
		}
	}

	public void importSyntheticData(final String filename, final Connection connection) throws SQLException, IOException, SAXException {
		if (filename==null || (filename.length()<=".zip".length()) || !filename.endsWith(".zip")) {
			return;
		}

		LOGGER.info("Importing synthetic data "+filename);
		Instant start = Instant.now();
		Instant lastLogged = start;
		long importCount = 0;

		String companyId = filename.substring(0, filename.length()-".zip".length());

		final String sql = "DELETE FROM nsg.company WHERE orgno=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, companyId);
			stmt.executeUpdate();
		}

		ClassLoader loader = ConnectionManager.class.getClassLoader();
		try (InputStream is = loader.getResourceAsStream("SyntheticData/"+filename);
			 ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is))) {
			ZipEntry ze;

			String xmlDocument;
			byte[] buffer = new byte[10*1024];
			int bytesRead;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory() || ze.getSize()<=0) {
					continue;
				}

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((bytesRead = zis.read(buffer)) >= 0) {
					baos.write(buffer, 0, bytesRead);
				}
				xmlDocument = baos.toString();

				if (ze.getName().startsWith(companyId+"/purchase_invoices/")) {
					invoiceManager.createInvoice(companyId, xmlDocument, connection);
				} else if (ze.getName().startsWith(companyId+"/sales_invoices/")) {
					invoiceManager.createInvoice(companyId, xmlDocument, connection);
				} else if (ze.getName().startsWith(companyId+"/bank_statements/")) {
					invoiceManager.createInvoice(companyId, xmlDocument, connection);
				} else if (ze.getName().startsWith(companyId+"/orders/")) {
					invoiceManager.createInvoice(companyId, xmlDocument, connection);
				} else if (ze.getName().startsWith(companyId+"/receipts/")) {
					invoiceManager.createInvoice(companyId, xmlDocument, connection);
				} else if (ze.getName().startsWith(companyId+"/other/")) {
					invoiceManager.createInvoice(companyId, xmlDocument, connection);
				}

				importCount++;
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
