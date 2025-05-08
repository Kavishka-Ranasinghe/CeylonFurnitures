package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Lamp2D implements Furniture2D {
    private static final int BASE_WIDTH = 300;  // mm
    private static final int BASE_HEIGHT = 300; // mm

    @Override
    public void draw(Graphics2D g2d, Furniture furniture) {
        int x = furniture.getX();
        int y = furniture.getY();
        int width = furniture.getWidth();
        int height = furniture.getHeight();
        float rotation = furniture.getRotation();
        Color color = furniture.getColor();
        float shading = furniture.getShading();

        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float brightness = hsb[2] * (1.0f - shading);
        Color shadedColor = Color.getHSBColor(hsb[0], hsb[1], brightness);

        g2d.rotate(Math.toRadians(rotation), x + width / 2.0, y + height / 2.0);

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        int baseSize = Math.min(width, height);

        // ==== Lamp Base (square base rectangle) ====
        g2d.setColor(shadedColor.darker());
        g2d.fill(new Rectangle2D.Double(centerX - baseSize / 6.0, centerY + baseSize / 6.0, baseSize / 3.0, baseSize / 6.0));

        // ==== Pole (line from center to shade) ====
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(centerX - 2, centerY - baseSize / 6, 4, baseSize / 3);

        // ==== Square Lampshade (head) ====
        g2d.setColor(shadedColor);
        g2d.fill(new Rectangle2D.Double(centerX - baseSize / 4.0, centerY - baseSize / 4.0, baseSize / 2.0, baseSize / 2.0));

        // ==== Inner light bulb (simulated glow center) ====
        g2d.setColor(new Color(255, 255, 150, 150));
        g2d.fill(new Rectangle2D.Double(centerX - 6, centerY - 6, 12, 12));

        // ==== Outline ====
        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Double(centerX - baseSize / 4.0, centerY - baseSize / 4.0, baseSize / 2.0, baseSize / 2.0));

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
