package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.util.NavigationUtil;
import com.teamoneboxoffice.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Staff Home screen (StaffHome.fxml).
 * Extends BaseDashboardController for common elements.
 */
public class StaffHomeController extends BaseDashboardController {

    @FXML private Button makeBookingButton;
    @FXML private Button discountOptionsButton;

    /**
     * Initializes the controller. Calls the base class initializer.
     * User information will be populated later via setCurrentUser.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle); // Call base initialize
    }

    /**
     * Updates the user interface elements specific to the Staff role,
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
     * Handles the Help button action. Navigates to the Help page.
     */
    @FXML
    @Override
    public void handleHelp(ActionEvent event) {
        System.out.println("Help button clicked!");
        NavigationUtil.loadPage("/scenes/HelpPage.fxml", "Help", event);
    }

    /**
     * Handles the Make a Booking button action. Navigates to the Booking page.
     */
    @FXML
    void handleMakeBooking(ActionEvent event) {
        System.out.println("Make a Booking button clicked!");
        NavigationUtil.loadPage("/scenes/MakeBookingPage.fxml", "Make Booking", event);
    }

    /**
     * Handles the Discount Options button action. Navigates to the Discount page.
     */
    @FXML
    void handleDiscountOptions(ActionEvent event) {
        System.out.println("Discount Options button clicked!");
        NavigationUtil.loadPage("/scenes/DiscountPage.fxml", "Discount Options", event);
    }
}
