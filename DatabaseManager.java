import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:company.db";

    public Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL);
    }

    public void createTables() {
        String deptTableSql = "CREATE TABLE IF NOT EXISTS departments (" +
                              "dept_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "dept_name TEXT UNIQUE NOT NULL);";

        String empTableSql = "CREATE TABLE IF NOT EXISTS employees (" +
                             "emp_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "name TEXT NOT NULL, " +
                             "salary REAL NOT NULL, " +
                             "dept_id INTEGER, " +
                             "FOREIGN KEY (dept_id) REFERENCES departments(dept_id));";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(deptTableSql);
            stmt.execute(empTableSql);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    // ======================== CREATE ========================

    public boolean addDepartment(String name) {
        String sql = "INSERT INTO departments(dept_name) VALUES(?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addEmployee(String name, double salary, int deptId) {
        if (!departmentExists(deptId)) {
            System.out.println("--> Error: Department ID " + deptId + " does not exist.");
            return false;
        }
        String sql = "INSERT INTO employees(name, salary, dept_id) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, salary);
            pstmt.setInt(3, deptId);
            pstmt.executeUpdate();
            System.out.println("--> Employee added successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    // ======================== READ ========================

    public void displayAllEmployeesWithDept() {
        String sql = "SELECT e.emp_id, e.name, e.salary, d.dept_name " +
                     "FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "ORDER BY e.emp_id;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n+------+------------------+------------+------------------+");
            System.out.printf("| %-4s | %-16s | %-10s | %-16s |\n", "ID", "Name", "Salary", "Department");
            System.out.println("+------+------------------+------------+------------------+");

            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("emp_id");
                String name = rs.getString("name");
                double salary = rs.getDouble("salary");
                String dept = rs.getString("dept_name");
                if (dept == null) dept = "Unassigned";
                System.out.printf("| %-4d | %-16s | $%8.2f | %-16s |\n", id, name, salary, dept);
                count++;
            }
            System.out.println("+------+------------------+------------+------------------+");
            System.out.println("Total employees: " + count);

        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
    }

    public void displayDepartmentSalarySummary() {
        String sql = "SELECT d.dept_name, COUNT(e.emp_id) AS total_emp, " +
                     "COALESCE(AVG(e.salary), 0) AS avg_salary, " +
                     "COALESCE(SUM(e.salary), 0) AS total_salary " +
                     "FROM departments d " +
                     "LEFT JOIN employees e ON d.dept_id = e.dept_id " +
                     "GROUP BY d.dept_id " +
                     "ORDER BY d.dept_name;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n+------------------+--------+------------+------------+");
            System.out.printf("| %-16s | %-6s | %-10s | %-10s |\n", "Department", "Count", "Avg Salary", "Total");
            System.out.println("+------------------+--------+------------+------------+");

            double grandTotal = 0;
            int grandCount = 0;
            while (rs.next()) {
                String dept = rs.getString("dept_name");
                int count = rs.getInt("total_emp");
                double avgSal = rs.getDouble("avg_salary");
                double totalSal = rs.getDouble("total_salary");
                System.out.printf("| %-16s | %-6d | $%8.2f | $%8.2f |\n", dept, count, avgSal, totalSal);
                grandTotal += totalSal;
                grandCount += count;
            }
            System.out.println("+------------------+--------+------------+------------+");
            System.out.printf("  Grand Total: %d employees | $%.2f total budget\n", grandCount, grandTotal);

        } catch (SQLException e) {
            System.err.println("Error fetching analytics: " + e.getMessage());
        }
    }

    public void searchEmployees(String keyword) {
        String sql = "SELECT e.emp_id, e.name, e.salary, d.dept_name " +
                     "FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "WHERE e.name LIKE ? " +
                     "ORDER BY e.name;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nSearch results for \"" + keyword + "\":");
            System.out.println("+------+------------------+------------+------------------+");
            System.out.printf("| %-4s | %-16s | %-10s | %-16s |\n", "ID", "Name", "Salary", "Department");
            System.out.println("+------+------------------+------------+------------------+");

            int count = 0;
            while (rs.next()) {
                System.out.printf("| %-4d | %-16s | $%8.2f | %-16s |\n",
                    rs.getInt("emp_id"),
                    rs.getString("name"),
                    rs.getDouble("salary"),
                    rs.getString("dept_name") != null ? rs.getString("dept_name") : "Unassigned");
                count++;
            }
            System.out.println("+------+------------------+------------+------------------+");
            System.out.println("Found: " + count + " employee(s)");

        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
        }
    }

    public void displayEmployeesByDepartment(int deptId) {
        String deptName = getDepartmentName(deptId);
        if (deptName == null) {
            System.out.println("--> Department ID " + deptId + " not found.");
            return;
        }

        String sql = "SELECT emp_id, name, salary FROM employees WHERE dept_id = ? ORDER BY name;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nEmployees in department: " + deptName);
            System.out.println("+------+------------------+------------+");
            System.out.printf("| %-4s | %-16s | %-10s |\n", "ID", "Name", "Salary");
            System.out.println("+------+------------------+------------+");

            int count = 0;
            while (rs.next()) {
                System.out.printf("| %-4d | %-16s | $%8.2f |\n",
                    rs.getInt("emp_id"), rs.getString("name"), rs.getDouble("salary"));
                count++;
            }
            System.out.println("+------+------------------+------------+");
            System.out.println("Total: " + count + " employee(s)");

        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
    }

    public void listAllDepartments() {
        String sql = "SELECT d.dept_id, d.dept_name, COUNT(e.emp_id) AS emp_count " +
                     "FROM departments d " +
                     "LEFT JOIN employees e ON d.dept_id = e.dept_id " +
                     "GROUP BY d.dept_id " +
                     "ORDER BY d.dept_name;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n+------+------------------+--------+");
            System.out.printf("| %-4s | %-16s | %-6s |\n", "ID", "Department", "Emps");
            System.out.println("+------+------------------+--------+");

            while (rs.next()) {
                System.out.printf("| %-4d | %-16s | %-6d |\n",
                    rs.getInt("dept_id"), rs.getString("dept_name"), rs.getInt("emp_count"));
            }
            System.out.println("+------+------------------+--------+");

        } catch (SQLException e) {
            System.err.println("Error listing departments: " + e.getMessage());
        }
    }

    // ======================== UPDATE ========================

    public boolean updateEmployeeSalary(int empId, double newSalary) {
        if (!employeeExists(empId)) {
            System.out.println("--> Employee ID " + empId + " not found.");
            return false;
        }

        String sql = "UPDATE employees SET salary = ? WHERE emp_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newSalary);
            pstmt.setInt(2, empId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("--> Salary updated successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating salary: " + e.getMessage());
        }
        return false;
    }

    public boolean transferEmployee(int empId, int newDeptId) {
        if (!employeeExists(empId)) {
            System.out.println("--> Employee ID " + empId + " not found.");
            return false;
        }
        if (!departmentExists(newDeptId)) {
            System.out.println("--> Department ID " + newDeptId + " does not exist.");
            return false;
        }

        String sql = "UPDATE employees SET dept_id = ? WHERE emp_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newDeptId);
            pstmt.setInt(2, empId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("--> Employee transferred successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error transferring employee: " + e.getMessage());
        }
        return false;
    }

    // ======================== DELETE ========================

    public boolean deleteEmployee(int empId) {
        if (!employeeExists(empId)) {
            System.out.println("--> Employee ID " + empId + " not found.");
            return false;
        }

        String sql = "DELETE FROM employees WHERE emp_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("--> Employee deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteDepartment(int deptId) {
        if (!departmentExists(deptId)) {
            System.out.println("--> Department ID " + deptId + " not found.");
            return false;
        }

        String checkSql = "SELECT COUNT(*) FROM employees WHERE dept_id = ?";
        try (Connection conn = connect(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, deptId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("--> Cannot delete: department has " + rs.getInt(1) + " employee(s). Reassign them first.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking department: " + e.getMessage());
            return false;
        }

        String sql = "DELETE FROM departments WHERE dept_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("--> Department deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting department: " + e.getMessage());
        }
        return false;
    }

    // ======================== HELPERS ========================

    public boolean employeeExists(int empId) {
        String sql = "SELECT 1 FROM employees WHERE emp_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean departmentExists(int deptId) {
        String sql = "SELECT 1 FROM departments WHERE dept_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getDepartmentName(int deptId) {
        String sql = "SELECT dept_name FROM departments WHERE dept_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("dept_name");
        } catch (SQLException e) {
            System.err.println("Error looking up department: " + e.getMessage());
        }
        return null;
    }
}
