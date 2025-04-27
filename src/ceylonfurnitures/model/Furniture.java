package ceylonfurnitures.model;

public class Furniture {
    private String type;
    private String displayName;

    public Furniture(String type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }
}