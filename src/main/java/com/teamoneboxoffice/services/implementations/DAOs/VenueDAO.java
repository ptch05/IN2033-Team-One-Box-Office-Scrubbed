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

    /**
     * Creates a new venue in the database.
     *
     * @param venue The venue to be created.
     * @return The ID of the created venue, or -1 if the creation failed.
     */
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

    /**
     * Retrieves a venue by its ID.
     *
     * @param venueId The ID of the venue to retrieve.
     * @return The venue with the specified ID, or null if not found.
     */
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

    /**
     * Updates an existing venue's information in the database.
     *
     * @param venue The Venue object containing the updated venue information
     * @param venueId The ID of the venue to be updated
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Deletes a venue from the database.
     *
     * @param venueId The ID of the venue to delete.
     * @return true if the deletion was successful, false otherwise.
     */
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

    /**
     * Retrieves all venues from the database.
     *
     * @return A list of all venues.
     */
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

    /**
     * Maps a ResultSet row to a Venue object.
     *
     * @param rs The ResultSet containing venue data to be mapped
     * @return A new Venue object populated with data from the ResultSet
     * @throws SQLException If there is an error accessing the ResultSet data
     */
    private Venue mapResultSetToVenue(ResultSet rs) throws SQLException {
        String venueName = rs.getString("Venue_Name");
        boolean venueInUse = rs.getBoolean("Venue_In_Use");
        int venueCapacity = rs.getInt("Venue_Capacity");
        boolean isMeetingRoom = rs.getBoolean("Is_Meeting_Room");

        return new Venue(venueName, venueInUse, venueCapacity, isMeetingRoom);
    }
}