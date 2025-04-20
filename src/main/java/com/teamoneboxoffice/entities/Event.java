package com.teamoneboxoffice.entities;

import java.util.Date;

/**
 * Represents an event.
 * Matches the structure of the 'Event' database table.
 */
public class Event {
    private String eventID;
    private String eventType;
    private String eventName;
    private double eventPrice;
    private double ticketRevenue;
    private String hallType;
    private Date eventDate;
    private String eventTime; //--Format: HH:MM:SS
    private double rentalCost;
    private int ticketNumbers;

    /**
     * Constructs a new Event object.
     *
     * @param eventID    The unique identifier for the event.
     * @param eventName  The name of the event.
     * @param eventType  The type of the event.
     * @param eventPrice The price of the event tickets.
     * @param hallType   The type of hall the event is held in.
     * @param eventDate  The date of the event.
     * @param eventTime  The time of the event (in HH:MM:SS format).
     */
    public Event(
            String eventID,
            String eventName,
            String eventType,
            double eventPrice,
            String hallType,
            Date eventDate,
            String eventTime
    ) {
        this.eventID = eventID;
        this.eventType = eventType;
        this.eventPrice = eventPrice;
        this.hallType = hallType;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventName = eventName;
    }

    /**
     * Gets the event ID.
     *
     * @return The event ID.
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Gets the event price.
     *
     * @return The event price.
     */
    public double getEventPrice() {
        return eventPrice;
    }

    /**
     * Gets the hall type.
     *
     * @return The hall type.
     */
    public String getHallType() {
        return hallType;
    }

    /**
     * Gets the event time.
     *
     * @return The event time (in HH:MM:SS format).
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * Gets the event date.
     *
     * @return The event date.
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Gets the event type.
     *
     * @return The event type.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Gets the rental cost for the event.
     *
     * @return The rental cost.
     */
    public double getRentalCost() {
        return rentalCost;
    }

    /**
     * Gets the ticket revenue for the event.
     *
     * @return The ticket revenue.
     */
    public double getTicketRevenue() {
        return ticketRevenue;
    }

    /**
     * Gets the number of tickets for the event.
     *
     * @return The number of tickets.
     */
    public int getTicketNumbers() {
        return ticketNumbers;
    }

    /**
     * Gets the name of the event.
     *
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the ticket revenue for the event.
     *
     * @param ticketRevenue The ticket revenue to set.
     */
    public void setTicketRevenue(double ticketRevenue) {
        this.ticketRevenue = ticketRevenue;
    }

    /**
     * Sets the number of tickets for the event.
     *
     * @param ticketNumbers The number of tickets to set.
     */
    public void setTicketNumbers(int ticketNumbers) {
        this.ticketNumbers = ticketNumbers;
    }
}
