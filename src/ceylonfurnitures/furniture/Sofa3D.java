package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

import java.awt.Color;

public class Sofa3D implements Furniture3D {
    private static final float LEG_HEIGHT = 0.1f;
    private static final float BASE_HEIGHT = 0.3f;
    private static final float BACK_HEIGHT = 0.5f;

    @Override
    public void draw(GL2 gl, Furniture furniture) {
        float width = furniture.getWidth() / 1000.0f;
        float depth = furniture.getHeight() / 1000.0f;
        float height = LEG_HEIGHT + BASE_HEIGHT + BACK_HEIGHT;

        Color color = furniture.getColor();
        float shading = furniture.getShading();
        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0] * (1.0f - shading);
        float g = rgb[1] * (1.0f - shading);
        float b = rgb[2] * (1.0f - shading);

        gl.glPushMatrix();

        // ==== Legs ====
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        float legW = 0.05f;
        drawCuboid(gl, 0, 0, 0, legW, LEG_HEIGHT, legW); // front-left
        drawCuboid(gl, width - legW, 0, 0, width, LEG_HEIGHT, legW); // front-right
        drawCuboid(gl, 0, 0, depth - legW, legW, LEG_HEIGHT, depth); // back-left
        drawCuboid(gl, width - legW, 0, depth - legW, width, LEG_HEIGHT, depth); // back-right

        // ==== Base ====
        gl.glColor3f(r, g, b);
        drawCuboid(gl, 0, LEG_HEIGHT, 0, width, LEG_HEIGHT + BASE_HEIGHT, depth);

        // ==== Inner Cushion Layer ====
        gl.glColor3f(Math.min(r * 1.1f, 1f), Math.min(g * 1.1f, 1f), Math.min(b * 1.1f, 1f));
        drawCuboid(gl, 0.03f, LEG_HEIGHT + BASE_HEIGHT, 0.03f, width - 0.03f, LEG_HEIGHT + BASE_HEIGHT + 0.12f, depth - 0.03f);

        // ==== Seat Dividers (3 sections like Sofa2D) ====
        gl.glColor3f(1f, 1f, 1f); // Light colored divider lines
        float seatGap = 0.02f;
        float seatTop = LEG_HEIGHT + BASE_HEIGHT + 0.12f;
        for (int i = 1; i <= 2; i++) {
            float x = i * width / 3;
            drawCuboid(gl, x - seatGap / 2, LEG_HEIGHT + BASE_HEIGHT, 0.04f, x + seatGap / 2, seatTop, depth - 0.04f);
        }

        // ==== Backrest ====
        gl.glColor3f(r * 0.7f, g * 0.7f, b * 0.7f);
        drawCuboid(gl, 0f, LEG_HEIGHT + BASE_HEIGHT + 0.12f, -0.05f, width, height, 0.03f);

        // ==== Armrests ====
        gl.glColor3f(r * 0.85f, g * 0.85f, b * 0.85f);
        float armW = 0.08f;
        drawCuboid(gl, 0, LEG_HEIGHT + BASE_HEIGHT, 0.02f, armW, height - 0.05f, depth - 0.02f); // left
        drawCuboid(gl, width - armW, LEG_HEIGHT + BASE_HEIGHT, 0.02f, width, height - 0.05f, depth - 0.02f); // right

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
        return 1.8f;
    }

    @Override
    public float getDefaultHeight() {
        return LEG_HEIGHT + BASE_HEIGHT + BACK_HEIGHT;
    }

    @Override
    public float getDefaultDepth() {
        return 0.7f;
    }
}
