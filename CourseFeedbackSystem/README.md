# Course Feedback and Faculty Improvement Analytics System

An **Advanced Java** college desktop application designed with **Java Swing**, **JDBC**, **MySQL**, and managed using **Apache Maven**. It enables students to anonymously submit course & faculty feedback and provides academic administrators with automated faculty improvement analytics, KPI summaries, and visual rating indicators.

---

## 1. Project Overview & Architecture

The application is built on a clean **4-Layer Architecture** (UI &rarr; Service &rarr; DAO &rarr; Database):

```
┌────────────────────────────────────────────────────────┐
│                   Java Swing UI Layer                  │
│   (MainFrame, FeedbackPanel, AdminDashboard, Dialogs)  │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                   Service Business Layer               │
│   (FeedbackService, CourseService, AuthService)        │
│   - Validates ratings (1-5) and input bounds           │
│   - Algorithmic Faculty Improvement Recommendations    │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                   DAO Data Access Layer                │
│   (FeedbackDao, CourseDao, FacultyDao, AdminDao)       │
│   - JDBC PreparedStatements (protects SQL injection)   │
│   - Relational JOINs, GROUP BY, and SQL Aggregates     │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                 MySQL Relational Database              │
│    Tables: admin_users, faculty, courses, feedback     │
└────────────────────────────────────────────────────────┘
```

---

## 2. Prerequisites

1. **Java Development Kit (JDK)**: JDK 8 or higher (compatible with JDK 8, 11, 17, 21).
2. **Apache Maven**: Version 3.6 or higher (`mvn -v`).
3. **MySQL Server**: Standard MySQL Server 8.0+ or **XAMPP** / **WampServer**.

---

## 3. Database Setup Commands

Make sure your MySQL service is running. If `'mysql' is not recognized as an internal or external command`, it means MySQL's `bin` folder is not in your Windows `PATH`. Choose any of the methods below:

### Method 1: Using the Full Path to MySQL (Detected on Windows)
Run this from inside the `CourseFeedbackSystem` folder:

```cmd
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < database\setup.sql
```
*(Enter your MySQL password when prompted, or press Enter if blank).*

*If using XAMPP, the path is:*
```cmd
"C:\xampp\mysql\bin\mysql.exe" -u root < database\setup.sql
```

---

### Method 2: Using the 1-Click Database Setup Batch Script
Double-click:
`setup-db.bat`
*(It automatically searches for MySQL at standard installation locations and runs `database/setup.sql`).*

---

### Method 3: Using Maven (Zero MySQL CLI Required!)
You can initialize the database directly via Java without needing `mysql.exe`:

```bash
mvn compile exec:java -Dexec.mainClass="com.college.feedback.util.DatabaseInitializer"
```

---

### Method 4: Using phpMyAdmin or MySQL Workbench (GUI)
1. Open **MySQL Workbench** or **phpMyAdmin** (`http://localhost/phpmyadmin`).
2. Go to the **Import** tab (or open SQL editor).
3. Select and execute: `database/setup.sql`.

---

## 4. Database Connection Configuration

Verify your local MySQL credentials in:  
[`src/main/java/com/college/feedback/util/DBConnection.java`](file:///src/main/java/com/college/feedback/util/DBConnection.java)

```java
// Located in DBConnection.java:
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "course_feedback_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = ""; // Enter your MySQL root password here
```

---

## 5. How to Run the App with Maven

Open Command Prompt or Terminal in the `CourseFeedbackSystem` directory:

```bash
cd CourseFeedbackSystem
mvn clean compile exec:java
```

### Maven Build Details (`pom.xml`):
- **MySQL JDBC Driver**: `com.mysql:mysql-connector-j:8.0.33` (pulled automatically by Maven).
- **Configured Main Class**: `com.college.feedback.Main` in `exec-maven-plugin`.

---

## 6. Demo Admin Credentials

For accessing the Admin Dashboard:
- **Username:** `admin`
- **Password:** `admin123`

---

## 7. Main Demonstration Workflow

1. **Step 1 - Student Feedback Submission**:
   - The app starts on the **Student Feedback Form**.
   - Select a course from the dropdown (e.g., `CS401: Advanced Java Programming`).
   - The assigned faculty member (`Dr. Donald Knuth`) will automatically populate.
   - Select ratings from 1 to 5 for Teaching Clarity, Subject Knowledge, Punctuality, and Doubt Resolution.
   - Enter an optional comment (e.g., *"Great theory, need more lab time for doubts."*).
   - Click **Submit Feedback**. Notice the confirmation modal stating feedback was recorded anonymously.

2. **Step 2 - Administrator Login**:
   - In the top navigation bar, click **Admin Dashboard**.
   - Enter `admin` / `admin123` and click **Login**.

3. **Step 3 - Faculty Improvement Analytics & Visual Indicators**:
   - View the top KPI metric cards:
     - **Total Feedback Responses**
     - **College Average Overall Rating**
     - **Active Evaluated Courses**
   - On the **Faculty Improvement Analytics** tab, observe:
     - Average ratings calculated across all 4 criteria per course and faculty.
     - **Color-coded visual rating bars** (rendered via pure Swing `JProgressBar`):
       - **Green (>= 4.0):** Strong performance.
       - **Amber (3.0 - 3.9):** Moderate / Acceptable.
       - **Red (< 3.0):** Needs Attention.
     - The **Automated Improvement Recommendation** column (e.g., *"Action Required: Conduct more interactive doubt-clearing sessions"*).

4. **Step 4 - Dynamic Filtering**:
   - Select a specific course or faculty in the filter bar at the top and click **Apply Filters**.
   - Click **Reset** to return to all entries.

5. **Step 5 - Raw Anonymous Feedbacks**:
   - Switch to the **Raw Anonymous Feedback** tab.
   - View individual submissions with submission timestamps.
   - Click any row to view the full student comment in the lower detail panel.

6. **Step 6 - Logout**:
   - Click **Logout** at the top right to return to the student submission view.
