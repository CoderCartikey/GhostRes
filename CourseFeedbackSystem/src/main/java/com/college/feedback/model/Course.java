package com.college.feedback.model;

/**
 * Model class representing a Course linked to an assigned Faculty member.
 * Layer: Model (POJO)
 */
public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private int facultyId;
    private String facultyName;

    public Course() {
    }

    public Course(int id, String courseCode, String courseName, int facultyId, String facultyName) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName + (facultyName != null ? " (" + facultyName + ")" : "");
    }
}
