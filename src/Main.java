package ceylonfurnitures;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.model.User;
import ceylonfurnitures.view.DashboardPanel;
import ceylonfurnitures.view.LoginPanel;
import ceylonfurnitures.view.SignupPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Main {
    private static JFrame frame;
    private static DatabaseManager dbManager;
    private static FurnitureFactory furnitureFactory;

    public static void main(String[] args) {
        // Initialize the database and furniture factory
        dbManager = new DatabaseManager();
        furnitureFactory = new FurnitureFactory();

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
        frame.getContentPane().removeAll();
        LoginPanel loginPanel = new LoginPanel(
                dbManager,
                Main::showSignupPanel,
                Main::showDashboardPanel
        );
        frame.add(loginPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showSignupPanel() {
        frame.getContentPane().removeAll();
        SignupPanel signupPanel = new SignupPanel(dbManager, Main::showLoginPanel);
        frame.add(signupPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showDashboardPanel(User user) {
        frame.getContentPane().removeAll();
        DashboardPanel dashboardPanel = new DashboardPanel(
                user,
                dbManager,
                furnitureFactory,
                Main::showLoginPanel,
                () -> System.out.println("Show saved designs clicked (to be implemented in Day 7)"),
                () -> System.out.println("Start design clicked (to be implemented in Day 5)")
        );
        frame.add(dashboardPanel);
        frame.revalidate();
        frame.repaint();
    }
}