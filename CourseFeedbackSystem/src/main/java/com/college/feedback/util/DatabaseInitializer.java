package com.college.feedback.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility to automatically execute 'database/setup.sql' using JDBC.
 * Eliminates the need to have 'mysql' CLI added to the system PATH.
 */
public class DatabaseInitializer {

    // Connects to MySQL server directly (without specifying database name first)
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Change if local root has a password

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("Starting Automated MySQL Database Setup via JDBC...");
        System.out.println("===================================================================");

        File sqlFile = new File("database/setup.sql");
        if (!sqlFile.exists()) {
            sqlFile = new File("CourseFeedbackSystem/database/setup.sql");
        }

        if (!sqlFile.exists()) {
            System.err.println("[ERROR] Could not find 'database/setup.sql'. Run this command from CourseFeedbackSystem folder.");
            return;
        }

        System.out.println("Connecting to MySQL server at localhost:3306...");
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            System.out.println("Reading " + sqlFile.getPath() + "...");
            String sqlContent = readSqlFile(sqlFile);

            // Split into individual SQL commands separated by semicolons
            String[] queries = sqlContent.split(";");

            for (String query : queries) {
                String trimmed = query.trim();
                // Filter out empty lines or pure comment blocks
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    stmt.execute(trimmed);
                }
            }

            System.out.println("\n===================================================================");
            System.out.println("[SUCCESS] Database 'course_feedback_db' created & populated!");
            System.out.println("All tables (admin_users, faculty, courses, feedback) are ready.");
            System.out.println("===================================================================");

        } catch (SQLException e) {
            System.err.println("\n[ERROR] Database initialization failed: " + e.getMessage());
            System.err.println("If your MySQL root user has a password, please update PASSWORD in DatabaseInitializer.java & DBConnection.java.");
        } catch (IOException e) {
            System.err.println("\n[ERROR] Failed to read setup.sql: " + e.getMessage());
        }
    }

    private static String readSqlFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore SQL comments
                String trimmed = line.trim();
                if (!trimmed.startsWith("--")) {
                    sb.append(line).append("\n");
                }
            }
        }
        return sb.toString();
    }
}
