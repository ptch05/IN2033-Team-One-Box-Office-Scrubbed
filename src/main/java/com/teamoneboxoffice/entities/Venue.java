package com.teamoneboxoffice.entities;

public class Venue {
    private int VenueId;
    private String VenueName;
    private boolean VenueInUse;
    private int VenueCapacity;
    private boolean IsMeetingRoom;

    public String getVenueName() {return VenueName;}
    public boolean isVenueInUse() {return VenueInUse;}
    public int getVenueCapacity() {return VenueCapacity;}
    public boolean IsMeetingRoom() {return IsMeetingRoom;}

    public void setVenueName(String venueName) {this.VenueName = venueName;}
    public void setVenueInUse(boolean venueInUse) {this.VenueInUse = venueInUse;}
    public void setVenueCapacity(int venueCapacity) {this.VenueCapacity = venueCapacity;}
    public void SetMeetingRoom(boolean meetingRoom) {this.IsMeetingRoom = meetingRoom;}

    public Venue(String venueName, boolean venueInUse, int venueCapacity, boolean isMeetingRoom) {
        this.VenueName = venueName;
        this.VenueInUse = venueInUse;
        this.VenueCapacity = venueCapacity;
        this.IsMeetingRoom = isMeetingRoom;
    }
}
