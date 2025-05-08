package ceylonfurnitures.controller;

import ceylonfurnitures.furniture.*;
import ceylonfurnitures.model.Furniture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL2;

public class FurnitureFactory {
    private List<Furniture> furnitureTypes;
    private Map<String, Furniture2D> furniture2DMap;
    private Map<String, Furniture3D> furniture3DMap;

    public FurnitureFactory() {
        // Initialize furniture types
        furnitureTypes = new ArrayList<>();
        furnitureTypes.add(new Furniture("bed", "Bed"));
        furnitureTypes.add(new Furniture("table", "Table"));
        furnitureTypes.add(new Furniture("chair", "Chair"));
        furnitureTypes.add(new Furniture("sofa", "Sofa"));
        furnitureTypes.add(new Furniture("cupboard", "Cupboard"));

        // Map furniture types to their 2D rendering classes
        furniture2DMap = new HashMap<>();
        furniture2DMap.put("bed", new Bed2D());
        furniture2DMap.put("table", new Table2D());
        furniture2DMap.put("chair", new Chair2D());
        furniture2DMap.put("sofa", new DefaultFurniture2D());
        furniture2DMap.put("cupboard", new Cupboard2D());

        // Map furniture types to their 3D rendering classes
        furniture3DMap = new HashMap<>();
        furniture3DMap.put("bed", new Bed3D());
        furniture3DMap.put("table", new Table3D());
        furniture3DMap.put("chair", new Chair3D());
        furniture3DMap.put("sofa", new DefaultFurniture3D());
        furniture3DMap.put("cupboard", new Cupboard3D());
    }

    public List<Furniture> getFurnitureTypes() {
        return new ArrayList<>(furnitureTypes);
    }

    public Furniture createFurniture(String type) {
        for (Furniture furniture : furnitureTypes) {
            if (furniture.getType().equalsIgnoreCase(type)) {
                Furniture newFurniture = new Furniture(type, furniture.getDisplayName());
                Furniture2D renderer2D = getFurniture2D(type);
                Furniture3D renderer3D = getFurniture3D(type);
                newFurniture.setWidth(renderer2D.getDefaultWidth());
                newFurniture.setHeight(renderer2D.getDefaultHeight());
                newFurniture.setDepth(renderer3D.getDefaultDepth());
                return newFurniture;
            }
        }
        return null; // Type not found
    }

    public Furniture2D getFurniture2D(String type) {
        return furniture2DMap.getOrDefault(type.toLowerCase(), new DefaultFurniture2D());
    }

    public Furniture3D getFurniture3D(String type) {
        return furniture3DMap.getOrDefault(type.toLowerCase(), new DefaultFurniture3D());
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
            float rotation = furniture.getRotation();
            Color color = furniture.getColor();
            float shading = furniture.getShading();

            // Adjust color for shading
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            float brightness = hsb[2] * (1.0f - shading);
            Color shadedColor = Color.getHSBColor(hsb[0], hsb[1], brightness);

            // Rotate and draw
            g2d.rotate(Math.toRadians(rotation), x + width / 2.0, y + height / 2.0);
            g2d.setColor(shadedColor);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
            g2d.rotate(-Math.toRadians(rotation), x + width / 2.0, y + height / 2.0); // Reset rotation
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

    // Default 3D renderer for furniture types we haven't implemented yet
    private static class DefaultFurniture3D implements Furniture3D {
        private static final float BASE_WIDTH = 0.5f; // 0.5 meters
        private static final float BASE_HEIGHT = 0.5f; // 0.5 meters
        private static final float BASE_DEPTH = 0.5f; // 0.5 meters

        @Override
        public void draw(GL2 gl, Furniture furniture) {
            float x = furniture.getX() / 1000.0f;
            float y = furniture.getY() / 1000.0f;
            float width = furniture.getWidth() / 1000.0f;
            float height = BASE_HEIGHT;
            float depth = furniture.getDepth();
            float rotation = furniture.getRotation();
            Color color = furniture.getColor();
            float shading = furniture.getShading();

            float[] rgb = color.getRGBColorComponents(null);
            float r = rgb[0] * (1.0f - shading);
            float g = rgb[1] * (1.0f - shading);
            float b = rgb[2] * (1.0f - shading);

            gl.glPushMatrix();
            gl.glTranslatef(x + width / 2, height / 2, y + depth / 2);
            gl.glRotatef(rotation, 0, 1, 0);
            gl.glScalef(width, height, depth);

            gl.glColor3f(r, g, b);
            drawCuboid(gl, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f);

            gl.glPopMatrix();
        }

        private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3f(xMin, yMin, zMax);
            gl.glVertex3f(xMax, yMin, zMax);
            gl.glVertex3f(xMax, yMax, zMax);
            gl.glVertex3f(xMin, yMax, zMax);
            gl.glEnd();

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3f(xMin, yMin, zMin);
            gl.glVertex3f(xMax, yMin, zMin);
            gl.glVertex3f(xMax, yMax, zMin);
            gl.glVertex3f(xMin, yMax, zMin);
            gl.glEnd();

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3f(xMin, yMax, zMin);
            gl.glVertex3f(xMax, yMax, zMin);
            gl.glVertex3f(xMax, yMax, zMax);
            gl.glVertex3f(xMin, yMax, zMax);
            gl.glEnd();

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3f(xMin, yMin, zMin);
            gl.glVertex3f(xMax, yMin, zMin);
            gl.glVertex3f(xMax, yMin, zMax);
            gl.glVertex3f(xMin, yMin, zMax);
            gl.glEnd();

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3f(xMin, yMin, zMin);
            gl.glVertex3f(xMin, yMin, zMax);
            gl.glVertex3f(xMin, yMax, zMax);
            gl.glVertex3f(xMin, yMax, zMin);
            gl.glEnd();

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3f(xMax, yMin, zMin);
            gl.glVertex3f(xMax, yMin, zMax);
            gl.glVertex3f(xMax, yMax, zMax);
            gl.glVertex3f(xMax, yMax, zMin);
            gl.glEnd();
        }

        @Override
        public float getDefaultWidth() {
            return BASE_WIDTH;
        }

        @Override
        public float getDefaultHeight() {
            return BASE_HEIGHT;
        }

        @Override
        public float getDefaultDepth() {
            return BASE_DEPTH;
        }
    }
}