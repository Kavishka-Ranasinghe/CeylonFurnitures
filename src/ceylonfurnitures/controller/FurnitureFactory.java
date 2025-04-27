package ceylonfurnitures.controller;

import ceylonfurnitures.model.Furniture;

import java.util.ArrayList;
import java.util.List;

public class FurnitureFactory {
    private List<Furniture> furnitureTypes;

    public FurnitureFactory() {
        // Initialize with some basic furniture types
        furnitureTypes = new ArrayList<>();
        furnitureTypes.add(new Furniture("bed", "Bed"));
        furnitureTypes.add(new Furniture("table", "Table"));
        furnitureTypes.add(new Furniture("chair", "Chair"));
        furnitureTypes.add(new Furniture("sofa", "Sofa"));
    }

    public List<Furniture> getFurnitureTypes() {
        return new ArrayList<>(furnitureTypes); // Return a copy to prevent modification
    }
}