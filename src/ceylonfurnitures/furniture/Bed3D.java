package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Bed3D implements Furniture3D {
    private static final float BASE_HEIGHT = 0.5f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float x = furniture.getX() * 10.0f / 1000.0f;
        float z = furniture.getY() * 10.0f / 1000.0f;
        float width = furniture.getWidth() * 10.0f / 1000.0f;
        float height = BASE_HEIGHT;
        float depth = furniture.getHeight() * 10.0f / 1000.0f;
        float rotation = furniture.getRotation();

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // Move to center of the furniture
        gl.glTranslatef(x + width / 2f, 0, z + depth / 2f);

        // Apply rotation
        gl.glRotatef(rotation, 0, 1, 0);

        // Move back to draw from (0,0,0)
        gl.glTranslatef(-width / 2f, 0, -depth / 2f);

        // Draw bed base
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0f, 0f, 0f, width, height, depth);

        // Draw headboard at the far end (foot side):
        gl.glColor3f(r * 0.8f, g * 0.8f, b * 0.8f);
        drawCuboid(gl, 0f, height, depth - 0.05f, width, height + 1.0f, depth);

        gl.glPopMatrix();
    }

    private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        gl.glBegin(GL2.GL_QUADS);
        // Front
        gl.glNormal3f(0, 0, 1);
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMax);

        // Back
        gl.glNormal3f(0, 0, -1);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMin);

        // Top
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(xMin, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMax);

        // Bottom
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMin);

        // Left
        gl.glNormal3f(-1, 0, 0);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMin);

        // Right
        gl.glNormal3f(1, 0, 0);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glEnd();
    }

    @Override
    public float getDefaultWidth() {
        return 1.2f;
    }

    @Override
    public float getDefaultHeight() {
        return BASE_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return 2.0f;
    }
}
