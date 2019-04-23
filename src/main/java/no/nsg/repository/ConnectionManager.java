package no.nsg.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
				connection.commit();
			}

			return connection;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
