@echo off
if not exist out mkdir out
javac -d out src\hostel\*.java
if errorlevel 1 exit /b 1
java -cp out hostel.HostelManagementApp