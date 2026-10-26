package com.college.feedback.dao;

import com.college.feedback.model.Faculty;
import com.college.feedback.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Faculty entities.
 * Layer: DAO
 */
public class FacultyDao {

    public List<Faculty> getAllFaculty() throws SQLException {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT id, faculty_code, name, department, email FROM faculty ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Faculty f = new Faculty(
                        rs.getInt("id"),
                        rs.getString("faculty_code"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("email")
                );
                list.add(f);
            }
        }
        return list;
    }

    public Faculty getFacultyById(int id) throws SQLException {
        String sql = "SELECT id, faculty_code, name, department, email FROM faculty WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Faculty(
                            rs.getInt("id"),
                            rs.getString("faculty_code"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }
}
