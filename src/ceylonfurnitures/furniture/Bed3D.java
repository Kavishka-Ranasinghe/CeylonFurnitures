package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Bed3D implements Furniture3D {
    private static final float BASE_HEIGHT = 0.5f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f;
        float height = BASE_HEIGHT;
        float depth = furniture.getHeight() / 1000.0f;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // ==== Bed Base ====
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0f, 0f, 0f, width, 0.15f, depth);

        // ==== Mattress ====
        gl.glColor3f(Math.min(r * 1.1f, 1.0f), Math.min(g * 1.1f, 1.0f), Math.min(b * 1.1f, 1.0f));
        drawCuboid(gl, 0.04f, 0.15f, 0.04f, width - 0.04f, 0.35f, depth - 0.04f);

        // ==== Quilt-Like Layer on Mattress ====
        gl.glColor3f(Math.min(r * 1.2f, 1.0f), Math.min(g * 1.2f, 1.0f), Math.min(b * 1.2f, 1.0f));
        for (float z = 0.05f; z < depth - 0.05f; z += 0.1f) {
            drawCuboid(gl, 0.05f, 0.35f, z, width - 0.05f, 0.36f, z + 0.05f);
        }

        // ==== Headboard ====
        gl.glColor3f(r * 0.6f, g * 0.6f, b * 0.6f);
        drawCuboid(gl, 0f, 0.15f, 0f, width, 0.9f, -0.06f);

        // ==== Pillows ====
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        drawCuboid(gl, width * 0.08f, 0.36f, 0.08f, width * 0.38f, 0.43f, 0.22f);
        drawCuboid(gl, width * 0.62f, 0.36f, 0.08f, width * 0.92f, 0.43f, 0.22f);

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
