package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Bed3D implements Furniture3D {
    private static final float BASE_HEIGHT = 0.5f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f; // Already scaled in DesignPanel
        float height = BASE_HEIGHT; // Fixed height for bed base
        float depth = furniture.getHeight() / 1000.0f; // Already scaled in DesignPanel

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // Bed base (from Z=0 to Z=depth, headboard at Z=0)
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0f, 0f, 0f, width, 0.15f, depth);

        // Mattress (slightly inset, on top of the base)
        gl.glColor3f(r * 1.1f, g * 1.1f, b * 1.1f);
        drawCuboid(gl, 0.05f, 0.15f, 0f, width - 0.05f, 0.35f, depth - 0.05f);

        // Headboard (at Z=0, the "top" of the bed)
        gl.glColor3f(r * 0.7f, g * 0.7f, b * 0.7f);
        drawCuboid(gl, 0f, 0.15f, 0f, width, 0.9f, -0.07f); // Headboard at Z=0 to Z=-0.07

        // Pillows (positioned near the headboard, on the mattress)
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        drawCuboid(gl, width * 0.1f, 0.35f, 0f, width * 0.4f, 0.45f, 0.17f);
        drawCuboid(gl, width * 0.6f, 0.35f, 0f, width * 0.9f, 0.45f, 0.17f);

        gl.glPopMatrix();
    }

    private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        gl.glBegin(GL2.GL_QUADS);
        // Front (positive Z face)
        gl.glNormal3f(0, 0, 1);
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMax);
        // Back (negative Z face)
        gl.glNormal3f(0, 0, -1);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMax, zMin);
        gl.glVertex3f(xMax, yMax, zMin);
        // Top (positive Y face)
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glVertex3f(xMin, yMax, zMin);
        // Bottom (negative Y face)
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMin, yMin, zMax);
        // Left (negative X face)
        gl.glNormal3f(-1, 0, 0);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glVertex3f(xMin, yMax, zMin);
        // Right (positive X face)
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