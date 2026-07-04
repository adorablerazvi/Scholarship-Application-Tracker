package ui;

import dao.ApplicationDAO;
import model.Application;
import model.User;
import javax.swing.*;
import java.awt.*;

public class ApplyScholarshipDialog extends JDialog {
    private JTextField gpaField, incomeField;
    private JTextArea sopArea;
    private JTextField documentsField;
    private User currentUser;
    private int scholarshipId;
    private ApplicationDAO dao = new ApplicationDAO();

    public ApplyScholarshipDialog(JFrame parent, User user, int scholarshipId, String scholarshipName) {
        super(parent, "Apply for: " + scholarshipName, true);
        this.currentUser = user;
        this.scholarshipId = scholarshipId;
        setSize(500, 500);
        setLocationRelativeTo(parent);
        initComponents(scholarshipName);
    }

    private void initComponents(String scholarshipName) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // Title
        JLabel title = new JLabel("Applying for: " + scholarshipName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(title, gbc);
        row++;
        gbc.gridwidth = 1;

        // GPA
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("GPA (0.0 - 4.0):"), gbc);
        gpaField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(gpaField, gbc);

        // Income
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Family Annual Income ($):"), gbc);
        incomeField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(incomeField, gbc);

        // Documents path
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Documents (file path):"), gbc);
        JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        documentsField = new JTextField(15);
        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                StringBuilder paths = new StringBuilder();
                for (var file : fc.getSelectedFiles()) {
                    if (paths.length() > 0) paths.append(";");
                    paths.append(file.getAbsolutePath());
                }
                documentsField.setText(paths.toString());
            }
        });
        docPanel.add(documentsField);
        docPanel.add(browseBtn);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(docPanel, gbc);

        // SOP
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Statement of Purpose:"), gbc);
        sopArea = new JTextArea(8, 20);
        sopArea.setLineWrap(true);
        sopArea.setWrapStyleWord(true);
        sopArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(new JScrollPane(sopArea), gbc);

        // Submit button
        JButton submitBtn = new JButton("📝 Submit Application");
        submitBtn.setBackground(new Color(46, 204, 113));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 8, 8, 8);
        panel.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> submitApplication());

        add(new JScrollPane(panel));
    }

    private void submitApplication() {
        try {
            double gpa = Double.parseDouble(gpaField.getText().trim());
            double income = Double.parseDouble(incomeField.getText().trim());
            String sop = sopArea.getText().trim();
            String docs = documentsField.getText().trim();

            if (gpa < 0 || gpa > 4.0) {
                JOptionPane.showMessageDialog(this, "GPA must be between 0.0 and 4.0!");
                return;
            }
            if (sop.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please write a Statement of Purpose!");
                return;
            }

            Application app = new Application();
            app.setUserId(currentUser.getUserId());
            app.setScholarshipId(scholarshipId);
            app.setGpa(gpa);
            app.setIncome(income);
            app.setStatementOfPurpose(sop);
            app.setDocumentsPath(docs);

            if (dao.applyForScholarship(app)) {
                JOptionPane.showMessageDialog(this, "Application submitted successfully! 🎉");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed! You may have already applied.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for GPA and Income!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}