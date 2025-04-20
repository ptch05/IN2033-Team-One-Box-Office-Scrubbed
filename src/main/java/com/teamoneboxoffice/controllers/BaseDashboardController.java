package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * controller for dashboard/home screens (Manager, Deputy, Staff).
 */
public abstract class BaseDashboardController implements Initializable {

    @FXML protected Label nameLabel;
    @FXML protected Label emailLabel;
    @FXML protected Button logoutButton;
    @FXML protected Button helpButton;

    protected User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("BaseDashboardController initialized for: " + this.getClass().getSimpleName());
        clearUserInfo();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }

    protected void updateUserInfo() {
        if (currentUser != null) {
            if (nameLabel != null) {
                nameLabel.setText(currentUser.getUserName());
            }
            if (emailLabel != null) {
                emailLabel.setText("👤 " + currentUser.getRole());
            }
        } else {
            clearUserInfo();
        }
    }

    protected void clearUserInfo() {
        if (nameLabel != null) nameLabel.setText("...");
        if (emailLabel != null) emailLabel.setText("");
    }

    @FXML
    public abstract void handleLogout(ActionEvent event);

    @FXML
    public abstract void handleHelp(ActionEvent event);

    public User getCurrentUser() {
        return currentUser;
    }
}
