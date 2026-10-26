package com.college.feedback.ui;

import com.college.feedback.model.AdminUser;
import com.college.feedback.service.AuthService;
import com.college.feedback.service.CourseService;
import com.college.feedback.service.FeedbackService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application frame orchestrating navigation between:
 * 1. Student Feedback Form
 * 2. Admin Login & Faculty Improvement Analytics Dashboard
 * Layer: UI
 * Entry point for the Swing graphical interface; implements CardLayout navigation.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel containerPanel;

    private final AuthService authService;
    private final CourseService courseService;
    private final FeedbackService feedbackService;

    private FeedbackPanel feedbackPanel;
    private AdminDashboardPanel adminDashboardPanel;

    private AdminUser currentAdmin = null;

    private static final String CARD_FEEDBACK = "FEEDBACK";
    private static final String CARD_ADMIN = "ADMIN";

    private JButton navFeedbackBtn;
    private JButton navAdminBtn;

    public MainFrame() {
        super("Course Feedback & Faculty Improvement Analytics System");

        this.authService = new AuthService();
        this.courseService = new CourseService();
        this.feedbackService = new FeedbackService();

        this.cardLayout = new CardLayout();
        this.containerPanel = new JPanel(cardLayout);

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(33, 37, 41));
        navBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel brandLabel = new JLabel("College Feedback Analytics System");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandLabel.setForeground(Color.WHITE);
        navBar.add(brandLabel, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navButtons.setOpaque(false);

        navFeedbackBtn = new JButton("Student Feedback Form");
        navFeedbackBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        navFeedbackBtn.setFocusPainted(false);
        navFeedbackBtn.addActionListener(e -> showFeedbackView());

        navAdminBtn = new JButton("Admin Dashboard");
        navAdminBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        navAdminBtn.setFocusPainted(false);
        navAdminBtn.addActionListener(e -> onAdminNavClicked());

        JButton helpBtn = new JButton("About");
        helpBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        helpBtn.setFocusPainted(false);
        helpBtn.addActionListener(e -> showAboutDialog());

        navButtons.add(navFeedbackBtn);
        navButtons.add(navAdminBtn);
        navButtons.add(helpBtn);
        navBar.add(navButtons, BorderLayout.EAST);

        add(navBar, BorderLayout.NORTH);

        feedbackPanel = new FeedbackPanel(courseService, feedbackService);
        adminDashboardPanel = new AdminDashboardPanel(courseService, feedbackService, this::handleLogout);

        containerPanel.add(feedbackPanel, CARD_FEEDBACK);
        containerPanel.add(adminDashboardPanel, CARD_ADMIN);

        add(containerPanel, BorderLayout.CENTER);

        showFeedbackView();
    }

    public void showFeedbackView() {
        feedbackPanel.loadCourses();
        cardLayout.show(containerPanel, CARD_FEEDBACK);
        navFeedbackBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navAdminBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void onAdminNavClicked() {
        if (currentAdmin == null) {
            AdminLoginDialog loginDialog = new AdminLoginDialog(this, authService);
            loginDialog.setVisible(true);

            AdminUser loggedIn = loginDialog.getAuthenticatedUser();
            if (loggedIn != null) {
                currentAdmin = loggedIn;
                showAdminView();
            }
        } else {
            showAdminView();
        }
    }

    private void showAdminView() {
        adminDashboardPanel.updateAdminUser(currentAdmin);
        adminDashboardPanel.loadFilters();
        adminDashboardPanel.refreshDashboard();
        cardLayout.show(containerPanel, CARD_ADMIN);
        navAdminBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navFeedbackBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void handleLogout() {
        currentAdmin = null;
        JOptionPane.showMessageDialog(this, "Administrator logged out successfully.", "Logged Out", JOptionPane.INFORMATION_MESSAGE);
        showFeedbackView();
    }

    private void showAboutDialog() {
        String info = "<html><body style='width: 380px; padding: 5px;'>"
                + "<h3 style='margin-bottom: 5px; color: #0d6efd;'>Course Feedback & Faculty Improvement Analytics</h3>"
                + "<p><b>Architecture:</b> 4-Tier Layered (UI &rarr; Service &rarr; DAO &rarr; MySQL Database)</p>"
                + "<hr/>"
                + "<p><b>Demo Credentials:</b><br/>"
                + "&bull; Username: <code>admin</code><br/>"
                + "&bull; Password: <code>admin123</code></p>"
                + "<p><b>Key Features:</b><br/>"
                + "&bull; 100% Anonymous student submissions (no student identity stored).<br/>"
                + "&bull; Relational SQL JOINs and Aggregates (AVG, COUNT, GROUP BY).<br/>"
                + "&bull; Visual rating indicators using pure Java Swing components.<br/>"
                + "&bull; Automated rule-based Faculty Improvement Recommendations.</p>"
                + "</body></html>";

        JOptionPane.showMessageDialog(this, new JLabel(info), "About Course Feedback System", JOptionPane.INFORMATION_MESSAGE);
    }
}
