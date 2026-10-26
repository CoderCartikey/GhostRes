package com.college.feedback.model;

/**
 * Model class representing an Administrator.
 * Layer: Model (POJO)
 */
public class AdminUser {
    private int id;
    private String username;
    private String password;
    private String fullName;

    public AdminUser() {
    }

    public AdminUser(int id, String username, String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
