package com.college.feedback.ui;

import com.college.feedback.model.AdminUser;
import com.college.feedback.model.Course;
import com.college.feedback.model.Faculty;
import com.college.feedback.model.FacultyAnalytics;
import com.college.feedback.model.Feedback;
import com.college.feedback.service.CourseService;
import com.college.feedback.service.FeedbackService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Admin Dashboard screen providing:
 * 1. Faculty Improvement Analytics with visual rating bars
 * 2. Detailed Anonymous Feedback records
 * 3. Filter by Course and Faculty
 * Layer: UI
 */
public class AdminDashboardPanel extends JPanel {

    private final CourseService courseService;
    private final FeedbackService feedbackService;
    private final Runnable onLogout;

    // Filter controls
    private JComboBox<FilterItem> courseFilterCombo;
    private JComboBox<FilterItem> facultyFilterCombo;
    private JLabel adminInfoLabel;

    // KPI / Summary labels
    private JLabel totalResponsesLbl;
    private JLabel collegeAvgRatingLbl;
    private JLabel totalCoursesLbl;

    // Tables & Models
    private JTable analyticsTable;
    private DefaultTableModel analyticsTableModel;

    private JTable feedbackTable;
    private DefaultTableModel feedbackTableModel;
    private JTextArea commentDetailArea;

    public AdminDashboardPanel(CourseService courseService, FeedbackService feedbackService, Runnable onLogout) {
        this.courseService = courseService;
        this.feedbackService = feedbackService;
        this.onLogout = onLogout;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(new Color(245, 247, 250));

        // 1. Top Header with User info and Logout
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel titleLbl = new JLabel("Faculty Improvement & Feedback Analytics Dashboard");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(new Color(33, 37, 41));

        JPanel rightUserPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightUserPanel.setOpaque(false);

        adminInfoLabel = new JLabel("Logged in as: Administrator");
        adminInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        adminInfoLabel.setForeground(new Color(108, 117, 125));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        rightUserPanel.add(adminInfoLabel);
        rightUserPanel.add(logoutBtn);

        topHeader.add(titleLbl, BorderLayout.WEST);
        topHeader.add(rightUserPanel, BorderLayout.EAST);

        // 2. Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));

        filterBar.add(new JLabel("Filter by Course:"));
        courseFilterCombo = new JComboBox<>();
        courseFilterCombo.setPreferredSize(new Dimension(200, 26));
        filterBar.add(courseFilterCombo);

        filterBar.add(new JLabel("Filter by Faculty:"));
        facultyFilterCombo = new JComboBox<>();
        facultyFilterCombo.setPreferredSize(new Dimension(180, 26));
        filterBar.add(facultyFilterCombo);

        JButton applyFilterBtn = new JButton("Apply Filters");
        applyFilterBtn.setBackground(new Color(13, 110, 253));
        applyFilterBtn.setForeground(Color.WHITE);
        applyFilterBtn.setFocusPainted(false);
        applyFilterBtn.addActionListener(e -> refreshDashboard());
        filterBar.add(applyFilterBtn);

        JButton resetFilterBtn = new JButton("Reset");
        resetFilterBtn.addActionListener(e -> {
            if (courseFilterCombo.getItemCount() > 0) courseFilterCombo.setSelectedIndex(0);
            if (facultyFilterCombo.getItemCount() > 0) facultyFilterCombo.setSelectedIndex(0);
            refreshDashboard();
        });
        filterBar.add(resetFilterBtn);

        // KPI Stat Banner
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        kpiPanel.setOpaque(false);
        totalResponsesLbl = createKpiCard(kpiPanel, "Total Feedback Responses", "0", new Color(13, 110, 253));
        collegeAvgRatingLbl = createKpiCard(kpiPanel, "Average Overall Rating", "0.0 / 5.0", new Color(40, 167, 69));
        totalCoursesLbl = createKpiCard(kpiPanel, "Active Courses Evaluated", "0", new Color(102, 16, 242));

        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        northPanel.setOpaque(false);
        northPanel.add(topHeader, BorderLayout.NORTH);
        northPanel.add(filterBar, BorderLayout.CENTER);
        northPanel.add(kpiPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // 3. Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel analyticsTab = buildAnalyticsTab();
        tabbedPane.addTab("Faculty Improvement Analytics", analyticsTab);

        JPanel recordsTab = buildRecordsTab();
        tabbedPane.addTab("Raw Anonymous Feedback", recordsTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JLabel createKpiCard(JPanel parent, String title, String initValue, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(108, 117, 125));

        JLabel valLbl = new JLabel(initValue);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLbl.setForeground(new Color(33, 37, 41));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        parent.add(card);
        return valLbl;
    }

    private JPanel buildAnalyticsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {
            "Course", "Faculty Member", "Responses",
            "Clarity", "Knowledge", "Punctuality", "Doubt Support",
            "Overall", "Improvement Recommendation"
        };

        analyticsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        analyticsTable = new JTable(analyticsTableModel);
        analyticsTable.setRowHeight(28);
        analyticsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        analyticsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        RatingBarRenderer barRenderer = new RatingBarRenderer();
        for (int col = 3; col <= 7; col++) {
            analyticsTable.getColumnModel().getColumn(col).setCellRenderer(barRenderer);
            analyticsTable.getColumnModel().getColumn(col).setPreferredWidth(100);
        }

        analyticsTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        analyticsTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        analyticsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        analyticsTable.getColumnModel().getColumn(8).setPreferredWidth(340);

        JScrollPane scrollPane = new JScrollPane(analyticsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        legendPanel.setOpaque(false);
        legendPanel.add(new JLabel("<html><b>Rating Indicator:</b> "
                + "<font color='#2e7d32'>■ Green: Strong (&ge; 4.0)</font> &nbsp;&nbsp; "
                + "<font color='#ef6c00'>■ Amber: Moderate (3.0 - 3.9)</font> &nbsp;&nbsp; "
                + "<font color='#c62828'>■ Red: Needs Attention (&lt; 3.0)</font></html>"));
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildRecordsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {
            "ID", "Course Code", "Course Name", "Faculty",
            "Clarity", "Knowledge", "Punctuality", "Doubt Help",
            "Avg", "Comments", "Submission Date"
        };

        feedbackTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        feedbackTable = new JTable(feedbackTableModel);
        feedbackTable.setRowHeight(24);
        feedbackTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        feedbackTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        feedbackTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        feedbackTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        feedbackTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        feedbackTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        feedbackTable.getColumnModel().getColumn(4).setPreferredWidth(55);
        feedbackTable.getColumnModel().getColumn(5).setPreferredWidth(55);
        feedbackTable.getColumnModel().getColumn(6).setPreferredWidth(65);
        feedbackTable.getColumnModel().getColumn(7).setPreferredWidth(65);
        feedbackTable.getColumnModel().getColumn(8).setPreferredWidth(50);
        feedbackTable.getColumnModel().getColumn(9).setPreferredWidth(200);
        feedbackTable.getColumnModel().getColumn(10).setPreferredWidth(130);

        JScrollPane tableScroll = new JScrollPane(feedbackTable);

        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentPanel.setBorder(BorderFactory.createTitledBorder("Full Student Comment"));
        commentDetailArea = new JTextArea(3, 30);
        commentDetailArea.setEditable(false);
        commentDetailArea.setLineWrap(true);
        commentDetailArea.setWrapStyleWord(true);
        commentPanel.add(new JScrollPane(commentDetailArea), BorderLayout.CENTER);

        feedbackTable.getSelectionModel().addListSelectionListener(e -> {
            int row = feedbackTable.getSelectedRow();
            if (row != -1) {
                Object commentObj = feedbackTable.getValueAt(row, 9);
                commentDetailArea.setText(commentObj != null ? commentObj.toString() : "(No written comment provided)");
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, commentPanel);
        splitPane.setResizeWeight(0.75);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    public void updateAdminUser(AdminUser user) {
        if (user != null) {
            adminInfoLabel.setText("Logged in as: " + user.getFullName() + " (" + user.getUsername() + ")");
        }
    }

    public void loadFilters() {
        courseFilterCombo.removeAllItems();
        courseFilterCombo.addItem(new FilterItem(0, "-- All Courses --"));

        facultyFilterCombo.removeAllItems();
        facultyFilterCombo.addItem(new FilterItem(0, "-- All Faculty --"));

        try {
            List<Course> courses = courseService.getAllCourses();
            for (Course c : courses) {
                courseFilterCombo.addItem(new FilterItem(c.getId(), c.getCourseCode() + ": " + c.getCourseName()));
            }

            List<Faculty> facultyList = courseService.getAllFaculty();
            for (Faculty f : facultyList) {
                facultyFilterCombo.addItem(new FilterItem(f.getId(), f.getName()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error populating filters: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshDashboard() {
        FilterItem selectedCourse = (FilterItem) courseFilterCombo.getSelectedItem();
        FilterItem selectedFaculty = (FilterItem) facultyFilterCombo.getSelectedItem();

        Integer courseId = (selectedCourse != null && selectedCourse.id > 0) ? selectedCourse.id : null;
        Integer facultyId = (selectedFaculty != null && selectedFaculty.id > 0) ? selectedFaculty.id : null;

        analyticsTableModel.setRowCount(0);
        int totalResponses = 0;
        double sumOverall = 0;
        int countAnalytics = 0;

        try {
            List<FacultyAnalytics> analytics = feedbackService.getFacultyAnalytics(courseId, facultyId);
            for (FacultyAnalytics a : analytics) {
                totalResponses += a.getTotalResponses();
                sumOverall += a.getOverallAverage();
                countAnalytics++;

                analyticsTableModel.addRow(new Object[]{
                    a.getCourseCode() + " - " + a.getCourseName(),
                    a.getFacultyName(),
                    a.getTotalResponses(),
                    a.getAvgClarity(),
                    a.getAvgKnowledge(),
                    a.getAvgPunctuality(),
                    a.getAvgDoubtSupport(),
                    a.getOverallAverage(),
                    a.getImprovementRecommendation()
                });
            }

            totalResponsesLbl.setText(String.valueOf(totalResponses));
            double collegeAvg = countAnalytics > 0 ? (sumOverall / countAnalytics) : 0.0;
            collegeAvgRatingLbl.setText(String.format("%.2f / 5.0", collegeAvg));
            totalCoursesLbl.setText(String.valueOf(countAnalytics));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading analytics: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        feedbackTableModel.setRowCount(0);
        commentDetailArea.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackEntries(courseId, facultyId);
            for (Feedback fb : feedbacks) {
                feedbackTableModel.addRow(new Object[]{
                    fb.getId(),
                    fb.getCourseCode(),
                    fb.getCourseName(),
                    fb.getFacultyName(),
                    fb.getClarityRating(),
                    fb.getKnowledgeRating(),
                    fb.getPunctualityRating(),
                    fb.getDoubtSupportRating(),
                    String.format("%.1f", fb.getAverageRating()),
                    fb.getComments() != null ? fb.getComments() : "",
                    fb.getSubmissionDate() != null ? sdf.format(fb.getSubmissionDate()) : "N/A"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading feedback records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class FilterItem {
        public final int id;
        public final String label;

        public FilterItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
