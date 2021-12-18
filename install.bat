SET MVNW=%cd%\mvnw

call %MVNW% clean install -DskipTests=true

echo Press Enter to continue
pause >nul