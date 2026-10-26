package com.college.feedback.model;

/**
 * Model class representing aggregated analytics and faculty improvement metrics.
 * Layer: Model (DTO / POJO)
 */
public class FacultyAnalytics {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String facultyName;
    private int totalResponses;
    private double avgClarity;
    private double avgKnowledge;
    private double avgPunctuality;
    private double avgDoubtSupport;
    private double overallAverage;
    private String improvementRecommendation;

    public FacultyAnalytics() {
    }

    public FacultyAnalytics(int courseId, String courseCode, String courseName, String facultyName, 
                            int totalResponses, double avgClarity, double avgKnowledge, 
                            double avgPunctuality, double avgDoubtSupport, double overallAverage, 
                            String improvementRecommendation) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.facultyName = facultyName;
        this.totalResponses = totalResponses;
        this.avgClarity = avgClarity;
        this.avgKnowledge = avgKnowledge;
        this.avgPunctuality = avgPunctuality;
        this.avgDoubtSupport = avgDoubtSupport;
        this.overallAverage = overallAverage;
        this.improvementRecommendation = improvementRecommendation;
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

    public int getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }

    public double getAvgClarity() {
        return avgClarity;
    }

    public void setAvgClarity(double avgClarity) {
        this.avgClarity = avgClarity;
    }

    public double getAvgKnowledge() {
        return avgKnowledge;
    }

    public void setAvgKnowledge(double avgKnowledge) {
        this.avgKnowledge = avgKnowledge;
    }

    public double getAvgPunctuality() {
        return avgPunctuality;
    }

    public void setAvgPunctuality(double avgPunctuality) {
        this.avgPunctuality = avgPunctuality;
    }

    public double getAvgDoubtSupport() {
        return avgDoubtSupport;
    }

    public void setAvgDoubtSupport(double avgDoubtSupport) {
        this.avgDoubtSupport = avgDoubtSupport;
    }

    public double getOverallAverage() {
        return overallAverage;
    }

    public void setOverallAverage(double overallAverage) {
        this.overallAverage = overallAverage;
    }

    public String getImprovementRecommendation() {
        return improvementRecommendation;
    }

    public void setImprovementRecommendation(String improvementRecommendation) {
        this.improvementRecommendation = improvementRecommendation;
    }
}
