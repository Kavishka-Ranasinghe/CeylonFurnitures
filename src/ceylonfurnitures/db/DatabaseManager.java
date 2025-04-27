package ceylonfurnitures.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:db/ceylonfurnitures.db";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Create Users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "email TEXT NOT NULL" +
                    ")";
            stmt.execute(createUsersTable);

            // Create Designs table
            String createDesignsTable = "CREATE TABLE IF NOT EXISTS Designs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "room_dimensions TEXT NOT NULL," +
                    "room_colors TEXT NOT NULL," +
                    "furniture TEXT NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES Users(id)" +
                    ")";
            stmt.execute(createDesignsTable);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- User CRUD Operations ---

    // Create a new user (signup)
    public int createUser(String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real app, hash the password
            pstmt.setString(3, email);
            pstmt.executeUpdate();

            // Get the generated user ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1; // Indicate failure
        }
    }

    // Read a user by username (for login)
    public User readUser(String username) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
            return null; // User not found
        }
    }

    // Update a user's profile
    public void updateUser(int userId, String username, String password, String email) throws SQLException {
        String sql = "UPDATE Users SET username = ?, password = ?, email = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();
        }
    }

    // Delete a user
    public void deleteUser(int userId) throws SQLException {
        // First, delete all designs associated with the user
        String deleteDesignsSql = "DELETE FROM Designs WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteDesignsSql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }

        // Then, delete the user
        String deleteUserSql = "DELETE FROM Users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    // --- Design CRUD Operations ---

    // Create a new design
    public int createDesign(int userId, String name, String roomDimensions, String roomColors, String furniture) throws SQLException {
        String sql = "INSERT INTO Designs (user_id, name, room_dimensions, room_colors, furniture) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, roomDimensions);
            pstmt.setString(4, roomColors);
            pstmt.setString(5, furniture);
            pstmt.executeUpdate();

            // Get the generated design ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1; // Indicate failure
        }
    }

    // Read all designs for a user
    public List<Design> readDesigns(int userId) throws SQLException {
        List<Design> designs = new ArrayList<>();
        String sql = "SELECT * FROM Designs WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                designs.add(new Design(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("room_dimensions"),
                        rs.getString("room_colors"),
                        rs.getString("furniture")
                ));
            }
        }
        return designs;
    }

    // Update a design
    public void updateDesign(int designId, String name, String roomDimensions, String roomColors, String furniture) throws SQLException {
        String sql = "UPDATE Designs SET name = ?, room_dimensions = ?, room_colors = ?, furniture = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, roomDimensions);
            pstmt.setString(3, roomColors);
            pstmt.setString(4, furniture);
            pstmt.setInt(5, designId);
            pstmt.executeUpdate();
        }
    }

    // Delete a design
    public void deleteDesign(int designId) throws SQLException {
        String sql = "DELETE FROM Designs WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, designId);
            pstmt.executeUpdate();
        }
    }

    // Test method to verify database connection
    public void testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inner User class (for simplicity, until we create a separate model)
    public static class User {
        private int id;
        private String username;
        private String password;
        private String email;

        public User(int id, String username, String password, String email) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.email = email;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
    }

    // Inner Design class (for simplicity, until we create a separate model)
    public static class Design {
        private int id;
        private int userId;
        private String name;
        private String roomDimensions;
        private String roomColors;
        private String furniture;

        public Design(int id, int userId, String name, String roomDimensions, String roomColors, String furniture) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.roomDimensions = roomDimensions;
            this.roomColors = roomColors;
            this.furniture = furniture;
        }

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getName() { return name; }
        public String getRoomDimensions() { return roomDimensions; }
        public String getRoomColors() { return roomColors; }
        public String getFurniture() { return furniture; }
    }
}