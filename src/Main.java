package ceylonfurnitures;

import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.db.DatabaseManager.User;
import ceylonfurnitures.db.DatabaseManager.Design;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager dbManager = new DatabaseManager();

        // Test the database connection
        dbManager.testConnection();

        // Test CRUD operations
        try {
            // Test User CRUD
            System.out.println("Testing User CRUD operations...");

            // Create a user
            int userId = dbManager.createUser("testuser", "password123", "testuser@example.com");
            System.out.println("Created user with ID: " + userId);

            // Read the user
            User user = dbManager.readUser("testuser");
            if (user != null) {
                System.out.println("Read user: ID=" + user.getId() + ", Username=" + user.getUsername() +
                        ", Email=" + user.getEmail());
            } else {
                System.out.println("User not found.");
            }

            // Update the user
            dbManager.updateUser(userId, "testuser_updated", "newpassword123", "updated@example.com");
            user = dbManager.readUser("testuser_updated");
            if (user != null) {
                System.out.println("Updated user: ID=" + user.getId() + ", Username=" + user.getUsername() +
                        ", Email=" + user.getEmail());
            }

            // Test Design CRUD
            System.out.println("\nTesting Design CRUD operations...");

            // Create a design
            int designId = dbManager.createDesign(userId, "Test Design",
                    "{\"width\":5000, \"depth\":3000, \"height\":2700}",
                    "{\"walls\":\"#FFFFE0\", \"floor\":\"#D3D3D3\"}",
                    "[{\"type\":\"table\", \"position\":{\"x\":100, \"y\":100}}]");
            System.out.println("Created design with ID: " + designId);

            // Read designs
            List<Design> designs = dbManager.readDesigns(userId);
            for (Design design : designs) {
                System.out.println("Read design: ID=" + design.getId() + ", Name=" + design.getName() +
                        ", Room Dimensions=" + design.getRoomDimensions() +
                        ", Room Colors=" + design.getRoomColors() +
                        ", Furniture=" + design.getFurniture());
            }

            // Update the design
            dbManager.updateDesign(designId, "Updated Design",
                    "{\"width\":6000, \"depth\":4000, \"height\":2800}",
                    "{\"walls\":\"#FFFFFF\", \"floor\":\"#808080\"}",
                    "[{\"type\":\"table\", \"position\":{\"x\":200, \"y\":200}}]");
            designs = dbManager.readDesigns(userId);
            for (Design design : designs) {
                System.out.println("Updated design: ID=" + design.getId() + ", Name=" + design.getName() +
                        ", Room Dimensions=" + design.getRoomDimensions() +
                        ", Room Colors=" + design.getRoomColors() +
                        ", Furniture=" + design.getFurniture());
            }

            // Delete the design
            dbManager.deleteDesign(designId);
            System.out.println("Deleted design with ID: " + designId);
            designs = dbManager.readDesigns(userId);
            System.out.println("Designs after deletion: " + (designs.isEmpty() ? "None" : designs.size()));

            // Delete the user
            dbManager.deleteUser(userId);
            System.out.println("Deleted user with ID: " + userId);
            user = dbManager.readUser("testuser_updated");
            System.out.println("User after deletion: " + (user == null ? "Not found" : "Still exists"));

        } catch (SQLException e) {
            System.err.println("Error during CRUD operations: " + e.getMessage());
            e.printStackTrace();
        }

        // Run the UI (unchanged from Day 1)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ceylon Furnitures");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            frame.add(panel);

            frame.setVisible(true);
        });
    }
}