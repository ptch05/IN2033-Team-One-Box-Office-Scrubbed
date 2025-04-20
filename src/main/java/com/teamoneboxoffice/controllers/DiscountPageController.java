package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.services.implementations.DAOs.DiscountDAO;
import com.teamoneboxoffice.entities.Discount;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class DiscountPageController implements Initializable {
    @FXML private ComboBox<String> reasonComboBox;
    @FXML private TextField percentageDisplayField;
    @FXML private TextField generatedCodeField;
    @FXML private Button generateCodeButton;

    private final Map<String, Integer> discountReasons = new HashMap<>();
    private Database database;
    private DiscountDAO discountDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            database = new Database();
            discountDAO = new DiscountDAO(database);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to initialize database connection: " + e.getMessage());
            // Disable functionality if DB is unavailable
            generateCodeButton.setDisable(true);
            reasonComboBox.setDisable(true);
        }
        discountReasons.put("NHS Personnel", 15);
        discountReasons.put("Military Personnel", 15);
        discountReasons.put("Disabled Guest + Carer", 20);
        discountReasons.put("Local Resident", 10);
        discountReasons.put("Student", 10);

        reasonComboBox.setItems(FXCollections.observableArrayList(discountReasons.keySet()));

        reasonComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int percentage = discountReasons.getOrDefault(newVal, 0);
                percentageDisplayField.setText(String.valueOf(percentage));
                if (database != null && discountDAO != null) {
                    generateCodeButton.setDisable(false);
                }
                generatedCodeField.clear();
            } else {
                percentageDisplayField.clear();
                generatedCodeField.clear();
                generateCodeButton.setDisable(true);
            }
        });

        percentageDisplayField.clear();
        generatedCodeField.clear();
        generateCodeButton.setDisable(true);
    }

    @FXML
    void handleGenerateCode() {
        if (discountDAO == null) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Database service is unavailable.");
            return;
        }

        String selectedReason = reasonComboBox.getValue();
        if (selectedReason == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a discount reason.");
            return;
        }

        int percentage = discountReasons.getOrDefault(selectedReason, 0);
        if (percentage <= 0) {
            showAlert(Alert.AlertType.ERROR, "Configuration Error", "Invalid percentage for selected reason.");
            return;
        }

        String reasonCode = selectedReason.substring(0, Math.min(selectedReason.length(), 3)).toUpperCase();
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String discountCode = reasonCode + "-" + percentage + "-" + uniquePart;

        Discount newDiscount = new Discount(discountCode, percentage, selectedReason);

        try {
            discountDAO.addDiscount(newDiscount);
            generatedCodeField.setText(discountCode);

            showAlert(Alert.AlertType.INFORMATION, "Discount Code Generated & Saved",
                    "Code generated and saved to database: " + discountCode +
                            "\nYou can now copy it from the 'Generated Code' field.");

        } catch (SQLException e) {
            System.err.println("Failed to save discount code: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save discount code: " + e.getMessage());
            generatedCodeField.clear();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
