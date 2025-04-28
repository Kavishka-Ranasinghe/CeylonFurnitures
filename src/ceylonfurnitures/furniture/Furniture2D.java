package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.Graphics2D;

public interface Furniture2D {
    void draw(Graphics2D g2d, Furniture furniture);
    int getDefaultWidth(); // Default width in pixels
    int getDefaultHeight(); // Default height in pixels
}