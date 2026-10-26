package com.college.feedback.model;

import java.sql.Timestamp;

/**
 * Model class representing anonymous Student Feedback for a Course/Faculty.
 * Layer: Model (POJO)
 */
public class Feedback {
    private int id;
    private int courseId;
    private String courseCode;
    private String courseName;
    private String facultyName;
    
    // Ratings from 1 (Poor) to 5 (Excellent)
    private int clarityRating;
    private int knowledgeRating;
    private int punctualityRating;
    private int doubtSupportRating;
    
    // Optional comment
    private String comments;
    
    // Auto-generated timestamp
    private Timestamp submissionDate;

    public Feedback() {
    }

    public Feedback(int courseId, int clarityRating, int knowledgeRating, 
                    int punctualityRating, int doubtSupportRating, String comments) {
        this.courseId = courseId;
        this.clarityRating = clarityRating;
        this.knowledgeRating = knowledgeRating;
        this.punctualityRating = punctualityRating;
        this.doubtSupportRating = doubtSupportRating;
        this.comments = comments;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public int getClarityRating() {
        return clarityRating;
    }

    public void setClarityRating(int clarityRating) {
        this.clarityRating = clarityRating;
    }

    public int getKnowledgeRating() {
        return knowledgeRating;
    }

    public void setKnowledgeRating(int knowledgeRating) {
        this.knowledgeRating = knowledgeRating;
    }

    public int getPunctualityRating() {
        return punctualityRating;
    }

    public void setPunctualityRating(int punctualityRating) {
        this.punctualityRating = punctualityRating;
    }

    public int getDoubtSupportRating() {
        return doubtSupportRating;
    }

    public void setDoubtSupportRating(int doubtSupportRating) {
        this.doubtSupportRating = doubtSupportRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
    }

    public double getAverageRating() {
        return (clarityRating + knowledgeRating + punctualityRating + doubtSupportRating) / 4.0;
    }
}
