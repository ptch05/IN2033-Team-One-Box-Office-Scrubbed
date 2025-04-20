package com.teamoneboxoffice.services.implementations.DAOs;

import com.teamoneboxoffice.entities.User;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Database db;

    public UserDAO() {
        this.db = new Database();
    }

    /**
     * Creates a new user in the database.
     *
     * @param user The user to be created.
     * @return true if the user was created successfully, false otherwise.
     */
    public boolean create(User user) {
        String sql = "INSERT INTO User (User_ID, User_Name, Password, Role, Is_Active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setBoolean(5, user.isActive());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user with the specified ID, or null if not found.
     */
    public User getById(long userId) {
        String sql = "SELECT * FROM User WHERE User_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The user with the specified username, or null if not found.
     */
    public User getByUsername(String username) {
        String sql = "SELECT * FROM User WHERE User_Name = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user by username: " + e.getMessage());
        }

        return null;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user The user with updated information.
     * @return true if the user was updated successfully, false otherwise.
     */
    public boolean update(User user) {
        String sql = "UPDATE User SET User_Name = ?, Password = ?, Role = ?, Is_Active = ? " +
                "WHERE User_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setBoolean(4, user.isActive());
            stmt.setLong(5, user.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the active status of a user in the database.
     * 
     * @param userId The unique identifier of the user to update
     * @param isActive The new active status to set (true for active, false for inactive)
     * @return boolean Returns true if the update was successful, false if it failed
     */
    public boolean updateActiveStatus(long userId, boolean isActive) {
        String sql = "UPDATE User SET Is_Active = ? WHERE User_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isActive);
            stmt.setLong(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user active status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    public boolean delete(long userId) {
        String sql = "DELETE FROM User WHERE User_ID = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users.
     */
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Retrieves users by their role from the database.
     *
     * @param role The role of the users to retrieve.
     * @return A list of users with the specified role.
     */
    public List<User> getByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE Role = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving users by role: " + e.getMessage());
        }

        return users;
    }

    /**
     * Authenticates a user by checking their username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The authenticated user, or null if authentication failed.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM User WHERE User_Name = ? AND Password = ? AND Is_Active = true";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Maps a ResultSet to a User object.
     *
     * @param rs The ResultSet to map.
     * @return The mapped User object.
     * @throws SQLException If an SQL error occurs.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("User_ID");
        String userName = rs.getString("User_Name");
        String password = rs.getString("Password");
        String role = rs.getString("Role");
        boolean isActive = rs.getBoolean("Is_Active");

        return new User(id, userName, password, role, isActive);
    }
}