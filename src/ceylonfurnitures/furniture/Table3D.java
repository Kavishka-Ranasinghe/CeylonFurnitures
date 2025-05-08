package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Table3D implements Furniture3D {
    private static final float BASE_HEIGHT = 0.5f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f;
        float depth = furniture.getHeight() / 1000.0f;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // ==== Legs ====
        float legThickness = 0.06f;
        float legHeight = 0.6f;

        gl.glColor3f(0.25f, 0.25f, 0.25f);  // Darker leg color

        // Front-left leg
        drawCuboid(gl, 0f, 0f, 0f, legThickness, legHeight, legThickness);
        // Front-right leg
        drawCuboid(gl, width - legThickness, 0f, 0f, width, legHeight, legThickness);
        // Back-left leg
        drawCuboid(gl, 0f, 0f, depth - legThickness, legThickness, legHeight, depth);
        // Back-right leg
        drawCuboid(gl, width - legThickness, 0f, depth - legThickness, width, legHeight, depth);

        // ==== Table Top ====
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0f, legHeight, 0f, width, legHeight + 0.1f, depth);

        // ==== Inset Surface Layer (like polished top) ====
        gl.glColor3f(Math.min(r * 1.1f, 1.0f), Math.min(g * 1.1f, 1.0f), Math.min(b * 1.1f, 1.0f));
        drawCuboid(gl, 0.03f, legHeight + 0.1f, 0.03f, width - 0.03f, legHeight + 0.12f, depth - 0.03f);

        // ==== Decorative Grain Stripes (simulated with thin slats) ====
        gl.glColor3f(1f, 1f, 1f);  // Light accent lines
        for (float x = 0.05f; x < width - 0.05f; x += 0.12f) {
            drawCuboid(gl, x, legHeight + 0.12f, 0.05f, x + 0.01f, legHeight + 0.125f, depth - 0.05f);
        }

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
        gl.glVertex3f(xMin, yMax, zMax);
        gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMax, yMax, zMin);
        gl.glVertex3f(xMin, yMax, zMin);
        // Bottom
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMin, yMin, zMax);
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
        return 1.0f;
    }

    @Override
    public float getDefaultHeight() {
        return BASE_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return 0.6f;
    }
}
