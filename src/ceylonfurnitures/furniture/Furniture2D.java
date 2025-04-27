package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.Graphics2D;

public interface Furniture2D {
    void draw(Graphics2D g2d, Furniture furniture);
    int getWidth(Furniture furniture); // Width in pixels (considering scale)
    int getHeight(Furniture furniture); // Height in pixels (considering scale)
}