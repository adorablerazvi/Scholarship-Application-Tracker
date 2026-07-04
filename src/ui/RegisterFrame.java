package ui;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("Scholarship Tracker - Register");
        setSize(450, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(44, 62, 80));

        JLabel header = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(44, 62, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        nameField = addFormField(formPanel, gbc, "Full Name:", row++);
        emailField = addFormField(formPanel, gbc, "Email:", row++);
        phoneField = addFormField(formPanel, gbc, "Phone:", row++);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row * 2;
        formPanel.add(passLabel, gbc);
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 33));
        gbc.gridy = row * 2 + 1;
        formPanel.add(passwordField, gbc);
        row++;

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = row * 2;
        formPanel.add(confirmLabel, gbc);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setPreferredSize(new Dimension(250, 33));
        gbc.gridy = row * 2 + 1;
        formPanel.add(confirmPasswordField, gbc);
        row++;

        JButton registerBtn = new JButton("REGISTER");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setBackground(new Color(52, 152, 219));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(250, 40));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = row * 2;
        gbc.insets = new Insets(20, 10, 5, 10);
        formPanel.add(registerBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setForeground(new Color(46, 204, 113));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(5, 10, 10, 10);
        formPanel.add(backBtn, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        registerBtn.addActionListener(e -> performRegister());
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String labelText, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row * 2;
        panel.add(label, gbc);
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(250, 33));
        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
        return field;
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Email & Password are required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(name, email, password, phone, "STUDENT");
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}