package ui;

import dao.ScholarshipDAO;
import model.Scholarship;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class AddScholarshipDialog extends JDialog {
    private JTextField nameField, providerField, amountField, deadlineField, categoryField;
    private JTextArea descArea, eligibilityArea;
    private JComboBox<String> statusCombo;
    private Scholarship existing;
    private ScholarshipDAO dao = new ScholarshipDAO();

    public AddScholarshipDialog(JFrame parent, Scholarship existing) {
        super(parent, existing == null ? "Add Scholarship" : "Edit Scholarship", true);
        this.existing = existing;
        setSize(500, 600);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(nameField, gbc);

        // Provider
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Provider:"), gbc);
        providerField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(providerField, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Amount ($):"), gbc);
        amountField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(amountField, gbc);

        // Deadline
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        deadlineField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(deadlineField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Category:"), gbc);
        categoryField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(categoryField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Status:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"OPEN", "CLOSED"});
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(statusCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Description:"), gbc);
        descArea = new JTextArea(4, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(new JScrollPane(descArea), gbc);

        // Eligibility
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Eligibility:"), gbc);
        eligibilityArea = new JTextArea(4, 25);
        eligibilityArea.setLineWrap(true);
        eligibilityArea.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(new JScrollPane(eligibilityArea), gbc);

        // Save button
        JButton saveBtn = new JButton(existing == null ? "➕ Add Scholarship" : "💾 Save Changes");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; gbc.gridy = row;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(saveBtn, gbc);

        // Pre-fill if editing
        if (existing != null) {
            nameField.setText(existing.getName());
            providerField.setText(existing.getProvider());
            amountField.setText(String.valueOf(existing.getAmount()));
            deadlineField.setText(existing.getDeadline().toString());
            categoryField.setText(existing.getCategory());
            statusCombo.setSelectedItem(existing.getStatus());
            descArea.setText(existing.getDescription());
            eligibilityArea.setText(existing.getEligibilityCriteria());
        }

        saveBtn.addActionListener(e -> saveScholarship());

        add(new JScrollPane(panel));
    }

    private void saveScholarship() {
        try {
            String name = nameField.getText().trim();
            String provider = providerField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            Date deadline = Date.valueOf(deadlineField.getText().trim());
            String category = categoryField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();
            String desc = descArea.getText().trim();
            String eligibility = eligibilityArea.getText().trim();

            if (name.isEmpty() || provider.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields!");
                return;
            }

            Scholarship s;
            if (existing != null) {
                s = existing;
            } else {
                s = new Scholarship();
            }
            s.setName(name);
            s.setProvider(provider);
            s.setAmount(amount);
            s.setDeadline(deadline);
            s.setCategory(category);
            s.setStatus(status);
            s.setDescription(desc);
            s.setEligibilityCriteria(eligibility);

            boolean success;
            if (existing != null) {
                success = dao.updateScholarship(s);
            } else {
                success = dao.addScholarship(s);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, existing != null ? "Updated!" : "Added!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Operation failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}