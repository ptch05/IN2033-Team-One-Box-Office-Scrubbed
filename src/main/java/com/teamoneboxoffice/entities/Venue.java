package com.teamoneboxoffice.entities;

/**
 * Represents a venue or hall.
 * Matches the structure of the 'Venue' database table.
 */
public class Venue {
    private int VenueId;
    private String VenueName;
    private boolean VenueInUse;
    private int VenueCapacity;
    private boolean IsMeetingRoom;

    /**
     * Constructs a new Venue object.
     *
     * @param venueName     The name of the venue.
     * @param venueInUse    Indicates if the venue is currently in use.
     * @param venueCapacity The capacity of the venue.
     * @param isMeetingRoom Indicates if the venue is a meeting room.
     */
    public Venue(
            String venueName,
            boolean venueInUse,
            int venueCapacity,
            boolean isMeetingRoom
    ) {
        this.VenueName = venueName;
        this.VenueInUse = venueInUse;
        this.VenueCapacity = venueCapacity;
        this.IsMeetingRoom = isMeetingRoom;
    }

    /**
     * Gets the venue ID.
     *
     * @return The venue ID.
     */
    public int getVenueId() {
        return VenueId;
    }

    /**
     * Gets the name of the venue.
     *
     * @return The venue name.
     */
    public String getVenueName() {
        return VenueName;
    }

    /**
     * Checks if the venue is currently in use.
     *
     * @return True if the venue is in use, false otherwise.
     */
    public boolean isVenueInUse() {
        return VenueInUse;
    }

    /**
     * Gets the capacity of the venue.
     *
     * @return The venue capacity.
     */
    public int getVenueCapacity() {
        return VenueCapacity;
    }

    /**
     * Checks if the venue is a meeting room.
     *
     * @return True if the venue is a meeting room, false otherwise.
     */
    public boolean isMeetingRoom() {
        return IsMeetingRoom;
    }

    /**
     * Sets the name of the venue.
     *
     * @param venueName The venue name to set.
     */
    public void setVenueName(String venueName) {
        this.VenueName = venueName;
    }

    /**
     * Sets the in-use status of the venue.
     *
     * @param venueInUse The in-use status to set.
     */
    public void setVenueInUse(boolean venueInUse) {
        this.VenueInUse = venueInUse;
    }

    /**
     * Sets the capacity of the venue.
     *
     * @param venueCapacity The venue capacity to set.
     */
    public void setVenueCapacity(int venueCapacity) {
        this.VenueCapacity = venueCapacity;
    }

    /**
     * Sets whether the venue is a meeting room.
     *
     * @param meetingRoom True if it's a meeting room, false otherwise.
     */
    public void SetMeetingRoom(boolean meetingRoom) {
        this.IsMeetingRoom = meetingRoom;
    }
}
