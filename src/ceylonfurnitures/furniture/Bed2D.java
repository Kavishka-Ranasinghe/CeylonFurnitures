package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Bed2D implements Furniture2D {
    private static final int BASE_WIDTH = 1200; // 1200 mm (scaled down for display)
    private static final int BASE_HEIGHT = 1500; // 2000 mm (scaled down for display)

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

        // ==== Base Frame ====
        g2d.setColor(shadedColor);
        g2d.fillRect(x, y, width, height);

        // ==== Mattress Layer ====
        g2d.setColor(shadedColor.brighter());
        g2d.fillRect(x + 4, y + 4, width - 8, height - 8);

        // ==== Headboard ====
        g2d.setColor(shadedColor.darker().darker());
        g2d.fillRect(x, y - 8, width, 8); // Above the top edge

        // ==== Pillows ====
        g2d.setColor(Color.WHITE);
        int pillowHeight = 14;
        int pillowWidth = width / 4;
        g2d.fill(new Rectangle2D.Double(x + 10, y + 8, pillowWidth, pillowHeight));
        g2d.fill(new Rectangle2D.Double(x + width - 10 - pillowWidth, y + 8, pillowWidth, pillowHeight));

        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Double(x + 10, y + 8, pillowWidth, pillowHeight));
        g2d.draw(new Rectangle2D.Double(x + width - 10 - pillowWidth, y + 8, pillowWidth, pillowHeight));

        // ==== Outline ====
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, width, height);

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