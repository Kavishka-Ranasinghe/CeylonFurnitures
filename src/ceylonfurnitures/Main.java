package ceylonfurnitures;

import ceylonfurnitures.controller.FurnitureFactory;
import ceylonfurnitures.db.DatabaseManager;
import ceylonfurnitures.model.User;
import ceylonfurnitures.model.Design;
import ceylonfurnitures.view.DashboardPanel;
import ceylonfurnitures.view.DesignPanel;
import ceylonfurnitures.view.LoginPanel;
import ceylonfurnitures.view.SignupPanel;
import ceylonfurnitures.view.SavedDesignsPanel;

import javax.swing.*;
import java.util.List;

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
            frame.setSize(1000, 700);
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
                () -> showSavedDesignsPanel(user),
                () -> showDesignPanel(user)
        );
        frame.add(dashboardPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showDesignPanel(User user) {
        frame.getContentPane().removeAll();
        DesignPanel designPanel = new DesignPanel(user, dbManager, furnitureFactory, () -> showDashboardPanel(user));
        frame.add(designPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void showDesignPanel(User user, Design design) {
        frame.getContentPane().removeAll();
        DesignPanel designPanel = new DesignPanel(user, dbManager, furnitureFactory, () -> showDashboardPanel(user), design);
        frame.add(designPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showSavedDesignsPanel(User user) {
        frame.getContentPane().removeAll();
        try {
            List<Design> designs = dbManager.readDesigns(user.getId());
            SavedDesignsPanel savedDesignsPanel = new SavedDesignsPanel(
                    user,
                    designs,
                    dbManager,
                    furnitureFactory,
                    () -> showDashboardPanel(user),
                    Main::showSavedDesignsPanel // Pass the refresh callback
            );
            frame.add(savedDesignsPanel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading saved designs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            showDashboardPanel(user); // Fallback to dashboard on error
        }
        frame.revalidate();
        frame.repaint();
    }
}