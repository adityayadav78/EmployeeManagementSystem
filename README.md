# Smart Employee & Department Management System

A modular Java application demonstrating Object-Oriented Programming (OOP) principles, JDBC connection lifecycle management, SQLite persistence, and a modern Swing GUI alongside a full-featured CLI.

---

## 🌟 Key Features

- **Dual Mode Interface:**
  - **Swing GUI:** Visual dashboard with sidebar navigation, real-time metrics cards, interactive data tables, and input dialogs.
  - **CLI Terminal Mode:** Clean, menu-driven command-line interface for rapid administrative workflows.
- **Full CRUD Operations:** Comprehensive Create, Read, Update, and Delete operations for both Employees and Departments with strict relational integrity.
- **Advanced SQL Analytics:** Formulates complex SQL queries utilizing `LEFT JOIN` and `GROUP BY` aggregate functions (`AVG`, `COUNT`, `SUM`) to generate departmental payroll audits and headcount summaries.
- **Secure Persistence via JDBC:**
  - Manages database connection lifecycles cleanly using `sqlite-jdbc`.
  - Implements parameterized `PreparedStatement` execution across all queries to prevent SQL injection vulnerabilities.
- **Search & Department Transfers:** Real-time partial name search (`LIKE %query%`) and atomic department transfer operations.
- **Robust Input Validation:** Enforces positive non-zero salary checks, non-empty text validations, and foreign key existence verification before execution.

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 17+)
- **Architecture:** Object-Oriented Programming (OOP), Separation of Concerns (Presentation, Business Logic, Persistence)
- **GUI Toolkit:** Java Swing & AWT
- **Database:** SQLite3
- **Connectivity:** JDBC (`sqlite-jdbc`)
- **Version Control:** Git & GitHub

---

## 📊 Database Schema

```text
departments                    employees
+---------+-----------+       +---------+--------+--------+---------+
| dept_id | dept_name |       | emp_id  | name   | salary | dept_id |
| (PK)    | (UNIQUE)  |       | (PK)    |        |        | (FK)    |
+---------+-----------+       +---------+--------+--------+---------+
```

---

## 🚀 How to Run

### 1. Compile
```bash
javac -cp ".;sqlite-jdbc-3.36.0.3.jar" DatabaseManager.java Main.java GUI.java
```

### 2. Launch
- **Interactive Launcher (Choose GUI or CLI):**
  ```bash
  java -cp ".;sqlite-jdbc-3.36.0.3.jar" Main
  ```
- **Direct to Swing GUI:**
  ```bash
  java -cp ".;sqlite-jdbc-3.36.0.3.jar" GUI
  ```
- **Direct to CLI Mode:**
  ```bash
  java -cp ".;sqlite-jdbc-3.36.0.3.jar" Main --cli
  ```

---

## 📁 Project Structure

```
├── Main.java              # Main launcher (GUI vs CLI routing)
├── GUI.java               # Modern Swing graphical desktop interface
├── DatabaseManager.java   # Data Access Layer & JDBC persistence logic
├── company.db             # Local SQLite database (auto-initialized)
├── sqlite-jdbc-3.36.0.3.jar # JDBC SQLite driver
└── README.md              # Project documentation
```
