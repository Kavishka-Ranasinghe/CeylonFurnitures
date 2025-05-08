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

        // Set up the main panel with a gradient background
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background as a fallback

        // Create a centered card panel for the signup form
        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded corners
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardPanel.setPreferredSize(new Dimension(400, 500));
        cardPanel.setMaximumSize(new Dimension(400, 500));

        // Add shadow effect using a border
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding for a spacious feel

        // Title
        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41)); // Dark gray text
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(passwordField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(emailLabel, gbc);

        emailField = new JTextField(15);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(emailField, gbc);

        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupButton.setBackground(new Color(0, 123, 255)); // Blue button
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setOpaque(true);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add hover effect
        signupButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signupButton.setBackground(new Color(0, 105, 217)); // Darker blue on hover
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signupButton.setBackground(new Color(0, 123, 255)); // Reset
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(signupButton, gbc);

        // Switch to Login Button
        switchToLoginButton = new JButton("Already have an account? Login");
        switchToLoginButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        switchToLoginButton.setForeground(new Color(0, 123, 255));
        switchToLoginButton.setBorderPainted(false);
        switchToLoginButton.setContentAreaFilled(false);
        switchToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add hover effect
        switchToLoginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                switchToLoginButton.setForeground(new Color(0, 105, 217)); // Darker blue on hover
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                switchToLoginButton.setForeground(new Color(0, 123, 255)); // Reset
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(switchToLoginButton, gbc);

        // Add the card panel to the center of the main panel
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(cardPanel, new GridBagConstraints());
        add(wrapperPanel, BorderLayout.CENTER);

        // Action Listeners (unchanged)
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Create a gradient background
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(180, 220, 255), // Light blue
                0, getHeight(), new Color(245, 245, 245) // Light gray
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}