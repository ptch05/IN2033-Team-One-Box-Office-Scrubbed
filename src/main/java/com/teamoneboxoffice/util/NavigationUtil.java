package com.teamoneboxoffice.util;

import com.teamoneboxoffice.controllers.BaseDashboardController;
import com.teamoneboxoffice.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for handling page navigation within a single Stage
 * and dynamically injecting role-based navigation bars.
 */
public class NavigationUtil {

    private static Stage primaryStage = null;
    private static Object lastLoadedController = null;


    /**
     * Stores the primary stage reference for later use in navigation.
     * Should be called once in the application's start method.
     * @param stage The primary stage of the application.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }



    /**
     * Loads a new FXML page into the primary stage's scene.
     * Triggered by an ActionEvent (e.g., Button click).
     * @param fxmlPath The resource path to the FXML file (e.g., "/scenes/MyPage.fxml").
     * @param title The title to set for the window.
     * @param event The ActionEvent that triggered the navigation.
     */
    public static void loadPage(String fxmlPath, String title, ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage == null) stage = primaryStage; // Fallback to primary stage if source stage is null
        loadPageShared(fxmlPath, title, stage, null);
    }

    /**
     * Loads a new FXML page into the primary stage's scene.
     * Triggered by a MouseEvent (e.g., Label click).
     * @param fxmlPath The resource path to the FXML file.
     * @param title The title to set for the window.
     * @param event The MouseEvent that triggered the navigation.
     */
    public static void loadPage(String fxmlPath, String title, MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage == null) stage = primaryStage; // Fallback
        loadPageShared(fxmlPath, title, stage, null);
    }

    /**
     * Loads a new FXML page, potentially passing user data.
     * Useful for initial login navigation.
     * @param fxmlPath The resource path to the FXML file.
     * @param title The title to set for the window.
     * @param eventSource A Node within the current scene to get the Stage from.
     * @param currentUser The User object to pass to the new controller (if applicable).
     */
    public static void loadPage(String fxmlPath, String title, Node eventSource, User currentUser) {
        Stage stage = (Stage) eventSource.getScene().getWindow();
        if (stage == null) stage = primaryStage; // Fallback
        loadPageShared(fxmlPath, title, stage, currentUser);
    }

    /**
     * Core logic to load FXML, inject navbar, set scene, and pass user data.
     * @param fxmlPath Path to the target FXML file.
     * @param title Window title for the new scene.
     * @param stage The stage on which to set the new scene.
     * @param currentUser The user data to pass (can be null).
     */
    private static void loadPageShared(String fxmlPath, String title, Stage stage, User currentUser) {
        lastLoadedController = null; // Reset controller cache
        if (stage == null) {
            System.err.println("Error: Stage object is null in loadPageShared. Cannot navigate.");
            showAlert("Navigation Error", "Internal error: Application window reference lost.");
            return;
        }
        try {
            URL location = NavigationUtil.class.getResource(fxmlPath);
            if (location == null) {
                throw new IOException("Cannot find FXML file: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent pageRoot = loader.load();
            lastLoadedController = loader.getController();

            String userRole = SessionManager.getCurrentUserRole();
            String navBarFxmlPath = null;
            if ("Manager".equals(userRole)) { navBarFxmlPath = "/scenes/ManagerNavBar.fxml"; }
            else if ("Deputy".equals(userRole)) { navBarFxmlPath = "/scenes/DeputyNavBar.fxml"; }
            else if ("Staff".equals(userRole)) { navBarFxmlPath = "/scenes/StaffNavBar.fxml"; }

            if (pageRoot instanceof BorderPane && navBarFxmlPath != null && !fxmlPath.contains("Home.fxml") && !fxmlPath.contains("Login")) {
                BorderPane borderPane = (BorderPane) pageRoot;
                URL navBarLocation = NavigationUtil.class.getResource(navBarFxmlPath);
                if (navBarLocation != null) {
                    FXMLLoader navLoader = new FXMLLoader(navBarLocation);
                    Node navBarNode = navLoader.load();
                    borderPane.setTop(navBarNode);
                    attachNavbarHandlers(navBarNode, stage);
                } else {
                    System.err.println("Could not find Navbar FXML: " + navBarFxmlPath);
                }
            }

            if (currentUser != null && lastLoadedController instanceof BaseDashboardController) {
                ((BaseDashboardController) lastLoadedController).setCurrentUser(currentUser);
            } else if (lastLoadedController instanceof BaseDashboardController && SessionManager.getCurrentUserRole() != null) {
            }

            // Prepare and set the new scene
            Scene scene = new Scene(pageRoot);
            String cssPath = "/styles/styles.css";
            URL cssUrl = NavigationUtil.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Could not find stylesheet resource: " + cssPath);
            }

            stage.setScene(scene);
            stage.setTitle(title);

        } catch (IOException e) {
            System.err.println("Failed to load page: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
            showAlert("Load Error", "Could not load the requested page: " + fxmlPath);
        } catch (IllegalStateException e) {
            System.err.println("Error during FXML loading (check controller/FXML linkage): " + fxmlPath);
            e.printStackTrace();
            showAlert("Load Error", "An error occurred while processing the page: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during navigation.");
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred.");
        }
    }

    /**
     * Gets the controller instance of the most recently loaded FXML page.
     * Useful for passing data after navigation.
     * @return The controller object, or null if no page was loaded or an error occurred.
     */
    public static Object getLastLoadedController() {
        return lastLoadedController;
    }

    /**
     * Finds navbar links within a loaded navbar node and attaches click handlers.
     * @param navBarNode The root node of the loaded navbar FXML.
     * @param stage The stage to use for navigation triggered by these links.
     */
    private static void attachNavbarHandlers(Node navBarNode, Stage stage) {
        Label homeLink = (Label) navBarNode.lookup("#homeLink");
        Label discountLink = (Label) navBarNode.lookup("#discountLink");
        Label refundLink = (Label) navBarNode.lookup("#refundLink");
        Label seatingConfigurationLink = (Label) navBarNode.lookup("#seatingConfigurationLink");
        Label viewEventsLink = (Label) navBarNode.lookup("#viewEventsLink");
        Label helpLink = (Label) navBarNode.lookup("#helpLink");
        Label makeBookingLink = (Label) navBarNode.lookup("#makeBookingLink");
        Label friendsOfLancasterLink = (Label) navBarNode.lookup("#friendsOfLancasterLink");


        if (homeLink != null) homeLink.setOnMouseClicked(e -> handleNavLinkClick(e, "Home", stage));
        if (discountLink != null) discountLink.setOnMouseClicked(e -> handleNavLinkClick(e, "Discount", stage));
        if (refundLink != null) refundLink.setOnMouseClicked(e -> handleNavLinkClick(e, "Refund", stage));
        if (seatingConfigurationLink != null) seatingConfigurationLink.setOnMouseClicked(e -> handleNavLinkClick(e, "SeatingConfiguration", stage));
        if (viewEventsLink != null) viewEventsLink.setOnMouseClicked(e -> handleNavLinkClick(e, "ViewEvents", stage));
        if (helpLink != null) helpLink.setOnMouseClicked(e -> handleNavLinkClick(e, "Help", stage));
        if (makeBookingLink != null) makeBookingLink.setOnMouseClicked(e -> handleNavLinkClick(e, "MakeBooking", stage));
        if (friendsOfLancasterLink != null) friendsOfLancasterLink.setOnMouseClicked(e -> handleNavLinkClick(e, "FriendsOfLancaster", stage));
    }

    /**
     * Generic handler for clicks on dynamically added navbar links.
     * Determines the target FXML based on link type and user role, then navigates.
     * @param event The MouseEvent from the clicked Label.
     * @param linkType A string identifying the type of link clicked (e.g., "Home", "Discount").
     * @param stage The application stage to navigate within.
     */
    private static void handleNavLinkClick(MouseEvent event, String linkType, Stage stage) {
        System.out.println(linkType + " link clicked.");
        String fxmlPath = null;
        String title = null;
        String role = SessionManager.getCurrentUserRole();
        User currentUser = SessionManager.getCurrentUser();

        switch (linkType) {
            case "Home":
                if ("Manager".equals(role)) { fxmlPath = "/scenes/ManagerHome.fxml"; title = "Manager Home"; }
                else if ("Deputy".equals(role)) { fxmlPath = "/scenes/DeputyHome.fxml"; title = "Deputy Home"; }
                else if ("Staff".equals(role)) { fxmlPath = "/scenes/StaffHome.fxml"; title = "Staff Home"; }
                else { fxmlPath = "/scenes/LoginForm.fxml"; title = "Login"; }
                break;
            case "Discount":
                if (SessionManager.isManager() || SessionManager.isDeputy()) {
                    fxmlPath = "/scenes/DiscountPage.fxml"; title = "Discount Options";
                } else { showAlert("Access Denied", "You do not have permission to access Discount Options."); }
                break;
            case "Refund":
                if (SessionManager.isManager() || SessionManager.isDeputy()) {
                    fxmlPath = "/scenes/RefundPage.fxml"; title = "Refund Options";
                } else { showAlert("Access Denied", "You do not have permission to access Refund Options."); }
                break;
            case "SeatingConfiguration":
                if (SessionManager.isManager() || SessionManager.isDeputy()) {
                    fxmlPath = "/scenes/ViewSeatingConfigurations.fxml"; title = "Seating Configurations";
                } else { showAlert("Access Denied", "You do not have permission to access Seating Configuration."); }
                break;
            case "ViewEvents":
                if (SessionManager.isManager() || SessionManager.isDeputy()) {
                    fxmlPath = "/scenes/ViewEvents.fxml"; title = "View Events";
                } else { showAlert("Access Denied", "You do not have permission to view events."); }
                break;
            case "Help":
                if (role != null) {
                    fxmlPath = "/scenes/HelpPage.fxml"; title = "Help";
                }
                break;
            case "MakeBooking":
                if (role != null) {
                    fxmlPath = "/scenes/MakeBookingPage.fxml"; title = "Make Booking";
                }
                break;
            case "FriendsOfLancaster":
                if (SessionManager.isManager() || SessionManager.isDeputy()) {
                    fxmlPath = "/scenes/FriendsOfLancasterListPage.fxml"; title = "Friends of Lancaster";
                } else { showAlert("Access Denied", "You do not have permission to view the Friends list."); }
                break;
            default:
                System.err.println("Unknown link type in handleNavLinkClick: " + linkType);
        }

        if (fxmlPath != null) {
            loadPageShared(fxmlPath, title, stage, currentUser);
        }
    }

    /**
     * Displays an alert dialog (changed to public for potential external use).
     * @param title Dialog title.
     * @param message Dialog message.
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
