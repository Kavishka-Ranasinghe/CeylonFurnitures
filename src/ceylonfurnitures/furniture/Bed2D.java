package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Bed2D implements Furniture2D {
    private static final int BASE_WIDTH = 120; // 1200 mm (scaled down for display)
    private static final int BASE_HEIGHT = 200; // 2000 mm (scaled down for display)

    @Override
    public void draw(Graphics2D g2d, Furniture furniture) {
        int x = furniture.getX();
        int y = furniture.getY();
        int width = furniture.getWidth();
        int height = furniture.getHeight();
        float rotation = furniture.getRotation();
        Color color = furniture.getColor();
        float shading = furniture.getShading();

        // Adjust color for shading
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float brightness = hsb[2] * (1.0f - shading);
        Color shadedColor = Color.getHSBColor(hsb[0], hsb[1], brightness);

        // Rotate around the center of the bed
        g2d.rotate(Math.toRadians(rotation), x + width / 2.0, y + height / 2.0);

        // Draw the bed (rectangle for mattress)
        g2d.setColor(shadedColor);
        g2d.fillRect(x, y, width, height);

        // Draw outline
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, width, height);

        // Draw headboard
        g2d.setColor(shadedColor.darker());
        g2d.fillRect(x, y - 10, width, 10);

        // Draw pillows (scaled based on width)
        g2d.setColor(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(x + 10, y + 10, width / 4, 20));
        g2d.fill(new Rectangle2D.Double(x + width / 2, y + 10, width / 4, 20));
        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Double(x + 10, y + 10, width / 4, 20));
        g2d.draw(new Rectangle2D.Double(x + width / 2, y + 10, width / 4, 20));

        // Reset rotation
        g2d.rotate(-Math.toRadians(rotation), x + width / 2.0, y + height / 2.0);
    }

    @Override
    public int getDefaultWidth() {
        return BASE_WIDTH;
    }

    @Override
    public int getDefaultHeight() {
        return BASE_HEIGHT;
    }
}