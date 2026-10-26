package com.college.feedback.model;

/**
 * Model class representing a Faculty member.
 * Layer: Model (POJO)
 */
public class Faculty {
    private int id;
    private String facultyCode;
    private String name;
    private String department;
    private String email;

    public Faculty() {
    }

    public Faculty(int id, String facultyCode, String name, String department, String email) {
        this.id = id;
        this.facultyCode = facultyCode;
        this.name = name;
        this.department = department;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name + " (" + department + ")";
    }
}
