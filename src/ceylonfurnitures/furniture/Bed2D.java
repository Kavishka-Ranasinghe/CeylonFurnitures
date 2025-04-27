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
        double scale = furniture.getScale();
        Color color = furniture.getColor();
        float shading = furniture.getShading();

        int scaledWidth = (int) (BASE_WIDTH * scale);
        int scaledHeight = (int) (BASE_HEIGHT * scale);

        // Adjust color for shading
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float brightness = hsb[2] * (1.0f - shading);
        Color shadedColor = Color.getHSBColor(hsb[0], hsb[1], brightness);

        // Draw the bed (rectangle for mattress)
        g2d.setColor(shadedColor);
        g2d.fillRect(x, y, scaledWidth, scaledHeight);

        // Draw outline
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, scaledWidth, scaledHeight);

        // Draw headboard
        g2d.setColor(shadedColor.darker());
        g2d.fillRect(x, y - 10, scaledWidth, 10);

        // Draw pillows
        g2d.setColor(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(x + 10, y + 10, scaledWidth / 4, 20));
        g2d.fill(new Rectangle2D.Double(x + scaledWidth / 2, y + 10, scaledWidth / 4, 20));
        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Double(x + 10, y + 10, scaledWidth / 4, 20));
        g2d.draw(new Rectangle2D.Double(x + scaledWidth / 2, y + 10, scaledWidth / 4, 20));
    }

    @Override
    public int getWidth(Furniture furniture) {
        return (int) (BASE_WIDTH * furniture.getScale());
    }

    @Override
    public int getHeight(Furniture furniture) {
        return (int) (BASE_HEIGHT * furniture.getScale());
    }
}