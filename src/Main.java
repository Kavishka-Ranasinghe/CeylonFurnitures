package ceylonfurnitures;

import ceylonfurnitures.db.DatabaseManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run the UI code on the Event Dispatch Thread (EDT) for thread safety
        DatabaseManager dbManager = new DatabaseManager();

        // Test the database connection
        dbManager.testConnection();

        // Run the UI code on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Create the main window
            JFrame frame = new JFrame("Ceylon Furnitures");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Center the window on the screen

            // For now, just add a blank panel
            JPanel panel = new JPanel();
            frame.add(panel);

            // Make the window visible
            frame.setVisible(true);
        });
    }
}