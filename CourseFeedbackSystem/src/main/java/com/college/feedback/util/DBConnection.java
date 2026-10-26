package com.college.feedback.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage database connections using JDBC.
 * Layer: Utility
 */
public class DBConnection {

    // =========================================================================
    // DATABASE CONFIGURATION SETTINGS
    // Modify these values according to your local MySQL server setup.
    // =========================================================================
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "course_feedback_db";
    
    // Default URL string configured for standard MySQL connections
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
    // MySQL credentials (change password if your local root user has a password)
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // e.g., "root", "admin", or "" for XAMPP default

    // Static block to explicitly load MySQL JDBC Driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new connection to the MySQL database.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Quick diagnostic check to test if MySQL is reachable and credentials are valid.
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }
}
