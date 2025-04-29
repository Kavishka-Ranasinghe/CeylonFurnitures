package ceylonfurnitures.model;

import java.awt.Color;

public class Furniture {
    private String type;
    private String displayName;
    private int x; // Position in 2D view (pixels)
    private int y; // Position in 2D view (pixels)
    private int width; // Width in pixels (default set by Furniture2D implementation)
    private int height; // Height in pixels (default set by Furniture2D implementation)
    private float depth; // Depth in OpenGL units (meters, default set by Furniture3D)
    private float rotation; // Rotation in degrees (around Y-axis in 3D)
    private Color color;
    private float shading; // 0.0 (no shading) to 1.0 (full shading)

    public Furniture(String type, String displayName) {
        this.type = type;
        this.displayName = displayName;
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.depth = 0;
        this.rotation = 0.0f;
        this.color = Color.LIGHT_GRAY;
        this.shading = 0.0f;
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public float getRotation() {
        return rotation;
    }

    public Color getColor() {
        return color;
    }

    public float getShading() {
        return shading;
    }

    // Setters
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShading(float shading) {
        if (shading < 0.0f) shading = 0.0f;
        if (shading > 1.0f) shading = 1.0f;
        this.shading = shading;
    }
}