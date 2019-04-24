package no.nsg.repository;

import org.springframework.util.StringUtils;

import java.sql.*;
import java.text.MessageFormat;


public class ConnectionManager {

	public static final String DB_SCHEMA = "nsg";


	public static Connection getConnection() throws SQLException {
		return getConnection(false);
	}

	public static Connection getConnection(final boolean requireDboPermissions) throws SQLException {
		try {
			PropertyManager propertyManager = PropertyManager.getInstance();
			Class.forName(propertyManager.getProperty("driver")).newInstance();

			String host = System.getenv("NSG_POSTGRES_HOST");
			String db = System.getenv("NSG_POSTGRES_DB");
			if (db==null) {
				db = "nsg_db";
			}

			String username = null;
			String password = null;
			if (requireDboPermissions) {
				username = System.getenv("NSG_POSTGRES_DBO_USER");
				password = System.getenv("NSG_POSTGRES_DBO_PASSWORD");
			}
			if (username==null) {
				username = System.getenv("NSG_POSTGRES_USER");
				password = System.getenv("NSG_POSTGRES_PASSWORD");
			}

			if (host==null || username==null || password==null) {
				throw new RuntimeException("System environment variable NSG_POSTGRES_HOST, NSG_POSTGRES_DB, NSG_POSTGRES_DBO_USER/NSG_POSTGRES_USER and NSG_POSTGRES_DBO_PASSWORD/NSG_POSTGRES_PASSWORD not set correctly.");
			}

			String dbUrl = MessageFormat.format(propertyManager.getProperty("nsg_db_url"), host, db, username, password);
			Connection connection = DriverManager.getConnection(dbUrl);
			connection.setAutoCommit(false);

			if (requireDboPermissions) {
				try (Statement stmt = connection.createStatement()) {
					stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + DB_SCHEMA);
				} catch (Exception e) {
					throw e;
				}

				// Is the regular user created?
				int user_count = 1;
				try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(1) FROM pg_user WHERE pg_user.usename=?")) {
					stmt.setString(1, System.getenv("NSG_POSTGRES_USER"));
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
						stmt.executeUpdate("CREATE USER " +
										   StringUtils.replace(System.getenv("NSG_POSTGRES_USER"), "'", "''") +
								           " WITH PASSWORD '" +
								           StringUtils.replace(System.getenv("NSG_POSTGRES_PASSWORD"), "'", "''") +
								           "'");
					} catch (Exception e) {
						throw e;
					}
				}

				connection.commit();
			}

			return connection;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
