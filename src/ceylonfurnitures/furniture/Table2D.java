package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Table2D implements Furniture2D {
    private static final int BASE_WIDTH = 1000;  // mm
    private static final int BASE_HEIGHT = 600;  // mm

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

        // ==== Table Top (Rounded) ====
        g2d.setColor(shadedColor);
        g2d.fillRoundRect(x, y, width, height, 20, 20);

        // ==== Surface (Inset Effect) ====
        g2d.setColor(shadedColor.brighter());
        g2d.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 15, 15);

        // ==== Decorative Grain Lines ====
        g2d.setColor(new Color(255, 255, 255, 40));
        for (int i = x + 15; i < x + width - 15; i += 30) {
            g2d.drawLine(i, y + 8, i, y + height - 8);
        }

        // ==== Outline ====
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, width, height, 20, 20);

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
