package ceylonfurnitures.view;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.model.Furniture;
import ceylonfurnitures.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardPanel extends JPanel {
    private User user;
    private DatabaseManager dbManager;
    private FurnitureFactory furnitureFactory;
    private Runnable onLogout;
    private Runnable onShowSavedDesigns;
    private Runnable onStartDesign;

    // Slideshow variables
    private JLabel slideshowLabel;
    private List<ImageIcon> images;
    private int currentImageIndex = 0;
    private static final int SLIDESHOW_DELAY = 3000; // 3 seconds delay between slides

    public DashboardPanel(User user, DatabaseManager dbManager, FurnitureFactory furnitureFactory,
                          Runnable onLogout, Runnable onShowSavedDesigns, Runnable onStartDesign) {
        this.user = user;
        this.dbManager = dbManager;
        this.furnitureFactory = furnitureFactory;
        this.onLogout = onLogout;
        this.onShowSavedDesigns = onShowSavedDesigns;
        this.onStartDesign = onStartDesign;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // App Bar (North)
        JPanel appBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), getWidth(), 0, new Color(100, 149, 237));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        appBar.setLayout(new BorderLayout());
        appBar.setPreferredSize(new Dimension(0, 60));
        appBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        appBar.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = createModernButton("Logout", new Color(220, 53, 69), Color.WHITE);
        logoutButton.addActionListener(e -> onLogout.run());
        appBar.add(logoutButton, BorderLayout.EAST);

        add(appBar, BorderLayout.NORTH);

        // Slideshow Panel (Below App Bar)
        JPanel slideshowPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 235), 0, getHeight(), new Color(173, 216, 230));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        slideshowPanel.setPreferredSize(new Dimension(300, 200)); // Height for slideshow
        slideshowPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        slideshowLabel = new JLabel();
        slideshowLabel.setHorizontalAlignment(JLabel.CENTER);
        slideshowPanel.add(slideshowLabel, BorderLayout.CENTER);

        // Load images from the "App_ss" folder
        loadSlideshowImages();
        if (!images.isEmpty()) {
            slideshowLabel.setIcon(images.get(0)); // Set initial image
            startSlideshow();
        } else {
            slideshowLabel.setText("No images found in App_ss folder.");
        }

        add(slideshowPanel, BorderLayout.CENTER);

        // Action Grid (South)
        JPanel actionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(0, 123, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create Design Button
        JButton createDesignButton = createModernButton("Create Design", new Color(31, 117, 9), Color.WHITE);
        createDesignButton.addActionListener(e -> {
            System.out.println("Create Design button clicked");
            onStartDesign.run();
        });
        actionPanel.add(createDesignButton);

        // Saved Design Button
        JButton savedDesignButton = createModernButton("Saved Design", new Color(31, 117, 9), Color.WHITE);
        savedDesignButton.addActionListener(e -> onShowSavedDesigns.run());
        actionPanel.add(savedDesignButton);

        // Profile Button
        JButton profileButton = createModernButton("Profile", new Color(31, 117, 9), Color.WHITE);
        profileButton.addActionListener(e -> showProfileDialog());
        actionPanel.add(profileButton);

        // About Us Button
        JButton aboutUsButton = createModernButton("About Us", new Color(31, 117, 9), Color.WHITE);
        aboutUsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Ceylon Furnitures\n" +
                            "Version 1.0\n" +
                            "Developed by: Your Team Name\n" +
                            "A simple furniture design application to help you create and visualize room layouts.",
                    "About Us",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        actionPanel.add(aboutUsButton);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add shadow and rounded corners
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(button.getBackground());
                g2d.fillRoundRect(0, 0, button.getWidth(), getHeight(), 15, 15);
                super.paint(g, c);
            }
        });

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showProfileDialog() {
        JDialog profileDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Profile", true);
        profileDialog.setLayout(new BorderLayout());
        profileDialog.setBackground(new Color(245, 245, 245));

        // Main panel for form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(user.getUsername(), 15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(user.getPassword(), 15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(user.getEmail(), 15);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(emailField, gbc);

        profileDialog.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton updateButton = createModernButton("Update", new Color(40, 167, 69), Color.WHITE);
        updateButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(updateButton);

        JButton deleteButton = createModernButton("Delete Account", new Color(220, 53, 69), Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(deleteButton);

        profileDialog.add(buttonPanel, BorderLayout.SOUTH);

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

    private void loadSlideshowImages() {
        images = new ArrayList<>();
        // Updated path to the App_ss folder
        File folder = new File("E:\\CODE EDITORS\\InteliJ\\CeylonFurnitures\\resources\\App_ss");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
            if (files != null) {
                for (File file : files) {
                    images.add(new ImageIcon(file.getAbsolutePath()));
                }
            }
        }
        if (images.isEmpty()) {
            images.add(new ImageIcon("default_image.png")); // Fallback image if none found
        }
    }

    private void startSlideshow() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    currentImageIndex = (currentImageIndex + 1) % images.size();
                    ImageIcon icon = images.get(currentImageIndex);
                    // Scale image to fit the slideshow panel while maintaining aspect ratio
                    Image img = icon.getImage().getScaledInstance(slideshowLabel.getWidth(), slideshowLabel.getHeight(), Image.SCALE_SMOOTH);
                    slideshowLabel.setIcon(new ImageIcon(img));
                });
            }
        }, 0, SLIDESHOW_DELAY);
    }
}