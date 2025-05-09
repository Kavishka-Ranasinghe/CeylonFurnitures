package ceylonfurnitures.view;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.furniture.Furniture2D;
import ceylonfurnitures.furniture.Furniture3D;
import ceylonfurnitures.model.Design;
import ceylonfurnitures.model.Furniture;
import ceylonfurnitures.model.Room;
import ceylonfurnitures.model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.glu.GLU;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;

public class DesignPanel extends JPanel {
    private User user;
    private DatabaseManager dbManager;
    private FurnitureFactory furnitureFactory;
    private Runnable onBackToDashboard;
    private Room room;
    private List<Furniture> placedFurniture;
    private Furniture selectedFurniture;
    private DrawingPanel drawingPanel2D;
    private GLJPanel drawingPanel3D;
    private JPanel glPanelWrapper;
    private boolean is3DView;
    private float cameraAngleX = 0.0f;
    private float cameraAngleY = 30.0f;
    private float cameraDistance = 7.0f;
    private float cameraPosX = 0.0f;
    private float cameraPosZ = 0.0f;
    private int lastX;
    private int lastY;
    private FPSAnimator animator;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton colorButton;
    private JTextField widthField, heightField, rotationField;
    private JSlider shadingSlider;
    private JButton applyButton, deleteButton;
    private JButton toggleSidePanelsButton;
    private List<JButton> furnitureButtons = new ArrayList<>();
    private GLU glu = new GLU();
    private static final float PIXEL_TO_MM_SCALE = 10.0f;
    private static final float MM_TO_OPENGL_SCALE = 1000.0f;
    private JLabel titleLabel;
    private Design currentDesign;

    public DesignPanel(User user, DatabaseManager dbManager, FurnitureFactory furnitureFactory, Runnable onBackToDashboard) {
        this(user, dbManager, furnitureFactory, onBackToDashboard, null);
    }

    public DesignPanel(User user, DatabaseManager dbManager, FurnitureFactory furnitureFactory, Runnable onBackToDashboard, Design design) {
        this.user = user;
        this.dbManager = dbManager;
        this.furnitureFactory = furnitureFactory;
        this.onBackToDashboard = onBackToDashboard;
        this.room = new Room(5000, 3000, 2700, Color.LIGHT_GRAY, new Color(180, 140, 100));
        this.placedFurniture = new ArrayList<>();
        this.selectedFurniture = null;
        this.is3DView = false;
        this.currentDesign = design;

        setLayout(new BorderLayout());

        // Title Label
        titleLabel = new JLabel(design != null ? design.getName() : "not saved");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Dashboard");
        JButton toggleViewButton = new JButton("Toggle 2D/3D View");
        JButton saveButton = new JButton("Save Design");
        toggleSidePanelsButton = new JButton("Hide Side Panels");
        toggleSidePanelsButton.addActionListener(e -> {
            boolean visible = leftPanel.isVisible();
            leftPanel.setVisible(!visible);
            rightPanel.setVisible(!visible);
            toggleSidePanelsButton.setText(visible ? "Show Side Panels" : "Hide Side Panels");
        });

        topBar.add(toggleSidePanelsButton);
        topBar.add(backButton);
        topBar.add(toggleViewButton);
        topBar.add(saveButton);
        add(topBar, BorderLayout.NORTH);

        // Left Panel (Furniture Library)
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Furniture Library"));
        JLabel leftDisabledLabel = new JLabel("Panel disabled");
        leftDisabledLabel.setForeground(Color.RED);
        leftDisabledLabel.setVisible(false);
        leftPanel.add(leftDisabledLabel);
        List<Furniture> furnitureTypes = furnitureFactory.getFurnitureTypes();
        for (Furniture furniture : furnitureTypes) {
            JButton furnitureButton = new JButton(furniture.getDisplayName());
            furnitureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            furnitureButton.addActionListener(e -> {
                if (is3DView) return;
                Furniture newFurniture = furnitureFactory.createFurniture(furniture.getType());
                if (newFurniture != null) {
                    newFurniture.setX((int) (50 * PIXEL_TO_MM_SCALE));
                    newFurniture.setY((int) (50 * PIXEL_TO_MM_SCALE));
                    placedFurniture.add(newFurniture);
                    if (is3DView) {
                        drawingPanel3D.repaint();
                    } else {
                        drawingPanel2D.repaint();
                    }
                }
            });
            furnitureButtons.add(furnitureButton);
            leftPanel.add(furnitureButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        add(new JScrollPane(leftPanel), BorderLayout.WEST);

        // Center Panel (Drawing Area)
        drawingPanel2D = new DrawingPanel();
        drawingPanel2D.setPreferredSize(new Dimension(600, 400));

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Design Area (2D) Top View of the Room");
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 16));
        titledBorder.setTitleColor(Color.BLACK);
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, titledBorder);
        drawingPanel2D.setBorder(compoundBorder);

        // Set up 3D panel with JOGL
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDepthBits(16);
        capabilities.setAlphaBits(8);
        capabilities.setDoubleBuffered(true);
        capabilities.setHardwareAccelerated(true);
        capabilities.setStencilBits(0);
        capabilities.setAccumRedBits(0);
        capabilities.setAccumGreenBits(0);
        capabilities.setAccumBlueBits(0);
        capabilities.setAccumAlphaBits(0);
        drawingPanel3D = new GLJPanel(capabilities);
        drawingPanel3D.setPreferredSize(new Dimension(600, 400));

        glPanelWrapper = new JPanel(new BorderLayout());
        glPanelWrapper.setBorder(BorderFactory.createTitledBorder("Design Area (3D)"));
        glPanelWrapper.add(drawingPanel3D, BorderLayout.CENTER);

        drawingPanel3D.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glDepthFunc(GL2.GL_LESS);
                gl.glClearDepth(1.0f);

                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT0);
                float[] lightPosition = {0.0f, 5.0f, 5.0f, 1.0f};
                float[] lightDiffuse = {0.5f, 0.5f, 0.5f, 1.0f};
                float[] lightAmbient = {0.4f, 0.4f, 0.4f, 1.0f};
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
                gl.glEnable(GL2.GL_COLOR_MATERIAL);
                gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE);
                gl.glEnable(GL2.GL_CULL_FACE);
                gl.glCullFace(GL2.GL_BACK);
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

                gl.glLoadIdentity();
                gl.glTranslatef(-cameraPosX, 0.0f, -cameraDistance + cameraPosZ);
                gl.glRotatef(cameraAngleY, 1, 0, 0);
                gl.glRotatef(cameraAngleX, 0, 1, 0);

                float roomWidth = room.getWidth() / MM_TO_OPENGL_SCALE;
                float roomDepth = room.getDepth() / MM_TO_OPENGL_SCALE;
                float roomHeight = room.getHeight() / MM_TO_OPENGL_SCALE;

                gl.glTranslatef(-roomWidth / 2, 0, -roomDepth / 2);

                float[] floorColor = room.getFloorColor().getRGBColorComponents(null);
                float[] floorAmbientDiffuse = {floorColor[0], floorColor[1], floorColor[2], 1.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, floorAmbientDiffuse, 0);
                gl.glColor3f(floorColor[0], floorColor[1], floorColor[2]);
                gl.glDisable(GL2.GL_CULL_FACE);

                gl.glBegin(GL2.GL_QUADS);
                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glVertex3f(0, 0, roomDepth);
                gl.glVertex3f(roomWidth, 0, roomDepth);
                gl.glVertex3f(roomWidth, 0, 0);
                gl.glVertex3f(0, 0, 0);
                gl.glEnd();

                gl.glEnable(GL2.GL_CULL_FACE);

                float[] wallColor = room.getWallColor().getRGBColorComponents(null);
                float[] wallAmbientDiffuse = {wallColor[0], wallColor[1], wallColor[2], 1.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, wallAmbientDiffuse, 0);
                gl.glColor3f(wallColor[0], wallColor[1], wallColor[2]);

                gl.glBegin(GL2.GL_QUADS);
                gl.glNormal3f(0.0f, 0.0f, 1.0f);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(roomWidth, 0, 0);
                gl.glVertex3f(roomWidth, roomHeight, 0);
                gl.glVertex3f(0, roomHeight, 0);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glNormal3f(0.0f, 0.0f, -1.0f);
                gl.glVertex3f(0, 0, roomDepth);
                gl.glVertex3f(0, roomHeight, roomDepth);
                gl.glVertex3f(roomWidth, roomHeight, roomDepth);
                gl.glVertex3f(roomWidth, 0, roomDepth);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glNormal3f(1.0f, 0.0f, 0.0f);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(0, roomHeight, 0);
                gl.glVertex3f(0, roomHeight, roomDepth);
                gl.glVertex3f(0, 0, roomDepth);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glNormal3f(-1.0f, 0.0f, 0.0f);
                gl.glVertex3f(roomWidth, 0, 0);
                gl.glVertex3f(roomWidth, 0, roomDepth);
                gl.glVertex3f(roomWidth, roomHeight, roomDepth);
                gl.glVertex3f(roomWidth, roomHeight, 0);
                gl.glEnd();

                for (Furniture furniture : placedFurniture) {
                    Furniture3D renderer = furnitureFactory.getFurniture3D(furniture.getType());
                    float[] furnitureColor = furniture.getColor().getRGBColorComponents(null);
                    float[] furnitureAmbientDiffuse = {furnitureColor[0], furnitureColor[1], furnitureColor[2], 1.0f};
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, furnitureAmbientDiffuse, 0);

                    gl.glPushMatrix();
                    float x = furniture.getX() / MM_TO_OPENGL_SCALE;
                    float z = furniture.getY() / MM_TO_OPENGL_SCALE;
                    float width = furniture.getWidth() / MM_TO_OPENGL_SCALE;
                    float depth = furniture.getHeight() / MM_TO_OPENGL_SCALE;
                    float rotation = furniture.getRotation();

                    gl.glTranslatef(x, 0, z);
                    gl.glRotatef(-rotation, 0, 1, 0);
                    Furniture scaledFurniture = new Furniture(furniture.getType(), furniture.getDisplayName());
                    scaledFurniture.setX(0);
                    scaledFurniture.setY(0);
                    scaledFurniture.setWidth((int) (furniture.getWidth() / MM_TO_OPENGL_SCALE * MM_TO_OPENGL_SCALE));
                    scaledFurniture.setHeight((int) (furniture.getHeight() / MM_TO_OPENGL_SCALE * MM_TO_OPENGL_SCALE));
                    scaledFurniture.setRotation(furniture.getRotation());
                    scaledFurniture.setColor(furniture.getColor());
                    scaledFurniture.setShading(furniture.getShading());
                    renderer.draw(gl, scaledFurniture);
                    gl.glPopMatrix();

                    if (furniture == selectedFurniture) {
                        gl.glPushMatrix();
                        float height = 0.5f;
                        gl.glTranslatef(x, 0, z);
                        gl.glRotatef(-rotation, 0, 1, 0);
                        gl.glScalef(width, height, depth);

                        gl.glDisable(GL2.GL_LIGHTING);
                        gl.glColor3f(1.0f, 0.0f, 0.0f);
                        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
                        drawCuboid(gl, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
                        gl.glEnable(GL2.GL_LIGHTING);

                        gl.glPopMatrix();
                    }
                }
            }

            private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(xMin, yMin, zMax);
                gl.glVertex3f(xMax, yMin, zMax);
                gl.glVertex3f(xMax, yMax, zMax);
                gl.glVertex3f(xMin, yMax, zMax);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(xMax, yMin, zMin);
                gl.glVertex3f(xMin, yMin, zMin);
                gl.glVertex3f(xMin, yMax, zMin);
                gl.glVertex3f(xMax, yMax, zMin);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(xMin, yMax, zMin);
                gl.glVertex3f(xMax, yMax, zMin);
                gl.glVertex3f(xMax, yMax, zMax);
                gl.glVertex3f(xMin, yMax, zMax);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(xMin, yMin, zMax);
                gl.glVertex3f(xMax, yMin, zMax);
                gl.glVertex3f(xMax, yMin, zMin);
                gl.glVertex3f(xMin, yMin, zMin);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(xMin, yMin, zMin);
                gl.glVertex3f(xMin, yMin, zMax);
                gl.glVertex3f(xMin, yMax, zMax);
                gl.glVertex3f(xMin, yMax, zMin);
                gl.glEnd();

                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(xMax, yMin, zMax);
                gl.glVertex3f(xMax, yMin, zMin);
                gl.glVertex3f(xMax, yMax, zMin);
                gl.glVertex3f(xMax, yMax, zMax);
                gl.glEnd();
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glViewport(0, 0, width, height);
                gl.glMatrixMode(GL2.GL_PROJECTION);
                gl.glLoadIdentity();

                float aspect = (float) width / height;
                glu.gluPerspective(45.0f, aspect, 0.1f, 50.0f);

                gl.glMatrixMode(GL2.GL_MODELVIEW);
            }
        });

        drawingPanel3D.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();

                if (!e.isControlDown()) {
                    selectedFurniture = null;
                    Point3D worldPoint = unproject(lastX, lastY);
                    if (worldPoint != null) {
                        float roomWidth = room.getWidth() / MM_TO_OPENGL_SCALE;
                        float roomDepth = room.getDepth() / MM_TO_OPENGL_SCALE;

                        // Adjust world coordinates to account for the room's translation
                        float adjustedX = (float) worldPoint.x + (roomWidth / 2);
                        float adjustedZ = (float) worldPoint.z + (roomDepth / 2);

                        System.out.println("Click world coordinates: x=" + adjustedX + ", z=" + adjustedZ);

                        for (Furniture furniture : placedFurniture) {
                            Rectangle2D bounds = getFurnitureBounds3D(furniture);
                            System.out.println("Furniture " + furniture.getDisplayName() + " bounds: x=" + bounds.getX() + ", y=" + bounds.getY() +
                                    ", width=" + bounds.getWidth() + ", height=" + bounds.getHeight());

                            if (bounds.contains(adjustedX, adjustedZ)) {
                                selectedFurniture = furniture;
                                System.out.println("Selected furniture: " + furniture.getDisplayName());
                                if (selectedFurniture != null) {
                                    widthField.setText(String.valueOf(selectedFurniture.getWidth()));
                                    heightField.setText(String.valueOf(selectedFurniture.getHeight()));
                                    rotationField.setText(String.valueOf(selectedFurniture.getRotation()));
                                }
                                break;
                            }
                        }
                    } else {
                        System.out.println("Unproject failed to return valid coordinates.");
                    }
                    drawingPanel3D.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedFurniture = null;
            }
        });

        drawingPanel3D.addMouseMotionListener(new MouseMotionAdapter() {
            private Point3D initialWorldPoint;

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                if (e.isControlDown()) {
                    cameraAngleX += dx * 0.5f;
                    cameraAngleY += dy * 0.5f;
                    if (cameraAngleY > 90) cameraAngleY = 90;
                    if (cameraAngleY < -90) cameraAngleY = -90;
                } else if (selectedFurniture != null) {
                    Point3D newWorldPoint = unproject(e.getX(), e.getY());
                    if (newWorldPoint != null && initialWorldPoint != null) {
                        float roomWidth = room.getWidth() / MM_TO_OPENGL_SCALE;
                        float roomDepth = room.getDepth() / MM_TO_OPENGL_SCALE;

                        float adjustedNewX = (float) newWorldPoint.x + (roomWidth / 2);
                        float adjustedNewZ = (float) newWorldPoint.z + (roomDepth / 2);
                        float adjustedInitialX = (float) initialWorldPoint.x + (roomWidth / 2);
                        float adjustedInitialZ = (float) initialWorldPoint.z + (roomDepth / 2);

                        float deltaX = adjustedNewX - adjustedInitialX;
                        float deltaZ = adjustedNewZ - adjustedInitialZ;

                        float newXPixels = selectedFurniture.getX() + (int) (deltaX * MM_TO_OPENGL_SCALE);
                        float newYPixels = selectedFurniture.getY() + (int) (deltaZ * MM_TO_OPENGL_SCALE);

                        int roomWidthPixels = room.getWidth();
                        int roomDepthPixels = room.getDepth();

                        Rectangle2D newBounds = getRotatedBounds((int) newXPixels, (int) newYPixels,
                                selectedFurniture.getWidth(), selectedFurniture.getHeight(), selectedFurniture.getRotation());

                        if (newXPixels < 0) newXPixels = 0;
                        if (newYPixels < 0) newYPixels = 0;
                        if (newBounds.getMaxX() > roomWidthPixels)
                            newXPixels = (int) (roomWidthPixels - newBounds.getWidth());
                        if (newBounds.getMaxY() > roomDepthPixels)
                            newYPixels = (int) (roomDepthPixels - newBounds.getHeight());

                        boolean overlap = false;
                        for (Furniture other : placedFurniture) {
                            if (other == selectedFurniture) continue;
                            Rectangle2D otherBounds = getRotatedBounds(other.getX(), other.getY(),
                                    other.getWidth(), other.getHeight(), other.getRotation());
                            if (newBounds.intersects(otherBounds)) {
                                overlap = true;
                                break;
                            }
                        }

                        System.out.println("New world coordinates: x=" + newXPixels + ", z=" + newYPixels);

                        if (!overlap) {
                            selectedFurniture.setX((int) newXPixels);
                            selectedFurniture.setY((int) newYPixels);
                        }
                    } else {
                        System.out.println("Unproject failed during drag.");
                    }
                } else {
                    float roomWidth = room.getWidth() / MM_TO_OPENGL_SCALE;
                    float roomDepth = room.getDepth() / MM_TO_OPENGL_SCALE;

                    cameraPosX -= dx * 0.01f;
                    cameraPosZ -= dy * 0.01f;

                    float margin = 0.5f;
                    if (cameraPosX < -roomWidth / 2 + margin) cameraPosX = -roomWidth / 2 + margin;
                    if (cameraPosX > roomWidth / 2 - margin) cameraPosX = roomWidth / 2 - margin;
                    if (cameraPosZ < -roomDepth / 2 + margin) cameraPosZ = -roomDepth / 2 + margin;
                    if (cameraPosZ > roomDepth / 2 - margin) cameraPosZ = roomDepth / 2 - margin;
                }

                lastX = e.getX();
                lastY = e.getY();
                if (selectedFurniture != null) {
                    initialWorldPoint = unproject(lastX, lastY); // Update initial point during drag
                }
                drawingPanel3D.repaint();
            }

            public void mousePressed(MouseEvent e) {
                super.mouseDragged(e);
                if (selectedFurniture != null) {
                    initialWorldPoint = unproject(e.getX(), e.getY()); // Set initial point on press
                }
            }
        });

        drawingPanel3D.addMouseWheelListener(e -> {
            cameraDistance += e.getWheelRotation() * 0.5f;
            if (cameraDistance < 1.0f) cameraDistance = 1.0f;
            if (cameraDistance > 10.0f) cameraDistance = 10.0f;
            drawingPanel3D.repaint();
        });

        animator = new FPSAnimator(drawingPanel3D, 60, true);

        add(drawingPanel2D, BorderLayout.CENTER);

        // Right Panel (Customization)
        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Customization"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel rightDisabledLabel = new JLabel("Panel disabled");
        rightDisabledLabel.setForeground(Color.RED);
        rightDisabledLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightDisabledLabel.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 13;
        rightPanel.add(rightDisabledLabel, gbc);

        JButton roomButton = new JButton("Edit Room Properties");
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(roomButton, gbc);

        JLabel colorLabel = new JLabel("Furniture Color:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(colorLabel, gbc);

        colorButton = new JButton("Choose Color");
        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(colorButton, gbc);

        JLabel widthLabel = new JLabel("Width (mm):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(widthLabel, gbc);

        widthField = new JTextField("0", 5);
        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(widthField, gbc);

        JLabel heightLabel = new JLabel("Height (mm):");
        gbc.gridx = 0;
        gbc.gridy = 5;
        rightPanel.add(heightLabel, gbc);

        heightField = new JTextField("0", 5);
        gbc.gridx = 0;
        gbc.gridy = 6;
        rightPanel.add(heightField, gbc);

        JLabel rotationLabel = new JLabel("Rotation (degrees):");
        gbc.gridx = 0;
        gbc.gridy = 7;
        rightPanel.add(rotationLabel, gbc);

        rotationField = new JTextField("0", 5);
        gbc.gridx = 0;
        gbc.gridy = 8;
        rightPanel.add(rotationField, gbc);

        JLabel shadingLabel = new JLabel("Shading:");
        gbc.gridx = 0;
        gbc.gridy = 9;
        rightPanel.add(shadingLabel, gbc);

        shadingSlider = new JSlider(0, 100, 0);
        shadingSlider.setMajorTickSpacing(25);
        shadingSlider.setPaintTicks(true);
        shadingSlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 10;
        rightPanel.add(shadingSlider, gbc);

        applyButton = new JButton("Apply Changes");
        gbc.gridx = 0;
        gbc.gridy = 11;
        rightPanel.add(applyButton, gbc);

        deleteButton = new JButton("Delete Selected Item");
        gbc.gridx = 0;
        gbc.gridy = 12;
        rightPanel.add(deleteButton, gbc);

        add(rightPanel, BorderLayout.EAST);

        // Load design if provided
        if (design != null) {
            loadDesign(design);
        }

        // Action Listeners
        backButton.addActionListener(e -> onBackToDashboard.run());
        toggleViewButton.addActionListener(e -> {
            is3DView = !is3DView;

            boolean enableFurnitureControls = !is3DView;
            colorButton.setEnabled(enableFurnitureControls);
            widthField.setEnabled(enableFurnitureControls);
            heightField.setEnabled(enableFurnitureControls);
            rotationField.setEnabled(enableFurnitureControls);
            shadingSlider.setEnabled(enableFurnitureControls);
            applyButton.setEnabled(enableFurnitureControls);
            deleteButton.setEnabled(enableFurnitureControls);
            leftDisabledLabel.setVisible(is3DView);
            rightDisabledLabel.setVisible(is3DView);
            for (JButton btn : furnitureButtons) {
                btn.setEnabled(!is3DView);
            }

            try {
                remove(is3DView ? drawingPanel2D : glPanelWrapper);
                add(is3DView ? glPanelWrapper : drawingPanel2D, BorderLayout.CENTER);
                revalidate();
                repaint();
                if (is3DView) {
                    startAnimatorWithRetry(animator, drawingPanel3D, 5);
                    drawingPanel3D.repaint();
                } else {
                    if (animator.isStarted()) {
                        animator.stop();
                    }
                }
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

        saveButton.addActionListener(e -> saveDesign());

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

        deleteButton.addActionListener(e -> {
            if (selectedFurniture != null) {
                placedFurniture.remove(selectedFurniture);
                selectedFurniture = null;
                widthField.setText("0");
                heightField.setText("0");
                rotationField.setText("0");
                shadingSlider.setValue(0);
                repaintDrawingArea();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a furniture item to delete.");
            }
        });

        shadingSlider.addChangeListener(e -> {
            if (selectedFurniture != null) {
                selectedFurniture.setShading(shadingSlider.getValue() / 100.0f);
                repaintDrawingArea();
            }
        });
    }

    private void loadDesign(Design design) {
        try {
            // Parse room dimensions (width, depth, height)
            String[] dimensions = design.getRoomDimensions().split(",");
            if (dimensions.length == 3) {
                room = new Room(
                        Integer.parseInt(dimensions[0]),
                        Integer.parseInt(dimensions[1]),
                        Integer.parseInt(dimensions[2]),
                        Color.LIGHT_GRAY,
                        new Color(180, 140, 100)
                );
            }

            // Parse room colors (wall: R,G,B,floor: R,G,B)
            String[] colors = design.getRoomColors().split(",");
            if (colors.length == 6) {
                Color wallColor = new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
                Color floorColor = new Color(Integer.parseInt(colors[3]), Integer.parseInt(colors[4]), Integer.parseInt(colors[5]));
                room.setWallColor(wallColor);
                room.setFloorColor(floorColor);
            }

            // Parse furniture data
            placedFurniture.clear();
            String[] furnitureItems = design.getFurniture().split("\\|");
            for (String item : furnitureItems) {
                if (!item.isEmpty()) {
                    String[] parts = item.split(",");
                    if (parts.length == 10) {
                        Furniture furniture = furnitureFactory.createFurniture(parts[0]);
                        if (furniture != null) {
                            furniture.setX(Integer.parseInt(parts[1]));
                            furniture.setY(Integer.parseInt(parts[2]));
                            furniture.setWidth(Integer.parseInt(parts[3]));
                            furniture.setHeight(Integer.parseInt(parts[4]));
                            furniture.setRotation(Float.parseFloat(parts[5]));
                            furniture.setColor(new Color(Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), Integer.parseInt(parts[8])));
                            furniture.setShading(Float.parseFloat(parts[9]));
                            placedFurniture.add(furniture);
                        }
                    }
                }
            }

            repaintDrawingArea();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Error loading design data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDesign() {
        String designName = JOptionPane.showInputDialog(this, "Enter a name for your design:", currentDesign != null ? currentDesign.getName() : "not saved", JOptionPane.PLAIN_MESSAGE);
        if (designName == null || designName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Design name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Check if the name already exists for this user
            List<Design> existingDesigns = dbManager.readDesigns(user.getId());
            for (Design existingDesign : existingDesigns) {
                if (existingDesign.getName().equals(designName.trim()) && (currentDesign == null || existingDesign.getId() != currentDesign.getId())) {
                    JOptionPane.showMessageDialog(this, "Choose another name, there is a project with the same name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Serialize room dimensions
            String roomDimensions = room.getWidth() + "," + room.getDepth() + "," + room.getHeight();

            // Serialize room colors (wall and floor)
            Color wallColor = room.getWallColor();
            Color floorColor = room.getFloorColor();
            String roomColors = wallColor.getRed() + "," + wallColor.getGreen() + "," + wallColor.getBlue() + "," +
                    floorColor.getRed() + "," + floorColor.getGreen() + "," + floorColor.getBlue();

            // Serialize furniture data
            StringBuilder furnitureData = new StringBuilder();
            for (int i = 0; i < placedFurniture.size(); i++) {
                Furniture furniture = placedFurniture.get(i);
                Color color = furniture.getColor();
                furnitureData.append(furniture.getType()).append(",")
                        .append(furniture.getX()).append(",")
                        .append(furniture.getY()).append(",")
                        .append(furniture.getWidth()).append(",")
                        .append(furniture.getHeight()).append(",")
                        .append(furniture.getRotation()).append(",")
                        .append(color.getRed()).append(",")
                        .append(color.getGreen()).append(",")
                        .append(color.getBlue()).append(",")
                        .append(furniture.getShading());
                if (i < placedFurniture.size() - 1) {
                    furnitureData.append("|");
                }
            }

            // Save or update design
            if (currentDesign == null) {
                int designId = dbManager.createDesign(user.getId(), designName.trim(), roomDimensions, roomColors, furnitureData.toString());
                if (designId != -1) {
                    currentDesign = new Design(designId, user.getId(), designName.trim(), roomDimensions, roomColors, furnitureData.toString());
                    titleLabel.setText(designName.trim());
                    JOptionPane.showMessageDialog(this, "Design saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save design.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                dbManager.updateDesign(currentDesign.getId(), designName.trim(), roomDimensions, roomColors, furnitureData.toString());
                currentDesign.setName(designName.trim());
                currentDesign.setRoomDimensions(roomDimensions);
                currentDesign.setRoomColors(roomColors);
                currentDesign.setFurniture(furnitureData.toString());
                titleLabel.setText(designName.trim());
                JOptionPane.showMessageDialog(this, "Design updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving design: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class Point3D {
        double x, y, z;

        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private Point3D unproject(int mouseX, int mouseY) {
        GL2 gl = drawingPanel3D.getGL().getGL2();
        int[] viewport = new int[4];
        double[] modelview = new double[16];
        double[] projection = new double[16];
        FloatBuffer depth = FloatBuffer.allocate(1);
        double[] nearPos = new double[3];
        double[] farPos = new double[3];

        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);

        int y = viewport[3] - mouseY;
        gl.glReadPixels(mouseX, y, 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, depth);

        depth.rewind();
        float depthValue = depth.get();
        System.out.println("Depth value: " + depthValue);

        // Handle invalid depth (e.g., 1.0f indicating no object or 0.0f)
        if (depthValue == 1.0f || depthValue <= 0.0f || depthValue > 1.0f) {
            // Fallback: Try to intersect with the floor plane (y=0)
            for (float z = 0.0f; z <= 1.0f; z += 0.1f) {
                glu.gluUnProject(mouseX, y, z, modelview, 0, projection, 0, viewport, 0, nearPos, 0);
                if (nearPos[1] >= 0 && nearPos[1] <= 0.1) { // Close to floor level
                    return new Point3D(nearPos[0], 0, nearPos[2]);
                }
            }
            System.out.println("Fallback unproject failed, using camera ray.");
        } else {
            glu.gluUnProject(mouseX, y, depthValue, modelview, 0, projection, 0, viewport, 0, nearPos, 0);
            glu.gluUnProject(mouseX, y, 1.0, modelview, 0, projection, 0, viewport, 0, farPos, 0);
        }

        System.out.println("Near pos: " + nearPos[0] + ", " + nearPos[1] + ", " + nearPos[2]);
        System.out.println("Far pos: " + farPos[0] + ", " + farPos[1] + ", " + farPos[2]);

        double dirX = farPos[0] - nearPos[0];
        double dirY = farPos[1] - nearPos[1];
        double dirZ = farPos[2] - nearPos[2];

        System.out.println("Direction vector: " + dirX + ", " + dirY + ", " + dirZ);

        if (dirY == 0) {
            System.out.println("Direction Y is zero, using camera fallback.");
            // Fallback: Use camera forward vector (approximate based on angles)
            double camDirX = -Math.sin(Math.toRadians(cameraAngleX));
            double camDirZ = -Math.cos(Math.toRadians(cameraAngleX)) * Math.cos(Math.toRadians(cameraAngleY));
            double camDirY = -Math.sin(Math.toRadians(cameraAngleY));
            dirX = camDirX;
            dirY = camDirY;
            dirZ = camDirZ;
            if (camDirY == 0) {
                System.out.println("Camera direction Y is zero, forcing small Y.");
                dirY = 0.001; // Force a small Y direction to avoid division by zero
            }
        }

        if (dirY == 0) {
            System.out.println("Direction Y is still zero, unproject failed.");
            return null;
        }

        double t = -nearPos[1] / dirY;
        if (t < 0) {
            System.out.println("Negative t value, unproject failed.");
            return null;
        }

        double x = nearPos[0] + t * dirX;
        double z = nearPos[2] + t * dirZ;

        return new Point3D(x, 0, z);
    }

    private Rectangle2D getFurnitureBounds3D(Furniture furniture) {
        float x = furniture.getX() / MM_TO_OPENGL_SCALE;
        float z = furniture.getY() / MM_TO_OPENGL_SCALE;
        float width = furniture.getWidth() / MM_TO_OPENGL_SCALE;
        float depth = furniture.getHeight() / MM_TO_OPENGL_SCALE;
        float rotation = furniture.getRotation();

        // Adjust bounds to account for rotation
        double rad = Math.toRadians(rotation);
        double sin = Math.abs(Math.sin(rad));
        double cos = Math.abs(Math.cos(rad));
        double newWidth = width * cos + depth * sin;
        double newDepth = width * sin + depth * cos;
        double newX = x - (newWidth - width) / 2; // Adjust for rotation offset
        double newZ = z - (newDepth - depth) / 2;

        return new Rectangle2D.Double(newX, newZ, newWidth, newDepth);
    }

    private void startAnimatorWithRetry(FPSAnimator animator, GLJPanel canvas, int retries) {
        Window window = SwingUtilities.getWindowAncestor(canvas);
        if (window != null && window.isVisible() && canvas.isShowing() && !animator.isStarted()) {
            animator.start();
        } else if (retries > 0) {
            new Timer(100, e -> {
                startAnimatorWithRetry(animator, canvas, retries - 1);
                ((Timer) e.getSource()).stop();
            }).start();
        }
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

        JLabel wallColorLabel = new JLabel("Walls:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        roomDialog.add(wallColorLabel, gbc);

        Color[] wallColors = {
                new Color(200, 200, 200),
                Color.WHITE,
                new Color(240, 220, 220),
                new Color(220, 240, 220),
                new Color(240, 240, 220),
                new Color(220, 220, 240),
                new Color(240, 240, 200),
                new Color(230, 220, 240),
                new Color(245, 245, 240)
        };
        JPanel wallColorPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        for (Color color : wallColors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.addActionListener(e -> {
                room.setWallColor(color);
                repaintDrawingArea();
            });
            wallColorPanel.add(colorButton);
        }
        JButton customWallColorButton = new JButton("Custom");
        customWallColorButton.addActionListener(e -> {
            Color customColor = JColorChooser.showDialog(roomDialog, "Choose Custom Wall Color", room.getWallColor());
            if (customColor != null) {
                room.setWallColor(customColor);
                repaintDrawingArea();
            }
        });
        wallColorPanel.add(customWallColorButton);
        gbc.gridx = 1;
        gbc.gridy = 3;
        roomDialog.add(wallColorPanel, gbc);

        JLabel floorColorLabel = new JLabel("Floor:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        roomDialog.add(floorColorLabel, gbc);

        Color[] floorColors = {
                new Color(180, 140, 100),
                new Color(200, 160, 120),
                new Color(220, 180, 140),
                new Color(50, 50, 50),
                new Color(150, 150, 150),
                new Color(120, 80, 40),
                new Color(240, 240, 240),
                new Color(100, 100, 100),
                new Color(220, 200, 180)
        };
        JPanel floorColorPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        for (Color color : floorColors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.addActionListener(e -> {
                room.setFloorColor(color);
                repaintDrawingArea();
            });
            floorColorPanel.add(colorButton);
        }
        JButton customFloorColorButton = new JButton("Custom");
        customFloorColorButton.addActionListener(e -> {
            Color customColor = JColorChooser.showDialog(roomDialog, "Choose Custom Floor Color", room.getFloorColor());
            if (customColor != null) {
                room.setFloorColor(customColor);
                repaintDrawingArea();
            }
        });
        floorColorPanel.add(customFloorColorButton);
        gbc.gridx = 1;
        gbc.gridy = 4;
        roomDialog.add(floorColorPanel, gbc);

        JButton applyButton = new JButton("Apply");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        roomDialog.add(applyButton, gbc);

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

    private class DrawingPanel extends JPanel {
        private Point dragStart;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedFurniture = null;
                    int mouseXInMM = (int) (e.getX() * PIXEL_TO_MM_SCALE);
                    int mouseYInMM = (int) (e.getY() * PIXEL_TO_MM_SCALE);
                    for (Furniture furniture : placedFurniture) {
                        Rectangle2D bounds = getRotatedBounds(furniture);
                        if (bounds.contains(mouseXInMM, mouseYInMM)) {
                            selectedFurniture = furniture;
                            dragStart = e.getPoint();
                            if (selectedFurniture != null) {
                                DesignPanel.this.widthField.setText(String.valueOf(selectedFurniture.getWidth()));
                                DesignPanel.this.heightField.setText(String.valueOf(selectedFurniture.getHeight()));
                                DesignPanel.this.rotationField.setText(String.valueOf(selectedFurniture.getRotation()));
                            } else {
                                DesignPanel.this.widthField.setText("0");
                                DesignPanel.this.heightField.setText("0");
                                DesignPanel.this.rotationField.setText("0");
                            }
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
                        int dxPixels = e.getX() - dragStart.x;
                        int dyPixels = e.getY() - dragStart.y;
                        int dxMM = (int) (dxPixels * PIXEL_TO_MM_SCALE);
                        int dyMM = (int) (dyPixels * PIXEL_TO_MM_SCALE);
                        int newX = selectedFurniture.getX() + dxMM;
                        int newY = selectedFurniture.getY() + dyMM;

                        int roomWidth = room.getWidth();
                        int roomDepth = room.getDepth();

                        Rectangle2D newBounds = getRotatedBounds(newX, newY, selectedFurniture.getWidth(), selectedFurniture.getHeight(), selectedFurniture.getRotation());

                        if (newX < 0) newX = 0;
                        if (newY < 0) newY = 0;
                        if (newBounds.getMaxX() > roomWidth) newX = (int) (roomWidth - newBounds.getWidth());
                        if (newBounds.getMaxY() > roomDepth) newY = (int) (roomDepth - newBounds.getHeight());

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
                Furniture scaledFurniture = new Furniture(furniture.getType(), furniture.getDisplayName());
                scaledFurniture.setX((int) (furniture.getX() / PIXEL_TO_MM_SCALE));
                scaledFurniture.setY((int) (furniture.getY() / PIXEL_TO_MM_SCALE));
                scaledFurniture.setWidth((int) (furniture.getWidth() / PIXEL_TO_MM_SCALE));
                scaledFurniture.setHeight((int) (furniture.getHeight() / PIXEL_TO_MM_SCALE));
                scaledFurniture.setRotation(furniture.getRotation());
                scaledFurniture.setColor(furniture.getColor());
                scaledFurniture.setShading(furniture.getShading());

                Furniture2D renderer = furnitureFactory.getFurniture2D(furniture.getType());
                renderer.draw(g2d, scaledFurniture);

                if (furniture == selectedFurniture) {
                    Rectangle2D bounds = getRotatedBounds(furniture);
                    int scaledX = (int) (bounds.getX() / PIXEL_TO_MM_SCALE);
                    int scaledY = (int) (bounds.getY() / PIXEL_TO_MM_SCALE);
                    int scaledWidth = (int) (bounds.getWidth() / PIXEL_TO_MM_SCALE);
                    int scaledHeight = (int) (bounds.getHeight() / PIXEL_TO_MM_SCALE);
                    g2d.setColor(Color.RED);
                    g2d.drawRect(scaledX, scaledY, scaledWidth, scaledHeight);
                }
            }
        }
    }
}