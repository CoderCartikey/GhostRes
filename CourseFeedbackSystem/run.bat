@echo off
setlocal
echo ===================================================================
echo Launching Course Feedback & Faculty Improvement Analytics System...
echo ===================================================================

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Detected Apache Maven. Launching with Maven...
    mvn exec:java
) else (
    echo [INFO] Maven not in PATH. Launching with Java Runtime...
    if not exist bin\com\college\feedback\Main.class (
        echo [INFO] Binaries not found. Running compile.bat...
        call compile.bat
    )
    java -cp "bin;lib\mysql-connector-j-8.0.33.jar" com.college.feedback.Main
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [INFO] Application exited with error code %ERRORLEVEL%.
    pause
)

endlocal
