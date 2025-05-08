package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Sofa2D implements Furniture2D {
    private static final int BASE_WIDTH = 1800;  // mm
    private static final int BASE_HEIGHT = 700;  // mm

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

        // ==== Sofa Base ====
        g2d.setColor(shadedColor);
        g2d.fillRoundRect(x, y, width, height, 25, 25);

        // ==== Inner Layer ====
        g2d.setColor(shadedColor.brighter());
        g2d.fillRoundRect(x + 6, y + 6, width - 12, height - 12, 20, 20);

        // ==== Seat Division Lines ====
        g2d.setColor(new Color(255, 255, 255, 60));
        int seatCount = 3;
        for (int i = 1; i < seatCount; i++) {
            int dividerX = x + (i * width / seatCount);
            g2d.drawLine(dividerX, y + 10, dividerX, y + height - 10);
        }

        // ==== Backrest ====
        g2d.setColor(shadedColor.darker());
        g2d.fillRect(x, y - 8, width, 8);

        // ==== Armrests ====
        g2d.setColor(shadedColor.darker().darker());
        g2d.fillRoundRect(x - 8, y + 10, 8, height - 20, 10, 10);  // Left arm
        g2d.fillRoundRect(x + width, y + 10, 8, height - 20, 10, 10);  // Right arm

        // ==== Outline ====
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, width, height, 25, 25);

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
