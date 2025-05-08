package ceylonfurnitures.model;

public class Design {
    private int id;
    private int userId;
    private String name;
    private String roomDimensions; // Format: "width,depth,height"
    private String roomColors;     // Format: "wallR,wallG,wallB,floorR,floorG,floorB"
    private String furniture;      // Format: "type,x,y,width,height,rotation,r,g,b,shading|type,..."

    public Design(int id, int userId, String name, String roomDimensions, String roomColors, String furniture) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.roomDimensions = roomDimensions;
        this.roomColors = roomColors;
        this.furniture = furniture;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getRoomDimensions() { return roomDimensions; }
    public String getRoomColors() { return roomColors; }
    public String getFurniture() { return furniture; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setRoomDimensions(String roomDimensions) { this.roomDimensions = roomDimensions; }
    public void setRoomColors(String roomColors) { this.roomColors = roomColors; }
    public void setFurniture(String furniture) { this.furniture = furniture; }
}