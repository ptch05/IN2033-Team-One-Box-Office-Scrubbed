package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.services.implementations.DAOs.TicketDAO;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Refund screen (RefundPage.fxml).
 * Handles input, confirmation, and deletion of tickets for refunds.
 */
public class RefundPageController implements Initializable {

    @FXML private TextField ticketNumberTextField;
    @FXML private ComboBox<String> reasonComboBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button processButton;

    // --- Database Access ---
    private Database database;
    private TicketDAO ticketDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            database = new Database();
            ticketDAO = new TicketDAO();
        } catch (Exception e) {
            // Handle initialization errors (e.g., DB connection failed)
            showAlert(AlertType.ERROR, "Database Error", "Failed to initialize database connection: " + e.getMessage());
            // Disable functionality if DB is unavailable
            processButton.setDisable(true);
            ticketNumberTextField.setDisable(true);
            reasonComboBox.setDisable(true);
            descriptionTextArea.setDisable(true);
        }


        reasonComboBox.setItems(FXCollections.observableArrayList(
                "Event Cancelled",
                "Incorrect Purchase",
                "Unable to Attend",
                "Duplicate Purchase",
                "Technical Issue During Purchase",
                "Show Quality/Dissatisfaction",
                "Other (See Description)"
        ));
        reasonComboBox.setPromptText("Select a reason...");
    }

    /**
     * Handles the Process Refund button action.
     * Validates input, shows confirmation, and deletes the ticket from the DB if confirmed.
     */
    @FXML
    void handleProcessRefund(ActionEvent event) {
        String ticketNumber = ticketNumberTextField.getText().trim();
        String reason = reasonComboBox.getValue();
        String description = descriptionTextArea.getText().trim();

        // --- Input Validation ---
        if (ticketNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Ticket Number must be provided.");
            ticketNumberTextField.requestFocus();
            return;
        }
        if (reason == null || reason.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please select a Reason.");
            reasonComboBox.requestFocus();
            return;
        }
        if (description.isEmpty() && "Other (See Description)".equals(reason)) {
            showAlert(AlertType.ERROR, "Input Error", "Description must be provided when 'Other' reason is selected.");
            descriptionTextArea.requestFocus();
            return;
        } else if (description.isEmpty()) {
            description = "N/A";
        }
        // --- End Validation ---

        if (ticketDAO == null) {
            showAlert(AlertType.ERROR, "System Error", "Database service is unavailable. Cannot process refund.");
            return;
        }

        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Refund");
        confirmationAlert.setHeaderText("Process Refund & Delete Ticket: " + ticketNumber + "?");
        confirmationAlert.setContentText("Reason: " + reason + "\nThis action will permanently delete the ticket record.\nAre you sure you want to proceed?");

        ButtonType buttonTypeYes = new ButtonType("Yes, Process Refund & Delete");
        ButtonType buttonTypeNo = new ButtonType("No, Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        // --- Process if Confirmed ---
        if (result.isPresent() && result.get() == buttonTypeYes){
            System.out.println("Processing Refund & Deleting Ticket (Confirmed):");
            System.out.println("Ticket Number: " + ticketNumber);
            System.out.println("Reason: " + reason);
            System.out.println("Description: " + description);

            // --- Attempt to Delete Ticket from Database ---
            try {
                boolean deleted = ticketDAO.delete(ticketNumber);

                if (deleted) {
                    showAlert(AlertType.INFORMATION, "Success", "Refund processed and ticket '" + ticketNumber + "' deleted successfully.");

                    ticketNumberTextField.clear();
                    reasonComboBox.getSelectionModel().clearSelection();
                    reasonComboBox.setPromptText("Select a reason...");
                    descriptionTextArea.clear();
                } else {
                    showAlert(AlertType.ERROR, "Deletion Failed", "Ticket '" + ticketNumber + "' not found in the database. Please verify the ticket number.");
                }

            } catch (Exception e) {
                // --- Database Error During Deletion ---
                System.err.println("Error deleting ticket during refund: " + e.getMessage());
                showAlert(AlertType.ERROR, "Database Error", "An error occurred while trying to delete the ticket: " + e.getMessage());
            }

        } else {
            // User clicked No or closed the dialog
            System.out.println("Refund cancelled by user for ticket: " + ticketNumber);
            showAlert(AlertType.INFORMATION, "Cancelled", "Refund process cancelled.");
        }
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
