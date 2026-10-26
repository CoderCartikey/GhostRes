package com.college.feedback.ui;

import com.college.feedback.model.AdminUser;
import com.college.feedback.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Modal dialog for Administrator authentication.
 * Layer: UI
 * Validates admin credentials and displays demo credentials.
 */
public class AdminLoginDialog extends JDialog {

    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AdminUser authenticatedUser = null;

    public AdminLoginDialog(Frame parent, AuthService authService) {
        super(parent, "Administrator Login", true);
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setSize(400, 260);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 25, 15, 25));
        contentPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Admin Dashboard Access");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 37, 41));
        contentPanel.add(titleLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(userLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        usernameField = new JTextField("admin", 15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(passLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        passwordField = new JPasswordField("admin123", 15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(passwordField, gbc);

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel hintLabel = new JLabel("<html><font color='#6c757d' size='2'>Demo Credentials: <b>admin</b> / <b>admin123</b></font></html>");
        contentPanel.add(hintLabel, gbc);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginButton.setBackground(new Color(13, 110, 253));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> performLogin());

        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            AdminUser user = authService.login(username, password);
            if (user != null) {
                this.authenticatedUser = user;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failure:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public AdminUser getAuthenticatedUser() {
        return authenticatedUser;
    }
}
