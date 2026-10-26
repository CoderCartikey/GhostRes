@echo off
setlocal
echo ===================================================================
echo Compiling Course Feedback and Faculty Improvement Analytics System
echo ===================================================================

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Detected Apache Maven. Compiling via Maven...
    mvn clean compile
) else (
    echo [INFO] Maven not in PATH. Compiling via standard javac...
    if not exist bin mkdir bin
    javac -cp "lib\mysql-connector-j-8.0.33.jar" -d bin src\main\java\com\college\feedback\*.java src\main\java\com\college\feedback\model\*.java src\main\java\com\college\feedback\dao\*.java src\main\java\com\college\feedback\service\*.java src\main\java\com\college\feedback\ui\*.java src\main\java\com\college\feedback\util\*.java
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================================
    echo [SUCCESS] Compilation completed successfully!
    echo ===================================================================
) else (
    echo.
    echo [ERROR] Compilation failed.
)

endlocal
pause
