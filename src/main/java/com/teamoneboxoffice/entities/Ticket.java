package com.teamoneboxoffice.entities;

/**
 * Represents a ticket for an event.
 */
public class Ticket {

    private String ticketID;
    private String customerID;
    private int seatNumber;
    private int rowNumber;
    private String hall;
    private String ticketType;
    private boolean eligibleForDiscount;
    private boolean wheelchair;
    private double price;
    private String priorityStatus;

    /**
     * Constructs a new Ticket object.
     *
     * @param ticketID          The unique identifier for the ticket.
     * @param seatNumber        The seat number of the ticket.
     * @param rowNumber         The row number of the ticket.
     * @param hall              The hall where the seat is located.
     * @param ticketType        The type of ticket (e.g., Adult, Child, Concession).
     * @param eligibleForDiscount Indicates if the ticket is eligible for a discount.
     * @param isWheelchair      Indicates if the seat is wheelchair accessible.
     * @param price             The price of the ticket.
     * @param priorityStatus    The priority status of the ticket (if applicable).
     */
    public Ticket(
            String ticketID,
            int seatNumber,
            int rowNumber,
            String hall,
            String ticketType,
            boolean eligibleForDiscount,
            boolean isWheelchair,
            double price,
            String priorityStatus
    ) {
        this.ticketID = ticketID;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.hall = hall;
        this.ticketType = ticketType;
        this.eligibleForDiscount = eligibleForDiscount;
        this.wheelchair = isWheelchair;
        this.price = price;
        this.priorityStatus = priorityStatus;
    }

    /**
     * Gets the ticket ID.
     *
     * @return The ticket ID.
     */
    public String getTicketID() {
        return ticketID;
    }

    /**
     * Gets the customer ID associated with the ticket.
     *
     * @return The customer ID.
     */
    public String getCustomerID() {
        return customerID;
    }

    /**
     * Gets the seat number of the ticket.
     *
     * @return The seat number.
     */
    public int getSeatNumber() {
        return seatNumber;
    }

    /**
     * Gets the row number of the ticket.
     *
     * @return The row number.
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * Gets the hall where the seat is located.
     *
     * @return The hall name.
     */
    public String getHall() {
        return hall;
    }

    /**
     * Gets the type of the ticket.
     *
     * @return The ticket type.
     */
    public String getTicketType() {
        return ticketType;
    }

    /**
     * Checks if the ticket is eligible for a discount.
     *
     * @return True if eligible for discount, false otherwise.
     */
    public boolean getEligibleForDiscount() {
        return eligibleForDiscount;
    }

    /**
     * Checks if the seat is wheelchair accessible.
     *
     * @return True if wheelchair accessible, false otherwise.
     */
    public boolean getIsWheelchair() {
        return wheelchair;
    }

    /**
     * Gets the price of the ticket.
     *
     * @return The ticket price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the priority status of the ticket.
     *
     * @return The priority status.
     */
    public String getPriorityStatus() {
        return priorityStatus;
    }
}
