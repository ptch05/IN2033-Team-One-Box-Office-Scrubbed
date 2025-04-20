package com.teamoneboxoffice;

import com.teamoneboxoffice.entities.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private static final String TEST_EVENT_ID = "E12345";
    private static final String TEST_EVENT_NAME = "Concert Night";
    private static final String TEST_EVENT_TYPE = "Music";
    private static final double TEST_EVENT_PRICE = 75.5;
    private static final String TEST_HALL_TYPE = "Grand Hall";
    private static final Date TEST_EVENT_DATE = new Date(); // Using current date for simplicity
    private static final String TEST_EVENT_TIME = "19:30:00";

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event(TEST_EVENT_ID, TEST_EVENT_NAME, TEST_EVENT_TYPE, TEST_EVENT_PRICE,
                TEST_HALL_TYPE, TEST_EVENT_DATE, TEST_EVENT_TIME);
    }

    @Test
    void testEventCreationAndGetters() {
        assertEquals(TEST_EVENT_ID, event.getEventID(), "Event ID should match the test value");
        assertEquals(TEST_EVENT_NAME, event.getEventName(), "Event name should match the test value");
        assertEquals(TEST_EVENT_TYPE, event.getEventType(), "Event type should match the test value");
        assertEquals(TEST_EVENT_PRICE, event.getEventPrice(), "Event price should match the test value");
        assertEquals(TEST_HALL_TYPE, event.getHallType(), "Hall type should match the test value");
        assertEquals(TEST_EVENT_DATE, event.getEventDate(), "Event date should match the test value");
        assertEquals(TEST_EVENT_TIME, event.getEventTime(), "Event time should match the test value");
        assertEquals(0.0, event.getTicketRevenue(), "Ticket revenue should initialize to 0.0");
        assertEquals(0, event.getTicketNumbers(), "Ticket numbers should initialize to 0");
    }

    @Test
    void testSetters() {
        // Modify the ticket revenue and number of tickets
        event.setTicketRevenue(5000.0);
        event.setTicketNumbers(200);

        assertEquals(5000.0, event.getTicketRevenue(), "Ticket revenue should be updated");
        assertEquals(200, event.getTicketNumbers(), "Ticket numbers should be updated");
    }
}
