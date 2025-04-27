package ceylonfurnitures.view;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.model.Furniture;
import ceylonfurnitures.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardPanel extends JPanel {
    private User user;
    private DatabaseManager dbManager;
    private FurnitureFactory furnitureFactory;
    private Runnable onLogout;
    private Runnable onShowSavedDesigns;
    private Runnable onStartDesign;

    public DashboardPanel(User user, DatabaseManager dbManager, FurnitureFactory furnitureFactory,
                          Runnable onLogout, Runnable onShowSavedDesigns, Runnable onStartDesign) {
        this.user = user;
        this.dbManager = dbManager;
        this.furnitureFactory = furnitureFactory;
        this.onLogout = onLogout;
        this.onShowSavedDesigns = onShowSavedDesigns;
        this.onStartDesign = onStartDesign;

        setLayout(new BorderLayout());

        // App Bar (North)
        JPanel appBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        JButton profileButton = new JButton("Profile");
        JButton savedDesignsButton = new JButton("Saved Designs");
        JButton logoutButton = new JButton("Logout");

        appBar.add(welcomeLabel);
        appBar.add(profileButton);
        appBar.add(savedDesignsButton);
        appBar.add(logoutButton);
        add(appBar, BorderLayout.NORTH);

        // Furniture Grid (Center)
        JPanel furniturePanel = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 columns, dynamic rows
        furniturePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        List<Furniture> furnitureTypes = furnitureFactory.getFurnitureTypes();
        for (Furniture furniture : furnitureTypes) {
            JButton furnitureButton = new JButton(furniture.getDisplayName());
            furnitureButton.addActionListener(e -> {
                System.out.println("Selected furniture: " + furniture.getType());
                onStartDesign.run();
            });
            furniturePanel.add(furnitureButton);
        }
        add(new JScrollPane(furniturePanel), BorderLayout.CENTER);

        // Action Listeners
        profileButton.addActionListener(e -> showProfileDialog());
        savedDesignsButton.addActionListener(e -> onShowSavedDesigns.run());
        logoutButton.addActionListener(e -> onLogout.run());
    }

    private void showProfileDialog() {
        JDialog profileDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Profile", true);
        profileDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        profileDialog.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(user.getUsername(), 15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        profileDialog.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        profileDialog.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(user.getPassword(), 15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        profileDialog.add(passwordField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        profileDialog.add(emailLabel, gbc);

        JTextField emailField = new JTextField(user.getEmail(), 15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        profileDialog.add(emailField, gbc);

        // Buttons
        JButton updateButton = new JButton("Update");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        profileDialog.add(updateButton, gbc);

        JButton deleteButton = new JButton("Delete Account");
        gbc.gridx = 0;
        gbc.gridy = 4;
        profileDialog.add(deleteButton, gbc);

        // Action Listeners
        updateButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            String newEmail = emailField.getText().trim();

            if (newUsername.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(profileDialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                dbManager.updateUser(user.getId(), newUsername, newPassword, newEmail);
                user.setUsername(newUsername);
                user.setPassword(newPassword);
                user.setEmail(newEmail);
                JOptionPane.showMessageDialog(profileDialog, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                profileDialog.dispose();
                // Refresh the welcome label
                repaint();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(profileDialog, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(profileDialog,
                    "Are you sure you want to delete your account? This cannot be undone.",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    dbManager.deleteUser(user.getId());
                    JOptionPane.showMessageDialog(profileDialog, "Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    profileDialog.dispose();
                    onLogout.run(); // Log out after deletion
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(profileDialog, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        profileDialog.pack();
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setVisible(true);
    }
}