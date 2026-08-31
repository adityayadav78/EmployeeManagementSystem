import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;

public class GUI extends JFrame {
    private DatabaseManager db;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private DefaultTableModel empTableModel;
    private DefaultTableModel deptTableModel;
    private DefaultTableModel searchTableModel;
    private JTable empTable;
    private JTable deptTable;
    private JTable searchTable;
    private JLabel empCountLabel, deptCountLabel, budgetLabel;

    private static final Color PRIMARY = new Color(41, 98, 255);
    private static final Color PRIMARY_DARK = new Color(25, 72, 200);
    private static final Color BG_LIGHT = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS = new Color(40, 167, 69);
    private static final Color DANGER = new Color(220, 53, 69);
    private static final Color WARNING = new Color(255, 193, 7);

    public GUI() {
        db = new DatabaseManager();
        db.createTables();
        setTitle("Smart Employee & Department Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_LIGHT);
        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(createEmployeesPanel(), "employees");
        contentPanel.add(createDepartmentsPanel(), "departments");
        contentPanel.add(createSearchPanel(), "search");
        contentPanel.add(createAnalyticsPanel(), "analytics");
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        refreshDashboard();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("  EMP MANAGER");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 15, 30, 0));
        sidebar.add(logo);

        String[][] items = {{"Dashboard","dashboard"},{"Employees","employees"},{"Departments","departments"},{"Search","search"},{"Analytics","analytics"}};
        for (String[] item : items) {
            JButton btn = createSidebarButton(item[0], item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }
        sidebar.add(Box.createVerticalGlue());
        JLabel ver = new JLabel("  v2.0 - Enhanced");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(255, 255, 255, 150));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(ver);
        return sidebar;
    }

    private JButton createSidebarButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
        });
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, panelName);
            switch (panelName) {
                case "dashboard": refreshDashboard(); break;
                case "employees": refreshEmployeeTable(); break;
                case "departments": refreshDeptTable(); break;
                case "analytics": refreshAnalytics(); break;
            }
        });
        return btn;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(TEXT_PRIMARY);
        panel.add(header, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setOpaque(false);

        empCountLabel = new JLabel("0");
        deptCountLabel = new JLabel("0");
        budgetLabel = new JLabel("$0.00");

        cardsPanel.add(createStatCard("Total Employees", empCountLabel, PRIMARY));
        cardsPanel.add(createStatCard("Departments", deptCountLabel, SUCCESS));
        cardsPanel.add(createStatCard("Total Budget", budgetLabel, WARNING));

        panel.add(cardsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 4));
        card.add(accentBar, BorderLayout.NORTH);
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(TEXT_SECONDARY);
        textPanel.add(titleLbl, BorderLayout.NORTH);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(TEXT_PRIMARY);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void refreshDashboard() {
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement()) {
            ResultSet r1 = stmt.executeQuery("SELECT COUNT(*) FROM employees");
            if (r1.next()) empCountLabel.setText(String.valueOf(r1.getInt(1)));
            ResultSet r2 = stmt.executeQuery("SELECT COUNT(*) FROM departments");
            if (r2.next()) deptCountLabel.setText(String.valueOf(r2.getInt(1)));
            ResultSet r3 = stmt.executeQuery("SELECT COALESCE(SUM(salary),0) FROM employees");
            if (r3.next()) budgetLabel.setText(String.format("$%,.2f", r3.getDouble(1)));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ======================== EMPLOYEES PANEL ========================

    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel header = new JLabel("Employees");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(TEXT_PRIMARY);
        topBar.add(header, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton addBtn = createActionButton("+ Add Employee", PRIMARY);
        addBtn.addActionListener(e -> showAddEmployeeDialog());
        JButton editBtn = createActionButton("Edit Salary", new Color(255, 152, 0));
        editBtn.addActionListener(e -> showEditSalaryDialog());
        JButton transferBtn = createActionButton("Transfer", new Color(0, 150, 136));
        transferBtn.addActionListener(e -> showTransferDialog());
        JButton deleteBtn = createActionButton("Delete", DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedEmployee());
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(transferBtn); btnPanel.add(deleteBtn);
        topBar.add(btnPanel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        empTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Salary", "Department"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        empTable = new JTable(empTableModel);
        styleTable(empTable);
        panel.add(new JScrollPane(empTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshEmployeeTable() {
        empTableModel.setRowCount(0);
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT e.emp_id, e.name, e.salary, COALESCE(d.dept_name,'Unassigned') FROM employees e LEFT JOIN departments d ON e.dept_id=d.dept_id ORDER BY e.emp_id")) {
            while (rs.next()) empTableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), String.format("$%,.2f", rs.getDouble(3)), rs.getString(4)});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add Employee", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField salaryField = new JTextField(20);
        JComboBox<String> deptCombo = new JComboBox<>();
        deptCombo.addItem("-- Select Department --");
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept_id, dept_name FROM departments ORDER BY dept_name")) {
            while (rs.next()) deptCombo.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException ex) { ex.printStackTrace(); }

        addFormRow(form, gbc, 0, "Name:", nameField);
        addFormRow(form, gbc, 1, "Salary:", salaryField);
        addFormRow(form, gbc, 2, "Department:", deptCombo);

        JButton saveBtn = createActionButton("Save", SUCCESS);
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double salary = Double.parseDouble(salaryField.getText().trim());
                int idx = deptCombo.getSelectedIndex();
                if (name.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Name is required!"); return; }
                if (salary < 0) { JOptionPane.showMessageDialog(dialog, "Salary cannot be negative!"); return; }
                if (idx <= 0) { JOptionPane.showMessageDialog(dialog, "Please select a department!"); return; }
                int deptId = Integer.parseInt(deptCombo.getItemAt(idx).split(" - ")[0]);
                db.addEmployee(name, salary, deptId);
                JOptionPane.showMessageDialog(dialog, "Employee added successfully!");
                dialog.dispose(); refreshEmployeeTable(); refreshDashboard();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(dialog, "Invalid salary!"); }
        });
        JButton cancelBtn = createActionButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        btnRow.add(cancelBtn); btnRow.add(saveBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btnRow, gbc);
        dialog.add(form);
        dialog.setVisible(true);
    }

    private void showEditSalaryDialog() {
        int row = empTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an employee!"); return; }
        int empId = (int) empTableModel.getValueAt(row, 0);
        String empName = (String) empTableModel.getValueAt(row, 1);
        String input = JOptionPane.showInputDialog(this, "New salary for " + empName + ":", "Edit Salary", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            try {
                double newSalary = Double.parseDouble(input.trim());
                if (newSalary < 0) { JOptionPane.showMessageDialog(this, "Salary cannot be negative!"); return; }
                db.updateEmployeeSalary(empId, newSalary);
                refreshEmployeeTable(); refreshDashboard();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid salary!"); }
        }
    }

    private void showTransferDialog() {
        int row = empTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an employee!"); return; }
        int empId = (int) empTableModel.getValueAt(row, 0);
        String empName = (String) empTableModel.getValueAt(row, 1);

        JComboBox<String> deptCombo = new JComboBox<>();
        deptCombo.addItem("-- Select New Department --");
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept_id, dept_name FROM departments ORDER BY dept_name")) {
            while (rs.next()) deptCombo.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException ex) { ex.printStackTrace(); }

        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.add(new JLabel("Transfer " + empName + " to:"), BorderLayout.NORTH);
        panel.add(deptCombo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int idx = deptCombo.getSelectedIndex();
            if (idx <= 0) { JOptionPane.showMessageDialog(this, "Please select a department!"); return; }
            int newDeptId = Integer.parseInt(deptCombo.getItemAt(idx).split(" - ")[0]);
            db.transferEmployee(empId, newDeptId);
            refreshEmployeeTable(); refreshDashboard();
        }
    }

    private void deleteSelectedEmployee() {
        int row = empTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an employee!"); return; }
        int empId = (int) empTableModel.getValueAt(row, 0);
        String empName = (String) empTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete employee \"" + empName + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteEmployee(empId);
            refreshEmployeeTable(); refreshDashboard();
        }
    }

    // ======================== DEPARTMENTS PANEL ========================

    private JPanel createDepartmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel header = new JLabel("Departments");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(TEXT_PRIMARY);
        topBar.add(header, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton addBtn = createActionButton("+ Add Department", PRIMARY);
        addBtn.addActionListener(e -> showAddDepartmentDialog());
        JButton deleteBtn = createActionButton("Delete", DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedDepartment());
        btnPanel.add(addBtn); btnPanel.add(deleteBtn);
        topBar.add(btnPanel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        deptTableModel = new DefaultTableModel(new Object[]{"ID", "Department", "Employees"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        deptTable = new JTable(deptTableModel);
        styleTable(deptTable);
        panel.add(new JScrollPane(deptTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshDeptTable() {
        deptTableModel.setRowCount(0);
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT d.dept_id, d.dept_name, COUNT(e.emp_id) FROM departments d LEFT JOIN employees e ON d.dept_id=e.dept_id GROUP BY d.dept_id ORDER BY d.dept_name")) {
            while (rs.next()) deptTableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3)});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void showAddDepartmentDialog() {
        String name = JOptionPane.showInputDialog(this, "Department name:", "Add Department", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            if (db.addDepartment(name.trim())) {
                JOptionPane.showMessageDialog(this, "Department added!");
                refreshDeptTable(); refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Department already exists or error occurred.");
            }
        }
    }

    private void deleteSelectedDepartment() {
        int row = deptTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a department!"); return; }
        int deptId = (int) deptTableModel.getValueAt(row, 0);
        String deptName = (String) deptTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete department \"" + deptName + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteDepartment(deptId);
            refreshDeptTable(); refreshDashboard();
        }
    }

    // ======================== SEARCH PANEL ========================

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);
        JLabel header = new JLabel("Search Employees");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(TEXT_PRIMARY);
        topBar.add(header, BorderLayout.WEST);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchRow.setOpaque(false);
        JTextField searchField = new JTextField(20);
        JButton searchBtn = createActionButton("Search", PRIMARY);
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) doSearch(keyword);
        });
        searchField.addActionListener(e -> searchBtn.doClick());
        searchRow.add(new JLabel("Name:"));
        searchRow.add(searchField);
        searchRow.add(searchBtn);
        topBar.add(searchRow, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        searchTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Salary", "Department"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        searchTable = new JTable(searchTableModel);
        styleTable(searchTable);
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        return panel;
    }

    private void doSearch(String keyword) {

        searchTableModel.setRowCount(0);
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT e.emp_id, e.name, e.salary, COALESCE(d.dept_name,'Unassigned') " +
                "FROM employees e LEFT JOIN departments d ON e.dept_id=d.dept_id " +
                "WHERE e.name LIKE ? ORDER BY e.name")) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                searchTableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2),
                    String.format("$%,.2f", rs.getDouble(3)), rs.getString(4)});
            if (searchTableModel.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "No results found for \"" + keyword + "\"");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ======================== ANALYTICS PANEL ========================

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel header = new JLabel("Salary Analytics");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(TEXT_PRIMARY);
        topBar.add(header, BorderLayout.WEST);
        panel.add(topBar, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        DefaultTableModel analyticsModel = new DefaultTableModel(
            new Object[]{"Department", "Employees", "Avg Salary", "Total Salary"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable analyticsTable = new JTable(analyticsModel);
        styleTable(analyticsTable);

        try (Connection conn = db.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT d.dept_name, COUNT(e.emp_id), COALESCE(AVG(e.salary),0), COALESCE(SUM(e.salary),0) " +
                "FROM departments d LEFT JOIN employees e ON d.dept_id=e.dept_id GROUP BY d.dept_id ORDER BY d.dept_name")) {
            while (rs.next())
                analyticsModel.addRow(new Object[]{rs.getString(1), rs.getInt(2),
                    String.format("$%,.2f", rs.getDouble(3)), String.format("$%,.2f", rs.getDouble(4))});
        } catch (SQLException e) { e.printStackTrace(); }

        tablePanel.add(new JScrollPane(analyticsTable), BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAnalytics() {}

    // ======================== HELPER METHODS ========================

    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
