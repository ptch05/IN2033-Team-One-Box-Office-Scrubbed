package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.util.NavigationUtil;
import com.teamoneboxoffice.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Manager Home screen (ManagerHome.fxml).
 */
public class ManagerHomeController extends BaseDashboardController {

    // Buttons specific to Manager Home (bottom bar)
    @FXML private Button viewEventsButton;
    @FXML private Button viewSeatingConfigurationsButton;
    @FXML private Button discountOptionsButton;
    @FXML private Button refundOptionsButton;
    @FXML private Button makeBookingButton;
    @FXML private Button friendsOfLancasterButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    @Override
    public void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked!");
        SessionManager.clearSession();
        NavigationUtil.loadPage("/scenes/LoginForm.fxml", "Box Office Login", event);
    }

    @FXML
    @Override
    public void handleHelp(ActionEvent event) {
        System.out.println("Help button clicked!");
        NavigationUtil.loadPage("/scenes/HelpPage.fxml", "Help", event);
    }

    @FXML
    void handleViewEvents(ActionEvent event) {
        System.out.println("View Events button clicked!");
        NavigationUtil.loadPage("/scenes/ViewEvents.fxml", "View Events", event);
    }

    @FXML
    void handleFriendsOfLancaster(ActionEvent event) {
        System.out.println("Friends Of Lancaster button clicked!");
        NavigationUtil.loadPage("/scenes/FriendsOfLancasterListPage.fxml", "Friends of Lancaster", event);
    }

    @FXML
    void handleViewSeatingConfigurations(ActionEvent event) {
        System.out.println("View Seating Configurations button clicked!");
        NavigationUtil.loadPage("/scenes/ViewSeatingConfigurations.fxml", "Seating Configurations", event);
    }

    @FXML
    void handleDiscountOptions(ActionEvent event) {
        System.out.println("Discount Options button clicked!");
        NavigationUtil.loadPage("/scenes/DiscountPage.fxml", "Discount Options", event);
    }

    @FXML
    void handleRefundOptions(ActionEvent event) {
        System.out.println("Refund Options button clicked!");
        NavigationUtil.loadPage("/scenes/RefundPage.fxml", "Refund Options", event);
    }

    @FXML
    void handleMakeBooking(ActionEvent event) {
        System.out.println("Make a Booking button clicked!");
        NavigationUtil.loadPage("/scenes/MakeBookingPage.fxml", "Make Booking", event);
    }
}
