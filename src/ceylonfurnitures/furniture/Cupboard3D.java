package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Cupboard3D implements Furniture3D {
    private static final float BODY_HEIGHT = 1.7f;
    private static final float LEG_HEIGHT = 0.1f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f;
        float depth = furniture.getHeight() / 1000.0f;
        float height = BODY_HEIGHT + LEG_HEIGHT;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // ==== Legs ====
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        float legW = 0.05f, legD = 0.05f;
        drawCuboid(gl, 0f, 0f, 0f, legW, LEG_HEIGHT, legD);
        drawCuboid(gl, width - legW, 0f, 0f, width, LEG_HEIGHT, legD);
        drawCuboid(gl, 0f, 0f, depth - legD, legW, LEG_HEIGHT, depth);
        drawCuboid(gl, width - legW, 0f, depth - legD, width, LEG_HEIGHT, depth);

        // ==== Main Body ====
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0f, LEG_HEIGHT, 0f, width, height, depth);

        // ==== Interior Shelves ====
        gl.glColor3f(0.7f, 0.7f, 0.7f); // light gray for shelves
        float shelfThickness = 0.01f;
        for (float y = LEG_HEIGHT + 0.5f; y < height - 0.1f; y += 0.4f) {
            drawCuboid(gl, 0.02f, y, 0.02f, width - 0.02f, y + shelfThickness, depth - 0.02f);
        }

        // ==== Left Door (closed) ====
        gl.glColor3f(r * 1.1f, g * 1.1f, b * 1.1f);
        drawCuboid(gl, 0.02f, LEG_HEIGHT + 0.02f, 0.01f, width / 2 - 0.01f, height - 0.01f, depth - 0.01f);

        // ==== Right Door (open, Y position fixed) ====
        gl.glPushMatrix();
        float doorWidth = width / 2 - 0.03f;
        float doorHeight = height - LEG_HEIGHT - 0.03f;
        float doorDepth = 0.02f;
        float doorAngle = -30f;
        gl.glTranslatef(width - 0.03f, LEG_HEIGHT + 0.02f, 0.01f);
        gl.glRotatef(doorAngle, 0, 1, 0);
        gl.glColor3f(r * 0.95f, g * 0.95f, b * 0.95f);
        drawCuboid(gl, -doorWidth, 0, 0, 0, doorHeight, doorDepth);
        gl.glPopMatrix();

        // ==== Left Handle ====
        gl.glColor3f(0f, 0f, 0f);
        float handleY = LEG_HEIGHT + BODY_HEIGHT / 2;
        drawCuboid(gl, width / 2 - 0.025f, handleY, depth - 0.015f, width / 2 - 0.02f, handleY + 0.12f, depth);

        // ==== Right Handle on Open Door ====
        gl.glPushMatrix();
        gl.glTranslatef(width - 0.03f, LEG_HEIGHT + 0.02f, 0.01f);
        gl.glRotatef(doorAngle, 0, 1, 0);
        drawCuboid(gl, -doorWidth + 0.02f, doorHeight / 2, doorDepth, -doorWidth + 0.025f, doorHeight / 2 + 0.12f, doorDepth + 0.005f);
        gl.glPopMatrix();

        // ==== Top Trim ====
        gl.glColor3f(r * 0.8f, g * 0.8f, b * 0.8f);
        drawCuboid(gl, 0f, height, 0f, width, height + 0.02f, depth);

        gl.glPopMatrix();
    }

    private void drawCuboid(GL2 gl, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0, 0, 1); // Front
        gl.glVertex3f(xMin, yMin, zMax); gl.glVertex3f(xMax, yMin, zMax);
        gl.glVertex3f(xMax, yMax, zMax); gl.glVertex3f(xMin, yMax, zMax);
        gl.glNormal3f(0, 0, -1); // Back
        gl.glVertex3f(xMax, yMin, zMin); gl.glVertex3f(xMin, yMin, zMin);
        gl.glVertex3f(xMin, yMax, zMin); gl.glVertex3f(xMax, yMax, zMin);
        gl.glNormal3f(0, 1, 0); // Top
        gl.glVertex3f(xMin, yMax, zMax); gl.glVertex3f(xMax, yMax, zMax);
        gl.glVertex3f(xMax, yMax, zMin); gl.glVertex3f(xMin, yMax, zMin);
        gl.glNormal3f(0, -1, 0); // Bottom
        gl.glVertex3f(xMin, yMin, zMin); gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMin, zMax); gl.glVertex3f(xMin, yMin, zMax);
        gl.glNormal3f(-1, 0, 0); // Left
        gl.glVertex3f(xMin, yMin, zMin); gl.glVertex3f(xMin, yMin, zMax);
        gl.glVertex3f(xMin, yMax, zMax); gl.glVertex3f(xMin, yMax, zMin);
        gl.glNormal3f(1, 0, 0); // Right
        gl.glVertex3f(xMax, yMin, zMax); gl.glVertex3f(xMax, yMin, zMin);
        gl.glVertex3f(xMax, yMax, zMin); gl.glVertex3f(xMax, yMax, zMax);
        gl.glEnd();
    }

    @Override
    public float getDefaultWidth() {
        return 0.8f;
    }

    @Override
    public float getDefaultHeight() {
        return BODY_HEIGHT + LEG_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return 0.4f;
    }
}
