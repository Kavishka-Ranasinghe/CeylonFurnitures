package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TVstand2D implements Furniture2D {
    private static final int BASE_WIDTH = 1000;  // mm
    private static final int BASE_HEIGHT = 300;  // mm

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

        // ==== Outer Body ====
        g2d.setColor(shadedColor);
        g2d.fillRoundRect(x, y, width, height, 18, 18);

        // ==== Inner Panel ====
        g2d.setColor(shadedColor.brighter());
        g2d.fillRoundRect(x + 4, y + 4, width - 8, height - 8, 14, 14);

        // ==== Shelf Dividers ====
        g2d.setColor(new Color(0, 0, 0, 60));
        int compartmentCount = 3;
        for (int i = 1; i < compartmentCount; i++) {
            int dividerX = x + (i * width / compartmentCount);
            g2d.drawLine(dividerX, y + 10, dividerX, y + height - 10);
        }

        // ==== Outline ====
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, width, height, 18, 18);

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
