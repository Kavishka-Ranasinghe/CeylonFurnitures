package ceylonfurnitures.model;

import java.awt.Color;

public class Room {
    private int width; // in mm
    private int depth; // in mm
    private int height; // in mm
    private Color wallColor;
    private Color floorColor;

    public Room(int width, int depth, int height, Color wallColor, Color floorColor) {
        this.width = width;
        this.depth = depth;
        this.height = height;
        this.wallColor = wallColor;
        this.floorColor = floorColor;
    }

    // Getters
    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public int getHeight() {
        return height;
    }

    public Color getWallColor() {
        return wallColor;
    }

    public Color getFloorColor() {
        return floorColor;
    }

    // Setters
    public void setWidth(int width) {
        this.width = width;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWallColor(Color wallColor) {
        this.wallColor = wallColor;
    }

    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }
}