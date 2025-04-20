package com.teamoneboxoffice.services.implementations.DAOs;

import com.teamoneboxoffice.entities.Event;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object for Event entities. Handles database operations related to events.
 */
public class EventDAO {
    private final Database db;

    public EventDAO() {
        this.db = new Database();
    }

    /**
     * Creates a new event in the database.
     *
     * @param event The event to create
     * @return true if creation was successful
     */
    public boolean create(Event event) {
        String sql =
                "INSERT INTO Event (Event_ID, Event_Type, Event_Name, Event_Price, "
                        + "Hall_Type, Event_Date, Event_Time) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getEventType());
            stmt.setString(3, event.getEventName());
            stmt.setDouble(4, event.getEventPrice());
            stmt.setString(5, event.getHallType());
            // Ensure event.getEventDate() is not null before calling getTime()
            if (event.getEventDate() != null) {
                stmt.setDate(6, new java.sql.Date(event.getEventDate().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.setString(7, event.getEventTime());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId The ID of the event to retrieve
     * @return The event object or null if not found
     */
    public Event getById(String eventId) {
        String sql = "SELECT * FROM Event WHERE Event_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving event by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing event in the database.
     *
     * @param event The event to update
     * @return true if update was successful
     */
    public boolean update(Event event) {
        String sql =
                "UPDATE Event SET Event_Type = ?, Event_Name = ?, Event_Price = ?, "
                        + "Hall_Type = ?, Event_Date = ?, Event_Time = ?, "
                        + "Ticket_Revenue = ?, Ticket_Numbers = ? WHERE Event_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getEventType());
            stmt.setString(2, event.getEventName());
            stmt.setDouble(3, event.getEventPrice());
            stmt.setString(4, event.getHallType());
            if (event.getEventDate() != null) {
                stmt.setDate(5, new java.sql.Date(event.getEventDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setString(6, event.getEventTime());
            stmt.setDouble(7, event.getTicketRevenue());
            stmt.setInt(8, event.getTicketNumbers());
            stmt.setString(9, event.getEventID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates ticket statistics for an event.
     *
     * @param eventId The ID of the event to update
     * @param ticketRevenue The new ticket revenue
     * @param ticketNumbers The new ticket numbers
     * @return true if update was successful
     */
    public boolean updateTicketStats(String eventId, double ticketRevenue, int ticketNumbers) {
        String sql = "UPDATE Event SET Ticket_Revenue = ?, Ticket_Numbers = ? WHERE Event_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, ticketRevenue);
            stmt.setInt(2, ticketNumbers);
            stmt.setString(3, eventId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating event ticket stats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an event from the database.
     *
     * @param eventId The ID of the event to delete
     * @return true if deletion was successful
     */
    public boolean delete(String eventId) {
        String sql = "DELETE FROM Event WHERE Event_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all events from the database.
     *
     * @return A list of all events
     */
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all events: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Retrieves events of a specific type.
     *
     * @param eventType The type of events to retrieve
     * @return A list of events of the specified type
     */
    public List<Event> getByType(String eventType) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE Event_Type = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving events by type: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Retrieves events occurring on a specific date.
     *
     * @param date The date to filter events by (java.util.Date)
     * @return A list of events on the specified date
     */
    public List<Event> getByDate(java.util.Date date) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE Event_Date = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (date != null) {
                stmt.setDate(1, new java.sql.Date(date.getTime()));
            } else {
                System.err.println("Attempted to query events by null date.");
                return events;
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving events by date: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Maps a ResultSet row to an Event object. Handles potential null values
     * and type conversions from the database.
     *
     * @param rs The ResultSet containing event data for the current row.
     * @return The mapped Event object.
     * @throws SQLException If data retrieval fails.
     */
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        String eventID = rs.getString("Event_ID");
        String eventName = rs.getString("Event_Name");
        String eventType = rs.getString("Event_Type");
        double eventPrice = rs.getDouble("Event_Price");

        String hallType = rs.getString("Hall_Type");
        java.sql.Date sqlDate = rs.getDate("Event_Date");
        java.util.Date eventDate = (sqlDate != null) ? new java.util.Date(sqlDate.getTime()) : null;
        String eventTime = rs.getString("Event_Time");

        Event event =
                new Event(eventID, eventName, eventType, eventPrice, hallType, eventDate, eventTime);

        double ticketRevenueValue = rs.getDouble("Ticket_Revenue");
        if (!rs.wasNull()) {
            event.setTicketRevenue(ticketRevenueValue);
        }
        int ticketNumbersValue = rs.getInt("Ticket_Numbers");
        if (!rs.wasNull()) {
            event.setTicketNumbers(ticketNumbersValue);
        }

        return event;
    }


    /**
     * Updates the status of a specific seat in the MainHallSeats table.
     *
     * @param seatId The ID of the seat to update.
     * @param status The new status (e.g., "Available", "Restricted").
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateSeatStatus(String seatId, String status) {
        // IMPORTANT: Make sure 'MainHallSeats' and 'Seat_ID' are correct table/column names
        String sql = "UPDATE MainHallSeats SET Status = ? WHERE Seat_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, seatId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating seat status for seat " + seatId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a set of all booked seat IDs from the Booked_Seats table.
     * NOTE: Ideally, this should be filtered by eventId if your schema supports it.
     * Example: getBookedSeatIdsForEvent(String eventId)
     *
     * @return A Set containing the IDs of all booked seats found in the table. Returns an empty set on error.
     */
    public Set<String> getAllBookedSeatIds() {
        Set<String> bookedSeats = new HashSet<>();
        String sql = "SELECT Seat_ID FROM Booked_Seats";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String seatId = rs.getString("Seat_ID");
                    if (seatId != null && !seatId.trim().isEmpty()) {
                        bookedSeats.add(seatId.trim());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving booked seat IDs: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Fetched " + bookedSeats.size() + " booked seat IDs.");
        return bookedSeats;
    }

    /**
     * Checks if a specific seat is booked by looking it up in the Booked_Seats table.
     * NOTE: This method is inefficient if called repeatedly in a loop.
     * Use getAllBookedSeatIds() and check against the returned set instead for performance.
     *
     * @param seatId The ID of the seat to check.
     * @return true if the seat exists in the Booked_Seats table, false otherwise or on error.
     */
    @Deprecated
    public boolean isSeatBooked(String seatId) {
        String sql = "SELECT 1 FROM Booked_Seats WHERE Seat_ID = ? LIMIT 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seatId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking booked status for seat " + seatId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
