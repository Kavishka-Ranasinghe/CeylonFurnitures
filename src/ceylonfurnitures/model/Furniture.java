package ceylonfurnitures.model;

import java.awt.Color;

public class Furniture {
    private String type;
    private String displayName;
    private int x; // Position in 2D view (pixels)
    private int y; // Position in 2D view (pixels)
    private double scale; // Scaling factor (e.g., 1.0 = normal size)
    private Color color;
    private float shading; // 0.0 (no shading) to 1.0 (full shading)

    public Furniture(String type, String displayName) {
        this.type = type;
        this.displayName = displayName;
        this.x = 0;
        this.y = 0;
        this.scale = 1.0;
        this.color = Color.LIGHT_GRAY; // Default color
        this.shading = 0.0f; // Default no shading
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

    public double getScale() {
        return scale;
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

    public void setScale(double scale) {
        this.scale = scale;
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