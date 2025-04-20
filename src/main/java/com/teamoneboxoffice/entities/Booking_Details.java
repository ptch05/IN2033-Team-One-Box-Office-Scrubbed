package com.teamoneboxoffice.entities;

public class Booking_Details {
    private int bookingID;
    private int status;
    private int customerID;
    private int ticketID;
    private String eventID;


    public Booking_Details(int bookingID, int status, int customerID, int ticketID, String eventID) {
        this.bookingID = bookingID;
        this.status = status;
        this.customerID = customerID;
        this.ticketID = ticketID;
        this.eventID = eventID;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }
    public String getEventID() { return eventID; }
    public void setEventID(String eventID) { this.eventID = eventID; }
}
