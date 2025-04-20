package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.*;
import com.teamoneboxoffice.services.implementations.DAOs.DiscountDAO;
import com.teamoneboxoffice.services.implementations.DAOs.EventDAO;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;
import com.teamoneboxoffice.util.NavigationUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MakeBookingPageController implements Initializable {
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private RadioButton wheelchairYesRadio;
    @FXML private RadioButton wheelchairNoRadio;
    @FXML private ToggleGroup wheelchairGroup;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private GridPane seatingPlanGrid;
    @FXML private ListView<String> selectedSeatsListView;
    @FXML private Button bookButton;
    @FXML private Label timerLabel;
    private Timeline countdownTimeline;
    private int timeSeconds = 600;
    private List<ToggleButton> selectedSeatButtons = new ArrayList<>();
    private ObservableList<String> selectedSeatIds = FXCollections.observableArrayList();
    private List<String> bookedSeats = new ArrayList<>();
    private int maxSeatsToSelect = 1;
    private SeatingConfig currentSeatingConfig;
    private Map<String, ToggleButton> seatButtonsMap = new HashMap<>();
    @FXML private TextField discountCodeField;
    @FXML private Label discountAppliedLabel;
    private Discount appliedDiscount = null;
    private DiscountDAO discountDAO;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. Sets up event handlers, combo boxes,
     * the seating plan, and starts the booking timer.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Database db = new Database();
            discountDAO = new DiscountDAO(db);
        } catch (Exception e) {
            System.err.println("Failed to initialize Database or DiscountDAO: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "System Error", "Database services are unavailable.");
            if (bookButton != null) bookButton.setDisable(true);
        }
        initializeEventHandlers();
        initializeComboBoxes();
        initializeSeatingPlan();
        startTimer();
    }

    /**
     * Sets up listeners for UI controls like the quantity spinner, wheelchair radio buttons,
     * combo boxes, and the selected seats list view.
     */
    private void initializeEventHandlers() {
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            maxSeatsToSelect = newValue;
            clearSeatSelection();
            updateBookButtonState();
        });

        wheelchairGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            clearSeatSelection();
        });

        selectedSeatsListView.setItems(selectedSeatIds);

        eventComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSeatingPlan();
        });
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSeatingPlan();
        });
        timeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSeatingPlan();
        });

        bookButton.setDisable(true);
    }



    private void initializeComboBoxes() {
        try {
            EventDAO eventDAO = new EventDAO();
            List<Event> events = eventDAO.getAll();

            if (events.isEmpty()) {
                setupDefaultEvents();
            }

            eventComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Event event) {
                    return event != null ? formatEventDisplayName(event) : "";
                }

                @Override
                public Event fromString(String string) {
                    return null;
                }
            });

            eventComboBox.setItems(FXCollections.observableArrayList(events));

            if (!events.isEmpty()) {
                eventComboBox.setValue(events.get(0));
            } else if (!eventComboBox.getItems().isEmpty()){
                eventComboBox.setValue(eventComboBox.getItems().get(0));
            }

            if (timeComboBox.getItems().isEmpty()) {
                timeComboBox.setItems(FXCollections.observableArrayList("14:00", "19:30", "20:00"));
                timeComboBox.setValue("19:30");
            }


        } catch (Exception e) {
            System.err.println("Error initializing ComboBoxes: " + e.getMessage());
            e.printStackTrace();
            setupDefaultEvents();
        }
    }

    private void setupDefaultEvents() {
        List<Event> defaultEvents = Arrays.asList(
                new Event(
                        "EVT001", "Event A", "LivePerformance", 50.00f, "Large Hall",
                        new java.sql.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000), "19:30"
                ),
                new Event(
                        "EVT002", "Event B", "Conference", 75.50f, "Small Hall",
                        new java.sql.Date(System.currentTimeMillis() + 45L * 24 * 60 * 60 * 1000), "14:00"
                ),
                new Event(
                        "EVT003", "Event C", "Concert", 65.25f, "Large Hall",
                        new java.sql.Date(System.currentTimeMillis() + 60L * 24 * 60 * 60 * 1000), "20:00"
                )
        );

        eventComboBox.setItems(FXCollections.observableArrayList(defaultEvents));
        if (!defaultEvents.isEmpty()) {
            eventComboBox.setValue(defaultEvents.get(0));
        }

        if (timeComboBox.getItems().isEmpty()) {
            timeComboBox.setItems(FXCollections.observableArrayList("14:00", "19:30", "20:00"));
            timeComboBox.setValue("19:30");
        }
    }

    private String formatEventDisplayName(Event event) {
        return event.getEventName() + " (" + event.getHallType() + ")";
    }

    private void initializeSeatingPlan() {
        SpinnerValueFactory<Integer> quantityFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        quantitySpinner.setValueFactory(quantityFactory);
        datePicker.setValue(LocalDate.now());
        updateSeatingPlan();
    }

    private void startTimer() {
        stopTimer();
        timeSeconds = 600;
        updateTimerLabel();
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            updateTimerLabel();
            if (timeSeconds <= 0) {
                stopTimer();
                showAlert(Alert.AlertType.WARNING, "Time Expired",
                        "Your 10-minute reservation period has expired. Please select seats again.");
                clearSeatSelection();
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void stopTimer() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
        }
    }

    private void updateTimerLabel() {
        if (timerLabel != null) {
            int minutes = timeSeconds / 60;
            int seconds = timeSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    /**
     * Updates the seating plan display based on the selected event, date, and time.
     * Clears the existing plan, fetches booked seats, determines the hall layout,
     * and loads the corresponding layout method. Does NOT reset the applied discount.
     */
    private void updateSeatingPlan() {
        System.out.println("[updateSeatingPlan] START. Current appliedDiscount: " + (this.appliedDiscount != null ? this.appliedDiscount.getCode() : "null")); // LOGGING

        Event selectedEvent = eventComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeComboBox.getValue();

        if (selectedEvent == null || selectedDate == null || selectedTime == null || selectedTime.isEmpty()) {
            seatingPlanGrid.getChildren().clear();
            clearSeatSelection();
            return;
        }

        clearSeatSelection();

        seatingPlanGrid.getChildren().clear();
        seatingPlanGrid.getColumnConstraints().clear();
        seatingPlanGrid.getRowConstraints().clear();
        seatingPlanGrid.setAlignment(Pos.CENTER);
        seatButtonsMap.clear();

        fetchBookedSeatsFromDatabase(selectedEvent, selectedDate, selectedTime);

        currentSeatingConfig = new SeatingConfig(
                selectedEvent.getHallType().toLowerCase().contains("large") ? 1 : 2,
                selectedEvent.getEventType()
        );

        if (selectedEvent.getHallType().toLowerCase().contains("large")) {
            loadLargeHallLayout();
        } else if (selectedEvent.getHallType().toLowerCase().contains("small")) {
            loadSmallHallLayout();
        } else {
            System.err.println("Unknown Hall Type: " + selectedEvent.getHallType());
        }
        System.out.println("[updateSeatingPlan] END. Current appliedDiscount: " + (this.appliedDiscount != null ? this.appliedDiscount.getCode() : "null")); // LOGGING
    }


    /**
     * Fetches the IDs of seats already booked for the specified event, date, and time
     * from the database. Populates the `bookedSeats` list. Includes fallback data for testing.
     * @param event The selected event.
     * @param eventDate The selected date.
     * @param eventTime The selected time.
     */
    private void fetchBookedSeatsFromDatabase(Event event, LocalDate eventDate, String eventTime) {
        Database db = new Database();
        Set<String> bookedSeatSet = new HashSet<>();
        String sql = "SELECT bs.Seat_ID " +
                "FROM Booked_Seats bs " +
                "JOIN Ticket t ON bs.Ticket_ID = t.Ticket_ID " +
                "JOIN Event e ON t.Event_ID = e.Event_ID " +
                "WHERE e.Event_ID = ? " +
                "AND e.Event_Date = ? " +
                "AND e.Event_Time = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getEventID());
            stmt.setDate(2, java.sql.Date.valueOf(eventDate));
            stmt.setString(3, eventTime);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String seatId = rs.getString("Seat_ID");
                    if (seatId != null && !seatId.trim().isEmpty()) {
                        bookedSeatSet.add(seatId.trim());
                    }
                }
            }
            System.out.println("Fetched " + bookedSeatSet.size() + " booked seats for " + event.getEventName() + " on " + eventDate + " at " + eventTime);

        } catch (SQLException e) {
            System.err.println("Database error fetching booked seats: " + e.getMessage());
            e.printStackTrace();
            bookedSeatSet.addAll(Arrays.asList(
                    "A5", "C8", "F4", "L1", "M3", "N2",
                    "CC4", "P6", "A1",
                    "AA34", "BB25", "CC7"
            ));
            System.out.println("Using fallback booked seats due to DB error.");
        } finally {
            bookedSeats = new ArrayList<>(bookedSeatSet);
        }
    }

    /**
     * Handles the event when the "Proceed to Customer Info" button is clicked.
     * @param event The action event.
     */
    @FXML
    void handleBookButton(ActionEvent event) {
        System.out.println("[handleBookButton] START. Current appliedDiscount: " + (this.appliedDiscount != null ? this.appliedDiscount.getCode() : "null")); // LOGGING
        validateAndProcessBooking();
    }

    /**
     * Validates the current seat selection against the required quantity.
     * Checks the discount code field and validates it if present.
     * If valid, stops the timer and navigates to the customer booking info page.
     * Shows error alerts if validation fails.
     */
    private void validateAndProcessBooking() {
        if (selectedSeatButtons.size() != maxSeatsToSelect) {
            showAlert(Alert.AlertType.ERROR, "Selection Error",
                    "Please select exactly " + maxSeatsToSelect + " seat(s). You have selected " + selectedSeatButtons.size() + ".");
            return;
        }

        int finalDiscountPercentage = 0;
        String enteredCode = (discountCodeField != null) ? discountCodeField.getText() : null;

        if (enteredCode != null && !enteredCode.trim().isEmpty()) {
            if (discountDAO == null) {
                showAlert(Alert.AlertType.ERROR, "System Error", "Discount service is unavailable. Cannot validate code.");
            } else {
                try {
                    Discount discount = discountDAO.getDiscountByCode(enteredCode.trim());
                    if (discount != null) {
                        finalDiscountPercentage = discount.getPercentage();
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Invalid Discount", "The entered discount code is not valid. Proceeding without discount.");
                    }
                } catch (SQLException e) {
                    System.err.println("[validateAndProcessBooking] Database error checking discount code: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Could not validate discount code. Proceeding without discount.");
                }
            }
        }

        int remainingTime = timeSeconds;
        stopTimer();

        List<String> finalSelectedSeatIds = selectedSeatButtons.stream()
                .map(button -> ((SeatData) button.getUserData()).getSeatId())
                .collect(Collectors.toList());

        Event selectedEvent = eventComboBox.getValue();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", "Could not determine the selected event. Please try again.");
            startTimer();
            return;
        }

        navigateToCustomerBookingPage(finalSelectedSeatIds, remainingTime, selectedEvent, finalDiscountPercentage);
    }

    /**
     * Navigates the user to the Customer Booking Information page.
     * Passes necessary booking details like selected seats, remaining time, event info, discount,
     * and the calculated base total price.
     * @param selectedSeats The list of confirmed seat IDs being booked.
     * @param remainingTime The time left on the reservation timer in seconds.
     * @param selectedEvent The event being booked.
     * @param discountPercentage The percentage discount applied, if any.
     */
    private void navigateToCustomerBookingPage(List<String> selectedSeats, int remainingTime, Event selectedEvent, int discountPercentage) {
        String fxmlPath = "/scenes/CustomerBookingInfoPage.fxml";
        String title = "Customer Information";
        System.out.println("Navigating to Customer Info Page..."); // LOGGING
        System.out.println("Discount Percentage being passed: " + discountPercentage); // LOGGING

        try {
            NavigationUtil.loadPage(fxmlPath, title, seatingPlanGrid, null);

            Object loadedController = NavigationUtil.getLastLoadedController();
            if (loadedController instanceof CustomerBookingInfoPageController) {
                System.out.println("Found CustomerBookingInfoPageController instance."); // LOGGING
                float baseTotalPrice = selectedSeatButtons.stream()
                        .map(button -> ((SeatData) button.getUserData()).getPrice())
                        .reduce(0f, Float::sum);
                System.out.println("Calculated baseTotalPrice: " + baseTotalPrice); // LOGGING

                System.out.println("Calling setBookingDetails with discount: " + discountPercentage); // LOGGING
                ((CustomerBookingInfoPageController) loadedController).setBookingDetails(
                        selectedSeats,
                        maxSeatsToSelect,
                        wheelchairYesRadio.isSelected(),
                        remainingTime,
                        selectedEvent.getEventID(),
                        discountPercentage, // Pass the applied discount percentage
                        baseTotalPrice
                );
                System.out.println("Finished calling setBookingDetails."); // LOGGING
            } else {
                System.err.println("Navigation Error: Loaded controller is NOT an instance of CustomerBookingInfoPageController."); // LOGGING
                System.err.println("Actual controller type: " + (loadedController != null ? loadedController.getClass().getName() : "null")); // LOGGING
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not pass booking details to the next page.");
                startTimer();
            }
        } catch (Exception e) {
            System.err.println("Error loading Customer Booking Info Page: " + e.getMessage()); // LOGGING
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the customer information page.");
            startTimer();
        }
    }


    /**
     * Updates the enabled/disabled state of the "Proceed" button based on whether
     * the correct number of seats has been selected.
     */
    private void updateBookButtonState() {
        bookButton.setDisable(selectedSeatButtons.size() != maxSeatsToSelect);
    }

    /**
     * Handles the event when a seat toggle button is clicked. Manages seat selection,
     * deselection, wheelchair logic (including adjacent seat blocking), and updates UI state.
     * @param event The action event from clicking a seat button.
     */
    @FXML
    private void handleSeatSelection(ActionEvent event) {
        ToggleButton selectedButton = (ToggleButton) event.getSource();
        SeatData seatData = (SeatData) selectedButton.getUserData();
        String selectedSeatId = seatData.getSeatId();
        boolean wantsWheelchair = wheelchairYesRadio.isSelected();
        boolean seatIsWheelchairAccessible = isWheelchairAccessibleSeat(selectedSeatId);

        if (selectedButton.isSelected()) {

            if (selectedSeatButtons.size() >= maxSeatsToSelect) {
                selectedButton.setSelected(false);
                showAlert(Alert.AlertType.WARNING, "Selection Limit", "You can only select up to " + maxSeatsToSelect + " seat(s).");
                return;
            }

            if (wantsWheelchair && seatIsWheelchairAccessible) {
                String adjacentSeatId = findAdjacentSeat(selectedSeatId);
                ToggleButton adjacentButton = (adjacentSeatId != null) ? seatButtonsMap.get(adjacentSeatId) : null;

                if (adjacentButton == null || adjacentButton.isDisabled() || adjacentButton.isSelected()) {
                    selectedButton.setSelected(false);
                    showAlert(Alert.AlertType.WARNING, "Selection Error", "The required adjacent seat for wheelchair access (" + (adjacentSeatId != null ? adjacentSeatId : "N/A") + ") is unavailable.");
                    return;
                }

                seatData.setBlockedAdjacentSeatId(adjacentSeatId);
                adjacentButton.setDisable(true);
                adjacentButton.getStyleClass().removeAll("seat-available", "seat-wheelchair-available", "seat-selected", "seat-booked", "seat-wheelchair-booked", "seat-blocked");
                adjacentButton.getStyleClass().add("seat-blocked");
                System.out.println("Blocked adjacent seat: " + adjacentSeatId);
            }

            selectedSeatButtons.add(selectedButton);
            selectedSeatIds.add(selectedSeatId);

            selectedButton.getStyleClass().removeAll("seat-available", "seat-wheelchair-available", "seat-booked", "seat-wheelchair-booked");
            selectedButton.getStyleClass().add("seat-selected");

        } else {
            selectedSeatButtons.remove(selectedButton);
            selectedSeatIds.remove(selectedSeatId);

            if (seatData.getBlockedAdjacentSeatId() != null) {
                String adjacentSeatId = seatData.getBlockedAdjacentSeatId();
                ToggleButton adjacentButton = seatButtonsMap.get(adjacentSeatId);
                if (adjacentButton != null && adjacentButton.getStyleClass().contains("seat-blocked")) {
                    adjacentButton.setDisable(false);
                    adjacentButton.getStyleClass().removeAll("seat-blocked", "seat-booked");

                    if (isWheelchairAccessibleSeat(adjacentSeatId)) {
                        adjacentButton.getStyleClass().add("seat-wheelchair-available");
                    } else {
                        adjacentButton.getStyleClass().add("seat-available");
                    }
                    System.out.println("Unblocked adjacent seat: " + adjacentSeatId);
                }
                seatData.setBlockedAdjacentSeatId(null);
            }

            selectedButton.getStyleClass().remove("seat-selected");
            if (!bookedSeats.contains(selectedSeatId)) {
                if (seatIsWheelchairAccessible) {
                    selectedButton.getStyleClass().add("seat-wheelchair-available");
                } else {
                    selectedButton.getStyleClass().add("seat-available");
                }
            } else {
                if (seatIsWheelchairAccessible) {
                    selectedButton.getStyleClass().add("seat-wheelchair-booked");
                } else {
                    selectedButton.getStyleClass().add("seat-booked");
                }
                selectedButton.setDisable(true); // Ensure booked remains disabled
            }
        }

        updateBookButtonState();
    }

    /**
     * Clears the current seat selection, deselecting all buttons, unblocking adjacent seats,
     * resetting styles, and clearing selection lists.
     */
    private void clearSeatSelection() {
        System.out.println("[clearSeatSelection] START. Current appliedDiscount: " + (this.appliedDiscount != null ? this.appliedDiscount.getCode() : "null")); // LOGGING

        List<ToggleButton> buttonsToClear = new ArrayList<>(selectedSeatButtons);

        for (ToggleButton button : buttonsToClear) {
            button.setSelected(false);

            SeatData seatData = (SeatData) button.getUserData();
            String seatId = seatData.getSeatId();

            if (seatData.getBlockedAdjacentSeatId() != null) {
                String adjacentSeatId = seatData.getBlockedAdjacentSeatId();
                ToggleButton adjacentButton = seatButtonsMap.get(adjacentSeatId);
                if (adjacentButton != null && adjacentButton.isDisabled() && adjacentButton.getStyleClass().contains("seat-blocked")) {
                    adjacentButton.setDisable(false);
                    adjacentButton.getStyleClass().remove("seat-blocked");
                    if (isWheelchairAccessibleSeat(adjacentSeatId)) {
                        adjacentButton.getStyleClass().add("seat-wheelchair-available");
                    } else {
                        adjacentButton.getStyleClass().add("seat-available");
                    }
                }
                seatData.setBlockedAdjacentSeatId(null);
            }

            button.getStyleClass().remove("seat-selected");
            if (!bookedSeats.contains(seatId)) {
                if (isWheelchairAccessibleSeat(seatId)) {
                    button.getStyleClass().add("seat-wheelchair-available");
                } else {
                    button.getStyleClass().add("seat-available");
                }
            } else {
                if (isWheelchairAccessibleSeat(seatId)) {
                    button.getStyleClass().add("seat-wheelchair-booked");
                } else {
                    button.getStyleClass().add("seat-booked");
                }
                button.setDisable(true);
            }
        }

        selectedSeatButtons.clear();
        selectedSeatIds.clear();
        updateBookButtonState();
        System.out.println("[clearSeatSelection] END. Current appliedDiscount: " + (this.appliedDiscount != null ? this.appliedDiscount.getCode() : "null")); // LOGGING
    }

    private boolean isWheelchairAccessibleSeat(String seatId) {
        String rowId = seatId.replaceAll("\\d+", "");
        int seatNum = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));

        if (rowId.equalsIgnoreCase("A") || rowId.equalsIgnoreCase("L")) {
            return true;
        }

        if (rowId.startsWith("CC") || rowId.startsWith("BB") || rowId.startsWith("AA")) {
            int minSeat = 1, maxSeat = 19;
            if (rowId.equals("CC")) { minSeat = 1; maxSeat = 8; }
            else if (rowId.equals("BB")) { minSeat = 6; maxSeat = 23; }
            else if (rowId.equals("AA")) { minSeat = 21; maxSeat = 33; }

            return seatNum == minSeat || seatNum == maxSeat;
        } else {
            char rowChar = rowId.charAt(0);
            int maxSeat;
            if (rowChar == 'Q') maxSeat = 10;
            else if (rowChar == 'P') maxSeat = 11;
            else if (rowChar == 'O') maxSeat = 20;
            else if (rowChar == 'N') maxSeat = 19;
            else if (rowChar == 'M') maxSeat = 16;
            else maxSeat = 19;

            return seatNum == 1 || seatNum == maxSeat;
        }
    }

    /**
     * Finds the potential adjacent seat ID needed for wheelchair access.
     * It prioritizes the right side seat (seatNum + 1) if available.
     * @param seatId The ID of the wheelchair seat selected.
     * @return The ID of the adjacent seat to block, or null if none is suitable/found.
     */
    private String findAdjacentSeat(String seatId) {
        String rowId = seatId.replaceAll("\\d+", "");
        int seatNum;
        try {
            seatNum = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return null;
        }

        String rightSeatId = rowId + (seatNum + 1);
        String leftSeatId = rowId + (seatNum - 1);

        if (seatButtonsMap.containsKey(rightSeatId)) {
            ToggleButton rightButton = seatButtonsMap.get(rightSeatId);
            if (!rightButton.isDisabled() && !rightButton.isSelected()) {
                return rightSeatId;
            }
        }

        if (seatButtonsMap.containsKey(leftSeatId)) {
            ToggleButton leftButton = seatButtonsMap.get(leftSeatId);
            if (!leftButton.isDisabled() && !leftButton.isSelected()) {
                return leftSeatId;
            }
        }

        return null;
    }

    private int getMaxSeatForRow(String rowId) {
        if (rowId.equals("CC")) return 8;
        if (rowId.equals("BB")) return 23;
        if (rowId.equals("AA")) return 33;

        return 19;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Creates a ToggleButton representing a single seat. Sets its text, style class,
     * user data (including seat ID and price derived from the event), and action handler.
     * @param seatId The unique identifier for the seat (e.g., "A1").
     * @param seatNumText The text to display on the button (usually the seat number).
     * @return The configured ToggleButton for the seat.
     */
    private ToggleButton createSeatButton(String seatId, String seatNumText) {
        ToggleButton seatButton = new ToggleButton(seatNumText);
        String rowId = seatId.replaceAll("\\d+", "");
        int seatNum = 0;
        try {
            seatNum = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            System.err.println("Could not parse seat number from seatId: " + seatId);
        }

        float price = findSeatPrice(rowId, seatNum);
        seatButton.setUserData(new SeatData(seatId, price));

        seatButton.getStyleClass().add("seat-button");
        seatButton.setPrefSize(35, 30);
        seatButton.setOnAction(this::handleSeatSelection);
        return seatButton;
    }


    /**
     * Finds the price of a seat. Prioritizes the base Event_Price from the
     * selected event in the ComboBox.
     * @param rowId The row identifier of the seat (currently unused for price).
     * @param seatNum The number of the seat within the row (currently unused for price).
     * @return The price of the seat, primarily based on the selected Event's price.
     */
    private float findSeatPrice(String rowId, int seatNum) {
        Event currentEvent = eventComboBox.getValue();
        if (currentEvent != null) {
            return (float) currentEvent.getEventPrice();
        }
        return 0f;
    }

    /**
     * Inner class to hold data associated with each seat button,
     * including its ID, price, and potentially state information like
     * whether it's blocking an adjacent seat for wheelchair access.
     */
    private static class SeatData {
        private final String seatId;
        private final float price;
        private String blockedAdjacentSeatId;

        /**
         * Constructor for SeatData.
         * @param seatId The unique ID of the seat.
         * @param price The price of the seat.
         */
        public SeatData(String seatId, float price) {
            this.seatId = seatId;
            this.price = price;
            this.blockedAdjacentSeatId = null;
        }

        public String getSeatId() { return seatId; }
        public float getPrice() { return price; }
        public String getBlockedAdjacentSeatId() { return blockedAdjacentSeatId; }
        public void setBlockedAdjacentSeatId(String blockedAdjacentSeatId) { this.blockedAdjacentSeatId = blockedAdjacentSeatId; }
    }


    /**
     * Loads the seating layout for the Small Hall configuration into the seatingPlanGrid.
     * Creates row labels, seat buttons, and applies initial styles based on availability and accessibility.
     * This version uses the layout logic from the initial user prompt.
     */
    private void loadSmallHallLayout() {
        System.out.println("Loading Small Hall Layout (A-N, Bottom-Up) - Reverted Layout Logic");
        int numRows = 14; int maxSeatsAC = 8;

        Label aisleLabel = new Label("A I S L E");
        aisleLabel.setRotate(-90); aisleLabel.setAlignment(Pos.CENTER);
        aisleLabel.setTextAlignment(TextAlignment.CENTER);
        GridPane.setValignment(aisleLabel, VPos.CENTER);
        seatingPlanGrid.add(aisleLabel, 0, 2, 1, 9);

        for (int gridRowIndex = 0; gridRowIndex < numRows; gridRowIndex++) {
            char rowChar = (char) ('N' - gridRowIndex);
            int seatsInThisRow; int startGridCol;

            if (rowChar >= 'A' && rowChar <= 'C') { seatsInThisRow = 8; startGridCol = 2; }
            else if (rowChar >= 'D' && rowChar <= 'L') { seatsInThisRow = 7; startGridCol = 2; }
            else if (rowChar == 'M') { seatsInThisRow = 4; startGridCol = 4; }
            else { seatsInThisRow = 4; startGridCol = 3; }

            Label rowLabel = new Label(String.valueOf(rowChar));
            rowLabel.setMinWidth(25); rowLabel.setAlignment(Pos.CENTER_RIGHT);
            rowLabel.setPadding(new Insets(0, 5, 0, 0));
            if (rowChar == 'N') { rowLabel.setTranslateX(-12.0); }
            seatingPlanGrid.add(rowLabel, 1, gridRowIndex);

            for (int seatNum = 1; seatNum <= seatsInThisRow; seatNum++) {
                String seatId = "" + rowChar + seatNum;
                ToggleButton seatButton = createSeatButton(seatId, String.valueOf(seatNum));
                boolean isAccessible = isWheelchairAccessibleSeat(seatId);
                if (bookedSeats.contains(seatId)) {
                    seatButton.setDisable(true);
                    if (isAccessible) {
                        seatButton.getStyleClass().add("seat-wheelchair-booked");
                    } else {
                        seatButton.getStyleClass().add("seat-booked");
                    }
                } else {
                    if (isAccessible) {
                        seatButton.getStyleClass().add("seat-wheelchair-available");
                    } else {
                        seatButton.getStyleClass().add("seat-available");
                    }
                }

                int gridColIndex = startGridCol + seatNum -1;
                seatingPlanGrid.add(seatButton, gridColIndex, gridRowIndex);
                seatButtonsMap.put(seatId, seatButton);
            }
            RowConstraints rc = new RowConstraints(); rc.setMinHeight(35);
            rc.setVgrow(Priority.NEVER); seatingPlanGrid.getRowConstraints().add(rc);
        }

        Label soundDeskLabel = new Label("SOUND\nDESK");
        soundDeskLabel.setTextAlignment(TextAlignment.CENTER); soundDeskLabel.setAlignment(Pos.CENTER);
        soundDeskLabel.setStyle("-fx-border-color: lightgrey; -fx-padding: 5px;");
        seatingPlanGrid.add(soundDeskLabel, 7, 0, 2, 1);

        Label stageLabel = new Label("STAGE"); stageLabel.setFont(new Font("System Bold", 14));
        stageLabel.setAlignment(Pos.CENTER); stageLabel.setMaxWidth(Double.MAX_VALUE);
        stageLabel.setPadding(new Insets(15,0,5,0));
        seatingPlanGrid.add(stageLabel, 2, numRows, maxSeatsAC, 1);

        RowConstraints stageRc = new RowConstraints(); stageRc.setMinHeight(40);
        seatingPlanGrid.getRowConstraints().add(stageRc);
    }

    /**
     * Loads the seating layout for the Large Hall configuration into the seatingPlanGrid.
     * Includes top balcony, stalls, side balconies, labels, and seat buttons with styling.
     * This version uses the layout logic from the initial user prompt.
     */
    private void loadLargeHallLayout() {
        System.out.println("Loading Large Hall Layout (Detailed) - Reverted Layout Logic");
        final int LEFT_BALCONY_LABEL_COL = 0;
        final int LEFT_BALCONY_SEAT_COL = 1;
        final int LEFT_AISLE_COL = 6;
        final int STALLS_ROW_LABEL_COL = 7;
        final int STALLS_SEAT_START_COL = 9;
        final int STALLS_MAX_WIDTH = 21;
        final int STALLS_SEAT_END_COL = STALLS_SEAT_START_COL + STALLS_MAX_WIDTH;
        final int RIGHT_AISLE_COL = STALLS_SEAT_END_COL + 5;
        final int RIGHT_BALCONY_SEAT_COL = RIGHT_AISLE_COL + 1;
        final int RIGHT_BALCONY_LABEL_COL = RIGHT_BALCONY_SEAT_COL + 1;
        int currentGridRow = 0;

        Label topBalconyLabel = new Label("BALCONY"); topBalconyLabel.setFont(new Font("System Bold", 14));
        topBalconyLabel.setMaxWidth(Double.MAX_VALUE); topBalconyLabel.setAlignment(Pos.CENTER);
        seatingPlanGrid.add(topBalconyLabel, STALLS_SEAT_START_COL, currentGridRow, 20, 1); currentGridRow++;
        addSeatRow("CC", currentGridRow, STALLS_ROW_LABEL_COL + 4, STALLS_SEAT_START_COL + 2 , 1, 8, true); currentGridRow++;
        addSeatRow("BB", currentGridRow, STALLS_ROW_LABEL_COL + 4, STALLS_SEAT_START_COL + 2, 6, 23, true); currentGridRow++;
        addSeatRow("AA", currentGridRow, STALLS_ROW_LABEL_COL + 4, STALLS_SEAT_START_COL + 2 , 21, 33, true); currentGridRow++;
        currentGridRow++;

        int stallsTitleRow = currentGridRow; currentGridRow++;
        char[] stallRows = {'Q', 'P', 'O', 'N', 'M', 'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        int sideBalconyStartRow = currentGridRow + 1 ;
        for (char rowChar : stallRows) {
            int seatsStartNum, seatsEndNum;
            if (rowChar == 'Q') { seatsStartNum = 1; seatsEndNum = 10; }
            else if (rowChar == 'P') { seatsStartNum = 1; seatsEndNum = 11; }
            else if (rowChar == 'O') { seatsStartNum = 1; seatsEndNum = 20; }
            else if (rowChar == 'N') { seatsStartNum = 1; seatsEndNum = 19; }
            else if (rowChar == 'M') { seatsStartNum = 1; seatsEndNum = 16; }
            else { seatsStartNum = 1; seatsEndNum = 19; }
            addSeatRow(String.valueOf(rowChar), currentGridRow, STALLS_ROW_LABEL_COL + 3, STALLS_SEAT_START_COL + 2, seatsStartNum, seatsEndNum, true);
            currentGridRow++;
        }
        int sideBalconyEndRow = currentGridRow -1;
        Label centerStallsLabel = new Label("STALLS"); centerStallsLabel.setFont(new Font("System Bold", 12)); centerStallsLabel.setMaxWidth(Double.MAX_VALUE); centerStallsLabel.setAlignment(Pos.CENTER);
        seatingPlanGrid.add(centerStallsLabel,  STALLS_SEAT_START_COL + 2 ,   stallsTitleRow, 20, 1);

        Label leftBalconyLabel = new Label("B\nA\nL\nC\nO\nN\nY");
        leftBalconyLabel.setTextAlignment(TextAlignment.CENTER);
        GridPane.setValignment(leftBalconyLabel, VPos.CENTER);
        seatingPlanGrid.add(leftBalconyLabel, LEFT_AISLE_COL, sideBalconyStartRow, 1, (sideBalconyEndRow - sideBalconyStartRow + 1));

        String[][] leftBalconySeatsA = { {"AA", "20"}, {"AA", "19"}, {"AA", "18"}, {"AA", "17"}, {"AA", "16"}, {"AA", "15"}, {"AA", "14"}, {"AA", "13"}, {"AA", "12"}, {"AA", "11"}, {"AA", "10"}, {"AA", "9"}, {"AA", "8"}, {"AA", "7"}, {"AA", "6"}, {"AA", "5"}, {"AA", "4"}, {"AA", "3"}, {"AA", "2"}, {"AA", "1"} };
        String[][] leftBalconySeatsB = { {"BB", "5"}, {"BB", "4"}, {"BB", "3"}, {"BB", "2"}, {"BB", "1"} };

        int leftBalconyGridRowOffset = sideBalconyStartRow;

        for (int i = 0; i < leftBalconySeatsB.length; i++) {
            int rowIndex = leftBalconyGridRowOffset + i;
            if (rowIndex > sideBalconyEndRow) break;
            String rowLabel = leftBalconySeatsB[i][0]; String seatNum = leftBalconySeatsB[i][1];
            Label label = new Label(rowLabel); label.setMinWidth(25); label.setAlignment(Pos.CENTER_RIGHT); label.setPadding(new Insets(0, 5, 0, 0));
            seatingPlanGrid.add(label, LEFT_BALCONY_LABEL_COL, rowIndex);
            String seatId = rowLabel + seatNum; ToggleButton seatButton = createSeatButton(seatId, seatNum);
            boolean isAccessible = isWheelchairAccessibleSeat(seatId);
            if (bookedSeats.contains(seatId)) { seatButton.setDisable(true); seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-booked" : "seat-booked"); }
            else { seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-available" : "seat-available"); }
            seatingPlanGrid.add(seatButton, LEFT_BALCONY_SEAT_COL, rowIndex);
            seatButtonsMap.put(seatId, seatButton);
        }
        for (int i = 0; i < leftBalconySeatsA.length; i++) {
            int rowIndex = leftBalconyGridRowOffset + i;
            if (rowIndex > sideBalconyEndRow) break;
            String rowLabel = leftBalconySeatsA[i][0]; String seatNum = leftBalconySeatsA[i][1];
            Label label = new Label(rowLabel); label.setMinWidth(25); label.setAlignment(Pos.CENTER_RIGHT); label.setPadding(new Insets(0, 5, 0, 0));
            seatingPlanGrid.add(label, LEFT_BALCONY_LABEL_COL + 2, rowIndex);
            String seatId = rowLabel + seatNum; ToggleButton seatButton = createSeatButton(seatId, seatNum);
            boolean isAccessible = isWheelchairAccessibleSeat(seatId);
            if (bookedSeats.contains(seatId)) { seatButton.setDisable(true); seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-booked" : "seat-booked"); }
            else { seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-available" : "seat-available"); }
            seatingPlanGrid.add(seatButton, LEFT_BALCONY_SEAT_COL + 3, rowIndex );
            seatButtonsMap.put(seatId, seatButton);
        }

        Label rightBalconyLabel = new Label("B\nA\nL\nC\nO\nN\nY");
        rightBalconyLabel.setTextAlignment(TextAlignment.CENTER);
        GridPane.setValignment(rightBalconyLabel, VPos.CENTER);
        seatingPlanGrid.add(rightBalconyLabel, RIGHT_AISLE_COL, sideBalconyStartRow, 1, (sideBalconyEndRow - sideBalconyStartRow + 1));

        String[][] rightBalconySeatsA = { {"AA", "34"}, {"AA", "35"}, {"AA", "36"}, {"AA", "37"}, {"AA", "38"}, {"AA", "39"}, {"AA", "40"}, {"AA", "41"}, {"AA", "42"}, {"AA", "43"}, {"AA", "44"}, {"AA", "45"}, {"AA", "46"}, {"AA", "47"}, {"AA", "48"}, {"AA", "49"}, {"AA", "50"}, {"AA", "51"}, {"AA", "52"}, {"AA", "53"} };
        String[][] rightBalconySeatsB = { {"BB", "24"}, {"BB", "25"}, {"BB", "26"}, {"BB", "27"}, {"BB", "28"} };

        int rightBalconyGridRowOffset = sideBalconyStartRow;

        for (int i = 0; i < rightBalconySeatsB.length; i++) {
            int rowIndex = rightBalconyGridRowOffset + i;
            if (rowIndex > sideBalconyEndRow) break;
            String rowLabel = rightBalconySeatsB[i][0]; String seatNum = rightBalconySeatsB[i][1];
            Label label = new Label(rowLabel); label.setMinWidth(25); label.setAlignment(Pos.CENTER_LEFT); label.setPadding(new Insets(0, 0, 0, 5));
            seatingPlanGrid.add(label, RIGHT_BALCONY_LABEL_COL + 3, rowIndex);
            String seatId = rowLabel + seatNum; ToggleButton seatButton = createSeatButton(seatId, seatNum);
            boolean isAccessible = isWheelchairAccessibleSeat(seatId);
            if (bookedSeats.contains(seatId)) { seatButton.setDisable(true); seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-booked" : "seat-booked"); }
            else { seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-available" : "seat-available"); }
            seatingPlanGrid.add(seatButton, RIGHT_BALCONY_SEAT_COL + 2, rowIndex);
            seatButtonsMap.put(seatId, seatButton);
        }
        for (int i = 0; i < rightBalconySeatsA.length; i++) {
            int rowIndex = rightBalconyGridRowOffset + i;
            if (rowIndex > sideBalconyEndRow) break;
            String rowLabel = rightBalconySeatsA[i][0]; String seatNum = rightBalconySeatsA[i][1];
            Label label = new Label(rowLabel); label.setMinWidth(25); label.setAlignment(Pos.CENTER_LEFT); label.setPadding(new Insets(0, 0, 0, 5));
            seatingPlanGrid.add(label, RIGHT_BALCONY_LABEL_COL, rowIndex);
            String seatId = rowLabel + seatNum; ToggleButton seatButton = createSeatButton(seatId, seatNum);
            boolean isAccessible = isWheelchairAccessibleSeat(seatId);
            if (bookedSeats.contains(seatId)) { seatButton.setDisable(true); seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-booked" : "seat-booked"); }
            else { seatButton.getStyleClass().add(isAccessible ? "seat-wheelchair-available" : "seat-available"); }
            seatingPlanGrid.add(seatButton, RIGHT_BALCONY_SEAT_COL, rowIndex);
            seatButtonsMap.put(seatId, seatButton);
        }

        Label stageLabel = new Label("STAGE"); stageLabel.setFont(new Font("System Bold", 14));
        stageLabel.setAlignment(Pos.CENTER); stageLabel.setMaxWidth(Double.MAX_VALUE);
        stageLabel.setPadding(new Insets(15,0,5,0));
        seatingPlanGrid.add(stageLabel, STALLS_SEAT_START_COL, currentGridRow, (STALLS_SEAT_END_COL - STALLS_SEAT_START_COL + 1), 1);

        for(int i=0; i<currentGridRow + 1; i++){
            RowConstraints rc = new RowConstraints(); rc.setMinHeight(35);
            rc.setVgrow(Priority.NEVER); seatingPlanGrid.getRowConstraints().add(rc);
        }

    }

    /**
     * Adds a row of seat buttons to the seating plan grid using original layout logic.
     * Applies updated styling based on seat state.
     * @param rowId The identifier for the row (e.g., "A", "CC").
     * @param gridRowIndex The grid row index where the seats should be placed.
     * @param labelGridCol The grid column index for the row labels.
     * @param seatStartGridCol The starting grid column index for the seat buttons.
     * @param startSeatNum The number of the first seat in the row.
     * @param endSeatNum The number of the last seat in the row.
     * @param addEndLabel Whether to add a label at the end of the row.
     */
    private void addSeatRow(String rowId, int gridRowIndex, int labelGridCol, int seatStartGridCol, int startSeatNum, int endSeatNum, boolean addEndLabel) {
        if (startSeatNum <= 0 || endSeatNum < startSeatNum) return;

        Label startLabel = new Label(rowId);
        startLabel.setMinWidth(25); startLabel.setAlignment(Pos.CENTER_RIGHT);
        startLabel.setPadding(new Insets(0, 5, 0, 0));
        seatingPlanGrid.add(startLabel, labelGridCol, gridRowIndex);

        int currentGridCol = seatStartGridCol;
        for (int seatNum = startSeatNum; seatNum <= endSeatNum; seatNum++) {
            String seatId = rowId + seatNum;
            ToggleButton seatButton = createSeatButton(seatId, String.valueOf(seatNum));

            boolean isAccessible = isWheelchairAccessibleSeat(seatId);
            if (bookedSeats.contains(seatId)) {
                seatButton.setDisable(true);
                if (isAccessible) {
                    seatButton.getStyleClass().add("seat-wheelchair-booked");
                } else {
                    seatButton.getStyleClass().add("seat-booked");
                }
            } else {
                if (isAccessible) {
                    seatButton.getStyleClass().add("seat-wheelchair-available");
                } else {
                    seatButton.getStyleClass().add("seat-available");
                }
            }

            int gridColIndex = seatStartGridCol + (seatNum - startSeatNum);
            seatingPlanGrid.add(seatButton, gridColIndex, gridRowIndex);
            currentGridCol = gridColIndex;

            seatButtonsMap.put(seatId, seatButton);
        }

        if (addEndLabel) {
            Label endLabel = new Label(rowId);
            endLabel.setMinWidth(25); endLabel.setAlignment(Pos.CENTER_LEFT);
            endLabel.setPadding(new Insets(0, 0, 0, 5));
            seatingPlanGrid.add(endLabel, currentGridCol + 1, gridRowIndex);
        }
    }
}