package com.teamoneboxoffice.entities;

/**
 * Represents the details of a booking, linking a booking to a customer, ticket,
 * and event.
 * Matches the structure of the 'Booking_Details' database table.
 */
public class Booking_Details {
    private int bookingID;
    private int status;
    private int customerID;
    private int ticketID;
    private String eventID;

    /**
     * Constructs a new Booking_Details object.
     *
     * @param bookingID  The unique identifier for the booking.
     * @param status     The status of the booking (e.g., confirmed, cancelled).
     * @param customerID The ID of the customer associated with the booking.
     * @param ticketID   The ID of the ticket associated with the booking.
     * @param eventID    The ID of the event associated with the booking.
     */
    public Booking_Details(
            int bookingID,
            int status,
            int customerID,
            int ticketID,
            String eventID
    ) {
        this.bookingID = bookingID;
        this.status = status;
        this.customerID = customerID;
        this.ticketID = ticketID;
        this.eventID = eventID;
    }

    /**
     * Gets the booking ID.
     *
     * @return The booking ID.
     */
    public int getBookingID() {
        return bookingID;
    }

    /**
     * Sets the booking ID.
     *
     * @param bookingID The booking ID to set.
     */
    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    /**
     * Gets the booking status.
     *
     * @return The booking status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the booking status.
     *
     * @param status The booking status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the customer ID associated with the booking.
     *
     * @return The customer ID.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Sets the customer ID associated with the booking.
     *
     * @param customerID The customer ID to set.
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * Gets the ticket ID associated with the booking.
     *
     * @return The ticket ID.
     */
    public int getTicketID() {
        return ticketID;
    }

    /**
     * Sets the ticket ID associated with the booking.
     *
     * @param ticketID The ticket ID to set.
     */
    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    /**
     * Gets the event ID associated with the booking.
     *
     * @return The event ID.
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the event ID associated with the booking.
     *
     * @param eventID The event ID to set.
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
