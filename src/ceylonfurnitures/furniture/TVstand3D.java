package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;
import java.awt.Color;

public class TVstand3D implements Furniture3D {
    private static final float STAND_HEIGHT = 0.3f;
    private static final float STAND_DEPTH = 0.4f;
    private static final int COMPARTMENTS = 3;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f;
        float depth = furniture.getHeight() / 1000.0f;
        float height = STAND_HEIGHT;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        float panelThickness = 0.02f;

        gl.glPushMatrix();

        // ==== Side Panels ====
        gl.glColor3f(r * 0.9f, g * 0.9f, b * 0.9f);
        drawCuboid(gl, 0, 0, 0, panelThickness, height, depth); // left
        drawCuboid(gl, width - panelThickness, 0, 0, width, height, depth); // right

        // ==== Top and Bottom Panels ====
        drawCuboid(gl, panelThickness, height - panelThickness, 0, width - panelThickness, height, depth); // top
        drawCuboid(gl, panelThickness, 0, 0, width - panelThickness, panelThickness, depth); // bottom

        // ==== Back Panel ====
        gl.glColor3f(r * 0.6f, g * 0.6f, b * 0.6f);
        drawCuboid(gl, panelThickness, panelThickness, 0.01f, width - panelThickness, height - panelThickness, 0.02f);

        // ==== Inner Compartments (dividers) ====
        gl.glColor3f(r * 1.2f, g * 1.2f, b * 1.2f);
        float compartmentWidth = (width - 2 * panelThickness) / COMPARTMENTS;
        float shelfY = panelThickness;
        for (int i = 1; i < COMPARTMENTS; i++) {
            float x = panelThickness + i * compartmentWidth;
            drawCuboid(gl, x, shelfY, 0.02f, x + panelThickness, height - panelThickness, depth - 0.015f);
        }

        gl.glPopMatrix();
    }

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
        return 1.0f;
    }

    @Override
    public float getDefaultHeight() {
        return STAND_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return STAND_DEPTH;
    }
}
