package com.teamoneboxoffice.services.implementations.DAOs;

import com.teamoneboxoffice.entities.Friends_Of_Lancaster;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Friends_Of_Lancaster entities.
 * Interacts with the dedicated 'Friends_Of_Lancaster' table.
 */
public class FriendsOfLancasterDAO {

    private final Database db;

    /**
     * Constructs a new FriendsOfLancasterDAO.
     */
    public FriendsOfLancasterDAO() {
        this.db = new Database();
    }

    /**
     * Retrieves a list of all members from the Friends_Of_Lancaster table.
     *
     * @return A List of Friends_Of_Lancaster objects. Returns an empty list if none are found or an error occurs.
     */
    public List<Friends_Of_Lancaster> getAllFriends() {
        List<Friends_Of_Lancaster> friendsList = new ArrayList<>();
        String sql = "SELECT FriendID, Name, Email, PhoneNumber FROM Friends_Of_Lancaster ORDER BY Name";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                friendsList.add(mapResultSetToFriend(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all Friends of Lancaster: " + e.getMessage());
            e.printStackTrace();
        }
        return friendsList;
    }

    /**
     * Retrieves a specific Friend of Lancaster by their ID.
     *
     * @param friendId The ID of the friend to retrieve.
     * @return The Friends_Of_Lancaster object if found, otherwise null.
     */
    public Friends_Of_Lancaster getFriendById(int friendId) {
        String sql = "SELECT FriendID, Name, Email, PhoneNumber FROM Friends_Of_Lancaster WHERE FriendID = ?";
        Friends_Of_Lancaster friend = null;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, friendId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    friend = mapResultSetToFriend(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving Friend of Lancaster by ID [" + friendId + "]: " + e.getMessage());
            e.printStackTrace();
        }
        return friend;
    }

    /**
     * Helper method to map a row from the ResultSet to a Friends_Of_Lancaster object.
     * Handles potential null values for dates and phone number.
     *
     * @param rs The ResultSet pointing to the current row.
     * @return A populated Friends_Of_Lancaster object.
     * @throws SQLException If a database access error occurs.
     */
    private Friends_Of_Lancaster mapResultSetToFriend(ResultSet rs) throws SQLException {
        int friendID = rs.getInt("FriendID");
        String name = rs.getString("Name");
        String email = rs.getString("Email");
        String phoneNumber = rs.getString("PhoneNumber");

        return new Friends_Of_Lancaster(friendID, name, email, phoneNumber);
    }
}
