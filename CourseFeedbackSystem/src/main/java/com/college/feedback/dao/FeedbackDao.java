package com.college.feedback.dao;

import com.college.feedback.model.FacultyAnalytics;
import com.college.feedback.model.Feedback;
import com.college.feedback.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Feedback submissions and aggregate analytics.
 * Layer: DAO
 */
public class FeedbackDao {

    public boolean saveFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO feedback (course_id, clarity_rating, knowledge_rating, punctuality_rating, doubt_support_rating, comments) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, feedback.getCourseId());
            stmt.setInt(2, feedback.getClarityRating());
            stmt.setInt(3, feedback.getKnowledgeRating());
            stmt.setInt(4, feedback.getPunctualityRating());
            stmt.setInt(5, feedback.getDoubtSupportRating());
            stmt.setString(6, feedback.getComments());

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public List<Feedback> getFeedbackList(Integer courseId, Integer facultyId) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT fb.id, fb.course_id, c.course_code, c.course_name, f.name AS faculty_name, "
          + "       fb.clarity_rating, fb.knowledge_rating, fb.punctuality_rating, fb.doubt_support_rating, "
          + "       fb.comments, fb.submission_date "
          + "FROM feedback fb "
          + "JOIN courses c ON fb.course_id = c.id "
          + "JOIN faculty f ON c.faculty_id = f.id "
          + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (courseId != null && courseId > 0) {
            sql.append("AND c.id = ? ");
            params.add(courseId);
        }
        if (facultyId != null && facultyId > 0) {
            sql.append("AND f.id = ? ");
            params.add(facultyId);
        }

        sql.append("ORDER BY fb.submission_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setId(rs.getInt("id"));
                    fb.setCourseId(rs.getInt("course_id"));
                    fb.setCourseCode(rs.getString("course_code"));
                    fb.setCourseName(rs.getString("course_name"));
                    fb.setFacultyName(rs.getString("faculty_name"));
                    fb.setClarityRating(rs.getInt("clarity_rating"));
                    fb.setKnowledgeRating(rs.getInt("knowledge_rating"));
                    fb.setPunctualityRating(rs.getInt("punctuality_rating"));
                    fb.setDoubtSupportRating(rs.getInt("doubt_support_rating"));
                    fb.setComments(rs.getString("comments"));
                    fb.setSubmissionDate(rs.getTimestamp("submission_date"));
                    list.add(fb);
                }
            }
        }
        return list;
    }

    public List<FacultyAnalytics> getAnalyticsList(Integer courseId, Integer facultyId) throws SQLException {
        List<FacultyAnalytics> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.id AS course_id, c.course_code, c.course_name, f.name AS faculty_name, "
          + "       COUNT(fb.id) AS total_responses, "
          + "       AVG(fb.clarity_rating) AS avg_clarity, "
          + "       AVG(fb.knowledge_rating) AS avg_knowledge, "
          + "       AVG(fb.punctuality_rating) AS avg_punctuality, "
          + "       AVG(fb.doubt_support_rating) AS avg_doubt_support "
          + "FROM courses c "
          + "JOIN faculty f ON c.faculty_id = f.id "
          + "JOIN feedback fb ON fb.course_id = c.id "
          + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (courseId != null && courseId > 0) {
            sql.append("AND c.id = ? ");
            params.add(courseId);
        }
        if (facultyId != null && facultyId > 0) {
            sql.append("AND f.id = ? ");
            params.add(facultyId);
        }

        sql.append("GROUP BY c.id, c.course_code, c.course_name, f.name ");
        sql.append("ORDER BY c.course_code ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FacultyAnalytics fa = new FacultyAnalytics();
                    fa.setCourseId(rs.getInt("course_id"));
                    fa.setCourseCode(rs.getString("course_code"));
                    fa.setCourseName(rs.getString("course_name"));
                    fa.setFacultyName(rs.getString("faculty_name"));
                    fa.setTotalResponses(rs.getInt("total_responses"));
                    fa.setAvgClarity(rs.getDouble("avg_clarity"));
                    fa.setAvgKnowledge(rs.getDouble("avg_knowledge"));
                    fa.setAvgPunctuality(rs.getDouble("avg_punctuality"));
                    fa.setAvgDoubtSupport(rs.getDouble("avg_doubt_support"));

                    double overall = (fa.getAvgClarity() + fa.getAvgKnowledge() + 
                                      fa.getAvgPunctuality() + fa.getAvgDoubtSupport()) / 4.0;
                    fa.setOverallAverage(overall);

                    list.add(fa);
                }
            }
        }
        return list;
    }
}
