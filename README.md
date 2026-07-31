# Smart Employee & Department Management System

A Java CLI application demonstrating Object-Oriented Programming (OOP) principles and relational database management using JDBC and SQLite.

## Features & Technical Highlights
- JDBC Integration: Utilized PreparedStatement objects to execute safe, parameterized SQL queries.
- Relational Schema: Designed tables connected by Foreign Key constraints between Employees and Departments.
- SQL Analytics: Implemented LEFT JOIN for cross-table display and GROUP BY with aggregate functions (AVG, COUNT) for departmental statistics.
- Error Handling: Handled database driver exceptions and input validation gracefully.

## Tech Stack
- Language: Java
- Database: SQLite3 via JDBC (sqlite-jdbc)
- Version Control: Git & GitHub

## How to Run
1. Compile the project with the SQLite driver:
   javac -cp ".;sqlite-jdbc-3.45.1.0.jar" DatabaseManager.java Main.java

2. Run the application:
   java -cp ".;sqlite-jdbc-3.45.1.0.jar" Main