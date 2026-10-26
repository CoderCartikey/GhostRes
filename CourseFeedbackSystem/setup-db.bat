@echo off
setlocal
echo ===================================================================
echo Setting up MySQL Database (course_feedback_db)
echo ===================================================================

set MYSQL_EXE=""

if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_EXE="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
) else if exist "C:\xampp\mysql\bin\mysql.exe" (
    set MYSQL_EXE="C:\xampp\mysql\bin\mysql.exe"
) else (
    set MYSQL_EXE=mysql
)

echo Using MySQL executable: %MYSQL_EXE%
echo.
echo Please enter your MySQL root password (or press Enter if empty):
%MYSQL_EXE% -u root -p < database\setup.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================================
    echo [SUCCESS] Database and sample seed data loaded successfully!
    echo You can now launch the app:
    echo     mvn clean compile exec:java
    echo ===================================================================
) else (
    echo.
    echo [WARNING] Direct MySQL execution failed. Attempting setup via Java/Maven...
    mvn compile exec:java -Dexec.mainClass="com.college.feedback.util.DatabaseInitializer"
)

endlocal
pause
