package com.deptassoc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Manages database connections using configuration from config.properties.
 * Loads MySQL JDBC driver and provides connection pooling via getConnection().
 */
public class DBConnectionManager {
    
    private static String DB_HOST;
    private static String DB_PORT;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    static {
        loadConfiguration();
        loadDriver();
    }
    
    /**
     * Loads database configuration from config.properties.
     */
    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = DBConnectionManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties not found in classpath");
            }
            props.load(input);
            
            DB_HOST = props.getProperty("db.host", "localhost");
            DB_PORT = props.getProperty("db.port", "3306");
            DB_NAME = props.getProperty("db.name", "department_association_v2");
            DB_USER = props.getProperty("db.user", "root");
            DB_PASSWORD = props.getProperty("db.password", "");
        } catch (IOException e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException("Configuration loading failed", e);
        }
    }
    
    /**
     * Loads the MySQL JDBC driver.
     */
    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found. " +
                "Ensure mysql-connector-java JAR is in lib/ directory.");
            throw new RuntimeException("Driver loading failed", e);
        }
    }
    
    /**
     * Gets a new database connection with UTC timezone and SSL disabled.
     * 
     * @return a JDBC Connection to the database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            DB_HOST, DB_PORT, DB_NAME
        );
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }
}
