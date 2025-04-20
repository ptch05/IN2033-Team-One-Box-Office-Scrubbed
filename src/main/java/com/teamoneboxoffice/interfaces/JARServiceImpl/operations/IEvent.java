package com.teamoneboxoffice.interfaces.JARServiceImpl.operations;

import java.sql.Date;
import java.sql.Time;

/**
 * Interface representing an event with basic event details and venue information.
 */
public interface IEvent {

    /**
     * Gets the unique identifier for the event.
     *
     * @return The event ID.
     */
    int getEventID();

    /**
     * Sets the unique identifier for the event.
     *
     * @param eventID The event ID to set.
     */
    void setEventID(int eventID);

    /**
     * Gets the name of the event.
     *
     * @return The event name.
     */
    String getEventName();

    /**
     * Sets the name of the event.
     *
     * @param eventName The event name to set.
     */
    void setEventName(String eventName);

    /**
     * Gets the type of the event.
     *
     * @return The event type.
     */
    String getEventType();

    /**
     * Sets the type of the event.
     *
     * @param eventType The event type to set.
     */
    void setEventType(String eventType);

    /**
     * Gets the date of the event.
     *
     * @return The event date.
     */
    Date getEventDate();

    /**
     * Sets the date of the event.
     *
     * @param eventDate The event date to set.
     */
    void setEventDate(Date eventDate);

    /**
     * Gets the start time of the event.
     *
     * @return The event start time.
     */
    Time getEventStartTime();

    /**
     * Sets the start time of the event.
     *
     * @param eventStartTime The event start time to set.
     */
    void setEventStartTime(Time eventStartTime);

    /**
     * Gets the end time of the event.
     *
     * @return The event end time.
     */
    Time getEventEndTime();

    /**
     * Sets the end time of the event.
     *
     * @param eventEndTime The event end time to set.
     */
    void setEventEndTime(Time eventEndTime);

    /**
     * Gets the price of the event tickets.
     *
     * @return The event price.
     */
    float getEventPrice();

    /**
     * Sets the price of the event tickets.
     *
     * @param eventPrice The event price to set.
     */
    void setEventPrice(float eventPrice);

    /**
     * Gets the ID of the venue where the event is held.
     *
     * @return The venue ID.
     */
    int getVenueID();

    /**
     * Sets the ID of the venue where the event is held.
     *
     * @param venueID The venue ID to set.
     */
    void setVenueID(int venueID);

    /**
     * Gets the name of the venue where the event is held.
     *
     * @return The venue name.
     */
    String getVenueName();

    /**
     * Sets the name of the venue where the event is held.
     *
     * @param venueName The venue name to set.
     */
    void setVenueName(String venueName);

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor

}
