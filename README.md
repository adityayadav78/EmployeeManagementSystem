# Smart Employee & Department Management System

A Java application demonstrating OOP principles, JDBC, SQLite, and Swing GUI.

## Features

- **GUI Mode** — Graphical interface with sidebar navigation, tables, dialogs, and stat cards
- **CLI Mode** — Full-featured terminal interface with formatted output
- **Full CRUD** — Create, Read, Update, Delete for both Employees and Departments
- **Search** — Find employees by name (partial match with LIKE)
- **Transfer** — Move employees between departments
- **Analytics** — Department salary summary with averages, totals, and grand total budget
- **Input Validation** — Existence checks, empty field checks, negative salary prevention

## Tech Stack

- **Language:** Java 26
- **GUI:** Java Swing
- **Database:** SQLite3 via JDBC (sqlite-jdbc)
- **Version Control:** Git & GitHub

## How to Run

### Compile
```bash
javac -cp ".;sqlite-jdbc-3.36.0.3.jar" DatabaseManager.java Main.java GUI.java
```

### Run (Mode Selector)
```bash
java -cp ".;sqlite-jdbc-3.36.0.3.jar" Main
```

### Run (Direct to GUI)
```bash
java -cp ".;sqlite-jdbc-3.36.0.3.jar" GUI
```

### Run (Direct to CLI)
```bash
java -cp ".;sqlite-jdbc-3.36.0.3.jar" Main --cli
```

## Database Schema

```
departments                    employees
+---------+-----------+       +---------+--------+--------+---------+
| dept_id | dept_name |       | emp_id  | name   | salary | dept_id |
| (PK)    | (UNIQUE)  |       | (PK)    |        |        | (FK)    |
+---------+-----------+       +---------+--------+--------+---------+
```

## Project Structure

```
├── Main.java              # Entry point (GUI/CLI launcher)
├── GUI.java               # Swing graphical interface
├── DatabaseManager.java   # Database operations (JDBC)
├── company.db             # SQLite database (auto-created)
└── sqlite-jdbc-3.36.0.3.jar
```
