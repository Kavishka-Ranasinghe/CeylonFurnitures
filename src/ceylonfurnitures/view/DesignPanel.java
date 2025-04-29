package ceylonfurnitures.view;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.furniture.Furniture2D;
import ceylonfurnitures.furniture.Furniture3D;
import ceylonfurnitures.model.Furniture;
import ceylonfurnitures.model.Room;
import ceylonfurnitures.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class DesignPanel extends JPanel {
    private User user;
    private FurnitureFactory furnitureFactory;
    private Runnable onBackToDashboard;
    private Room room;
    private List<Furniture> placedFurniture;
    private Furniture selectedFurniture;
    private DrawingPanel drawingPanel2D;
    private GLCanvas drawingPanel3D;
    private JPanel glPanelWrapper;
    private boolean is3DView;
    private float cameraAngleX = 45.0f;
    private float cameraAngleY = 30.0f;
    private float cameraDistance = 5.0f;
    private int lastX;
    private int lastY;

    public DesignPanel(User user, FurnitureFactory furnitureFactory, Runnable onBackToDashboard) {
        this.user = user;
        this.furnitureFactory = furnitureFactory;
        this.onBackToDashboard = onBackToDashboard;
        this.room = new Room(5000, 3000, 2700, Color.LIGHT_GRAY, Color.DARK_GRAY);
        this.placedFurniture = new ArrayList<>();
        this.selectedFurniture = null;
        this.is3DView = false;

        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Dashboard");
        JButton toggleViewButton = new JButton("Toggle 2D/3D View");
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
                    newFurniture.setX(50);
                    newFurniture.setY(50);
                    placedFurniture.add(newFurniture);
                    if (is3DView) {
                        drawingPanel3D.repaint();
                    } else {
                        drawingPanel2D.repaint();
                    }
                }
            });
            leftPanel.add(furnitureButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        add(new JScrollPane(leftPanel), BorderLayout.WEST);

        // Center Panel (Drawing Area)
        drawingPanel2D = new DrawingPanel();
        drawingPanel2D.setPreferredSize(new Dimension(600, 400));
        drawingPanel2D.setBorder(BorderFactory.createTitledBorder("Design Area (2D)"));

        // Set up 3D panel with JOGL
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDepthBits(0); // Use default depth
        capabilities.setDoubleBuffered(true);
        capabilities.setHardwareAccelerated(true);
        capabilities.setStencilBits(0);
        capabilities.setAccumRedBits(0);
        capabilities.setAccumGreenBits(0);
        capabilities.setAccumBlueBits(0);
        capabilities.setAccumAlphaBits(0);
        drawingPanel3D = new GLCanvas(capabilities);
        drawingPanel3D.setPreferredSize(new Dimension(600, 400));

        // Wrap GLCanvas in a JPanel to set the border
        glPanelWrapper = new JPanel(new BorderLayout());
        glPanelWrapper.setBorder(BorderFactory.createTitledBorder("Design Area (3D)"));

        drawingPanel3D.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                gl.glEnable(GL2.GL_DEPTH_TEST);
                System.out.println("GLEventListener.init called");
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
                System.out.println("GLEventListener.dispose called");
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                System.out.println("GLEventListener.display called"); // Add this
                GL2 gl = drawable.getGL().getGL2();
                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

                gl.glLoadIdentity();
                gl.glTranslatef(0.0f, 0.0f, -cameraDistance);
                gl.glRotatef(cameraAngleY, 1, 0, 0);
                gl.glRotatef(cameraAngleX, 0, 1, 0);

                // Draw room
                float roomWidth = room.getWidth() / 1000.0f; // Convert to meters
                float roomDepth = room.getDepth() / 1000.0f;
                float roomHeight = room.getHeight() / 1000.0f;

                // Floor
                float[] floorColor = room.getFloorColor().getRGBColorComponents(null);
                gl.glColor3f(floorColor[0], floorColor[1], floorColor[2]);
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(-roomWidth / 2, 0, -roomDepth / 2);
                gl.glVertex3f(roomWidth / 2, 0, -roomDepth / 2);
                gl.glVertex3f(roomWidth / 2, 0, roomDepth / 2);
                gl.glVertex3f(-roomWidth / 2, 0, roomDepth / 2);
                gl.glEnd();

                // Walls (simplified for now)
                float[] wallColor = room.getWallColor().getRGBColorComponents(null);
                gl.glColor3f(wallColor[0], wallColor[1], wallColor[2]);
                // Back wall
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(-roomWidth / 2, 0, -roomDepth / 2);
                gl.glVertex3f(roomWidth / 2, 0, -roomDepth / 2);
                gl.glVertex3f(roomWidth / 2, roomHeight, -roomDepth / 2);
                gl.glVertex3f(-roomWidth / 2, roomHeight, -roomDepth / 2);
                gl.glEnd();

                // Draw furniture
                for (Furniture furniture : placedFurniture) {
                    Furniture3D renderer = furnitureFactory.getFurniture3D(furniture.getType());
                    renderer.draw(gl, furniture);
                }
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glViewport(0, 0, width, height);
                gl.glMatrixMode(GL2.GL_PROJECTION);
                gl.glLoadIdentity();
                float aspect = (float) width / height;
                gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 100.0);
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                System.out.println("GLEventListener.reshape called");
            }
        });

        drawingPanel3D.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedFurniture = null;
            }
        });

        drawingPanel3D.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                cameraAngleX += dx * 0.5f;
                cameraAngleY += dy * 0.5f;
                if (cameraAngleY > 90) cameraAngleY = 90;
                if (cameraAngleY < -90) cameraAngleY = -90;
                lastX = e.getX();
                lastY = e.getY();
                drawingPanel3D.repaint();
            }
        });

        drawingPanel3D.addMouseWheelListener(e -> {
            cameraDistance += e.getWheelRotation() * 0.5f;
            if (cameraDistance < 1.0f) cameraDistance = 1.0f;
            if (cameraDistance > 10.0f) cameraDistance = 10.0f;
            drawingPanel3D.repaint();
        });

        FPSAnimator animator = new FPSAnimator(drawingPanel3D, 60, true);
        animator.start();
        System.out.println("FPSAnimator started: " + animator.isStarted());

        // Add 2D panel by default
        add(drawingPanel2D, BorderLayout.CENTER);

        // Add a ComponentListener to add the GLCanvas when the panel is visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!glPanelWrapper.isAncestorOf(drawingPanel3D)) {
                    SwingUtilities.invokeLater(() -> {
                        glPanelWrapper.add(drawingPanel3D, BorderLayout.CENTER);
                        glPanelWrapper.revalidate();
                        animator.start();
                        System.out.println("FPSAnimator started in ComponentListener: " + animator.isStarted());
                    });
                }
            }
        });

        // Right Panel (Customization)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Customization"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton roomButton = new JButton("Edit Room Properties");
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(roomButton, gbc);

        JLabel colorLabel = new JLabel("Furniture Color:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(colorLabel, gbc);

        JButton colorButton = new JButton("Choose Color");
        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(colorButton, gbc);

        JLabel widthLabel = new JLabel("Width (px):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(widthLabel, gbc);

        JTextField widthField = new JTextField("0", 5);
        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(widthField, gbc);

        JLabel heightLabel = new JLabel("Height (px):");
        gbc.gridx = 0;
        gbc.gridy = 5;
        rightPanel.add(heightLabel, gbc);

        JTextField heightField = new JTextField("0", 5);
        gbc.gridx = 0;
        gbc.gridy = 6;
        rightPanel.add(heightField, gbc);

        JLabel rotationLabel = new JLabel("Rotation (degrees):");
        gbc.gridx = 0;
        gbc.gridy = 7;
        rightPanel.add(rotationLabel, gbc);

        JTextField rotationField = new JTextField("0", 5);
        gbc.gridx = 0;
        gbc.gridy = 8;
        rightPanel.add(rotationField, gbc);

        JLabel shadingLabel = new JLabel("Shading:");
        gbc.gridx = 0;
        gbc.gridy = 9;
        rightPanel.add(shadingLabel, gbc);

        JSlider shadingSlider = new JSlider(0, 100, 0);
        shadingSlider.setMajorTickSpacing(25);
        shadingSlider.setPaintTicks(true);
        shadingSlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 10;
        rightPanel.add(shadingSlider, gbc);

        JButton applyButton = new JButton("Apply Changes");
        gbc.gridx = 0;
        gbc.gridy = 11;
        rightPanel.add(applyButton, gbc);

        add(rightPanel, BorderLayout.EAST);

        // Action Listeners
        backButton.addActionListener(e -> onBackToDashboard.run());
        toggleViewButton.addActionListener(e -> {
            is3DView = !is3DView;
            try {
                remove(is3DView ? drawingPanel2D : glPanelWrapper);
                add(is3DView ? glPanelWrapper : drawingPanel2D, BorderLayout.CENTER);
                revalidate();
                repaint();
            } catch (Exception ex) {
                is3DView = false;
                remove(glPanelWrapper);
                add(drawingPanel2D, BorderLayout.CENTER);
                revalidate();
                repaint();
                JOptionPane.showMessageDialog(DesignPanel.this,
                        "Failed to initialize 3D view. Reverting to 2D view.\nError: " + ex.getMessage(),
                        "3D View Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Save functionality will be implemented in Day 7!"));

        roomButton.addActionListener(e -> showRoomDialog());
        colorButton.addActionListener(e -> {
            if (selectedFurniture != null) {
                Color newColor = JColorChooser.showDialog(this, "Choose Furniture Color", selectedFurniture.getColor());
                if (newColor != null) {
                    selectedFurniture.setColor(newColor);
                    repaintDrawingArea();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a furniture item to customize.");
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (selectedFurniture != null) {
                    widthField.setText(String.valueOf(selectedFurniture.getWidth()));
                    heightField.setText(String.valueOf(selectedFurniture.getHeight()));
                    rotationField.setText(String.valueOf(selectedFurniture.getRotation()));
                } else {
                    widthField.setText("0");
                    heightField.setText("0");
                    rotationField.setText("0");
                }
            }
        });

        applyButton.addActionListener(e -> {
            if (selectedFurniture != null) {
                try {
                    int newWidth = Integer.parseInt(widthField.getText().trim());
                    int newHeight = Integer.parseInt(heightField.getText().trim());
                    float newRotation = Float.parseFloat(rotationField.getText().trim());
                    if (newWidth <= 0 || newHeight <= 0) {
                        JOptionPane.showMessageDialog(this, "Width and height must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    selectedFurniture.setWidth(newWidth);
                    selectedFurniture.setHeight(newHeight);
                    selectedFurniture.setRotation(newRotation);
                    repaintDrawingArea();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for width, height, and rotation.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a furniture item to customize.");
            }
        });

        shadingSlider.addChangeListener(e -> {
            if (selectedFurniture != null) {
                selectedFurniture.setShading(shadingSlider.getValue() / 100.0f);
                repaintDrawingArea();
            }
        });
    }

    private void showRoomDialog() {
        JDialog roomDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Room Properties", true);
        roomDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel widthLabel = new JLabel("Width (mm):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        roomDialog.add(widthLabel, gbc);

        JTextField widthField = new JTextField(String.valueOf(room.getWidth()), 10);
        gbc.gridx = 1;
        gbc.gridy = 0;
        roomDialog.add(widthField, gbc);

        JLabel depthLabel = new JLabel("Depth (mm):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        roomDialog.add(depthLabel, gbc);

        JTextField depthField = new JTextField(String.valueOf(room.getDepth()), 10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        roomDialog.add(depthField, gbc);

        JLabel heightLabel = new JLabel("Height (mm):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        roomDialog.add(heightLabel, gbc);

        JTextField heightField = new JTextField(String.valueOf(room.getHeight()), 10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        roomDialog.add(heightField, gbc);

        JLabel wallColorLabel = new JLabel("Wall Color:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        roomDialog.add(wallColorLabel, gbc);

        JButton wallColorButton = new JButton("Choose Wall Color");
        gbc.gridx = 1;
        gbc.gridy = 3;
        roomDialog.add(wallColorButton, gbc);

        JLabel floorColorLabel = new JLabel("Floor Color:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        roomDialog.add(floorColorLabel, gbc);

        JButton floorColorButton = new JButton("Choose Floor Color");
        gbc.gridx = 1;
        gbc.gridy = 4;
        roomDialog.add(floorColorButton, gbc);

        JButton applyButton = new JButton("Apply");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        roomDialog.add(applyButton, gbc);

        wallColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(roomDialog, "Choose Wall Color", room.getWallColor());
            if (newColor != null) {
                room.setWallColor(newColor);
                repaintDrawingArea();
            }
        });

        floorColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(roomDialog, "Choose Floor Color", room.getFloorColor());
            if (newColor != null) {
                room.setFloorColor(newColor);
                repaintDrawingArea();
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
                repaintDrawingArea();
                roomDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(roomDialog, "Please enter valid numbers for dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        roomDialog.pack();
        roomDialog.setLocationRelativeTo(this);
        roomDialog.setVisible(true);
    }

    private void repaintDrawingArea() {
        if (is3DView) {
            drawingPanel3D.repaint();
        } else {
            drawingPanel2D.repaint();
        }
    }

    private class DrawingPanel extends JPanel {
        private Point dragStart;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedFurniture = null;
                    for (Furniture furniture : placedFurniture) {
                        Rectangle2D bounds = getRotatedBounds(furniture);
                        if (bounds.contains(e.getPoint())) {
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

                        int roomWidth = room.getWidth() / 10;
                        int roomDepth = room.getDepth() / 10;

                        Rectangle2D newBounds = getRotatedBounds(newX, newY, selectedFurniture.getWidth(), selectedFurniture.getHeight(), selectedFurniture.getRotation());

                        // Check room bounds
                        if (newX < 0) newX = 0;
                        if (newY < 0) newY = 0;
                        if (newBounds.getMaxX() > roomWidth) newX = (int) (roomWidth - newBounds.getWidth());
                        if (newBounds.getMaxY() > roomDepth) newY = (int) (roomDepth - newBounds.getHeight());

                        // Check for overlap with other furniture
                        boolean overlap = false;
                        for (Furniture other : placedFurniture) {
                            if (other == selectedFurniture) continue;
                            Rectangle2D otherBounds = getRotatedBounds(other);
                            if (newBounds.intersects(otherBounds)) {
                                overlap = true;
                                break;
                            }
                        }

                        if (!overlap) {
                            selectedFurniture.setX(newX);
                            selectedFurniture.setY(newY);
                            dragStart = e.getPoint();
                        }
                        repaint();
                    }
                }
            });
        }

        private Rectangle2D getRotatedBounds(Furniture furniture) {
            return getRotatedBounds(furniture.getX(), furniture.getY(), furniture.getWidth(), furniture.getHeight(), furniture.getRotation());
        }

        private Rectangle2D getRotatedBounds(int x, int y, int width, int height, float rotation) {
            Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
            if (rotation == 0) return rect;

            double rad = Math.toRadians(rotation);
            double sin = Math.abs(Math.sin(rad));
            double cos = Math.abs(Math.cos(rad));
            double newWidth = width * cos + height * sin;
            double newHeight = width * sin + height * cos;
            double newX = x + (width - newWidth) / 2;
            double newY = y + (height - newHeight) / 2;
            return new Rectangle2D.Double(newX, newY, newWidth, newHeight);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int roomWidth = room.getWidth() / 10;
            int roomDepth = room.getDepth() / 10;

            g2d.setColor(room.getFloorColor());
            g2d.fillRect(0, 0, roomWidth, roomDepth);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, roomWidth, roomDepth);

            g2d.setColor(Color.BLACK);
            g2d.drawString("Width: " + room.getWidth() + " mm", 10, 20);
            g2d.drawString("Depth: " + room.getDepth() + " mm", 10, 40);

            g2d.setColor(Color.LIGHT_GRAY);
            for (int x = 0; x < roomWidth; x += 10) {
                g2d.drawLine(x, 0, x, roomDepth);
            }
            for (int y = 0; y < roomDepth; y += 10) {
                g2d.drawLine(0, y, roomWidth, y);
            }

            for (Furniture furniture : placedFurniture) {
                Furniture2D renderer = furnitureFactory.getFurniture2D(furniture.getType());
                renderer.draw(g2d, furniture);

                if (furniture == selectedFurniture) {
                    Rectangle2D bounds = getRotatedBounds(furniture);
                    g2d.setColor(Color.RED);
                    g2d.drawRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
                }
            }
        }
    }
}