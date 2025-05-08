package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Chair3D implements Furniture3D {
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

        // ==== Taller Legs ====
        float legThickness = 0.05f;
        float legHeight = 0.35f;  // Increased leg height

        gl.glColor3f(0.2f, 0.2f, 0.2f); // dark gray legs
        // Front left
        drawCuboid(gl, 0f, 0f, 0f, legThickness, legHeight, legThickness);
        // Front right
        drawCuboid(gl, width - legThickness, 0f, 0f, width, legHeight, legThickness);
        // Back left
        drawCuboid(gl, 0f, 0f, depth - legThickness, legThickness, legHeight, depth);
        // Back right
        drawCuboid(gl, width - legThickness, 0f, depth - legThickness, width, legHeight, depth);

        // ==== Seat Base (Raised due to leg height) ====
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0f, legHeight, 0f, width, legHeight + 0.1f, depth); // thinner seat base

        // ==== Seat Cushion (Slightly thinner) ====
        gl.glColor3f(Math.min(r * 1.1f, 1.0f), Math.min(g * 1.1f, 1.0f), Math.min(b * 1.1f, 1.0f));
        drawCuboid(gl, 0.04f, legHeight + 0.1f, 0.04f, width - 0.04f, legHeight + 0.18f, depth - 0.04f);

        // ==== Stitching Stripes ====
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        for (float z = 0.06f; z < depth - 0.06f; z += 0.08f) {
            drawCuboid(gl, 0.06f, legHeight + 0.18f, z, width - 0.06f, legHeight + 0.185f, z + 0.015f);
        }

        // ==== Backrest ====
        gl.glColor3f(r * 0.6f, g * 0.6f, b * 0.6f);
        drawCuboid(gl, 0f, legHeight + 0.1f, 0f, width, legHeight + 0.9f, -0.05f);

        // ==== Armrests ====
        float armHeight = 0.25f;
        float armTop = legHeight + 0.18f;
        float armWidth = 0.05f;

        gl.glColor3f(r * 0.8f, g * 0.8f, b * 0.8f);
        // Left armrest
        drawCuboid(gl, 0f, legHeight + 0.18f, 0.02f, armWidth, armTop + armHeight, depth - 0.02f);
        // Right armrest
        drawCuboid(gl, width - armWidth, legHeight + 0.18f, 0.02f, width, armTop + armHeight, depth - 0.02f);

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
        return 0.6f;
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
