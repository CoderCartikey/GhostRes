package com.college.feedback.service;

import com.college.feedback.dao.FeedbackDao;
import com.college.feedback.model.FacultyAnalytics;
import com.college.feedback.model.Feedback;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class handling validation, feedback submission, and analytics computation.
 * Layer: Service (Business Logic)
 */
public class FeedbackService {
    private final FeedbackDao feedbackDao;

    public FeedbackService() {
        this.feedbackDao = new FeedbackDao();
    }

    public boolean submitFeedback(Feedback feedback) throws IllegalArgumentException, SQLException {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback data cannot be null.");
        }
        if (feedback.getCourseId() <= 0) {
            throw new IllegalArgumentException("Please select a valid course.");
        }
        validateRating("Teaching Clarity", feedback.getClarityRating());
        validateRating("Subject Knowledge", feedback.getKnowledgeRating());
        validateRating("Punctuality", feedback.getPunctualityRating());
        validateRating("Doubt Support", feedback.getDoubtSupportRating());

        if (feedback.getComments() != null && feedback.getComments().trim().length() > 500) {
            throw new IllegalArgumentException("Comments must not exceed 500 characters.");
        }

        return feedbackDao.saveFeedback(feedback);
    }

    private void validateRating(String category, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(category + " rating must be between 1 and 5.");
        }
    }

    public List<Feedback> getFeedbackEntries(Integer courseId, Integer facultyId) throws SQLException {
        return feedbackDao.getFeedbackList(courseId, facultyId);
    }

    public List<FacultyAnalytics> getFacultyAnalytics(Integer courseId, Integer facultyId) throws SQLException {
        List<FacultyAnalytics> analyticsList = feedbackDao.getAnalyticsList(courseId, facultyId);

        for (FacultyAnalytics item : analyticsList) {
            item.setImprovementRecommendation(computeRecommendation(item));
        }

        return analyticsList;
    }

    private String computeRecommendation(FacultyAnalytics fa) {
        double clarity = fa.getAvgClarity();
        double knowledge = fa.getAvgKnowledge();
        double punctuality = fa.getAvgPunctuality();
        double doubt = fa.getAvgDoubtSupport();
        double overall = fa.getOverallAverage();

        if (fa.getTotalResponses() == 0) {
            return "No feedback recorded yet.";
        }

        if (overall >= 4.5) {
            return "Exceptional performance! Recognized as benchmark faculty.";
        }

        double minScore = Math.min(Math.min(clarity, knowledge), Math.min(punctuality, doubt));

        if (minScore < 3.5) {
            if (minScore == punctuality) {
                return "Action Required: Improve class punctuality & syllabus scheduling.";
            } else if (minScore == doubt) {
                return "Action Required: Conduct more interactive doubt-clearing sessions.";
            } else if (minScore == clarity) {
                return "Action Required: Simplify lecture delivery and use practical examples.";
            } else {
                return "Action Required: Review course prerequisites and curriculum depth.";
            }
        } else if (overall >= 3.8) {
            return "Good performance. Maintain current instructional standards.";
        } else {
            return "Average performance. Minor refinement in student engagement suggested.";
        }
    }
}
