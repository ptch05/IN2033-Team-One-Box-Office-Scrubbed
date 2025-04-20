package com.teamoneboxoffice.controllers;

import com.teamoneboxoffice.entities.Event;
import com.teamoneboxoffice.entities.Seat;
import com.teamoneboxoffice.entities.SeatingConfig;
import com.teamoneboxoffice.entities.Section;
import com.teamoneboxoffice.services.implementations.DAOs.EventDAO;
import javafx.scene.layout.Region;
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

import java.net.URL;
// Removed unused SQL imports
import java.util.*;


/**
 * Controller for managing seating configurations and event layouts.
 * Allows administrators to view and modify seat availability.
 */
public class ViewSeatingConfigurationsController implements Initializable {

    @FXML private ListView<Event> eventListView;
    @FXML private GridPane seatingPlanGrid;
    @FXML private Label statusLabel;

    private EventDAO eventDAO;
    private ObservableList<Event> eventList;
    private Map<String, ToggleButton> seatButtonsMap;
    private Event selectedEvent;
    private Set<String> bookedSeatIdsForCurrentEvent;


    /**
     * Initializes the controller by loading events, setting up UI components,
     * and initializing the set for booked seats.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the
     *     location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root
     *     object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventDAO = new EventDAO();
        seatButtonsMap = new HashMap<>();
        bookedSeatIdsForCurrentEvent = new HashSet<>(); // Initialize the set

        loadEvents();

        eventListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> handleEventSelection(newVal));
    }

    /**
     * Loads events from the database and populates the event list view.
     */
    private void loadEvents() {
        eventList = FXCollections.observableArrayList(eventDAO.getAll());
        eventListView.setItems(eventList);
        eventListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getEventName());
            }
        });
    }

    /**
     * Handles event selection by fetching booked seats for the selected event
     * and then loading the corresponding seating configuration layout. Clears
     * previous layout and state before loading the new one.
     *
     * @param event The newly selected event from the ListView, or null if deselected.
     */
    private void handleEventSelection(Event event) {
        selectedEvent = event;
        seatingPlanGrid.getChildren().clear();
        seatingPlanGrid.getRowConstraints().clear();
        seatingPlanGrid.getColumnConstraints().clear();
        seatButtonsMap.clear();
        bookedSeatIdsForCurrentEvent.clear();

        if (event != null) {
            statusLabel.setText("Loading seating configuration for: " + event.getEventName() + "...");
            System.out.println("Selected event: " + event.getEventName() + " (ID: " + event.getEventID() + ")");
            bookedSeatIdsForCurrentEvent = eventDAO.getAllBookedSeatIds();

            loadSeatingConfiguration();

        } else {
            statusLabel.setText("Please select an event.");
        }
    }

    /**
     * Loads the seating configuration for the selected event.
     * Generates the appropriate layout based on hall type.
     */
    private void loadSeatingConfiguration() {
        String hallType = selectedEvent.getHallType();

        if (hallType.equals("Large Hall")) {
            loadLargeHallLayout();
        } else if (hallType.equals("Small Hall")) {
            loadSmallHallLayout();
        }
    }

    /**
     * Loads the detailed seating layout for the "Large Hall".
     * It sets up sections like the top balcony, stalls, and side balconies,
     * creating labels and seat buttons based on predefined configurations.
     * Uses the pre-fetched set of booked seats to determine button states.
     */
    private void loadLargeHallLayout() {
        System.out.println("Loading Large Hall Layout (Detailed)");
        final int LEFT_BALCONY_LABEL_COL = 0;
        final int LEFT_BALCONY_SEAT_COL = 1;
        final int LEFT_AISLE_COL = 6;
        final int STALLS_ROW_LABEL_COL = 7;
        final int STALLS_SEAT_START_COL = 9;
        final int STALLS_SEAT_END_COL = STALLS_SEAT_START_COL + 21;
        final int RIGHT_AISLE_COL = STALLS_SEAT_END_COL + 5;
        final int RIGHT_BALCONY_SEAT_COL = RIGHT_AISLE_COL + 1;
        final int RIGHT_BALCONY_LABEL_COL = RIGHT_BALCONY_SEAT_COL + 1;
        int currentGridRow = 0;

        // --- Top Balcony ---
        Label topBalconyLabel = new Label("BALCONY");
        topBalconyLabel.setFont(new Font("System Bold", 14));
        topBalconyLabel.setMaxWidth(Double.MAX_VALUE);
        topBalconyLabel.setAlignment(Pos.CENTER);
        seatingPlanGrid.add(topBalconyLabel, STALLS_SEAT_START_COL, currentGridRow, 20, 1);
        currentGridRow++;
        addSeatRow("CC", currentGridRow, STALLS_ROW_LABEL_COL + 4, STALLS_SEAT_START_COL + 2, 1, 8, true);
        currentGridRow++;
        addSeatRow("BB", currentGridRow, STALLS_ROW_LABEL_COL + 4, STALLS_SEAT_START_COL + 2, 6, 23, true);
        currentGridRow++;
        addSeatRow("AA", currentGridRow, STALLS_ROW_LABEL_COL + 4, STALLS_SEAT_START_COL + 2, 21, 33, true);
        currentGridRow++;
        currentGridRow++; // Gap

        // --- Stalls ---
        char[] stallRows = {
                'Q', 'P', 'O', 'N', 'M', 'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'
        };
        int sideBalconyStartRow = currentGridRow;
        for (char rowChar : stallRows) {
            int seatsStartNum, seatsEndNum;
            if (rowChar == 'Q') { seatsStartNum = 1; seatsEndNum = 10; }
            else if (rowChar == 'P') { seatsStartNum = 1; seatsEndNum = 11; }
            else if (rowChar == 'O') { seatsStartNum = 1; seatsEndNum = 20; }
            else if (rowChar == 'N') { seatsStartNum = 1; seatsEndNum = 19; }
            else if (rowChar == 'M') { seatsStartNum = 1; seatsEndNum = 16; }
            else { seatsStartNum = 1; seatsEndNum = 19; } // Default for A-L
            addSeatRow(
                    String.valueOf(rowChar),
                    currentGridRow,
                    STALLS_ROW_LABEL_COL + 3,
                    STALLS_SEAT_START_COL + 2,
                    seatsStartNum,
                    seatsEndNum,
                    true);
            currentGridRow++;
        }
        int sideBalconyEndRow = currentGridRow -1;
        Label centerStallsLabel = new Label("STALLS");
        centerStallsLabel.setFont(new Font("System Bold", 12));
        centerStallsLabel.setMaxWidth(Double.MAX_VALUE);
        centerStallsLabel.setAlignment(Pos.CENTER);
        seatingPlanGrid.add(
                centerStallsLabel, STALLS_SEAT_START_COL + 2, sideBalconyStartRow -1 , 20, 1);

        // --- Side Balconies ---
        Label leftBalconyLabel = new Label("B\nA\nL\nC\nO\nN\nY");
        leftBalconyLabel.setTextAlignment(TextAlignment.CENTER);
        GridPane.setValignment(leftBalconyLabel, VPos.CENTER);
        int leftBalconyRowSpan = Math.max(1, sideBalconyEndRow - sideBalconyStartRow + 1);
        seatingPlanGrid.add(leftBalconyLabel, LEFT_AISLE_COL, sideBalconyStartRow, 1, leftBalconyRowSpan);

        String[][] leftBalconySeatsA = {

                {"AA", "20"}, // Row 6: AA 20
                {"AA", "19"}, // Row 7: AA 19
                {"AA", "18"}, // Row 8: AA 18
                {"AA", "17"}, // Row 9: AA 17
                {"AA", "16"}, // Row 10: AA 16
                {"AA", "15"}, // Row 11: AA 15
                {"AA", "14"}, // Row 12: AA 14
                {"AA", "13"}, // Row 13: AA 13
                {"AA", "12"}, // Row 14: AA 12
                {"AA", "11"}, // Row 15: AA 11
                {"AA", "10"}, // Row 16: AA 10
                {"AA", "9"},  // Row 17: AA 9
                {"AA", "8"},  // Row 18: AA 8
                {"AA", "7"},  // Row 19: AA 7
                {"AA", "6"},  // Row 20: AA 6
                {"AA", "5"},  // Row 21: AA 5
                {"AA", "4"},  // Row 22: AA 4
                {"AA", "3"},  // Row 23: AA 3
                {"AA", "2"},  // Row 24: AA 2
                {"AA", "1"}   // Row 25: AA 1

        };
        String[][] leftBalconySeatsB = {
                // Row 5: BB 1
                {"BB", "5"},  // Row 1: BB 5
                {"BB", "4"},  // Row 2: BB 4
                {"BB", "3"},  // Row 3: BB 3
                {"BB", "2"},  // Row 4: BB 2
                {"BB", "1"},
        };

        // Create left balcony seats A
        for (int i = 0; i < leftBalconySeatsA.length; i++) {
            int rowIndex = sideBalconyStartRow + i;
            if (rowIndex <= sideBalconyEndRow + 5) {
                String rowLabel = leftBalconySeatsA[i][0];
                String seatNum = leftBalconySeatsA[i][1];
                String seatId = rowLabel + seatNum;

                Label label = new Label(rowLabel);
                label.setMinWidth(25); label.setAlignment(Pos.CENTER_RIGHT);
                label.setPadding(new Insets(0, 5, 0, 0));
                seatingPlanGrid.add(label, LEFT_BALCONY_LABEL_COL + 2, rowIndex);

                float price = 0;
                try {
                    price = findSeatPrice(rowLabel, Integer.parseInt(seatNum));
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse seat number for price lookup: " + seatNum);
                }
                ToggleButton seatButton = createSeatButton(seatId, seatNum, price);

                boolean isWheelchairSeat = (rowLabel.equals("BB") && (seatNum.equals("1") || seatNum.equals("5"))) ||
                        (rowLabel.equals("AA") && (seatNum.equals("10") || seatNum.equals("20")));
                if (isWheelchairSeat) {
                    seatButton.getStyleClass().add("seat-wheelchair");
                }

                if (bookedSeatIdsForCurrentEvent.contains(seatId)) {
                    seatButton.setDisable(true);
                    seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair");
                    seatButton.getStyleClass().add("seat-booked");
                } else {
                    seatButton.setDisable(false);
                    if (!isWheelchairSeat) {
                        seatButton.getStyleClass().add("seat-available");
                        seatButton.getStyleClass().remove("seat-wheelchair");
                    } else {
                        if (!seatButton.getStyleClass().contains("seat-wheelchair")) {
                            seatButton.getStyleClass().add("seat-wheelchair");
                        }
                        seatButton.getStyleClass().remove("seat-available");
                    }
                }
                int gridColIndex = LEFT_BALCONY_SEAT_COL + 3;
                seatingPlanGrid.add(seatButton, gridColIndex, rowIndex);
                seatButtonsMap.put(seatId, seatButton);
            }
        }
        // Create left balcony seats B
        for (int i = 0; i < leftBalconySeatsB.length; i++) {
            int rowIndex = sideBalconyStartRow + i;
            if (rowIndex <= sideBalconyEndRow + 5) {
                String rowLabel = leftBalconySeatsB[i][0];
                String seatNum = leftBalconySeatsB[i][1];
                String seatId = rowLabel + seatNum;

                Label label = new Label(rowLabel);
                label.setMinWidth(25); label.setAlignment(Pos.CENTER_RIGHT);
                label.setPadding(new Insets(0, 5, 0, 0));
                seatingPlanGrid.add(label, LEFT_BALCONY_LABEL_COL, rowIndex);

                float price = 0;
                try {
                    price = findSeatPrice(rowLabel, Integer.parseInt(seatNum));
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse seat number for price lookup: " + seatNum);
                }
                ToggleButton seatButton = createSeatButton(seatId, seatNum, price);

                boolean isWheelchairSeat = (rowLabel.equals("BB") && (seatNum.equals("1") || seatNum.equals("5"))) ||
                        (rowLabel.equals("AA") && (seatNum.equals("10") || seatNum.equals("20")));
                if (isWheelchairSeat) {
                    seatButton.getStyleClass().add("seat-wheelchair");
                }

                if (bookedSeatIdsForCurrentEvent.contains(seatId)) {
                    seatButton.setDisable(true);
                    seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair");
                    seatButton.getStyleClass().add("seat-booked");
                } else {
                    seatButton.setDisable(false);
                    if (!isWheelchairSeat) {
                        seatButton.getStyleClass().add("seat-available");
                        seatButton.getStyleClass().remove("seat-wheelchair");
                    } else {
                        if (!seatButton.getStyleClass().contains("seat-wheelchair")) {
                            seatButton.getStyleClass().add("seat-wheelchair");
                        }
                        seatButton.getStyleClass().remove("seat-available");
                    }
                }
                seatingPlanGrid.add(seatButton, LEFT_BALCONY_SEAT_COL, rowIndex);
                seatButtonsMap.put(seatId, seatButton);
            }
        }

        // Right side balcony
        Label rightBalconyLabel = new Label("B\nA\nL\nC\nO\nN\nY");
        rightBalconyLabel.setTextAlignment(TextAlignment.CENTER);
        GridPane.setValignment(rightBalconyLabel, VPos.CENTER);
        int rightBalconyRowSpan = Math.max(1, sideBalconyEndRow - sideBalconyStartRow + 1);
        seatingPlanGrid.add(rightBalconyLabel, RIGHT_AISLE_COL, sideBalconyStartRow, 1, rightBalconyRowSpan);

        String[][] rightBalconySeatsA = {
                {"AA", "34"}, // Row 1: AA 34
                {"AA", "35"}, // Row 2: AA 35
                {"AA", "36"}, // Row 3: AA 36
                {"AA", "37"}, // Row 4: AA 37
                {"AA", "38"}, // Row 5: AA 38
                {"AA", "39"}, // Row 6: AA 39
                {"AA", "40"}, // Row 7: AA 40
                {"AA", "41"}, // Row 8: AA 41
                {"AA", "42"}, // Row 9: AA 42
                {"AA", "43"}, // Row 10: AA 43
                {"AA", "44"}, // Row 11: AA 44
                {"AA", "45"}, // Row 12: AA 45
                {"AA", "46"}, // Row 13: AA 46
                {"AA", "47"}, // Row 14: AA 47
                {"AA", "48"}, // Row 15: AA 48
                {"AA", "49"}, // Row 16: AA 49
                {"AA", "50"}, // Row 17: AA 50
                {"AA", "51"}, // Row 18: AA 51
                {"AA", "52"}, // Row 19: AA 52
                {"AA", "53"}  // Row 20: AA 53
        };
        String[][] rightBalconySeatsB = {
                {"BB", "24"}, // Row 1: BB 24
                {"BB", "25"}, // Row 2: BB 25
                {"BB", "26"}, // Row 3: BB 26
                {"BB", "27"}, // Row 4: BB 27
                {"BB", "28"}, // Row 5: BB 28
        };

        // Create right balcony seats A
        for (int i = 0; i < rightBalconySeatsA.length; i++) {
            int rowIndex = sideBalconyStartRow + i;
            if (rowIndex <= sideBalconyEndRow + 5) {
                String rowLabel = rightBalconySeatsA[i][0];
                String seatNum = rightBalconySeatsA[i][1];
                String seatId = rowLabel + seatNum;

                Label label = new Label(rowLabel);
                label.setMinWidth(25); label.setAlignment(Pos.CENTER_LEFT);
                label.setPadding(new Insets(0, 0, 0, 5));
                seatingPlanGrid.add(label, RIGHT_BALCONY_LABEL_COL, rowIndex);

                float price = 0;
                try {
                    price = findSeatPrice(rowLabel, Integer.parseInt(seatNum));
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse seat number for price lookup: " + seatNum);
                }
                ToggleButton seatButton = createSeatButton(seatId, seatNum, price); // Pass price

                boolean isWheelchairSeat = (rowLabel.equals("AA") && (seatNum.equals("34") || seatNum.equals("49")));
                if (isWheelchairSeat) {
                    seatButton.getStyleClass().add("seat-wheelchair");
                }

                if (bookedSeatIdsForCurrentEvent.contains(seatId)) {
                    seatButton.setDisable(true);
                    seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair");
                    seatButton.getStyleClass().add("seat-booked");
                } else {
                    seatButton.setDisable(false);
                    if (!isWheelchairSeat) {
                        seatButton.getStyleClass().add("seat-available");
                        seatButton.getStyleClass().remove("seat-wheelchair");
                    } else {
                        if (!seatButton.getStyleClass().contains("seat-wheelchair")) {
                            seatButton.getStyleClass().add("seat-wheelchair");
                        }
                        seatButton.getStyleClass().remove("seat-available");
                    }
                }
                seatingPlanGrid.add(seatButton, RIGHT_BALCONY_SEAT_COL, rowIndex);
                seatButtonsMap.put(seatId, seatButton);
            }
        }
        // Create right balcony seats B
        for (int i = 0; i < rightBalconySeatsB.length; i++) {
            int rowIndex = sideBalconyStartRow + i;
            if (rowIndex <= sideBalconyEndRow + 5) {
                String rowLabel = rightBalconySeatsB[i][0];
                String seatNum = rightBalconySeatsB[i][1];
                String seatId = rowLabel + seatNum;

                Label label = new Label(rowLabel);
                label.setMinWidth(25); label.setAlignment(Pos.CENTER_LEFT);
                label.setPadding(new Insets(0, 0, 0, 5));
                seatingPlanGrid.add(label, RIGHT_BALCONY_LABEL_COL + 3, rowIndex);

                float price = 0;
                try {
                    price = findSeatPrice(rowLabel, Integer.parseInt(seatNum));
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse seat number for price lookup: " + seatNum);
                }
                ToggleButton seatButton = createSeatButton(seatId, seatNum, price);
                boolean isWheelchairSeat = (rowLabel.equals("BB") && (seatNum.equals("28") || seatNum.equals("24")));
                if (isWheelchairSeat) {
                    seatButton.getStyleClass().add("seat-wheelchair");
                }

                if (bookedSeatIdsForCurrentEvent.contains(seatId)) {
                    seatButton.setDisable(true);
                    seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair");
                    seatButton.getStyleClass().add("seat-booked");
                } else {
                    seatButton.setDisable(false);
                    if (!isWheelchairSeat) {
                        seatButton.getStyleClass().add("seat-available");
                        seatButton.getStyleClass().remove("seat-wheelchair");
                    } else {
                        if (!seatButton.getStyleClass().contains("seat-wheelchair")) {
                            seatButton.getStyleClass().add("seat-wheelchair");
                        }
                        seatButton.getStyleClass().remove("seat-available");
                    }
                }
                seatingPlanGrid.add(seatButton, RIGHT_BALCONY_SEAT_COL + 2, rowIndex);
                seatButtonsMap.put(seatId, seatButton);
            }
        }

        // --- Stage Label ---
        Label stageLabel = new Label("STAGE");
        stageLabel.setFont(new Font("System Bold", 14));
        stageLabel.setAlignment(Pos.CENTER);
        stageLabel.setMaxWidth(Double.MAX_VALUE);
        stageLabel.setPadding(new Insets(15, 0, 5, 0));
        int stageRow = Math.max(currentGridRow, sideBalconyEndRow + 6);
        seatingPlanGrid.add(stageLabel, STALLS_SEAT_START_COL, stageRow, (STALLS_SEAT_END_COL - STALLS_SEAT_START_COL + 1), 1);

        // Add row constraints
        int totalRows = stageRow + 1;
        for (int i = 0; i < totalRows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(35);
            rc.setPrefHeight(Region.USE_COMPUTED_SIZE);
            rc.setVgrow(Priority.NEVER);
            if (i == stageRow) {
                rc.setMinHeight(40);
            }
            seatingPlanGrid.getRowConstraints().add(rc);
        }
        statusLabel.setText("Showing layout for: " + selectedEvent.getEventName());
    }




    /**
     * Loads the seating layout for the "Small Hall".
     * Arranges seats in rows A-N, handling specific offsets for rows M and N,
     * and includes labels for the aisle, sound desk, and stage.
     * Uses the pre-fetched set of booked seats to determine button states.
     */
    private void loadSmallHallLayout() {
        System.out.println("Loading Small Hall Layout (A-N, Bottom-Up)");
        int numRows = 14;
        int maxSeatsPerRow = 8;

        // Aisle Label
        Label aisleLabel = new Label("A I S L E");
        aisleLabel.setRotate(-90);
        aisleLabel.setAlignment(Pos.CENTER);
        aisleLabel.setTextAlignment(TextAlignment.CENTER);
        GridPane.setValignment(aisleLabel, VPos.CENTER);
        seatingPlanGrid.add(aisleLabel, 0, 2, 1, 9);

        // Generate rows
        for (int gridRowIndex = 0; gridRowIndex < numRows; gridRowIndex++) {
            char rowChar = (char) ('N' - gridRowIndex);
            int seatsInThisRow;
            int startGridCol;

            if (rowChar >= 'A' && rowChar <= 'C') { seatsInThisRow = 8; startGridCol = 2; }
            else if (rowChar >= 'D' && rowChar <= 'L') { seatsInThisRow = 7; startGridCol = 2; }
            else if (rowChar == 'M') { seatsInThisRow = 4; startGridCol = 4; }
            else { seatsInThisRow = 4; startGridCol = 3; }

            // Row Label
            Label rowLabel = new Label(String.valueOf(rowChar));
            rowLabel.setMinWidth(25);
            rowLabel.setAlignment(Pos.CENTER_RIGHT);
            rowLabel.setPadding(new Insets(0, 5, 0, 0));
            if (rowChar == 'N') {
                rowLabel.setTranslateX(-12.0);
            }
            seatingPlanGrid.add(rowLabel, 1, gridRowIndex);

            // Add Seat Buttons
            for (int seatNum = 1; seatNum <= seatsInThisRow; seatNum++) {
                String seatId = "" + rowChar + seatNum;

                float price = findSeatPrice(String.valueOf(rowChar), seatNum);
                ToggleButton seatButton = createSeatButton(seatId, String.valueOf(seatNum), price);
                boolean isWheelchairAccessible = false;
                if (rowChar == 'A' || rowChar == 'L') {
                    isWheelchairAccessible = true;
                } else if (rowChar >= 'D' && rowChar <= 'K') {
                    if (seatNum == 1 || seatNum == 7) isWheelchairAccessible = true;
                } else if (rowChar >= 'B' && rowChar <= 'C') {
                    if (seatNum == 1 || seatNum == 8) isWheelchairAccessible = true;
                }

                if (isWheelchairAccessible) {
                    seatButton.getStyleClass().add("seat-wheelchair");
                }

                if (bookedSeatIdsForCurrentEvent.contains(seatId)) {
                    seatButton.setDisable(true);
                    seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair");
                    seatButton.getStyleClass().add("seat-booked");
                } else {
                    seatButton.setDisable(false);
                    if (!isWheelchairAccessible) {
                        seatButton.getStyleClass().add("seat-available");
                        seatButton.getStyleClass().remove("seat-wheelchair");
                    } else {
                        if (!seatButton.getStyleClass().contains("seat-wheelchair")) {
                            seatButton.getStyleClass().add("seat-wheelchair");
                        }
                        seatButton.getStyleClass().remove("seat-available");
                    }
                }

                int gridColIndex = startGridCol + seatNum - 1;
                seatingPlanGrid.add(seatButton, gridColIndex, gridRowIndex);
                seatButtonsMap.put(seatId, seatButton);
            }
            // Add row constraints
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(35);
            rc.setPrefHeight(Region.USE_COMPUTED_SIZE);
            rc.setVgrow(Priority.NEVER);
            seatingPlanGrid.getRowConstraints().add(rc);
        }

        // Sound Desk Label
        Label soundDeskLabel = new Label("SOUND\nDESK");
        soundDeskLabel.setTextAlignment(TextAlignment.CENTER);
        soundDeskLabel.setAlignment(Pos.CENTER);
        soundDeskLabel.setStyle("-fx-border-color: lightgrey; -fx-padding: 5px;");
        seatingPlanGrid.add(soundDeskLabel, maxSeatsPerRow + 1, 0, 2, 1);

        // Stage Label
        Label stageLabel = new Label("STAGE");
        stageLabel.setFont(new Font("System Bold", 14));
        stageLabel.setAlignment(Pos.CENTER);
        stageLabel.setMaxWidth(Double.MAX_VALUE);
        stageLabel.setPadding(new Insets(15, 0, 5, 0));
        seatingPlanGrid.add(stageLabel, 2, numRows, maxSeatsPerRow, 1);

        // Stage Row Constraint
        RowConstraints stageRc = new RowConstraints();
        stageRc.setMinHeight(40);
        seatingPlanGrid.getRowConstraints().add(stageRc);

        statusLabel.setText("Showing layout for: " + selectedEvent.getEventName());
    }


    /**
     * Adds a single row of seat buttons to the seating plan grid.
     * Includes row labels at the start and optionally at the end.
     * Determines seat state (available, booked, wheelchair) using the
     * pre-fetched booked seats set and accessibility logic.
     *
     * @param rowId The identifier for the row (e.g., "A", "BB").
     * @param gridRowIndex The row index in the GridPane where this seat row should be placed.
     * @param labelGridCol The column index in the GridPane for the starting row label.
     * @param seatStartGridCol The starting column index in the GridPane for the seat buttons.
     * @param startSeatNum The number of the first seat in this row segment.
     * @param endSeatNum The number of the last seat in this row segment.
     * @param addEndLabel If true, adds a row label after the last seat button.
     */
    private void addSeatRow(String rowId, int gridRowIndex, int labelGridCol, int seatStartGridCol, int startSeatNum, int endSeatNum, boolean addEndLabel) {
        if (startSeatNum <= 0 || endSeatNum < startSeatNum) return;

        // Start Row Label
        Label startLabel = new Label(rowId);
        startLabel.setMinWidth(25);
        startLabel.setAlignment(Pos.CENTER_RIGHT);
        startLabel.setPadding(new Insets(0, 5, 0, 0));
        seatingPlanGrid.add(startLabel, labelGridCol, gridRowIndex);

        int currentGridCol = seatStartGridCol - 1;
        for (int seatNum = startSeatNum; seatNum <= endSeatNum; seatNum++) {
            String seatId = rowId + seatNum;

            float price = findSeatPrice(rowId, seatNum);
            ToggleButton seatButton = createSeatButton(seatId, String.valueOf(seatNum), price); // Pass price

            boolean isWheelchairAccessible = false;
            if (selectedEvent != null && selectedEvent.getHallType().equals("Small Hall")) {
                if (rowId.equalsIgnoreCase("A") || rowId.equalsIgnoreCase("L")) {
                    isWheelchairAccessible = true;
                } else if ((rowId.compareToIgnoreCase("D") >= 0 && rowId.compareToIgnoreCase("K") <= 0) && (seatNum == 1 || seatNum == 7)) {
                    isWheelchairAccessible = true;
                } else if ((rowId.equalsIgnoreCase("B") || rowId.equalsIgnoreCase("C")) && (seatNum == 1 || seatNum == 8)) {
                    isWheelchairAccessible = true;
                }
            } else if (selectedEvent != null && selectedEvent.getHallType().equals("Large Hall")) {
                if (rowId.equalsIgnoreCase("BB") && (seatNum == 1 || seatNum == 5 || seatNum == 24 || seatNum == 28)) isWheelchairAccessible = true;
                if (rowId.equalsIgnoreCase("AA") && (seatNum == 10 || seatNum == 20 || seatNum == 34 || seatNum == 49)) isWheelchairAccessible = true;
                if ((rowId.equalsIgnoreCase("A") || rowId.equalsIgnoreCase("L")) && (seatNum == startSeatNum || seatNum == endSeatNum)) isWheelchairAccessible = true;
            }
            if (isWheelchairAccessible) {
                seatButton.getStyleClass().add("seat-wheelchair");
            }

            if (bookedSeatIdsForCurrentEvent.contains(seatId)) {
                seatButton.setDisable(true);
                seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair");
                seatButton.getStyleClass().add("seat-booked");
            } else {
                seatButton.setDisable(false);
                if (!isWheelchairAccessible) {
                    seatButton.getStyleClass().add("seat-available");
                    seatButton.getStyleClass().remove("seat-wheelchair");
                } else {
                    if (!seatButton.getStyleClass().contains("seat-wheelchair")) {
                        seatButton.getStyleClass().add("seat-wheelchair");
                    }
                    seatButton.getStyleClass().remove("seat-available");
                }
            }

            int gridColIndex = seatStartGridCol + (seatNum - startSeatNum);
            seatingPlanGrid.add(seatButton, gridColIndex, gridRowIndex);
            currentGridCol = gridColIndex;
            seatButtonsMap.put(seatId, seatButton);
        }

        // End Row Label
        if (addEndLabel) {
            Label endLabel = new Label(rowId);
            endLabel.setMinWidth(25);
            endLabel.setAlignment(Pos.CENTER_LEFT);
            endLabel.setPadding(new Insets(0, 0, 0, 5));
            seatingPlanGrid.add(endLabel, currentGridCol + 1, gridRowIndex);
        }
    }


    /**
     * Creates a ToggleButton for a seat with the given seat ID, number, and price.
     * @param seatId The seat ID
     * @param seatNumText The seat number text
     * @param price The price of the seat
     * @return The ToggleButton for the seat
     */
    private ToggleButton createSeatButton(String seatId, String seatNumText, float price) {
        ToggleButton seatButton = new ToggleButton(seatNumText);
        seatButton.setUserData(new SeatData(seatId, price));
        seatButton.getStyleClass().add("seat-button");
        seatButton.setPrefSize(35, 30);
        seatButton.setOnAction(this::handleSeatClick);
        return seatButton;
    }


    /**
     * Handles clicks on individual seat ToggleButtons.
     * Updates the seat status in the database via the DAO based on the toggle state.
     * Updates the button style and status label accordingly.
     * Reverts the toggle state if the database update fails.
     *
     * @param event The ActionEvent triggered by clicking a seat button.
     */
    @FXML
    private void handleSeatClick(ActionEvent event) {
        ToggleButton seatButton = (ToggleButton) event.getSource();
        Object userData = seatButton.getUserData();
        String seatId = null;

        if (userData instanceof SeatData) {
            seatId = ((SeatData) userData).getSeatId();
        } else {
            // Fallback or error handling if userData is not SeatData
            System.err.println("Error: UserData for clicked button is not SeatData instance.");
            statusLabel.setText("Error processing seat click.");
            seatButton.setSelected(!seatButton.isSelected());
            return;
        }

        // Proceed only if seatId was successfully retrieved
        if (seatId == null || seatId.trim().isEmpty()) {
            System.err.println("Error: Retrieved seatId is null or empty.");
            statusLabel.setText("Error processing seat click (invalid ID).");
            seatButton.setSelected(!seatButton.isSelected());
            return;
        }


        if (seatButton.isSelected()) {
            // Attempt to mark seat as restricted
            if (eventDAO.updateSeatStatus(seatId, "Restricted")) {
                seatButton.getStyleClass().removeAll("seat-available", "seat-wheelchair", "seat-booked");
                seatButton.getStyleClass().add("seat-restricted");
                statusLabel.setText("Updated seat " + seatId + " to Restricted");
            } else {
                statusLabel.setText("Failed to update seat " + seatId + " status");
                seatButton.setSelected(false);
            }
        } else {
            // Attempt to mark seat as available
            if (eventDAO.updateSeatStatus(seatId, "Available")) {
                seatButton.getStyleClass().remove("seat-restricted");
                boolean isWheelchair = seatButton.getStyleClass().contains("seat-wheelchair-marker");
                if (isWheelchair) {
                    seatButton.getStyleClass().add("seat-wheelchair");
                } else {
                    seatButton.getStyleClass().add("seat-available");
                }

                statusLabel.setText("Updated seat " + seatId + " to Available");
            } else {
                statusLabel.setText("Failed to update seat " + seatId + " status");
                seatButton.setSelected(true); // Revert the toggle on failure
            }
        }
    }

    /**
     * Gets the seating configuration for the selected event.
     * @return The SeatingConfig object
     */
    private SeatingConfig getSeatingConfig() {
        if (selectedEvent == null) return null;

        Integer venueId = new HashMap<String, Integer>() {{
            put("Large Hall", 1);
            put("Small Hall", 2);
        }}.get(selectedEvent.getHallType());

        return new SeatingConfig(venueId, selectedEvent.getEventType());
    }

    /**
     * Finds the price of a seat.
     * @param rowId The row ID
     * @param seatNum The seat number
     * @return The price of the seat
     */
    private float findSeatPrice(String rowId, int seatNum) {
        if (selectedEvent == null) return 0;

        for (Section section : getSeatingConfig().getSections()) {
            for (Seat seat : section.getSeats()) {
                String configSeatId = seat.getSeatID();
                if (configSeatId.contains(rowId) && configSeatId.endsWith(String.valueOf(seatNum))) {
                    return seat.getPrice();
                }
            }
        }
        return 0;
    }
}

/**
 * Helper class to store seat data.
 */
class SeatData {
    private final String seatId;
    private final float price;
    private boolean isWheelchair;
    private String blockedSeatId;

    public SeatData(String seatId, float price) {
        this.seatId = seatId;
        this.price = price;
        this.isWheelchair = false;
        this.blockedSeatId = null;
    }

    public String getSeatId() {
        return seatId;
    }

    public float getPrice() {
        return price;
    }

    public String getBlockedSeatId() {
        return blockedSeatId;
    }

    public boolean isWheelchair() {
        return isWheelchair;
    }

    public void setWheelchair(boolean wheelchair) {
        this.isWheelchair = wheelchair;
    }
    public void setBlockedSeatId(String blockedSeatId) {
        this.blockedSeatId = blockedSeatId;
    }

}
