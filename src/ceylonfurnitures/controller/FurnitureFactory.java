package ceylonfurnitures.controller;

import ceylonfurnitures.furniture.Bed2D;
import ceylonfurnitures.furniture.Furniture2D;
import ceylonfurnitures.model.Furniture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Furniture newFurniture = new Furniture(type, furniture.getDisplayName());
                Furniture2D renderer = getFurniture2D(type);
                newFurniture.setWidth(renderer.getDefaultWidth());
                newFurniture.setHeight(renderer.getDefaultHeight());
                return newFurniture;
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
            int width = furniture.getWidth();
            int height = furniture.getHeight();
            Color color = furniture.getColor();
            float shading = furniture.getShading();

            // Adjust color for shading
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            float brightness = hsb[2] * (1.0f - shading);
            Color shadedColor = Color.getHSBColor(hsb[0], hsb[1], brightness);

            // Draw a simple rectangle
            g2d.setColor(shadedColor);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
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
}