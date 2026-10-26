package com.college.feedback.service;

import com.college.feedback.dao.AdminDao;
import com.college.feedback.model.AdminUser;

import java.sql.SQLException;

/**
 * Service class handling authentication logic.
 * Layer: Service (Business Logic)
 */
public class AuthService {
    private final AdminDao adminDao;

    public AuthService() {
        this.adminDao = new AdminDao();
    }

    public AdminUser login(String username, String password) throws SQLException, IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        return adminDao.authenticate(username.trim(), password.trim());
    }
}
