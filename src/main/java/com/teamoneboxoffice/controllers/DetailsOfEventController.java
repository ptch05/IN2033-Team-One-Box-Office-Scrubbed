package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.Event;
import com.teamoneboxoffice.services.implementations.DAOs.EventDAO;
import com.teamoneboxoffice.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.time.format.DateTimeFormatter;


/**
 * Controller for the Event Details screen (DetailsOfEvent.fxml).
 * Shows event details fetched from the database and allows generating a report.
 */
public class DetailsOfEventController implements Initializable {

    @FXML private TextField idTextField;
    @FXML private TextField nameTextField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TextField hallTextField;
    @FXML private TextField ticketPriceTextField;
    @FXML private TextField availableTicketsTextField;
    @FXML private TextField eventTypeTextField;
    @FXML private TextField eventTimeTextField;
    @FXML private Button generateReportButton;
    @FXML private Button closeButton;

    private String eventId;
    private Event currentEvent = null;
    private final EventDAO eventDAO = new EventDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set fields to read-only initially
        setFieldsReadOnly(true);
        generateReportButton.setDisable(true);
    }

    /**
     * Sets the event ID for this details page and triggers loading data from the database.
     * This should be called AFTER the FXML is loaded.
     * @param eventId The ID of the event to display
     */
    public void setEventDetails(String eventId) {
        this.eventId = eventId;
        if (eventId != null && !eventId.isEmpty()) {
            loadEventDetails();
        } else {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "No event ID provided.");
        }
    }

    /**
     * Populates the fields with event details fetched from the database using EventDAO.
     */
    private void loadEventDetails() {
        try {
            clearFields();

            currentEvent = eventDAO.getById(eventId);

            if (currentEvent != null) {
                idTextField.setText(currentEvent.getEventID());
                nameTextField.setText(currentEvent.getEventName());
                hallTextField.setText(currentEvent.getHallType());
                ticketPriceTextField.setText(String.format("£%.2f", currentEvent.getEventPrice()));

                int soldTickets = currentEvent.getTicketNumbers();
                int capacity = getVenueCapacity(currentEvent.getHallType());
                int availableTickets = capacity - soldTickets;

                availableTicketsTextField.setText(String.valueOf(availableTickets));

                if (eventTypeTextField != null) {
                    eventTypeTextField.setText(currentEvent.getEventType());
                }

                if (eventTimeTextField != null && currentEvent.getEventTime() != null) {
                    eventTimeTextField.setText(currentEvent.getEventTime());
                }

                if (currentEvent.getEventDate() != null) {
                    LocalDate eventLocalDate = new java.sql.Date(currentEvent.getEventDate().getTime()).toLocalDate();
                    fromDatePicker.setValue(eventLocalDate);
                }

                generateReportButton.setDisable(false);

                System.out.println("Successfully loaded event: " + currentEvent.getEventName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Data Error", "Event with ID " + eventId + " not found in database.");
                generateReportButton.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Loading Event",
                    "An error occurred while loading event details: " + e.getMessage());
            generateReportButton.setDisable(true);
        }
    }

    /**
     * Returns the approximate venue capacity based on hall type
     */
    private int getVenueCapacity(String hallType) {
        if (hallType == null) return 0;

        switch (hallType.toLowerCase()) {
            case "large hall":
                return 394;
            case "small hall":
                return 95;
            default:
                return 100;
        }
    }

    /**
     * Sets all detail fields to read-only or editable
     */
    private void setFieldsReadOnly(boolean readOnly) {
        idTextField.setEditable(!readOnly);
        nameTextField.setEditable(!readOnly);
        hallTextField.setEditable(!readOnly);
        ticketPriceTextField.setEditable(!readOnly);
        availableTicketsTextField.setEditable(!readOnly);
        fromDatePicker.setEditable(!readOnly);
        fromDatePicker.setDisable(readOnly);
        toDatePicker.setEditable(!readOnly);
        toDatePicker.setDisable(readOnly);

        if (eventTypeTextField != null) {
            eventTypeTextField.setEditable(!readOnly);
        }

        if (eventTimeTextField != null) {
            eventTimeTextField.setEditable(!readOnly);
        }
    }

    /**
     * Clears all detail fields
     */
    private void clearFields() {
        idTextField.clear();
        nameTextField.clear();
        hallTextField.clear();
        ticketPriceTextField.clear();
        availableTicketsTextField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);

        if (eventTypeTextField != null) {
            eventTypeTextField.clear();
        }

        if (eventTimeTextField != null) {
            eventTimeTextField.clear();
        }

        currentEvent = null;
    }

    /**
     * Handles the Generate Report button click. Generates a CSV file with event details
     * and prompts the user to save it.
     */
    @FXML
    void handleGenerateReport(ActionEvent event) {
        if (currentEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Cannot generate report. No event data available.");
            return;
        }

        System.out.println("Generating CSV report for event: " + currentEvent.getEventName());

        // 1. Prepare CSV Content
        String csvContent = generateCsvContent(currentEvent);
        if (csvContent == null) {
            showAlert(Alert.AlertType.ERROR, "Report Error", "Failed to generate CSV content.");
            return;
        }

        // 2. Configure FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Event Report");

        String suggestedFileName = currentEvent.getEventID() + "_" +
                currentEvent.getEventName().replaceAll("[^a-zA-Z0-9.-]", "_") + // Sanitize name
                "_report.csv";
        fileChooser.setInitialFileName(suggestedFileName);

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // 3. Show Save File Dialog
        Window stage = generateReportButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // 4. Write to File if user selected a location
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(csvContent);
                System.out.println("CSV report saved successfully to: " + file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Report Saved", "Event report saved successfully as:\n" + file.getName());
            } catch (IOException e) {
                System.err.println("Error writing CSV file: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "File Save Error", "Could not save the report to the selected file.\nError: " + e.getMessage());
            }
        } else {
            System.out.println("User cancelled the save operation.");
        }
    }

    /**
     * Generates the CSV content string for the given event.
     * @param event The Event object to report on.
     * @return A String containing the CSV data, or null on error.
     */
    private String generateCsvContent(Event event) {
        if (event == null) return null;

        StringBuilder csv = new StringBuilder();

        csv.append("Event ID,Event Name,Event Type,Hall Type,Date,Time,Ticket Price,Tickets Sold,Capacity,Available Tickets,Ticket Revenue\n");

        try {
            String eventId = event.getEventID() != null ? event.getEventID() : "";
            String eventName = event.getEventName() != null ? event.getEventName().replace(",", ";") : ""; // Replace commas to avoid CSV issues
            String eventType = event.getEventType() != null ? event.getEventType() : "";
            String hallType = event.getHallType() != null ? event.getHallType() : "";
            String eventDateStr = "";
            if (event.getEventDate() != null) {
                // Convert java.util.Date to LocalDate and format
                LocalDate localDate = new java.sql.Date(event.getEventDate().getTime()).toLocalDate();
                eventDateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // YYYY-MM-DD format
            }
            String eventTime = event.getEventTime() != null ? event.getEventTime() : "";
            String ticketPrice = String.format("%.2f", event.getEventPrice()); // Format price
            int ticketsSold = event.getTicketNumbers();
            int capacity = getVenueCapacity(hallType);
            int availableTickets = capacity - ticketsSold;
            String ticketRevenue = String.format("%.2f", event.getTicketRevenue()); // Format revenue

            csv.append(escapeCsvField(eventId)).append(",");
            csv.append(escapeCsvField(eventName)).append(",");
            csv.append(escapeCsvField(eventType)).append(",");
            csv.append(escapeCsvField(hallType)).append(",");
            csv.append(escapeCsvField(eventDateStr)).append(",");
            csv.append(escapeCsvField(eventTime)).append(",");
            csv.append(ticketPrice).append(","); // Numeric, no need to escape usually
            csv.append(ticketsSold).append(",");
            csv.append(capacity).append(",");
            csv.append(availableTickets).append(",");
            csv.append(ticketRevenue).append("\n"); // Numeric

            return csv.toString();

        } catch (Exception e) {
            System.err.println("Error formatting CSV data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Basic CSV field escaping (handles quotes and commas).
     * If a field contains a comma, newline, or double quote, it wraps the field
     * in double quotes and doubles any existing double quotes within the field.
     * @param field The string data for the field.
     * @return The properly escaped CSV field.
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            String escapedField = field.replace("\"", "\"\"");
            return "\"" + escapedField + "\"";
        } else {
            return field;
        }
    }


    /**
     * Handles the Back/Close button click
     */
    @FXML
    void handleClose(ActionEvent event) {
        NavigationUtil.loadPage("/scenes/ViewEvents.fxml", "View Events", event);
    }

    /**
     * Displays an alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}