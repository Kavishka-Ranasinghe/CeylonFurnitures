package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;
import java.awt.Color;

public class Bookshelf3D implements Furniture3D {
    private static final float BODY_HEIGHT = 1.2f;
    private static final float BODY_DEPTH = 0.3f;
    private static final int SHELF_COUNT = 3;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f;
        float depth = furniture.getHeight() / 1000.0f;
        float height = BODY_HEIGHT;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        float sideThickness = 0.03f;
        float backInset = 0.015f;

        gl.glPushMatrix();

        // ==== Left & Right Side Panels ====
        gl.glColor3f(r * 0.9f, g * 0.9f, b * 0.9f);
        drawCuboid(gl, 0f, 0f, 0f, sideThickness, height, depth); // left
        drawCuboid(gl, width - sideThickness, 0f, 0f, width, height, depth); // right

        // ==== Top & Bottom Panels ====
        drawCuboid(gl, sideThickness, height - sideThickness, 0f, width - sideThickness, height, depth); // top
        drawCuboid(gl, sideThickness, 0f, 0f, width - sideThickness, sideThickness, depth); // bottom

        // ==== Back Panel (darker) ====
        gl.glColor3f(r * 0.5f, g * 0.5f, b * 0.5f);
        drawCuboid(gl,
                sideThickness, sideThickness, backInset,
                width - sideThickness, height - sideThickness, backInset + 0.01f
        );

        // ==== Shelves ====
        gl.glColor3f(r * 1.2f, g * 1.2f, b * 1.2f); // bright inner shelves
        float shelfThickness = 0.015f;
        float shelfSpacing = (height - 2 * sideThickness) / SHELF_COUNT;

        for (int i = 1; i < SHELF_COUNT; i++) {
            float y = sideThickness + i * shelfSpacing;
            drawCuboid(gl,
                    sideThickness, y, backInset + 0.005f,
                    width - sideThickness, y + shelfThickness, depth - 0.015f
            );
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
        return 0.8f;
    }

    @Override
    public float getDefaultHeight() {
        return BODY_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return BODY_DEPTH;
    }
}
