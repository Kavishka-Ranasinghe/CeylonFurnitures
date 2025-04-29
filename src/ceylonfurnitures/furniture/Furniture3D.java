package ceylonfurnitures.furniture;

import ceylonfurnitures.model.Furniture;
import com.jogamp.opengl.GL2;

public interface Furniture3D {
    void draw(GL2 gl, Furniture furniture);
    float getDefaultWidth(); // Width in OpenGL units (meters)
    float getDefaultHeight(); // Height in OpenGL units (meters)
    float getDefaultDepth(); // Depth in OpenGL units (meters)
}