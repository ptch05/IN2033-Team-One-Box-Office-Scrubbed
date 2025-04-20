package com.teamoneboxoffice;

import com.teamoneboxoffice.entities.Booking_Details;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingDetailsTest {

    private static final int TEST_BOOKING_ID = 1001;
    private static final int TEST_STATUS = 1;  // Example: 1 for confirmed, 0 for pending
    private static final int TEST_CUSTOMER_ID = 5001;
    private static final int TEST_TICKET_ID = 3001;
    private static final String TEST_EVENT_ID = "EVT_1111";

    private Booking_Details bookingDetails;

    @BeforeEach
    void setUp() {
        bookingDetails = new Booking_Details(TEST_BOOKING_ID, TEST_STATUS, TEST_CUSTOMER_ID, TEST_TICKET_ID, TEST_EVENT_ID);
    }

    @Test
    void testBookingDetailsCreationAndGetters() {
        assertEquals(TEST_BOOKING_ID, bookingDetails.getBookingID(), "Booking ID should match the test value");
        assertEquals(TEST_STATUS, bookingDetails.getStatus(), "Status should match the test value");
        assertEquals(TEST_CUSTOMER_ID, bookingDetails.getCustomerID(), "Customer ID should match the test value");
        assertEquals(TEST_TICKET_ID, bookingDetails.getTicketID(), "Ticket ID should match the test value");
    }

    @Test
    void testSetters() {
        bookingDetails.setBookingID(2002);
        bookingDetails.setStatus(0);
        bookingDetails.setCustomerID(6002);
        bookingDetails.setTicketID(4002);

        assertEquals(2002, bookingDetails.getBookingID(), "Booking ID should be updated");
        assertEquals(0, bookingDetails.getStatus(), "Status should be updated to 0 (e.g., pending)");
        assertEquals(6002, bookingDetails.getCustomerID(), "Customer ID should be updated");
        assertEquals(4002, bookingDetails.getTicketID(), "Ticket ID should be updated");
    }
}

