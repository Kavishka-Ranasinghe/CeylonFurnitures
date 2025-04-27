package ceylonfurnitures;

import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.model.User;
import ceylonfurnitures.view.LoginPanel;
import ceylonfurnitures.view.SignupPanel;
import java.util.function.Consumer;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static JFrame frame;
    private static DatabaseManager dbManager;

    public static void main(String[] args) {
        // Initialize the database
        dbManager = new DatabaseManager();

        // Test the database connection
        dbManager.testConnection();

        // Run the UI
        SwingUtilities.invokeLater(() -> {
            // Create the main window
            frame = new JFrame("Ceylon Furnitures");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            // Show the login panel by default
            showLoginPanel();

            // Make the window visible
            frame.setVisible(true);
        });
    }

    private static void showLoginPanel() {
        frame.getContentPane().removeAll(); // Clear previous content
        LoginPanel loginPanel = new LoginPanel(
                dbManager,
                Main::showSignupPanel,
                user -> {
                    System.out.println("Logged in user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                    // TODO: Switch to DashboardPanel (Day 4)
                }
        );
        frame.add(loginPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showSignupPanel() {
        frame.getContentPane().removeAll(); // Clear previous content
        SignupPanel signupPanel = new SignupPanel(dbManager, Main::showLoginPanel);
        frame.add(signupPanel);
        frame.revalidate();
        frame.repaint();
    }
}