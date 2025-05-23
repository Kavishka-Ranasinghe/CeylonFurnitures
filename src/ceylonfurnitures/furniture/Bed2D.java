package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Bed2D implements Furniture2D {
    private static final int BASE_WIDTH = 1200;
    private static final int BASE_HEIGHT = 1500;

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

        // ==== Bed Base (Rounded Corners) ====
        g2d.setColor(shadedColor);
        g2d.fillRoundRect(x, y, width, height, 20, 20);

        // ==== Mattress (with quilting effect) ====
        g2d.setColor(shadedColor.brighter());
        g2d.fillRoundRect(x + 6, y + 6, width - 12, height - 12, 15, 15);

        g2d.setColor(new Color(255, 255, 255, 50)); // Light stitching lines
        for (int i = x + 10; i < x + width - 10; i += 20) {
            g2d.drawLine(i, y + 10, i, y + height - 10);
        }
        for (int j = y + 10; j < y + height - 10; j += 20) {
            g2d.drawLine(x + 10, j, x + width - 10, j);
        }

        // ==== Headboard ====
        g2d.setColor(shadedColor.darker().darker());
        g2d.fillRect(x, y - 10, width, 10);

        // ==== Pillows (Rounded) ====
        g2d.setColor(Color.WHITE);
        int pillowHeight = 18;
        int pillowWidth = width / 4;
        g2d.fill(new RoundRectangle2D.Double(x + 10, y + 10, pillowWidth, pillowHeight, 10, 10));
        g2d.fill(new RoundRectangle2D.Double(x + width - 10 - pillowWidth, y + 10, pillowWidth, pillowHeight, 10, 10));

        g2d.setColor(Color.BLACK);
        g2d.draw(new RoundRectangle2D.Double(x + 10, y + 10, pillowWidth, pillowHeight, 10, 10));
        g2d.draw(new RoundRectangle2D.Double(x + width - 10 - pillowWidth, y + 10, pillowWidth, pillowHeight, 10, 10));

        // ==== Outer Border ====
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
