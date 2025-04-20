package com.teamoneboxoffice.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;
/**
 * Controller for the Customer Booking Info screen (CustomerBookingInfoPage.fxml).
 * Collects customer details, displays timer, and finalizes the booking with confirmation.
 */

import java.sql.ResultSet;
import java.util.Random;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class CustomerBookingInfoPageController implements Initializable {

    // --- Input Fields ---
    @FXML private TextField emailTextField;
    @FXML private TextField nameTextField;
    @FXML private TextField phoneTextField;

    // --- Display Fields ---
    @FXML private TextField ticketNumberField;
    @FXML private TextField numSeatsField;
    @FXML private TextField seatsField;
    @FXML private TextField wheelchairSeatField;
    @FXML private TextField customerIdField;
    @FXML private TextField totalPriceField;
    @FXML private TextField discountAppliedField;
    @FXML private Label timerLabel;

    // --- Action Buttons ---
    @FXML private Button saveButton;
    @FXML private Button lookupCustomerButton;

    private List<String> bookedSeatIds;
    private float baseTotalPrice = 0.0f;
    private int discountPercentage = 0;

    private boolean isWheelchairBooking;
    private int numberOfSeats;

    // --- Timer ---
    private Timeline countdownTimeline;
    private int timeSeconds;

    // --- Event ID ---
    private String eventId;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("CustomerBookingInfoPageController initialize method called."); // LOGGING
        generateInitialData();
        System.out.println("CustomerBookingInfoPageController initialize finished."); // LOGGING
    }

    /**
     * Generates automatic data like Ticket Number.
     * Customer ID will be determined either by lookup or new generation.
     */
    private void generateInitialData() {
        String ticketNum = "TKT" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        ticketNumberField.setText(ticketNum);

        // Generate default customer ID
        customerIdField.setText(generateCustomerId());

        // Initialize discount field
        discountAppliedField.setText("0%");
    }

    /**
     * Generates a unique customer ID in the format used in the database (CUS_XXX##)
     */
    private String generateCustomerId() {
        // Generate random 3 uppercase letters
        String letters = UUID.randomUUID().toString().substring(0, 3).toUpperCase();

        // Generate random 2-digit number
        int number = new Random().nextInt(100);

        // Format: CUS_XXX## (matching your database format)
        return String.format("CUS_%s%02d", letters, number);
    }

    /**
     * Called by MakeBookingPageController AFTER FXML load.
     * Populates fields, calculates final price including discount, and starts the timer.
     * @param seatIds List of selected seat IDs.
     * @param quantity The number of seats selected.
     * @param isWheelchair True if wheelchair space required.
     * @param remainingSeconds The time left on the reservation timer.
     * @param eventId The ID of the event being booked.
     * @param discountPercentage The percentage discount applied (0 if none).
     * @param baseTotalPrice The total price of selected seats before discount.
     */
    public void setBookingDetails(List<String> seatIds, int quantity, boolean isWheelchair,
                                  int remainingSeconds, String eventId, int discountPercentage, float baseTotalPrice) {

        System.out.println("--- CustomerBookingInfoPageController.setBookingDetails ---"); // LOGGING
        System.out.println("Received discountPercentage: " + discountPercentage); // LOGGING
        System.out.println("Received baseTotalPrice: " + baseTotalPrice); // LOGGING

        this.bookedSeatIds = seatIds;
        this.numberOfSeats = quantity;
        this.isWheelchairBooking = isWheelchair;
        this.timeSeconds = remainingSeconds;
        this.eventId = eventId;
        this.discountPercentage = discountPercentage;
        this.baseTotalPrice = baseTotalPrice;

        numSeatsField.setText(String.valueOf(this.numberOfSeats));
        seatsField.setText(String.join(", ", this.bookedSeatIds));
        wheelchairSeatField.setText(this.isWheelchairBooking ? "Yes" : "No");

        if (discountAppliedField != null) {
            System.out.println("Setting discountAppliedField text to: " + this.discountPercentage + "%"); // LOGGING
            discountAppliedField.setText(this.discountPercentage + "%");
        } else {
            System.err.println("ERROR: discountAppliedField is null in setBookingDetails!"); // LOGGING
        }

        float discountMultiplier = 1.0f - (this.discountPercentage / 100.0f);
        float finalPrice = this.baseTotalPrice * discountMultiplier;
        System.out.println("Calculated finalPrice: " + finalPrice); // LOGGING

        if (totalPriceField != null) {
            totalPriceField.setText(String.format("£%.2f", finalPrice));
            System.out.println("Set totalPriceField text."); // LOGGING
        } else {
            System.err.println("ERROR: totalPriceField is null in setBookingDetails!"); // LOGGING
        }

        startTimer();
        System.out.println("--- Finished setBookingDetails ---"); // LOGGING
    }


    /**
     * Starts the countdown timer with the remaining time.
     */
    private void startTimer() {
        stopTimer();
        updateTimerLabel();

        if (timeSeconds <= 0) {
            handleTimerExpiration();
            return;
        }

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            updateTimerLabel();
            if (timeSeconds <= 0) {
                handleTimerExpiration();
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
        System.out.println("Customer Info Timer started with " + timeSeconds + " seconds.");
    }

    /**
     * Stops the countdown timer if it's running.
     */
    private void stopTimer() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
            System.out.println("Customer Info Timer stopped.");
        }
    }

    /**
     * Updates the timer label display (MM:SS format).
     */
    private void updateTimerLabel() {
        if (timerLabel != null) {
            int minutes = timeSeconds / 60;
            int seconds = timeSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    /**
     * Handles actions when the timer expires.
     */
    private void handleTimerExpiration() {
        stopTimer();
        showAlert(Alert.AlertType.WARNING, "Time Expired", "Your 10-minute reservation has expired. Please start the booking process again.");
        // Disable saving and potentially navigate back automatically
        saveButton.setDisable(true);
        emailTextField.setDisable(true);
        nameTextField.setDisable(true);
        phoneTextField.setDisable(true);
    }

    /**
     * Handle the customer lookup functionality.
     * Searches the database for a customer with the entered email and populates fields if found.
     */
    @FXML
    void handleLookupCustomer(ActionEvent event) {
        String email = emailTextField.getText();

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            showAlert(AlertType.WARNING, "Invalid Email", "Please enter a valid email address to lookup a customer.");
            return;
        }

        Database db = new Database();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = db.getConnection();

            String sql = "SELECT Customer_ID, Name, Phone FROM Customer WHERE Email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Customer found
                String customerId = rs.getString("Customer_ID");
                String name = rs.getString("Name");
                String phone = rs.getString("Phone");

                // Populate fields
                customerIdField.setText(customerId);
                nameTextField.setText(name);
                phoneTextField.setText(phone);

                showAlert(AlertType.INFORMATION, "Customer Found",
                        "Found existing customer: " + name + "\nCustomer ID: " + customerId);
            } else {
                // Customer not found
                showAlert(AlertType.INFORMATION, "Customer Not Found",
                        "No customer found with email: " + email + "\nA new Customer ID will be generated.");

                // Generate a new customer ID
                String customerId = generateCustomerId();
                customerIdField.setText(customerId);
            }

        } catch (SQLException e) {
            System.err.println("Database error during customer lookup: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error",
                    "Could not lookup customer: " + e.getMessage());

            String customerId = generateCustomerId();
            customerIdField.setText(customerId);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the Save Booking button action.
     * Validates input, shows confirmation, stops timer, and saves to database.
     */
    @FXML
    void handleSaveBooking(ActionEvent event) {
        String email = emailTextField.getText();
        String name = nameTextField.getText();
        String phone = phoneTextField.getText();
        String ticketNumber = ticketNumberField.getText();
        String customerId = customerIdField.getText();

        if (email.isEmpty() || !email.contains("@") || name.isEmpty() || phone.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please enter valid Name, Email, and Phone Number.");
            return;
        }

        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Booking");
        confirmationAlert.setHeaderText("Save this booking?");
        confirmationAlert.setContentText("Customer: " + name + "\nSeats: " + seatsField.getText() + "\nPrice: " + totalPriceField.getText() + "\n\nAre you sure?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK){
            stopTimer(); // Stop timer on successful save confirmation

            System.out.println("--- Saving Booking (Confirmed) ---");
            System.out.println("Customer Name: " + name);
            System.out.println("Customer ID: " + customerId);
            System.out.println("Event ID: " + eventId);

            // Save to database
            boolean saveSuccess = saveBookingToDatabase(
                    ticketNumber,
                    customerId,
                    name,
                    email,
                    phone,
                    bookedSeatIds,
                    isWheelchairBooking
            );

            if (saveSuccess) {
                showAlert(AlertType.INFORMATION, "Booking Saved", "Booking details saved successfully!");

                // Disable fields/button after successful save
                saveButton.setDisable(true);
                emailTextField.setEditable(false);
                nameTextField.setEditable(false);
                phoneTextField.setEditable(false);
            } else {
                showAlert(AlertType.ERROR, "Save Error", "Could not save booking to database. Please try again.");
            }
        } else {
            System.out.println("Booking save cancelled by user.");
        }
    }

    /**
     * Saves booking information to the database according to the provided SQL schema.
     *
     * @param ticketNumber Unique ticket identifier
     * @param customerId Unique customer identifier
     * @param customerName Customer's name
     * @param email Customer's email address
     * @param phone Customer's phone number
     * @param seatIds List of booked seat IDs
     * @param isWheelchair Whether the booking includes wheelchair access
     * @return true if save was successful, false otherwise
     */
    private boolean saveBookingToDatabase(String ticketNumber, String customerId,
                                          String customerName, String email, String phone,
                                          List<String> seatIds, boolean isWheelchair) {
        Database db = new Database();
        Connection conn = null;
        float finalPrice = Float.parseFloat(totalPriceField.getText().replace("£", "")); // Get final price from field

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            boolean customerExists = checkIfCustomerExists(conn, customerId, email);

            if (!customerExists) {
                System.out.println("Creating new customer: " + customerId);
                createCustomer(conn, customerId, customerName, email, phone);
                if (!checkIfCustomerExists(conn, customerId, email)) {
                    throw new SQLException("Failed to create customer record");
                }
            } else {
                System.out.println("Customer already exists: " + customerId);
            }

            System.out.println("Creating ticket with Customer_ID: " + customerId);
            // Pass the final calculated price to createTicket
            createTicket(conn, ticketNumber, customerId, isWheelchair, eventId, seatIds.get(0), finalPrice);

            System.out.println("Creating booking entry");
            createBooking(conn, ticketNumber, customerId);

            System.out.println("Adding booked seats");
            addBookedSeats(conn, ticketNumber, seatIds);

            conn.commit();
            System.out.println("Booking saved successfully to database.");
            return true;

        } catch (SQLException e) {
            System.err.println("Database error while saving booking: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); }
            catch (SQLException ex) { System.err.println("Error rolling back transaction: " + ex.getMessage()); }
            return false;

        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } }
            catch (SQLException e) { System.err.println("Error closing database connection: " + e.getMessage()); }
        }
    }


    /**
     * Checks if a customer exists in the database.
     */
    private boolean checkIfCustomerExists(Connection conn, String customerId, String email) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Check by customer ID
            String sql = "SELECT COUNT(*) FROM Customer WHERE Customer_ID = ? OR Email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, customerId);
            stmt.setString(2, email);

            rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Creates a new customer in the database.
     */
    private void createCustomer(Connection conn, String customerId, String name, String email, String phone)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO Customer (Customer_ID, Name, Email, Phone) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, customerId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, phone);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            System.out.println("Created new customer: " + customerId);

        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Creates a ticket entry in the database.
     * @param conn The database connection.
     * @param ticketId The unique ID for the ticket.
     * @param customerId The ID of the customer making the booking.
     * @param isWheelchair Whether the booking is for a wheelchair space.
     * @param eventId The ID of the event.
     * @param seatId The primary seat ID associated with the ticket (can be one of many).
     * @param finalPrice The calculated final price for the ticket after discounts.
     * @throws SQLException If a database error occurs.
     */
    private void createTicket(Connection conn, String ticketId, String customerId, boolean isWheelchair,
                              String eventId, String seatId, float finalPrice) throws SQLException { // Added finalPrice parameter
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO Ticket (Ticket_ID, Hall, Ticket_Type, Eligible_For_Discount, " +
                    "Wheelchair, Price, Priority_Status, Customer_ID, Event_ID, Seat_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, ticketId);
            stmt.setString(2, "Unknown"); // Placeholder - Fetch Hall Type based on eventId if needed
            stmt.setString(3, "Standard"); // Default ticket type
            stmt.setBoolean(4, this.discountPercentage > 0); // Eligible if discount was applied
            stmt.setBoolean(5, isWheelchair);
            stmt.setDouble(6, finalPrice); // Use the calculated final price
            stmt.setString(7, "Low");  // Default priority
            stmt.setString(8, customerId);
            stmt.setString(9, eventId);
            stmt.setString(10, seatId); // Link to one seat (Booked_Seats links all)

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }
            System.out.println("Created ticket: " + ticketId + " with final price: " + finalPrice);

        } finally {
            if (stmt != null) stmt.close();
        }
    }


    /**
     * Creates a booking entry in the database.
     */
    private void createBooking(Connection conn, String ticketId, String customerId) throws SQLException {
        PreparedStatement stmt = null;

        try {
            String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 6);

            String sql = "INSERT INTO Booking_Details (Booking_ID, Status, Customer_ID, Ticket_ID) " +
                    "VALUES (?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookingId);
            stmt.setBoolean(2, true); // Active booking
            stmt.setString(3, customerId);
            stmt.setString(4, ticketId);

            stmt.executeUpdate();
            System.out.println("Created booking: " + bookingId);

        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Adds booked seats to the database.
     */
    private void addBookedSeats(Connection conn, String ticketId, List<String> seatIds) throws SQLException {
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO Booked_Seats (Seat_ID, Ticket_ID) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);

            for (String seatId : seatIds) {
                stmt.setString(1, seatId);
                stmt.setString(2, ticketId);
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            System.out.println("Added " + results.length + " booked seats");

        } finally {
            if (stmt != null) stmt.close();
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