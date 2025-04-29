package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Bed3D implements Furniture3D {
    private static final float BASE_WIDTH = 1.2f; // 1.2 meters
    private static final float BASE_HEIGHT = 0.5f; // 0.5 meters (height of the bed)
    private static final float BASE_DEPTH = 2.0f; // 2.0 meters

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        // Convert pixel coordinates to world coordinates (meters)
        float x = (furniture.getX() * 10.0f / 1000.0f); // Adjusted scaling to match room
        float z = (furniture.getY() * 10.0f / 1000.0f); // In 3D, y from 2D becomes z
        float width = furniture.getWidth() * 10.0f / 1000.0f;
        float height = BASE_HEIGHT; // Fixed height for 3D
        float depth = furniture.getHeight() * 10.0f / 1000.0f; // In 2D, "height" is depth in 3D
        float rotation = furniture.getRotation();
        Color color = furniture.getColor();
        float shading = furniture.getShading();

        // Adjust color for shading
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();
        // Translate to the bed's position (adjust for room's coordinate system in DesignPanel)
        gl.glTranslatef(x, 0, z); // Position at the bottom-left corner, not centered
        gl.glRotatef(rotation, 0, 1, 0); // Rotate around Y-axis
        gl.glScalef(width, height, depth);

        // Draw the bed (simple cuboid for the mattress)
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);

        // Draw headboard
        gl.glColor3f(r * 0.8f, g * 0.8f, b * 0.8f); // Darker shade
        drawCuboid(gl, 0.0f, 1.0f, -0.05f, 1.0f, 2.0f, 0.0f);

        gl.glPopMatrix();
    }

    private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        // Front face (z = zMax)
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f); // Normal pointing forward
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glEnd();

        // Back face (z = zMin)
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, -1.0f); // Normal pointing backward
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glEnd();

        // Top face (y = yMax)
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 1.0f, 0.0f); // Normal pointing up
        gl.glVertex3f(xMin, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glEnd();

        // Bottom face (y = yMin)
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, -1.0f, 0.0f); // Normal pointing down
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glEnd();

        // Left face (x = xMin)
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f); // Normal pointing left
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMin);
        gl.glEnd();

        // Right face (x = xMax)
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(1.0f, 0.0f, 0.0f); // Normal pointing right
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMax);
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