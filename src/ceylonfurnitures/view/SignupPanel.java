package ceylonfurnitures.view;

import ceylonfurnitures.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SignupPanel extends JPanel {
    private DatabaseManager dbManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JButton signupButton;
    private JButton switchToLoginButton;
    private Runnable onSwitchToLogin;

    public SignupPanel(DatabaseManager dbManager, Runnable onSwitchToLogin) {
        this.dbManager = dbManager;
        this.onSwitchToLogin = onSwitchToLogin;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // Title
        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(emailLabel, gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(emailField, gbc);

        // Signup Button
        signupButton = new JButton("Sign Up");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(signupButton, gbc);

        // Switch to Login Button
        switchToLoginButton = new JButton("Already have an account? Login");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(switchToLoginButton, gbc);

        // Action Listeners
        signupButton.addActionListener(e -> handleSignup());
        switchToLoginButton.addActionListener(e -> onSwitchToLogin.run());
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int userId = dbManager.createUser(username, password, email);
            if (userId != -1) {
                JOptionPane.showMessageDialog(this, "Signup successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear fields
                usernameField.setText("");
                passwordField.setText("");
                emailField.setText("");
                // Switch to login panel
                onSwitchToLogin.run();
            } else {
                JOptionPane.showMessageDialog(this, "Signup failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}