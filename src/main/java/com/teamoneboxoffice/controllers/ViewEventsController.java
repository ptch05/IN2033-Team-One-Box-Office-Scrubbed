package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.Event;
import com.teamoneboxoffice.services.implementations.DAOs.EventDAO;
import com.teamoneboxoffice.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the View Events screen (ViewEvents.fxml).
 */
public class ViewEventsController implements Initializable {

    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private ListView<String> eventListView;

    private final Map<String, String> eventIdMap = new HashMap<>();
    private final ObservableList<String> allEvents = FXCollections.observableArrayList();
    private final EventDAO eventDAO = new EventDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEventsFromDatabase();

        eventListView.setOnMouseClicked(this::handleEventClick);
    }

    /**
     * Loads events from the database and populates the list view
     */
    private void loadEventsFromDatabase() {
        try {
            // Clear previous data
            allEvents.clear();
            eventIdMap.clear();

            List<Event> events = eventDAO.getAll();

            if (events.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Events",
                        "No events found in the database. Please add events first.");
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                for (Event event : events) {
                    String displayName = event.getEventName();

                    if (event.getEventDate() != null) {
                        String formattedDate = dateFormat.format(event.getEventDate());
                        displayName += " - " + formattedDate;

                        if (event.getEventTime() != null && !event.getEventTime().isEmpty()) {
                            displayName += " " + event.getEventTime();
                        }
                    }

                    if (event.getHallType() != null && !event.getHallType().isEmpty()) {
                        displayName += " (" + event.getHallType() + ")";
                    }

                    eventIdMap.put(displayName, event.getEventID());
                    allEvents.add(displayName);
                }

                FXCollections.sort(allEvents);
            }

            eventListView.setItems(allEvents);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load events from database: " + e.getMessage());
        }
    }

    /**
     * Handles clicks on the event list
     */
    private void handleEventClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click
            String selectedEventDisplay = eventListView.getSelectionModel().getSelectedItem();
            if (selectedEventDisplay != null) {
                String eventId = eventIdMap.get(selectedEventDisplay);

                if (eventId != null) {
                    NavigationUtil.loadPage("/scenes/DetailsOfEvent.fxml",
                            "Event Details: " + selectedEventDisplay, event);

                    // Pass the event ID to the details controller
                    Object loadedController = NavigationUtil.getLastLoadedController();
                    if (loadedController instanceof DetailsOfEventController) {
                        ((DetailsOfEventController) loadedController).setEventDetails(eventId);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Navigation Error",
                                "Could not pass event data to the details screen.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Selection Error",
                            "Could not find event ID for: " + selectedEventDisplay);
                }
            }
        }
    }

    /**
     * Handles the search button action.
     */
    @FXML
    void handleSearch(ActionEvent event) {
        String searchText = searchTextField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            eventListView.setItems(allEvents);
        } else {
            ObservableList<String> filteredList =
                    allEvents.stream()
                            .filter(eventName -> eventName.toLowerCase().contains(searchText))
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));

            if (filteredList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results",
                        "No events match your search criteria.");
            } else {
                eventListView.setItems(filteredList);
            }
        }
    }

    /**
     * Refreshes the event list by reloading from the database
     */
    @FXML
    void handleRefresh(ActionEvent event) {
        loadEventsFromDatabase();
        searchTextField.clear();
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}