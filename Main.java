import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.createTables();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== SMART EMPLOYEE MANAGEMENT SYSTEM ===");
            System.out.println("1. Add Department");
            System.out.println("2. Add Employee");
            System.out.println("3. View All Employees (SQL JOIN)");
            System.out.println("4. View Department Salary Analytics (SQL GROUP BY)");
            System.out.println("5. Exit");
            System.out.print("Enter choice (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter Department Name: ");
                    String deptName = scanner.nextLine().trim();
                    if (db.addDepartment(deptName)) {
                        System.out.println("--> Department added successfully!");
                    } else {
                        System.out.println("--> Error: Department already exists or error occurred.");
                    }
                    break;

                case "2":
                    try {
                        System.out.print("Enter Employee Name: ");
                        String empName = scanner.nextLine().trim();
                        System.out.print("Enter Salary: ");
                        double salary = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Enter Department ID: ");
                        int deptId = Integer.parseInt(scanner.nextLine().trim());

                        db.addEmployee(empName, salary, deptId);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! Salary and Dept ID must be numbers.");
                    }
                    break;

                case "3":
                    db.displayAllEmployeesWithDept();
                    break;

                case "4":
                    db.displayDepartmentSalarySummary();
                    break;

                case "5":
                    System.out.println("Exiting application...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}