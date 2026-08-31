import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            runCLI();
        } else {
            System.out.println("==========================================");
            System.out.println("   SMART EMPLOYEE MANAGEMENT SYSTEM");
            System.out.println("==========================================");
            System.out.println();
            System.out.println("  Launch mode:");
            System.out.println("  1. GUI (Graphical Interface)");
            System.out.println("  2. CLI (Terminal Interface)");
            System.out.println();
            System.out.print("  Choose (1 or 2): ");

            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim();
            scanner.close();

            if (choice.equals("2")) {
                runCLI();
            } else {
                try {
                    javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) { e.printStackTrace(); }
                javax.swing.SwingUtilities.invokeLater(() -> {
                    new GUI().setVisible(true);
                });
            }
        }
    }

    private static void runCLI() {
        DatabaseManager db = new DatabaseManager();
        db.createTables();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("   SMART EMPLOYEE MANAGEMENT SYSTEM");
            System.out.println("==========================================");
            System.out.println("  --- DEPARTMENTS ---");
            System.out.println("  1. Add Department");
            System.out.println("  2. View All Departments");
            System.out.println("  3. Delete Department");
            System.out.println("  --- EMPLOYEES ---");
            System.out.println("  4. Add Employee");
            System.out.println("  5. View All Employees");
            System.out.println("  6. Search Employees by Name");
            System.out.println("  7. View Employees by Department");
            System.out.println("  8. Update Employee Salary");
            System.out.println("  9. Transfer Employee to Department");
            System.out.println("  10. Delete Employee");
            System.out.println("  --- ANALYTICS ---");
            System.out.println("  11. Department Salary Summary");
            System.out.println("  --- OTHER ---");
            System.out.println("  0. Exit");
            System.out.println("==========================================");
            System.out.print("Enter choice (0-11): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": {
                    System.out.print("Enter Department Name: ");
                    String deptName = scanner.nextLine().trim();
                    if (deptName.isEmpty()) {
                        System.out.println("--> Error: Department name cannot be empty.");
                        break;
                    }
                    if (db.addDepartment(deptName)) {
                        System.out.println("--> Department added successfully!");
                    } else {
                        System.out.println("--> Error: Department already exists or error occurred.");
                    }
                    break;
                }
                case "2": { db.listAllDepartments(); break; }
                case "3": {
                    db.listAllDepartments();
                    System.out.print("Enter Department ID to delete: ");
                    try {
                        int deptId = Integer.parseInt(scanner.nextLine().trim());
                        db.deleteDepartment(deptId);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! ID must be a number.");
                    }
                    break;
                }
                case "4": {
                    db.listAllDepartments();
                    try {
                        System.out.print("Enter Employee Name: ");
                        String empName = scanner.nextLine().trim();
                        if (empName.isEmpty()) {
                            System.out.println("--> Error: Name cannot be empty.");
                            break;
                        }
                        System.out.print("Enter Salary: ");
                        double salary = Double.parseDouble(scanner.nextLine().trim());
                        if (salary < 0) {
                            System.out.println("--> Error: Salary cannot be negative.");
                            break;
                        }
                        System.out.print("Enter Department ID: ");
                        int deptId = Integer.parseInt(scanner.nextLine().trim());
                        db.addEmployee(empName, salary, deptId);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! Salary and Dept ID must be numbers.");
                    }
                    break;
                }
                case "5": { db.displayAllEmployeesWithDept(); break; }
                case "6": {
                    System.out.print("Enter name to search: ");
                    String keyword = scanner.nextLine().trim();
                    if (keyword.isEmpty()) {
                        System.out.println("--> Error: Search keyword cannot be empty.");
                        break;
                    }
                    db.searchEmployees(keyword);
                    break;
                }
                case "7": {
                    db.listAllDepartments();
                    System.out.print("Enter Department ID: ");
                    try {
                        int deptId = Integer.parseInt(scanner.nextLine().trim());
                        db.displayEmployeesByDepartment(deptId);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! ID must be a number.");
                    }
                    break;
                }
                case "8": {
                    db.displayAllEmployeesWithDept();
                    try {
                        System.out.print("Enter Employee ID: ");
                        int empId = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter New Salary: ");
                        double newSalary = Double.parseDouble(scanner.nextLine().trim());
                        if (newSalary < 0) {
                            System.out.println("--> Error: Salary cannot be negative.");
                            break;
                        }
                        db.updateEmployeeSalary(empId, newSalary);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! ID and Salary must be numbers.");
                    }
                    break;
                }
                case "9": {
                    db.displayAllEmployeesWithDept();
                    db.listAllDepartments();
                    try {
                        System.out.print("Enter Employee ID to transfer: ");
                        int empId = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter New Department ID: ");
                        int newDeptId = Integer.parseInt(scanner.nextLine().trim());
                        db.transferEmployee(empId, newDeptId);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! IDs must be numbers.");
                    }
                    break;
                }
                case "10": {
                    db.displayAllEmployeesWithDept();
                    System.out.print("Enter Employee ID to delete: ");
                    try {
                        int empId = Integer.parseInt(scanner.nextLine().trim());
                        db.deleteEmployee(empId);
                    } catch (NumberFormatException e) {
                        System.out.println("--> Invalid input! ID must be a number.");
                    }
                    break;
                }
                case "11": { db.displayDepartmentSalarySummary(); break; }
                case "0":
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please enter 0-11.");
            }
        }
    }
}
