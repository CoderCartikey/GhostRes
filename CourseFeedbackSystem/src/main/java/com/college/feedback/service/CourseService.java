package com.college.feedback.service;

import com.college.feedback.dao.CourseDao;
import com.college.feedback.dao.FacultyDao;
import com.college.feedback.model.Course;
import com.college.feedback.model.Faculty;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class handling course and faculty retrieval operations.
 * Layer: Service (Business Logic)
 */
public class CourseService {
    private final CourseDao courseDao;
    private final FacultyDao facultyDao;

    public CourseService() {
        this.courseDao = new CourseDao();
        this.facultyDao = new FacultyDao();
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDao.getAllCourses();
    }

    public List<Faculty> getAllFaculty() throws SQLException {
        return facultyDao.getAllFaculty();
    }
}
