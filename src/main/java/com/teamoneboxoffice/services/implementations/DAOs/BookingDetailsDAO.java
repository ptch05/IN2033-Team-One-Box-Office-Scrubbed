package com.teamoneboxoffice.services.implementations.DAOs;


import com.teamoneboxoffice.entities.Booking_Details;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookingDetailsDAO {
    private final Database db;

    public BookingDetailsDAO() {
        this.db = new Database();
    }


    public boolean create(Booking_Details booking) {
        String sql = "INSERT INTO Booking_Details (Booking_ID, Status, Customer_ID, Ticket_ID, Event_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getBookingID());
            stmt.setInt(2, booking.getStatus());
            stmt.setInt(3, booking.getCustomerID());
            stmt.setInt(4, booking.getTicketID());
            stmt.setString(5, booking.getEventID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }

    public Booking_Details getById(int bookingId) {
        String sql = "SELECT * FROM Booking_Details WHERE Booking_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving booking: " + e.getMessage());
        }

        return null;
    }


    public boolean update(Booking_Details booking) {
        String sql = "UPDATE Booking_Details SET Status = ?, Customer_ID = ?, Ticket_ID = ? " +
                "WHERE Booking_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getStatus());
            stmt.setInt(2, booking.getCustomerID());
            stmt.setInt(3, booking.getTicketID());
            stmt.setInt(4, booking.getBookingID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int bookingId, int status) {
        String sql = "UPDATE Booking_Details SET Status = ? WHERE Booking_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status);
            stmt.setInt(2, bookingId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int bookingId) {
        String sql = "DELETE FROM Booking_Details WHERE Booking_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    public List<Booking_Details> getAll() {
        List<Booking_Details> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking_Details";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all bookings: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking_Details> getByCustomerId(int customerId) {
        List<Booking_Details> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking_Details WHERE Customer_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving bookings by customer ID: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking_Details> getByTicketId(int ticketId) {
        List<Booking_Details> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking_Details WHERE Ticket_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving bookings by ticket ID: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking_Details> getByStatus(int status) {
        List<Booking_Details> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking_Details WHERE Status = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving bookings by status: " + e.getMessage());
        }

        return bookings;
    }

    private Booking_Details mapResultSetToBooking(ResultSet rs) throws SQLException {
        int bookingId = rs.getInt("Booking_ID");
        int status = rs.getInt("Status");
        int customerId = rs.getInt("Customer_ID");
        int ticketId = rs.getInt("Ticket_ID");
        String eventId = rs.getString("Event_ID");

        return new Booking_Details(bookingId, status, customerId, ticketId, eventId);
    }


}
