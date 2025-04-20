package com.teamoneboxoffice;

import com.teamoneboxoffice.entities.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VenueTest {

    private static final String TEST_VENUE_NAME = "Main Auditorium";
    private static final boolean TEST_VENUE_IN_USE = true;
    private static final int TEST_VENUE_CAPACITY = 500;
    private static final boolean TEST_IS_MEETING_ROOM = false;

    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = new Venue(TEST_VENUE_NAME, TEST_VENUE_IN_USE, TEST_VENUE_CAPACITY, TEST_IS_MEETING_ROOM);
    }

    @Test
    void testVenueCreationAndGetters() {
        assertEquals(TEST_VENUE_NAME, venue.getVenueName(), "Venue name should match the test value");
        assertEquals(TEST_VENUE_IN_USE, venue.isVenueInUse(), "Venue in use should match the test value");
        assertEquals(TEST_VENUE_CAPACITY, venue.getVenueCapacity(), "Venue capacity should match the test value");
        assertEquals(TEST_IS_MEETING_ROOM, venue.isMeetingRoom(), "Meeting room status should match the test value");
    }

    @Test
    void testSetters() {
        venue.setVenueName("Conference Hall");
        venue.setVenueInUse(false);
        venue.setVenueCapacity(300);
        venue.SetMeetingRoom(true);

        assertEquals("Conference Hall", venue.getVenueName(), "Venue name should be updated");
        assertFalse(venue.isVenueInUse(), "Venue in use should be updated to false");
        assertEquals(300, venue.getVenueCapacity(), "Venue capacity should be updated");
        assertTrue(venue.isMeetingRoom(), "Meeting room status should be updated to true");
    }
}

