package com.teamoneboxoffice.services.implementations.DAOs;


import com.teamoneboxoffice.entities.Venue;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {
    private final Database db;

    public VenueDAO() {
        this.db = new Database();
    }

    public int create(Venue venue) {
        String sql = "INSERT INTO Venue (Venue_Name, Venue_In_Use, Venue_Capacity, Is_Meeting_Room) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, venue.getVenueName());
            stmt.setBoolean(2, venue.isVenueInUse());
            stmt.setInt(3, venue.getVenueCapacity());
            stmt.setBoolean(4, venue.IsMeetingRoom());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating venue: " + e.getMessage());
            return -1;
        }
    }
    public Venue getById(int venueId) {
        String sql = "SELECT * FROM Venue WHERE Venue_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVenue(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving venue: " + e.getMessage());
        }

        return null;
    }

    public boolean update(Venue venue, int venueId) {
        String sql = "UPDATE Venue SET Venue_Name = ?, Venue_In_Use = ?, " +
                "Venue_Capacity = ?, Is_Meeting_Room = ? WHERE Venue_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, venue.getVenueName());
            stmt.setBoolean(2, venue.isVenueInUse());
            stmt.setInt(3, venue.getVenueCapacity());
            stmt.setBoolean(4, venue.IsMeetingRoom());
            stmt.setInt(5, venueId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating venue: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int venueId) {
        String sql = "DELETE FROM Venue WHERE Venue_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting venue: " + e.getMessage());
            return false;
        }
    }

    public List<Venue> getAll() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM Venue";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all venues: " + e.getMessage());
        }

        return venues;
    }

    private Venue mapResultSetToVenue(ResultSet rs) throws SQLException {
        String venueName = rs.getString("Venue_Name");
        boolean venueInUse = rs.getBoolean("Venue_In_Use");
        int venueCapacity = rs.getInt("Venue_Capacity");
        boolean isMeetingRoom = rs.getBoolean("Is_Meeting_Room");

        return new Venue(venueName, venueInUse, venueCapacity, isMeetingRoom);
    }
}