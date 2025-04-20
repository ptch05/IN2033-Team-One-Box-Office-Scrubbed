package com.teamoneboxoffice.entities;

/**
 * Represents a seat with its properties and booking status.
 */
public class Seat {
    private String seatID;
    private int row;
    private int column;
    private float price;
    private boolean isBooked;
    private boolean isVip;
    private boolean isRestrictedView;

    /**
     * Constructs a new Seat object.
     *
     * @param seatID The unique identifier for the seat.
     * @param row    The row number of the seat.
     * @param column The column number of the seat.
     * @param price  The base price of the seat.
     */
    public Seat(String seatID, int row, int column, float price) {
        this.seatID = seatID;
        this.row = row;
        this.column = column;
        this.price = price;
        this.isBooked = false;
        this.isVip = false;
        this.isRestrictedView = false;
    }

    /**
     * Gets the seat ID.
     *
     * @return The seat ID.
     */
    public String getSeatID() {
        return seatID;
    }

    /**
     * Gets the row number of the seat.
     *
     * @return The row number.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column number of the seat.
     *
     * @return The column number.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the price of the seat.
     *
     * @return The price of the seat.
     */
    public float getPrice() {
        return price;
    }

    /**
     * Sets the price of the seat.
     *
     * @param price The price to set.
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Checks if the seat is booked.
     *
     * @return True if the seat is booked, false otherwise.
     */
    public boolean isBooked() {
        return isBooked;
    }

    /**
     * Sets the booked status of the seat.
     *
     * @param booked The booked status to set.
     */
    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    /**
     * Checks if the seat is a VIP seat.
     *
     * @return True if the seat is a VIP seat, false otherwise.
     */
    public boolean isVip() {
        return isVip;
    }

    /**
     * Sets the VIP status of the seat. If set to true, the price is increased by 50%.
     *
     * @param vip The VIP status to set.
     */
    public void setVip(boolean vip) {
        this.isVip = vip;
        if (vip) {
            this.price *= 1.5f;
        }
    }

    /**
     * Checks if the seat has a restricted view.
     *
     * @return True if the seat has a restricted view, false otherwise.
     */
    public boolean isRestrictedView() {
        return isRestrictedView;
    }

    /**
     * Sets the restricted view status of the seat. If set to true, the price is decreased by 30%.
     *
     * @param restrictedView The restricted view status to set.
     */
    public void setRestrictedView(boolean restrictedView) {
        this.isRestrictedView = restrictedView;
        if (restrictedView) {
            this.price *= 0.7f;
        }
    }

    /**
     * Gets a string representation of the seat's status (Booked, VIP, Restricted View, or Available).
     *
     * @return The status string.
     */
    public String getStatusString() {
        if (isBooked) {
            return "Booked";
        } else if (isVip) {
            return "VIP";
        } else if (isRestrictedView) {
            return "Restricted View";
        } else {
            return "Available";
        }
    }

    /**
     * Returns a string representation of the Seat object.
     *
     * @return A string containing the seat ID, status, and price.
     */
    @Override
    public String toString() {
        return (
                seatID +
                " (" +
                getStatusString() +
                ", £" +
                String.format("%.2f", price) +
                ")"
        );
    }
}
