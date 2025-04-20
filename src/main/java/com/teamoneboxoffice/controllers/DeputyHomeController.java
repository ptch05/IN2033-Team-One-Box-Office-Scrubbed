package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.util.NavigationUtil;
import com.teamoneboxoffice.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Deputy Home screen (DeputyHome.fxml).
 * Extends BaseDashboardController for common elements.
 */
public class DeputyHomeController extends BaseDashboardController {

    @FXML private Button viewSeatingConfigurationsButton;
    @FXML private Button discountOptionsButton;
    @FXML private Button refundOptionsButton;
    @FXML private Button makeBookingButton;
    @FXML private Button friendsOfLancasterButton;

    /**
     * Initializes the controller. Calls the base class initializer.
     * User information will be populated later via setCurrentUser.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle); // Call base initialize
    }

    /**
     * Updates the user interface elements specific to the Deputy role,
     * called after the base user info is updated.
     */
    @Override
    protected void updateUserInfo() {
        super.updateUserInfo();
    }

    /**
     * Handles the Logout button action. Clears the session and navigates to Login.
     */
    @FXML
    @Override
    public void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked!");
        SessionManager.clearSession();
        NavigationUtil.loadPage("/scenes/LoginForm.fxml", "Box Office Login", event);
    }

    /**
     * Handles the FriendsOfLancaster button action.
     */
    @FXML
    void handleFriendsOfLancaster(ActionEvent event) {
        System.out.println("Friends Of Lancaster button clicked!");
        NavigationUtil.loadPage("/scenes/FriendsOfLancasterListPage.fxml", "Friends of Lancaster", event);
    }

    /**
     * Handles the Help button action. Navigates to the Help page.
     */
    @FXML
    @Override
    public void handleHelp(ActionEvent event) {
        System.out.println("Help button clicked!");
        NavigationUtil.loadPage("/scenes/HelpPage.fxml", "Help", event);
    }

    /**
     * Handles the View Seating Configurations button action.
     */
    @FXML
    void handleViewSeatingConfigurations(ActionEvent event) {
        System.out.println("View Seating Configurations button clicked!");
        NavigationUtil.loadPage("/scenes/ViewSeatingConfigurations.fxml", "Seating Configurations", event);
    }

    /**
     * Handles the Discount Options button action. Navigates to the Discount page.
     */
    @FXML
    void handleDiscountOptions(ActionEvent event) {
        System.out.println("Discount Options button clicked!");
        NavigationUtil.loadPage("/scenes/DiscountPage.fxml", "Discount Options", event);
    }

    /**
     * Handles the Refund Options button action. Navigates to the Refund page.
     */
    @FXML
    void handleRefundOptions(ActionEvent event) {
        System.out.println("Refund Options button clicked!");
        NavigationUtil.loadPage("/scenes/RefundPage.fxml", "Refund Options", event);
    }

    /**
     * Handles the Make a Booking button action. Navigates to the Booking page.
     */
    @FXML
    void handleMakeBooking(ActionEvent event) {
        System.out.println("Make a Booking button clicked!");
        NavigationUtil.loadPage("/scenes/MakeBookingPage.fxml", "Make Booking", event);
    }
}
