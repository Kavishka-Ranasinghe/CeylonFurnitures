package ceylonfurnitures.view;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.furniture.Furniture2D;
import ceylonfurnitures.model.Furniture;
import ceylonfurnitures.model.Room;
import ceylonfurnitures.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DesignPanel extends JPanel {
    private User user;
    private FurnitureFactory furnitureFactory;
    private Runnable onBackToDashboard;
    private Room room;
    private List<Furniture> placedFurniture;
    private Furniture selectedFurniture;
    private DrawingPanel drawingPanel;

    public DesignPanel(User user, FurnitureFactory furnitureFactory, Runnable onBackToDashboard) {
        this.user = user;
        this.furnitureFactory = furnitureFactory;
        this.onBackToDashboard = onBackToDashboard;
        this.room = new Room(5000, 3000, 2700, Color.LIGHT_GRAY, Color.DARK_GRAY); // Default room (5000x3000 mm)
        this.placedFurniture = new ArrayList<>();
        this.selectedFurniture = null;

        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Dashboard");
        JButton toggleViewButton = new JButton("Toggle 2D/3D (2D Only for Now)");
        JButton saveButton = new JButton("Save Design");
        topBar.add(backButton);
        topBar.add(toggleViewButton);
        topBar.add(saveButton);
        add(topBar, BorderLayout.NORTH);

        // Left Panel (Furniture Library)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Furniture Library"));
        List<Furniture> furnitureTypes = furnitureFactory.getFurnitureTypes();
        for (Furniture furniture : furnitureTypes) {
            JButton furnitureButton = new JButton(furniture.getDisplayName());
            furnitureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            furnitureButton.addActionListener(e -> {
                Furniture newFurniture = furnitureFactory.createFurniture(furniture.getType());
                if (newFurniture != null) {
                    newFurniture.setX(50); // Place near top-left of room
                    newFurniture.setY(50);
                    placedFurniture.add(newFurniture);
                    drawingPanel.repaint();
                }
            });
            leftPanel.add(furnitureButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        }
        add(new JScrollPane(leftPanel), BorderLayout.WEST);

        // Center Panel (Drawing Area)
        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(600, 400));
        drawingPanel.setBorder(BorderFactory.createTitledBorder("Design Area"));
        add(drawingPanel, BorderLayout.CENTER);

        // Right Panel (Customization)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Customization"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room Customization
        JButton roomButton = new JButton("Edit Room Properties");
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(roomButton, gbc);

        // Furniture Customization (shown only if a furniture is selected)
        JLabel colorLabel = new JLabel("Furniture Color:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(colorLabel, gbc);

        JButton colorButton = new JButton("Choose Color");
        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(colorButton, gbc);

        JLabel scaleLabel = new JLabel("Scale:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(scaleLabel, gbc);

        JSlider scaleSlider = new JSlider(50, 200, 100); // 0.5x to 2x
        scaleSlider.setMajorTickSpacing(50);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(scaleSlider, gbc);

        JLabel shadingLabel = new JLabel("Shading:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        rightPanel.add(shadingLabel, gbc);

        JSlider shadingSlider = new JSlider(0, 100, 0); // 0.0 to 1.0
        shadingSlider.setMajorTickSpacing(25);
        shadingSlider.setPaintTicks(true);
        shadingSlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 6;
        rightPanel.add(shadingSlider, gbc);

        add(rightPanel, BorderLayout.EAST);

        // Action Listeners
        backButton.addActionListener(e -> onBackToDashboard.run());
        toggleViewButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "3D view will be implemented in Day 6!"));
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Save functionality will be implemented in Day 7!"));

        roomButton.addActionListener(e -> showRoomDialog());
        colorButton.addActionListener(e -> {
            if (selectedFurniture != null) {
                Color newColor = JColorChooser.showDialog(this, "Choose Furniture Color", selectedFurniture.getColor());
                if (newColor != null) {
                    selectedFurniture.setColor(newColor);
                    drawingPanel.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a furniture item to customize.");
            }
        });

        scaleSlider.addChangeListener(e -> {
            if (selectedFurniture != null) {
                selectedFurniture.setScale(scaleSlider.getValue() / 100.0);
                drawingPanel.repaint();
            }
        });

        shadingSlider.addChangeListener(e -> {
            if (selectedFurniture != null) {
                selectedFurniture.setShading(shadingSlider.getValue() / 100.0f);
                drawingPanel.repaint();
            }
        });
    }

    private void showRoomDialog() {
        JDialog roomDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Room Properties", true);
        roomDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Width
        JLabel widthLabel = new JLabel("Width (mm):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        roomDialog.add(widthLabel, gbc);

        JTextField widthField = new JTextField(String.valueOf(room.getWidth()), 10);
        gbc.gridx = 1;
        gbc.gridy = 0;
        roomDialog.add(widthField, gbc);

        // Depth
        JLabel depthLabel = new JLabel("Depth (mm):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        roomDialog.add(depthLabel, gbc);

        JTextField depthField = new JTextField(String.valueOf(room.getDepth()), 10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        roomDialog.add(depthField, gbc);

        // Height
        JLabel heightLabel = new JLabel("Height (mm):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        roomDialog.add(heightLabel, gbc);

        JTextField heightField = new JTextField(String.valueOf(room.getHeight()), 10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        roomDialog.add(heightField, gbc);

        // Wall Color
        JLabel wallColorLabel = new JLabel("Wall Color:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        roomDialog.add(wallColorLabel, gbc);

        JButton wallColorButton = new JButton("Choose Wall Color");
        gbc.gridx = 1;
        gbc.gridy = 3;
        roomDialog.add(wallColorButton, gbc);

        // Floor Color
        JLabel floorColorLabel = new JLabel("Floor Color:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        roomDialog.add(floorColorLabel, gbc);

        JButton floorColorButton = new JButton("Choose Floor Color");
        gbc.gridx = 1;
        gbc.gridy = 4;
        roomDialog.add(floorColorButton, gbc);

        // Apply Button
        JButton applyButton = new JButton("Apply");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        roomDialog.add(applyButton, gbc);

        // Action Listeners
        wallColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(roomDialog, "Choose Wall Color", room.getWallColor());
            if (newColor != null) {
                room.setWallColor(newColor);
                drawingPanel.repaint();
            }
        });

        floorColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(roomDialog, "Choose Floor Color", room.getFloorColor());
            if (newColor != null) {
                room.setFloorColor(newColor);
                drawingPanel.repaint();
            }
        });

        applyButton.addActionListener(e -> {
            try {
                int width = Integer.parseInt(widthField.getText().trim());
                int depth = Integer.parseInt(depthField.getText().trim());
                int height = Integer.parseInt(heightField.getText().trim());
                if (width <= 0 || depth <= 0 || height <= 0) {
                    JOptionPane.showMessageDialog(roomDialog, "Dimensions must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                room.setWidth(width);
                room.setDepth(depth);
                room.setHeight(height);
                drawingPanel.repaint();
                roomDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(roomDialog, "Please enter valid numbers for dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        roomDialog.pack();
        roomDialog.setLocationRelativeTo(this);
        roomDialog.setVisible(true);
    }

    private class DrawingPanel extends JPanel {
        private Point dragStart;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            // Mouse listener for selecting and dragging furniture
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedFurniture = null;
                    for (Furniture furniture : placedFurniture) {
                        Furniture2D renderer = furnitureFactory.getFurniture2D(furniture.getType());
                        int width = renderer.getWidth(furniture);
                        int height = renderer.getHeight(furniture);
                        if (e.getX() >= furniture.getX() && e.getX() <= furniture.getX() + width &&
                                e.getY() >= furniture.getY() && e.getY() <= furniture.getY() + height) {
                            selectedFurniture = furniture;
                            dragStart = e.getPoint();
                            break;
                        }
                    }
                    repaint();
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectedFurniture != null && dragStart != null) {
                        int dx = e.getX() - dragStart.x;
                        int dy = e.getY() - dragStart.y;
                        int newX = selectedFurniture.getX() + dx;
                        int newY = selectedFurniture.getY() + dy;

                        // Keep furniture within room bounds (scaled down for display)
                        Furniture2D renderer = furnitureFactory.getFurniture2D(selectedFurniture.getType());
                        int width = renderer.getWidth(selectedFurniture);
                        int height = renderer.getHeight(selectedFurniture);
                        int roomWidth = room.getWidth() / 10; // Scale down by 10
                        int roomDepth = room.getDepth() / 10;

                        if (newX < 0) newX = 0;
                        if (newY < 0) newY = 0;
                        if (newX + width > roomWidth) newX = roomWidth - width;
                        if (newY + height > roomDepth) newY = roomDepth - height;

                        selectedFurniture.setX(newX);
                        selectedFurniture.setY(newY);
                        dragStart = e.getPoint();
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Scale down room dimensions for display (divide by 10)
            int roomWidth = room.getWidth() / 10;
            int roomDepth = room.getDepth() / 10;

            // Draw room floor
            g2d.setColor(room.getFloorColor());
            g2d.fillRect(0, 0, roomWidth, roomDepth);

            // Draw room outline
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, roomWidth, roomDepth);

            // Draw dimensions
            g2d.setColor(Color.BLACK);
            g2d.drawString("Width: " + room.getWidth() + " mm", 10, 20);
            g2d.drawString("Depth: " + room.getDepth() + " mm", 10, 40);

            // Draw grid (10 cm = 100 mm, scaled down to 10 pixels)
            g2d.setColor(Color.LIGHT_GRAY);
            for (int x = 0; x < roomWidth; x += 10) {
                g2d.drawLine(x, 0, x, roomDepth);
            }
            for (int y = 0; y < roomDepth; y += 10) {
                g2d.drawLine(0, y, roomWidth, y);
            }

            // Draw placed furniture
            for (Furniture furniture : placedFurniture) {
                Furniture2D renderer = furnitureFactory.getFurniture2D(furniture.getType());
                renderer.draw(g2d, furniture);

                // Highlight selected furniture
                if (furniture == selectedFurniture) {
                    g2d.setColor(Color.RED);
                    int width = renderer.getWidth(furniture);
                    int height = renderer.getHeight(furniture);
                    g2d.drawRect(furniture.getX(), furniture.getY(), width, height);
                }
            }
        }
    }
}