package com.teamoneboxoffice.services.implementations.DAOs;


import com.teamoneboxoffice.entities.Ticket;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    private final Database db;

    public TicketDAO() {
        this.db = new Database();
    }

    /**
     * Creates a new ticket in the database.
     *
     * @param ticket The ticket to be created.
     * @return true if the ticket was created successfully, false otherwise.
     */
    public boolean create(Ticket ticket) {
        String sql = "INSERT INTO Ticket (Ticket_ID, Seat_Number, Row_Number, Hall, " +
                "Ticket_Type, Eligible_For_Discount, Wheelchair, Price, Priority_Status, Customer_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getTicketID());
            stmt.setInt(2, ticket.getSeatNumber());
            stmt.setInt(3, ticket.getRowNumber());
            stmt.setString(4, ticket.getHall());
            stmt.setString(5, ticket.getTicketType());
            stmt.setBoolean(6, ticket.getEligibleForDiscount());
            stmt.setBoolean(7, ticket.getIsWheelchair());
            stmt.setDouble(8, ticket.getPrice());
            stmt.setString(9, ticket.getPriorityStatus());
            stmt.setString(10, ticket.getCustomerID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a ticket by its ID.
     *
     * @param ticketId The ID of the ticket to retrieve.
     * @return The ticket with the specified ID, or null if not found.
     */
    public Ticket getById(String ticketId) {
        String sql = "SELECT * FROM Ticket WHERE Ticket_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving ticket: " + e.getMessage());
        }

        return null;
    }

    /**
     * Updates an existing ticket in the database.
     *
     * @param ticket The ticket to be updated.
     * @return true if the ticket was updated successfully, false otherwise.
     */
    public boolean update(Ticket ticket) {
        String sql = "UPDATE Ticket SET Seat_Number = ?, Row_Number = ?, Hall = ?, " +
                "Ticket_Type = ?, Eligible_For_Discount = ?, Wheelchair = ?, " +
                "Price = ?, Priority_Status = ?, Customer_ID = ? WHERE Ticket_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getSeatNumber());
            stmt.setInt(2, ticket.getRowNumber());
            stmt.setString(3, ticket.getHall());
            stmt.setString(4, ticket.getTicketType());
            stmt.setBoolean(5, ticket.getEligibleForDiscount());
            stmt.setBoolean(6, ticket.getIsWheelchair());
            stmt.setDouble(7, ticket.getPrice());
            stmt.setString(8, ticket.getPriorityStatus());
            stmt.setString(9, ticket.getCustomerID());
            stmt.setString(10, ticket.getTicketID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a ticket and its associated booked seats within a transaction.
     * First deletes records from Booked_Seats, then deletes the Ticket record.
     *
     * @param ticketId The ID of the ticket to delete.
     * @return true if the ticket and associated booked seats were successfully deleted, false otherwise.
     */
    public boolean delete(String ticketId) {
        Connection conn = null;
        PreparedStatement deleteBookedSeatsStmt = null;
        PreparedStatement deleteTicketStmt = null;
        String deleteBookedSeatsSql = "DELETE FROM Booked_Seats WHERE Ticket_ID = ?";
        String deleteTicketSql = "DELETE FROM Ticket WHERE Ticket_ID = ?";
        boolean success = false;

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // 1. Delete from Booked_Seats first
            deleteBookedSeatsStmt = conn.prepareStatement(deleteBookedSeatsSql);
            deleteBookedSeatsStmt.setString(1, ticketId);
            int bookedSeatsDeleted = deleteBookedSeatsStmt.executeUpdate();
            System.out.println("Deleted " + bookedSeatsDeleted + " rows from Booked_Seats for Ticket_ID: " + ticketId);

            // 2. Delete from Ticket
            deleteTicketStmt = conn.prepareStatement(deleteTicketSql);
            deleteTicketStmt.setString(1, ticketId);
            int ticketDeletedRows = deleteTicketStmt.executeUpdate();

            if (ticketDeletedRows > 0) {
                conn.commit();
                System.out.println("Ticket deleted successfully: " + ticketId);
                success = true;
            } else {
                System.out.println("Ticket not found for deletion: " + ticketId + ". Rolling back potential Booked_Seats deletion.");
                conn.rollback();
                success = false;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting ticket transactionally [" + ticketId + "]: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.err.println("Rolling back transaction due to error.");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            success = false;

        } finally {
            try { if (deleteBookedSeatsStmt != null) deleteBookedSeatsStmt.close(); } catch (SQLException e) {}
            try { if (deleteTicketStmt != null) deleteTicketStmt.close(); } catch (SQLException e) {}
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {}
        }

        return success;
    }

    /**
     * Retrieves all tickets from the database.
     *
     * @return A list of all tickets.
     */
    public List<Ticket> getAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all tickets: " + e.getMessage());
        }

        return tickets;
    }

    /**
     * Retrieves tickets by customer ID from the database.
     *
     * @param customerId The ID of the customer whose tickets to retrieve.
     * @return A list of tickets associated with the specified customer ID.
     */
    public List<Ticket> getByCustomerId(String customerId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE Customer_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving tickets by customer ID: " + e.getMessage());
        }

        return tickets;
    }


    /**
     * Retrieves tickets by hall from the database.
     *
     * @param hall The hall of the tickets to retrieve.
     * @return A list of tickets associated with the specified hall.
     */
    public List<Ticket> getByHall(String hall) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE Hall = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hall);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving tickets by hall: " + e.getMessage());
        }

        return tickets;
    }

    /**
     * Retrieves tickets by ticket type from the database.
     *
     * @param ticketType The type of the tickets to retrieve.
     * @return A list of tickets associated with the specified ticket type.
     */
    public List<Ticket> getByTicketType(String ticketType) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE Ticket_Type = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticketType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving tickets by type: " + e.getMessage());
        }

        return tickets;
    }

    /**
     * Maps a ResultSet row to a Ticket object.
     *
     * @param rs The ResultSet containing the ticket data.
     * @return A Ticket object populated with the data from the ResultSet.
     * @throws SQLException If an SQL error occurs while mapping the ResultSet.
     */
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        String ticketID = rs.getString("Ticket_ID");
        int seatNumber = rs.getInt("Seat_Number");
        int rowNumber = rs.getInt("Row_Number");
        String hall = rs.getString("Hall");
        String ticketType = rs.getString("Ticket_Type");
        boolean eligibleForDiscount = rs.getBoolean("Eligible_For_Discount");
        boolean wheelchair = rs.getBoolean("Wheelchair");
        double price = rs.getDouble("Price");
        String priorityStatus = rs.getString("Priority_Status");

        return new Ticket(ticketID, seatNumber, rowNumber, hall, ticketType,
                eligibleForDiscount, wheelchair, price, priorityStatus);
    }
}