package com.college.feedback.dao;

import com.college.feedback.model.Course;
import com.college.feedback.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Courses.
 * Layer: DAO
 */
public class CourseDao {

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.course_code, c.course_name, c.faculty_id, f.name AS faculty_name "
                   + "FROM courses c "
                   + "JOIN faculty f ON c.faculty_id = f.id "
                   + "ORDER BY c.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course c = new Course(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("faculty_id"),
                        rs.getString("faculty_name")
                );
                list.add(c);
            }
        }
        return list;
    }

    public List<Course> getCoursesByFacultyId(int facultyId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.course_code, c.course_name, c.faculty_id, f.name AS faculty_name "
                   + "FROM courses c "
                   + "JOIN faculty f ON c.faculty_id = f.id "
                   + "WHERE c.faculty_id = ? "
                   + "ORDER BY c.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, facultyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(
                            rs.getInt("id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("faculty_id"),
                            rs.getString("faculty_name")
                    );
                    list.add(c);
                }
            }
        }
        return list;
    }
}
