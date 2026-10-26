-- ===================================================================
-- Course Feedback and Faculty Improvement Analytics System
-- Database Setup Script (MySQL)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS course_feedback_db;
USE course_feedback_db;

-- 1. Drop existing tables if re-running script (in reverse dependency order)
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS admin_users;

-- 2. Admin Users Table
CREATE TABLE admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- 3. Faculty Table
CREATE TABLE faculty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- 4. Courses Table (references Faculty)
CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    faculty_id INT NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE
);

-- 5. Feedback Table (references Courses, anonymous - no student details)
CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    clarity_rating INT NOT NULL CHECK (clarity_rating BETWEEN 1 AND 5),
    knowledge_rating INT NOT NULL CHECK (knowledge_rating BETWEEN 1 AND 5),
    punctuality_rating INT NOT NULL CHECK (punctuality_rating BETWEEN 1 AND 5),
    doubt_support_rating INT NOT NULL CHECK (doubt_support_rating BETWEEN 1 AND 5),
    comments TEXT,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- ===================================================================
-- Sample Seed Data for Demonstration
-- ===================================================================

-- Sample Admin Credentials (username: admin, password: admin123)
INSERT INTO admin_users (username, password, full_name) VALUES
('admin', 'admin123', 'Academic Dean / Administrator');

-- Sample Faculty Members
INSERT INTO faculty (id, faculty_code, name, department, email) VALUES
(1, 'FAC101', 'Dr. Alan Turing', 'Computer Science', 'alan.turing@college.edu'),
(2, 'FAC102', 'Prof. Grace Hopper', 'Information Technology', 'grace.hopper@college.edu'),
(3, 'FAC103', 'Dr. Donald Knuth', 'Computer Science', 'donald.knuth@college.edu'),
(4, 'FAC104', 'Prof. Ada Lovelace', 'Software Engineering', 'ada.lovelace@college.edu');

-- Sample Courses mapped to Faculty
INSERT INTO courses (id, course_code, course_name, faculty_id) VALUES
(1, 'CS201', 'Data Structures & Algorithms', 1),
(2, 'IT301', 'Database Management Systems', 2),
(3, 'CS401', 'Advanced Java Programming', 3),
(4, 'SE202', 'Software Engineering Principles', 4),
(5, 'CS405', 'Design & Analysis of Algorithms', 1);

-- Sample Feedbacks (Diverse ratings to showcase Faculty Improvement Analytics)
INSERT INTO feedback (course_id, clarity_rating, knowledge_rating, punctuality_rating, doubt_support_rating, comments, submission_date) VALUES
-- Feedbacks for Dr. Alan Turing (CS201: Strong concepts, punctuality could improve)
(1, 5, 5, 3, 4, 'Brilliant lectures and clear explanations, but sometimes starts class late.', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 4, 5, 2, 4, 'Very knowledgeable in DSA. Request him to adhere strictly to class timings.', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, 5, 5, 3, 5, 'Best algorithms teacher ever! Solves every doubt patiently.', DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Feedbacks for Prof. Grace Hopper (IT301: Outstanding overall)
(2, 5, 5, 5, 5, 'Amazing SQL practical examples! Very punctual and approachable.', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 4, 5, 5, 4, 'Great course structure and helpful lab guidance.', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 5, 4, 5, 5, 'Concepts of normalization and indexing were explained thoroughly.', DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Feedbacks for Dr. Donald Knuth (CS401: Deep knowledge, but needs simpler explanations & doubt support)
(3, 3, 5, 4, 2, 'Great depth of knowledge in Java, but topics move very fast. Need more time for doubts.', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, 2, 5, 4, 3, 'Teaching is very theoretical; could provide more live coding demonstrations.', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 3, 5, 4, 2, 'Needs to answer beginner doubts with more patience during lab sessions.', DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Feedbacks for Prof. Ada Lovelace (SE202: Good clarity, balanced ratings)
(4, 4, 4, 4, 4, 'Very organized slides and clear agile project methodologies.', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 4, 4, 5, 4, 'Punctual and always completes the syllabus on schedule.', DATE_SUB(NOW(), INTERVAL 1 DAY));
