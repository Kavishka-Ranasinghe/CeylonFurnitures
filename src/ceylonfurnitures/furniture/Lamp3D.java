package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Lamp3D implements Furniture3D {
    private static final float BASE_HEIGHT = 0.03f;
    private static final float POLE_HEIGHT = 1.0f;
    private static final float SHADE_HEIGHT = 0.2f;
    private static final float SHADE_WIDTH = 0.2f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float baseRadius = furniture.getWidth() / 2000.0f;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // ==== Solid Base ====
        gl.glColor3f(r * 0.7f, g * 0.7f, b * 0.7f);
        drawCylinder(gl, baseRadius, BASE_HEIGHT, 30);

        // ==== Pole ====
        gl.glTranslatef(0, BASE_HEIGHT, 0);
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        drawCylinder(gl, 0.02f, POLE_HEIGHT, 20);

        // ==== Square Lampshade ====
        gl.glTranslatef(0, POLE_HEIGHT, 0); // move to top of pole
        gl.glTranslatef(0, SHADE_HEIGHT / 2, 0); // center cube vertically
        gl.glRotatef(-20f, 1, 0, 0); // tilt head forward

        gl.glColor3f(r * 1.1f, g * 1.1f, b * 0.9f);
        drawCuboid(gl, -SHADE_WIDTH / 2, -SHADE_HEIGHT / 2, -SHADE_WIDTH / 2,
                SHADE_WIDTH / 2,  SHADE_HEIGHT / 2,  SHADE_WIDTH / 2);

        gl.glPopMatrix();
    }

    // ==== Draw Cylinder (used for base and pole) ====
    private void drawCylinder(GL2 gl, float radius, float height, int slices) {
        double angleStep = 2 * Math.PI / slices;

        // Side
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= slices; i++) {
            double angle = i * angleStep;
            float x = (float) (radius * Math.cos(angle));
            float z = (float) (radius * Math.sin(angle));
            gl.glNormal3f(x, 0, z);
            gl.glVertex3f(x, 0, z);
            gl.glVertex3f(x, height, z);
        }
        gl.glEnd();

        // Bottom
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(0, 0, 0);
        for (int i = slices; i >= 0; i--) {
            double angle = i * angleStep;
            float x = (float) (radius * Math.cos(angle));
            float z = (float) (radius * Math.sin(angle));
            gl.glVertex3f(x, 0, z);
        }
        gl.glEnd();

        // Top
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(0, height, 0);
        for (int i = 0; i <= slices; i++) {
            double angle = i * angleStep;
            float x = (float) (radius * Math.cos(angle));
            float z = (float) (radius * Math.sin(angle));
            gl.glVertex3f(x, height, z);
        }
        gl.glEnd();
    }

    // ==== Draw Cuboid (used for square lamp head) ====
    private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        gl.glBegin(GL2.GL_QUADS);
        // Front
        gl.glNormal3f(0, 0, 1);
        gl.glVertex3f(xMin, yMin, zMax); gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMax, zMax); gl.glVertex3f(xMin, yMax, zMax);
        // Back
        gl.glNormal3f(0, 0, -1);
        gl.glVertex3f(xMax, yMin, zMin); gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMax, zMin); gl.glVertex3f(xMax, yMax, zMin);
        // Top
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(xMin, yMax, zMax); gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMax, yMax, zMin); gl.glVertex3f(xMin, yMax, zMin);
        // Bottom
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(xMin, yMin, zMin); gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMin, zMax); gl.glVertex3f(xMin, yMin, zMax);
        // Left
        gl.glNormal3f(-1, 0, 0);
        gl.glVertex3f(xMin, yMin, zMin); gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMin, yMax, zMax); gl.glVertex3f(xMin, yMax, zMin);
        // Right
        gl.glNormal3f(1, 0, 0);
        gl.glVertex3f(xMax, yMin, zMax); gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMax, zMin); gl.glVertex3f(xMax, yMax, zMax);
        gl.glEnd();
    }

    @Override
    public float getDefaultWidth() {
        return 0.3f;
    }

    @Override
    public float getDefaultHeight() {
        return BASE_HEIGHT + POLE_HEIGHT + SHADE_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return 0.3f;
    }
}
