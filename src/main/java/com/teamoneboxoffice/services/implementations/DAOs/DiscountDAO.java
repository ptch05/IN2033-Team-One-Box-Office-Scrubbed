package com.teamoneboxoffice.services.implementations.DAOs;

import com.teamoneboxoffice.entities.Discount;
import com.teamoneboxoffice.services.implementations.databaseImpl.Database; // Import your Database class

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountDAO {

    private Database db;

    public DiscountDAO(Database database) {
        this.db = database;
    }

    /**
     * Adds a new discount record to the database.
     * @param discount The Discount object to add.
     * @throws SQLException if a database access error occurs.
     */
    public void addDiscount(Discount discount) throws SQLException {
        String sql = "INSERT INTO Discount (code, percentage, reason) VALUES (?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, discount.getCode());
            pstmt.setInt(2, discount.getPercentage());
            pstmt.setString(3, discount.getReason());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating discount failed, no rows affected.");
            }
            System.out.println("Discount added successfully: " + discount.getCode());

        } catch (SQLException e) {
            System.err.println("Error adding discount: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a Discount object from the database based on its code.
     * @param code The discount code to search for.
     * @return The Discount object if found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    public Discount getDiscountByCode(String code) throws SQLException {
        String sql = "SELECT code, percentage, reason FROM Discount WHERE code = ?";
        Discount discount = null;

        try (Connection conn = db.getConnection(); // Get connection
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Found the discount, create an object
                    String foundCode = rs.getString("code");
                    int percentage = rs.getInt("percentage");
                    String reason = rs.getString("reason");

                    discount = new Discount(foundCode, percentage, reason);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving discount by code: " + e.getMessage());
            throw e;
        }
        return discount;
    }
}
