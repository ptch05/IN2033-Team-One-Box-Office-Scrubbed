package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.User;
import com.teamoneboxoffice.services.implementations.DAOs.UserDAO;
import com.teamoneboxoffice.util.NavigationUtil;
import com.teamoneboxoffice.util.SessionManager; // Import SessionManager
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
// Stage import no longer needed here

import java.net.URL;

/**
 * Controller for the login screen (LoginForm.fxml).
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Error", "Username and password cannot be empty");
            return;
        }

        User authenticatedUser = userDAO.authenticate(username, password);

        if (authenticatedUser != null && authenticatedUser.isActive()) {
            // Store the full user object in the session
            SessionManager.setCurrentUser(authenticatedUser); // <<<--- STORE USER OBJECT
            System.out.println("Login successful for user: " + username);
            // Pass the button as source node and the user object
            navigateToRoleBasedScreen(authenticatedUser.getRole(), loginButton, authenticatedUser);
        } else {
            SessionManager.clearSession();
            System.out.println("Login failed. Invalid credentials or inactive account.");
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username/password or account inactive.");
            passwordField.clear();
        }
    }

    /**
     * Navigates to the correct home screen, passing the user object.
     */
    private void navigateToRoleBasedScreen(String role, Node sourceNode, User userToPass) { // Added User parameter
        String fxmlFile;
        String title;

        if ("Manager".equalsIgnoreCase(role)) {
            fxmlFile = "/scenes/ManagerHome.fxml";
            title = "Manager Home";
        } else if ("Deputy".equalsIgnoreCase(role)) {
            fxmlFile = "/scenes/DeputyHome.fxml";
            title = "Deputy Home";
        } else if("Staff".equalsIgnoreCase(role)) {
            fxmlFile = "/scenes/StaffHome.fxml";
            title = "Staff Home";
        } else {
            showAlert(AlertType.ERROR, "Navigation Error", "Unknown role: " + role);
            return;
        }
        NavigationUtil.loadPage(fxmlFile, title, sourceNode, userToPass); // <<<--- PASS USER
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        passwordField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (loginButton != null) {
                    loginButton.fire();
                } else {
                    System.err.println("Login button is null, cannot fire event on Enter.");
                }
            }
        });
        usernameField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
        if (loginButton != null) {
            loginButton.setDefaultButton(true);
        }
    }

}
