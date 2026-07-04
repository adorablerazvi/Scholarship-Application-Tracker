package ui;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private UserDAO userDAO = new UserDAO();
    private ScholarshipDAO scholarshipDAO = new ScholarshipDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Scholarship Tracker - " + (user.getRole().equals("ADMIN") ? "Admin" : "Student") + " Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(createScholarshipsPanel(), "SCHOLARSHIPS");
        contentPanel.add(createMyApplicationsPanel(), "MY_APPLICATIONS");

        if (currentUser.getRole().equals("ADMIN")) {
            contentPanel.add(createManageScholarshipsPanel(), "MANAGE_SCHOLARSHIPS");
            contentPanel.add(createAllApplicationsPanel(), "ALL_APPLICATIONS");
            contentPanel.add(createStudentsPanel(), "STUDENTS");
        }

        contentPanel.add(createProfilePanel(), "PROFILE");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "HOME");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User info
        JLabel userLabel = new JLabel(currentUser.getFullName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userLabel);

        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setForeground(new Color(149, 165, 166));
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(roleLabel);

        sidebar.add(Box.createVerticalStrut(30));

        // Menu buttons
        addSidebarButton(sidebar, "🏠 Home", "HOME");
        addSidebarButton(sidebar, "📚 Scholarships", "SCHOLARSHIPS");
        addSidebarButton(sidebar, "📝 My Applications", "MY_APPLICATIONS");

        if (currentUser.getRole().equals("ADMIN")) {
            sidebar.add(Box.createVerticalStrut(15));
            JLabel adminLabel = new JLabel("── ADMIN ──");
            adminLabel.setForeground(new Color(241, 196, 15));
            adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(adminLabel);
            sidebar.add(Box.createVerticalStrut(5));

            addSidebarButton(sidebar, "⚙ Manage Scholarships", "MANAGE_SCHOLARSHIPS");
            addSidebarButton(sidebar, "📋 All Applications", "ALL_APPLICATIONS");
            addSidebarButton(sidebar, "👥 Students", "STUDENTS");
        }

        sidebar.add(Box.createVerticalStrut(15));
        addSidebarButton(sidebar, "👤 Profile", "PROFILE");

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private void addSidebarButton(JPanel sidebar, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(41, 128, 185)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(44, 62, 80)); }
        });
        btn.addActionListener(e -> {
            // Refresh panels
            refreshContent(cardName);
            cardLayout.show(contentPanel, cardName);
        });
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private void refreshContent(String cardName) {
        // Remove old and re-add refreshed panels
        for (Component c : contentPanel.getComponents()) {
            // We'll just re-create panels for simplicity
        }
        switch (cardName) {
            case "HOME":
                replacePanel("HOME", createHomePanel());
                break;
            case "SCHOLARSHIPS":
                replacePanel("SCHOLARSHIPS", createScholarshipsPanel());
                break;
            case "MY_APPLICATIONS":
                replacePanel("MY_APPLICATIONS", createMyApplicationsPanel());
                break;
            case "MANAGE_SCHOLARSHIPS":
                replacePanel("MANAGE_SCHOLARSHIPS", createManageScholarshipsPanel());
                break;
            case "ALL_APPLICATIONS":
                replacePanel("ALL_APPLICATIONS", createAllApplicationsPanel());
                break;
            case "STUDENTS":
                replacePanel("STUDENTS", createStudentsPanel());
                break;
            case "PROFILE":
                replacePanel("PROFILE", createProfilePanel());
                break;
        }
    }

    private void replacePanel(String name, JPanel newPanel) {
        // Remove existing by iterating (CardLayout doesn't have remove by name easily)
        contentPanel.add(newPanel, name);
        cardLayout.show(contentPanel, name);
    }

    // ==================== HOME PANEL ====================
    private JPanel createHomePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(236, 240, 241));
    panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // Title
    JLabel title = new JLabel("📊 Dashboard Overview");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    panel.add(title, BorderLayout.NORTH);

    // ===== Fetch all data =====
    int totalScholarships = scholarshipDAO.getTotalScholarships();
    int totalApplications = applicationDAO.getTotalApplications();
    int pending = applicationDAO.getCountByStatus("PENDING");
    int approved = applicationDAO.getCountByStatus("APPROVED");
    int rejected = applicationDAO.getCountByStatus("REJECTED");
    int underReview = applicationDAO.getCountByStatus("UNDER_REVIEW");
    double totalFunding = scholarshipDAO.getTotalFunding();

    // ===== TOP ROW: Stat Cards (1 row, 4 cards) =====
    JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    statsPanel.setBackground(new Color(236, 240, 241));
    statsPanel.setPreferredSize(new Dimension(0, 90));

    statsPanel.add(createStatCard("Total Scholarships", String.valueOf(totalScholarships), new Color(52, 152, 219)));
    statsPanel.add(createStatCard("Total Applications", String.valueOf(totalApplications), new Color(155, 89, 182)));
    statsPanel.add(createStatCard("Total Funding", "$" + String.format("%.0f", totalFunding), new Color(46, 204, 113)));
    statsPanel.add(createStatCard("Approved", String.valueOf(approved), new Color(39, 174, 96)));

    // ===== MIDDLE ROW: Pie Chart + Bar Chart =====
    JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
    chartsPanel.setBackground(new Color(236, 240, 241));
    chartsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

    // Pie Chart
    PieChartPanel pieChart = new PieChartPanel(
        "Application Status",
        new String[]{"Pending", "Approved", "Rejected", "Under Review"},
        new double[]{pending, approved, rejected, underReview},
        new Color[]{
            new Color(241, 196, 15),
            new Color(39, 174, 96),
            new Color(231, 76, 60),
            new Color(52, 152, 219)
        }
    );
    pieChart.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    // Bar Chart
    BarChartPanel barChart = new BarChartPanel(
        "Applications Overview",
        new String[]{"Pending", "Approved", "Rejected", "Review"},
        new double[]{pending, approved, rejected, underReview},
        new Color[]{
            new Color(241, 196, 15),
            new Color(39, 174, 96),
            new Color(231, 76, 60),
            new Color(52, 152, 219)
        }
    );
    barChart.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    chartsPanel.add(pieChart);
    chartsPanel.add(barChart);

    // ===== BOTTOM ROW: Welcome + Approval Rate =====
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(new Color(236, 240, 241));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

    JLabel welcomeMsg = new JLabel("Welcome back, " + currentUser.getFullName() + "! | Role: " + currentUser.getRole());
    welcomeMsg.setFont(new Font("Segoe UI", Font.ITALIC, 13));
    welcomeMsg.setForeground(new Color(127, 140, 141));

    JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightInfo.setBackground(new Color(236, 240, 241));

    double approvalRate = totalApplications > 0 ? (approved * 100.0 / totalApplications) : 0;
    JLabel rateLabel = new JLabel(String.format("📈 Approval Rate: %.1f%%", approvalRate));
    rateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    rateLabel.setForeground(new Color(39, 174, 96));

    JLabel pendingLabel = new JLabel("  |  ⏳ Pending: " + pending);
    pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    pendingLabel.setForeground(new Color(241, 196, 15));

    JLabel rejLabel = new JLabel("  |  ❌ Rejected: " + rejected);
    rejLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    rejLabel.setForeground(new Color(231, 76, 60));

    rightInfo.add(rateLabel);
    rightInfo.add(pendingLabel);
    rightInfo.add(rejLabel);

    bottomPanel.add(welcomeMsg, BorderLayout.WEST);
    bottomPanel.add(rightInfo, BorderLayout.EAST);

    // ===== Assemble everything =====
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(new Color(236, 240, 241));
    centerPanel.add(statsPanel, BorderLayout.NORTH);
    centerPanel.add(chartsPanel, BorderLayout.CENTER);

    panel.add(centerPanel, BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    return panel;
}

private JPanel createStatCard(String title, String value, Color color) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 2),
        BorderFactory.createEmptyBorder(10, 15, 10, 15)
    ));

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    titleLabel.setForeground(new Color(127, 140, 141));
    card.add(titleLabel, BorderLayout.NORTH);

    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    valueLabel.setForeground(color);
    valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
    card.add(valueLabel, BorderLayout.CENTER);

    return card;
}
    // ==================== SCHOLARSHIPS PANEL (Browse & Apply) ====================
    private JPanel createScholarshipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("📚 Available Scholarships");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));

        JComboBox<String> categoryCombo = new JComboBox<>();
        for (String cat : scholarshipDAO.getAllCategories()) {
            categoryCombo.addItem(cat);
        }

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("  Category: "));
        searchPanel.add(categoryCombo);
        searchPanel.add(searchBtn);

        // Table
        String[] cols = {"ID", "Name", "Provider", "Amount ($)", "Deadline", "Category", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadScholarshipsTable(model, scholarshipDAO.getOpenScholarships());

        JScrollPane scrollPane = new JScrollPane(table);

        // Apply button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("📝 Apply for Selected Scholarship");
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        applyBtn.setBackground(new Color(46, 204, 113));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton viewDetailsBtn = new JButton("🔍 View Details");
        viewDetailsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        viewDetailsBtn.setBackground(new Color(52, 152, 219));
        viewDetailsBtn.setForeground(Color.WHITE);

        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(applyBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Search action
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            List<Scholarship> results = scholarshipDAO.searchScholarships(keyword, category);
            loadScholarshipsTable(model, results);
        });

        // View details
        viewDetailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a scholarship!");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            Scholarship s = scholarshipDAO.getScholarshipById(id);
            if (s != null) {
                String details = String.format(
                    "Name: %s\nProvider: %s\nAmount: $%.2f\nDeadline: %s\nCategory: %s\nStatus: %s\n\nDescription:\n%s\n\nEligibility:\n%s",
                    s.getName(), s.getProvider(), s.getAmount(), s.getDeadline(),
                    s.getCategory(), s.getStatus(), s.getDescription(), s.getEligibilityCriteria()
                );
                JTextArea textArea = new JTextArea(details);
                textArea.setEditable(false);
                textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                JScrollPane sp = new JScrollPane(textArea);
                sp.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, sp, "Scholarship Details", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Apply action
        applyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a scholarship to apply!");
                return;
            }
            int scholarshipId = (int) model.getValueAt(row, 0);
            String scholarshipName = (String) model.getValueAt(row, 1);

            if (applicationDAO.hasAlreadyApplied(currentUser.getUserId(), scholarshipId)) {
                JOptionPane.showMessageDialog(this, "You have already applied for this scholarship!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            new ApplyScholarshipDialog(this, currentUser, scholarshipId, scholarshipName).setVisible(true);
        });

        return panel;
    }

    private void loadScholarshipsTable(DefaultTableModel model, List<Scholarship> list) {
        model.setRowCount(0);
        for (Scholarship s : list) {
            model.addRow(new Object[]{
                s.getScholarshipId(), s.getName(), s.getProvider(),
                s.getAmount(), s.getDeadline(), s.getCategory(), s.getStatus()
            });
        }
    }

    // ==================== MY APPLICATIONS PANEL ====================
    private JPanel createMyApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("📝 My Applications");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"App ID", "Scholarship", "Amount ($)", "Date", "Status", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Custom renderer for status coloring
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        List<Application> apps = applicationDAO.getApplicationsByUser(currentUser.getUserId());
        for (Application app : apps) {
            model.addRow(new Object[]{
                app.getApplicationId(), app.getScholarshipName(), app.getScholarshipAmount(),
                app.getApplicationDate(), app.getStatus(), app.getRemarks() == null ? "" : app.getRemarks()
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);

        // Withdraw button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton withdrawBtn = new JButton("❌ Withdraw Application");
        withdrawBtn.setBackground(new Color(231, 76, 60));
        withdrawBtn.setForeground(Color.WHITE);
        withdrawBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        withdrawBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an application to withdraw!");
                return;
            }
            String status = (String) model.getValueAt(row, 4);
            if (!status.equals("PENDING")) {
                JOptionPane.showMessageDialog(this, "Can only withdraw PENDING applications!");
                return;
            }
            int appId = (int) model.getValueAt(row, 0);
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to withdraw?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                if (applicationDAO.deleteApplication(appId)) {
                    JOptionPane.showMessageDialog(this, "Application withdrawn successfully!");
                    model.removeRow(row);
                }
            }
        });
        btnPanel.add(withdrawBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== MANAGE SCHOLARSHIPS (ADMIN) ====================
    private JPanel createManageScholarshipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("⚙ Manage Scholarships");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Provider", "Amount ($)", "Deadline", "Category", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        loadScholarshipsTable(model, scholarshipDAO.getAllScholarships());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addBtn = new JButton("➕ Add New");
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton editBtn = new JButton("✏ Edit");
        editBtn.setBackground(new Color(52, 152, 219));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton deleteBtn = new JButton("🗑 Delete");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton toggleBtn = new JButton("🔄 Toggle Open/Closed");
        toggleBtn.setBackground(new Color(241, 196, 15));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(toggleBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Add scholarship
        addBtn.addActionListener(e -> {
            new AddScholarshipDialog(this, null).setVisible(true);
            loadScholarshipsTable(model, scholarshipDAO.getAllScholarships());
        });

        // Edit scholarship
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a scholarship!"); return; }
            int id = (int) model.getValueAt(row, 0);
            Scholarship s = scholarshipDAO.getScholarshipById(id);
            new AddScholarshipDialog(this, s).setVisible(true);
            loadScholarshipsTable(model, scholarshipDAO.getAllScholarships());
        });

        // Delete
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a scholarship!"); return; }
            int id = (int) model.getValueAt(row, 0);
            int choice = JOptionPane.showConfirmDialog(this, "Delete this scholarship?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                if (scholarshipDAO.deleteScholarship(id)) {
                    JOptionPane.showMessageDialog(this, "Deleted!");
                    loadScholarshipsTable(model, scholarshipDAO.getAllScholarships());
                }
            }
        });

        // Toggle status
        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a scholarship!"); return; }
            int id = (int) model.getValueAt(row, 0);
            Scholarship s = scholarshipDAO.getScholarshipById(id);
            s.setStatus(s.getStatus().equals("OPEN") ? "CLOSED" : "OPEN");
            scholarshipDAO.updateScholarship(s);
            loadScholarshipsTable(model, scholarshipDAO.getAllScholarships());
        });

        return panel;
    }

    // ==================== ALL APPLICATIONS (ADMIN) ====================
    private JPanel createAllApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("📋 All Applications (Admin)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"App ID", "Student", "Scholarship", "Amount ($)", "GPA", "Income", "Date", "Status", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        loadAllApplicationsTable(model, applicationDAO.getAllApplications());

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED"});
        JButton filterBtn = new JButton("Filter");
        filterBtn.setBackground(new Color(52, 152, 219));
        filterBtn.setForeground(Color.WHITE);
        filterPanel.add(new JLabel("Filter by Status: "));
        filterPanel.add(filterCombo);
        filterPanel.add(filterBtn);

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton approveBtn = new JButton("✅ Approve");
        approveBtn.setBackground(new Color(46, 204, 113));
        approveBtn.setForeground(Color.WHITE);

        JButton rejectBtn = new JButton("❌ Reject");
        rejectBtn.setBackground(new Color(231, 76, 60));
        rejectBtn.setForeground(Color.WHITE);

        JButton reviewBtn = new JButton("🔍 Under Review");
        reviewBtn.setBackground(new Color(241, 196, 15));
        reviewBtn.setForeground(Color.WHITE);

        JButton viewSopBtn = new JButton("📄 View SOP");
        viewSopBtn.setBackground(new Color(155, 89, 182));
        viewSopBtn.setForeground(Color.WHITE);

        btnPanel.add(viewSopBtn);
        btnPanel.add(reviewBtn);
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(btnPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Filter action
        filterBtn.addActionListener(e -> {
            String selected = (String) filterCombo.getSelectedItem();
            if (selected.equals("All")) {
                loadAllApplicationsTable(model, applicationDAO.getAllApplications());
            } else {
                loadAllApplicationsTable(model, applicationDAO.filterApplications(selected));
            }
        });

        // Status change actions
        ActionListener statusAction = e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select an application!"); return; }
            int appId = (int) model.getValueAt(row, 0);
            String newStatus;
            if (e.getSource() == approveBtn) newStatus = "APPROVED";
            else if (e.getSource() == rejectBtn) newStatus = "REJECTED";
            else newStatus = "UNDER_REVIEW";

            String remarks = JOptionPane.showInputDialog(this, "Enter remarks (optional):");
            if (applicationDAO.updateApplicationStatus(appId, newStatus, remarks)) {
                JOptionPane.showMessageDialog(this, "Status updated to " + newStatus);
                String filter = (String) filterCombo.getSelectedItem();
                if (filter.equals("All")) {
                    loadAllApplicationsTable(model, applicationDAO.getAllApplications());
                } else {
                    loadAllApplicationsTable(model, applicationDAO.filterApplications(filter));
                }
            }
        };
        approveBtn.addActionListener(statusAction);
        rejectBtn.addActionListener(statusAction);
        reviewBtn.addActionListener(statusAction);

        // View SOP
        viewSopBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select an application!"); return; }
            int appId = (int) model.getValueAt(row, 0);
            // Fetch full application
            List<Application> all = applicationDAO.getAllApplications();
            for (Application app : all) {
                if (app.getApplicationId() == appId) {
                    JTextArea ta = new JTextArea(app.getStatementOfPurpose());
                    ta.setEditable(false);
                    ta.setLineWrap(true);
                    ta.setWrapStyleWord(true);
                    ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    JScrollPane sp = new JScrollPane(ta);
                    sp.setPreferredSize(new Dimension(400, 300));
                    JOptionPane.showMessageDialog(this, sp, "Statement of Purpose", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        });

        return panel;
    }

    private void loadAllApplicationsTable(DefaultTableModel model, List<Application> list) {
        model.setRowCount(0);
        for (Application app : list) {
            model.addRow(new Object[]{
                app.getApplicationId(), app.getUserName(), app.getScholarshipName(),
                app.getScholarshipAmount(), app.getGpa(), app.getIncome(),
                app.getApplicationDate(), app.getStatus(),
                app.getRemarks() == null ? "" : app.getRemarks()
            });
        }
    }

    // ==================== STUDENTS LIST (ADMIN) ====================
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("👥 Registered Students");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Email", "Phone", "Registered Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        List<User> students = userDAO.getAllStudents();
        for (User u : students) {
            model.addRow(new Object[]{
                u.getUserId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getCreatedAt()
            });
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel countLabel = new JLabel("Total Students: " + students.size());
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(countLabel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== PROFILE PANEL ====================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("👤 My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        JTextField nameField = new JTextField(currentUser.getFullName(), 25);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(currentUser.getEmail(), 25);
        emailField.setEditable(false);
        emailField.setBackground(new Color(236, 240, 241));
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        JTextField phoneField = new JTextField(currentUser.getPhone(), 25);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("New Password:"), gbc);
        JPasswordField passField = new JPasswordField(25);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Role:"), gbc);
        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 1;
        panel.add(roleLabel, gbc);

        JButton saveBtn = new JButton("💾 Save Changes");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String newPass = new String(passField.getPassword());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty!");
                return;
            }

            currentUser.setFullName(name);
            currentUser.setPhone(phone);
            if (!newPass.isEmpty()) {
                if (newPass.length() < 6) {
                    JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!");
                    return;
                }
                currentUser.setPassword(newPass);
            }

            if (userDAO.updateUser(currentUser)) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // Custom cell renderer for status column
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";
            switch (status) {
                case "APPROVED": c.setBackground(new Color(212, 239, 223)); c.setForeground(new Color(39, 174, 96)); break;
                case "REJECTED": c.setBackground(new Color(250, 219, 216)); c.setForeground(new Color(231, 76, 60)); break;
                case "PENDING": c.setBackground(new Color(252, 243, 207)); c.setForeground(new Color(243, 156, 18)); break;
                case "UNDER_REVIEW": c.setBackground(new Color(214, 234, 248)); c.setForeground(new Color(41, 128, 185)); break;
                default: c.setBackground(Color.WHITE); c.setForeground(Color.BLACK);
            }
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return c;
        }
    }
}