package com.college.feedback.ui;

import com.college.feedback.model.Course;
import com.college.feedback.model.Feedback;
import com.college.feedback.service.CourseService;
import com.college.feedback.service.FeedbackService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Screen for Students to submit anonymous course and faculty feedback.
 * Layer: UI
 * Ensures student anonymity by design; validates inputs before calling Service layer.
 */
public class FeedbackPanel extends JPanel {

    private final CourseService courseService;
    private final FeedbackService feedbackService;

    // UI Components
    private JComboBox<CourseItem> courseComboBox;
    private JTextField facultyField;
    private JComboBox<String> clarityCombo;
    private JComboBox<String> knowledgeCombo;
    private JComboBox<String> punctualityCombo;
    private JComboBox<String> doubtCombo;
    private JTextArea commentsArea;
    private JButton submitButton;
    private JButton resetButton;

    // Rating lookup array
    private static final String[] RATING_OPTIONS = {
        "-- Select Rating --",
        "5 - Excellent",
        "4 - Very Good",
        "3 - Good",
        "2 - Average",
        "1 - Poor"
    };

    public FeedbackPanel(CourseService courseService, FeedbackService feedbackService) {
        this.courseService = courseService;
        this.feedbackService = feedbackService;
        initUI();
        loadCourses();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Student Course & Faculty Feedback Form");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));

        JLabel subLabel = new JLabel("Your response is strictly ANONYMOUS. No student ID or name is collected.");
        subLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subLabel.setForeground(new Color(108, 117, 125));

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 1. Course Selection
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel courseLbl = new JLabel("Select Course: *");
        courseLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(courseLbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        courseComboBox = new JComboBox<>();
        courseComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseComboBox.addActionListener(e -> onCourseSelected());
        formPanel.add(courseComboBox, gbc);

        row++;

        // 2. Assigned Faculty
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel facultyLbl = new JLabel("Assigned Faculty:");
        facultyLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(facultyLbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        facultyField = new JTextField();
        facultyField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        facultyField.setEditable(false);
        facultyField.setBackground(new Color(240, 240, 240));
        formPanel.add(facultyField, gbc);

        row++;

        // Section title for ratings
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel ratingsHeader = new JLabel("Performance Ratings (1 = Poor, 5 = Excellent)");
        ratingsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ratingsHeader.setForeground(new Color(13, 110, 253));
        formPanel.add(ratingsHeader, gbc);
        gbc.gridwidth = 1;

        row++;

        // 3. Teaching Clarity Rating
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("1. Teaching Clarity: *"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        clarityCombo = new JComboBox<>(RATING_OPTIONS);
        formPanel.add(clarityCombo, gbc);

        row++;

        // 4. Subject Knowledge Rating
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("2. Subject Knowledge: *"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        knowledgeCombo = new JComboBox<>(RATING_OPTIONS);
        formPanel.add(knowledgeCombo, gbc);

        row++;

        // 5. Punctuality Rating
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("3. Punctuality & Discipline: *"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        punctualityCombo = new JComboBox<>(RATING_OPTIONS);
        formPanel.add(punctualityCombo, gbc);

        row++;

        // 6. Doubt Support Rating
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("4. Doubt Resolution & Support: *"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        doubtCombo = new JComboBox<>(RATING_OPTIONS);
        formPanel.add(doubtCombo, gbc);

        row++;

        // 7. Comments Area
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel commentLbl = new JLabel("Comments (Optional):");
        commentLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(commentLbl, gbc);

        gbc.gridx = 1; gbc.gridy = row;
        commentsArea = new JTextArea(4, 25);
        commentsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentsArea);
        formPanel.add(scrollPane, gbc);

        JScrollPane centerScroll = new JScrollPane(formPanel);
        centerScroll.setBorder(null);
        add(centerScroll, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        resetButton = new JButton("Reset Form");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resetButton.addActionListener(e -> resetForm());

        submitButton = new JButton("Submit Feedback");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.setBackground(new Color(13, 110, 253));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> handleSubmit());

        buttonPanel.add(resetButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadCourses() {
        courseComboBox.removeAllItems();
        courseComboBox.addItem(new CourseItem(0, "-- Select a Course --", ""));
        try {
            List<Course> courses = courseService.getAllCourses();
            for (Course c : courses) {
                courseComboBox.addItem(new CourseItem(c.getId(), c.getCourseCode() + ": " + c.getCourseName(), c.getFacultyName()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while loading courses:\n" + e.getMessage() +
                    "\nPlease verify your MySQL server connection in DBConnection.java.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCourseSelected() {
        CourseItem selected = (CourseItem) courseComboBox.getSelectedItem();
        if (selected != null && selected.id > 0) {
            facultyField.setText(selected.facultyName != null ? selected.facultyName : "Unassigned");
        } else {
            facultyField.setText("");
        }
    }

    private void handleSubmit() {
        CourseItem selectedCourse = (CourseItem) courseComboBox.getSelectedItem();
        if (selectedCourse == null || selectedCourse.id <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a course from the list.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clarity = parseRating(clarityCombo.getSelectedIndex());
        int knowledge = parseRating(knowledgeCombo.getSelectedIndex());
        int punctuality = parseRating(punctualityCombo.getSelectedIndex());
        int doubt = parseRating(doubtCombo.getSelectedIndex());

        if (clarity == 0 || knowledge == 0 || punctuality == 0 || doubt == 0) {
            JOptionPane.showMessageDialog(this, "Please provide a 1 to 5 rating for all 4 performance criteria.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comment = commentsArea.getText().trim();
        Feedback fb = new Feedback(selectedCourse.id, clarity, knowledge, punctuality, doubt, comment);

        try {
            boolean success = feedbackService.submitFeedback(fb);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Thank you! Your feedback has been recorded anonymously.",
                        "Feedback Submitted",
                        JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit feedback. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while saving feedback:\n" + ex.getMessage() +
                    "\nPlease check if MySQL database 'course_feedback_db' is created.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseRating(int selectedIndex) {
        switch (selectedIndex) {
            case 1: return 5;
            case 2: return 4;
            case 3: return 3;
            case 4: return 2;
            case 5: return 1;
            default: return 0;
        }
    }

    public void resetForm() {
        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
        }
        facultyField.setText("");
        clarityCombo.setSelectedIndex(0);
        knowledgeCombo.setSelectedIndex(0);
        punctualityCombo.setSelectedIndex(0);
        doubtCombo.setSelectedIndex(0);
        commentsArea.setText("");
    }

    public static class CourseItem {
        public final int id;
        public final String title;
        public final String facultyName;

        public CourseItem(int id, String title, String facultyName) {
            this.id = id;
            this.title = title;
            this.facultyName = facultyName;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
