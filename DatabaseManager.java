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

    public void addEmployee(String name, double salary, int deptId) {
        String sql = "INSERT INTO employees(name, salary, dept_id) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, salary);
            pstmt.setInt(3, deptId);
            pstmt.executeUpdate();
            System.out.println("--> Employee added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
        }
    }

    public void displayAllEmployeesWithDept() {
        String sql = "SELECT e.emp_id, e.name, e.salary, d.dept_name " +
                     "FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nID | Name | Salary | Department");
            System.out.println("-----------------------------------");
            while (rs.next()) {
                int id = rs.getInt("emp_id");
                String name = rs.getString("name");
                double salary = rs.getDouble("salary");
                String dept = rs.getString("dept_name");
                if (dept == null) dept = "Unassigned";

                System.out.printf("%d | %s | $%.2f | %s\n", id, name, salary, dept);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
    }

    public void displayDepartmentSalarySummary() {
        String sql = "SELECT d.dept_name, COUNT(e.emp_id) AS total_emp, AVG(e.salary) AS avg_salary " +
                     "FROM departments d " +
                     "LEFT JOIN employees e ON d.dept_id = e.dept_id " +
                     "GROUP BY d.dept_id;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nDepartment | Total Employees | Avg Salary");
            System.out.println("-------------------------------------------");
            while (rs.next()) {
                String dept = rs.getString("dept_name");
                int count = rs.getInt("total_emp");
                double avgSal = rs.getDouble("avg_salary");

                System.out.printf("%s | %d | $%.2f\n", dept, count, avgSal);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching analytics: " + e.getMessage());
        }
    }
}