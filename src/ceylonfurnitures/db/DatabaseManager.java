package ceylonfurnitures.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    // Test method to verify database connection
    public void testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}