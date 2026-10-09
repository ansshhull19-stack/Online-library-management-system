package com.library.config;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enterprise Database Connection Provider.
 * Connects to MySQL by default using configuration from db.properties or environment variables.
 * If MySQL credentials are unconfigured or rejected, gracefully activates an embedded persistent
 * database so student registration, login, and catalog operations remain fully functional.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static String url;
    private static String username;
    private static String password;
    private static String driverClassName;
    private static boolean initialized = false;

    // Fallback embedded configuration
    private static final String FALLBACK_DRIVER = "org.h2.Driver";
    private static String fallbackUrl;
    private static volatile boolean useFallback = false;

    static {
        loadConfiguration();
    }

    private static synchronized void loadConfiguration() {
        if (initialized) {
            return;
        }

        Properties props = new Properties();

        // 1. Try loading from db.properties on classpath
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                LOGGER.info("Loaded database configuration from classpath: db.properties");
            } else {
                LOGGER.warning("db.properties not found on classpath, checking environment variables.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read db.properties: " + e.getMessage(), e);
        }

        // 2. Resolve parameters: Environment variables override properties file
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USERNAME");
        String envPass = System.getenv("DB_PASSWORD");

        url = (envUrl != null && !envUrl.isBlank()) ? envUrl : props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");

        username = (envUser != null && !envUser.isBlank()) ? envUser : props.getProperty("db.username", "root");
        password = (envPass != null) ? envPass : props.getProperty("db.password", "root");
        driverClassName = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        // 3. Configure persistent local database fallback path
        String userHome = System.getProperty("user.home", ".");
        File dbDir = new File(userHome, ".gemini/antigravity/scratch/library_data");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        String dbPath = new File(dbDir, "library_db").getAbsolutePath().replace('\\', '/');
        fallbackUrl = "jdbc:h2:file:" + dbPath + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

        // 4. Register drivers
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "MySQL Driver not found in classpath: " + e.getMessage());
        }

        try {
            Class.forName(FALLBACK_DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "H2 Driver not found in classpath: " + e.getMessage());
        }

        initialized = true;
    }

    /**
     * Obtains an active database connection.
     * Tries primary MySQL first. If MySQL is unavailable, seamlessly uses local database.
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            loadConfiguration();
        }

        if (!useFallback) {
            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                if (conn != null && !conn.isClosed()) {
                    DatabaseInitializer.ensureInitialized(conn);
                    return conn;
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "MySQL connection unsuccessful (" + e.getMessage() +
                        "). Automatically activating resilient local database fallback...");
                useFallback = true;
            }
        }

        // Active local database connection
        Connection fallbackConn = DriverManager.getConnection(fallbackUrl, "sa", "");
        DatabaseInitializer.ensureInitialized(fallbackConn);
        return fallbackConn;
    }

    /**
     * Utility method to test database connectivity.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Programmatic configuration override (useful for unit tests or custom runtime configs).
     */
    public static synchronized void configure(String customUrl, String customUser, String customPass) {
        url = customUrl;
        username = customUser;
        password = customPass;
        useFallback = false;
        initialized = true;
    }
}
