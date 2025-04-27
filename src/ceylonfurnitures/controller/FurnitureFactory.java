package ceylonfurnitures.controller;

import ceylonfurnitures.furniture.Bed2D;
import ceylonfurnitures.furniture.Furniture2D;
import ceylonfurnitures.model.Furniture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;
import java.awt.Graphics2D;

public class FurnitureFactory {
    private List<Furniture> furnitureTypes;
    private Map<String, Furniture2D> furniture2DMap;

    public FurnitureFactory() {
        // Initialize furniture types
        furnitureTypes = new ArrayList<>();
        furnitureTypes.add(new Furniture("bed", "Bed"));
        furnitureTypes.add(new Furniture("table", "Table"));
        furnitureTypes.add(new Furniture("chair", "Chair"));
        furnitureTypes.add(new Furniture("sofa", "Sofa"));

        // Map furniture types to their 2D rendering classes
        furniture2DMap = new HashMap<>();
        furniture2DMap.put("bed", new Bed2D());
        // Add placeholder for other types (we'll implement them later)
        furniture2DMap.put("table", new DefaultFurniture2D());
        furniture2DMap.put("chair", new DefaultFurniture2D());
        furniture2DMap.put("sofa", new DefaultFurniture2D());
    }

    public List<Furniture> getFurnitureTypes() {
        return new ArrayList<>(furnitureTypes);
    }

    public Furniture createFurniture(String type) {
        for (Furniture furniture : furnitureTypes) {
            if (furniture.getType().equalsIgnoreCase(type)) {
                return new Furniture(type, furniture.getDisplayName());
            }
        }
        return null; // Type not found
    }

    public Furniture2D getFurniture2D(String type) {
        return furniture2DMap.getOrDefault(type.toLowerCase(), new DefaultFurniture2D());
    }

    // Default 2D renderer for furniture types we haven't implemented yet
    private static class DefaultFurniture2D implements Furniture2D {
        private static final int BASE_WIDTH = 50;
        private static final int BASE_HEIGHT = 50;

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

            // Draw a simple rectangle
            g2d.setColor(shadedColor);
            g2d.fillRect(x, y, scaledWidth, scaledHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, scaledWidth, scaledHeight);
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
}