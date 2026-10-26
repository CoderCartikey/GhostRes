package com.college.feedback;

import com.college.feedback.ui.MainFrame;
import com.college.feedback.util.DBConnection;

import javax.swing.*;

/**
 * Application Entry Point.
 * Layer: Bootstrap / Launcher
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            boolean dbReachable = DBConnection.testConnection();
            if (!dbReachable) {
                JOptionPane.showMessageDialog(
                        null,
                        "Warning: Could not connect to MySQL database at:\n" +
                        DBConnection.getDbUrl() + "\n\n" +
                        "1. Ensure MySQL is running on port 3306 (e.g., via XAMPP or MySQL Service).\n" +
                        "2. Ensure you have executed 'database/setup.sql'.\n" +
                        "3. Check DBConnection.java to verify username/password.\n\n" +
                        "The application will launch, but database queries will fail until connected.",
                        "Database Connection Notice",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
