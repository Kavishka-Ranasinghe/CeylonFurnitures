package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Cupboard2D implements Furniture2D {
    private static final int BASE_WIDTH = 800;   // mm
    private static final int BASE_HEIGHT = 400;  // mm

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

        // Base shape
        g2d.setColor(shadedColor);
        g2d.fillRoundRect(x, y, width, height, 20, 20);

        // Panel detail
        g2d.setColor(shadedColor.brighter());
        g2d.fillRoundRect(x + 6, y + 6, width - 12, height - 12, 15, 15);

        // Door division line
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x + width / 2, y + 10, x + width / 2, y + height - 10);

        // Handles
        g2d.fillRect(x + width / 2 - 15, y + height / 2 - 5, 10, 10);
        g2d.fillRect(x + width / 2 + 5, y + height / 2 - 5, 10, 10);

        // Outline
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
