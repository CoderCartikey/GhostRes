package com.college.feedback.dao;

import com.college.feedback.model.AdminUser;
import com.college.feedback.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Admin Users.
 * Layer: DAO
 */
public class AdminDao {

    public AdminUser authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, full_name FROM admin_users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AdminUser user = new AdminUser();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setFullName(rs.getString("full_name"));
                    return user;
                }
            }
        }
        return null;
    }
}
