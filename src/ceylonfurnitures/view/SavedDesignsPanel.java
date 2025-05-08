package ceylonfurnitures.view;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.model.Design;
import ceylonfurnitures.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class SavedDesignsPanel extends JPanel {
    private User user;
    private DatabaseManager dbManager;
    private FurnitureFactory furnitureFactory;
    private Runnable onBackToDashboard;
    private Consumer<User> onRefresh; // Callback to refresh the panel

    public SavedDesignsPanel(User user, List<Design> designs, DatabaseManager dbManager, FurnitureFactory furnitureFactory, Runnable onBackToDashboard, Consumer<User> onRefresh) {
        this.user = user;
        this.dbManager = dbManager;
        this.furnitureFactory = furnitureFactory;
        this.onBackToDashboard = onBackToDashboard;
        this.onRefresh = onRefresh;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Top Bar with Back Button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setPreferredSize(new Dimension(0, 50)); // Reduced height for a sleeker look
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Adjusted padding
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Modern font and size
        backButton.setBackground(new Color(0, 123, 255)); // Blue background
        backButton.setForeground(Color.WHITE); // White text
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> onBackToDashboard.run());
        topBar.add(backButton);
        add(topBar, BorderLayout.NORTH);

        // Center Panel for Design Tiles
        JPanel designsPanel = new JPanel();
        designsPanel.setLayout(new BoxLayout(designsPanel, BoxLayout.Y_AXIS)); // Stack rows vertically
        designsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Adjusted padding
        designsPanel.setBackground(new Color(245, 245, 245)); // Match background

        // Create rows of tiles (3 tiles per row)
        JPanel currentRow = null;
        int columnsPerRow = 3;
        int count = 0;

        for (Design design : designs) {
            // Start a new row if needed
            if (count % columnsPerRow == 0) {
                currentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Horizontal row with gaps
                currentRow.setBackground(new Color(245, 245, 245)); // Match background
                designsPanel.add(currentRow);
                designsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Vertical gap between rows
            }

            JPanel tile = new JPanel(new BorderLayout());
            tile.setPreferredSize(new Dimension(150, 100)); // Smaller tile size
            tile.setMinimumSize(new Dimension(150, 100)); // Ensure minimum size
            tile.setMaximumSize(new Dimension(150, 100)); // Ensure maximum size
            tile.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)), // Lighter border
                    BorderFactory.createEmptyBorder(5, 5, 5, 5) // Reduced padding
            ));
            tile.setBackground(Color.WHITE); // Tile background
            tile.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor for interactivity

            // Add hover effect
            tile.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    tile.setBackground(new Color(240, 240, 240)); // Lighter on hover
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    tile.setBackground(Color.WHITE); // Reset on exit
                }
            });

            JLabel nameLabel = new JLabel(design.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Reduced font size
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Reduced padding for label
            tile.add(nameLabel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3)); // Reduced gaps
            JButton deleteButton = new JButton("Delete");
            JButton viewButton = new JButton("View");
            deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Smaller font
            viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Smaller font
            deleteButton.setBackground(new Color(220, 53, 69)); // Red for delete
            deleteButton.setForeground(Color.WHITE);
            viewButton.setBackground(new Color(40, 167, 69)); // Green for view
            viewButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            viewButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            viewButton.setBorderPainted(false);
            deleteButton.setOpaque(true);
            viewButton.setOpaque(true);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttonPanel.add(deleteButton);
            buttonPanel.add(viewButton);
            tile.add(buttonPanel, BorderLayout.SOUTH);

            // Add delete functionality
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete the design '" + design.getName() + "'?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        dbManager.deleteDesign(design.getId());
                        JOptionPane.showMessageDialog(this, "Design deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the panel by reloading the designs
                        onRefresh.accept(user);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting design: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add hover effects for buttons
            deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    deleteButton.setBackground(new Color(200, 40, 60)); // Darker red on hover
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    deleteButton.setBackground(new Color(220, 53, 69)); // Reset
                }
            });
            viewButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    viewButton.setBackground(new Color(30, 150, 60)); // Darker green on hover
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    viewButton.setBackground(new Color(40, 167, 69)); // Reset
                }
            });

            currentRow.add(tile);
            count++;
        }

        add(new JScrollPane(designsPanel), BorderLayout.CENTER);
    }
}