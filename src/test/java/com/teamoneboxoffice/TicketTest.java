package com.teamoneboxoffice;

import com.teamoneboxoffice.entities.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private static final String TEST_TICKET_ID = "T12345";
    private static final int TEST_SEAT_NUMBER = 10;
    private static final int TEST_ROW_NUMBER = 5;
    private static final String TEST_HALL = "IMAX";
    private static final String TEST_TICKET_TYPE = "VIP";
    private static final boolean TEST_ELIGIBLE_FOR_DISCOUNT = true;
    private static final boolean TEST_WHEELCHAIR = false;
    private static final double TEST_PRICE = 50.0;
    private static final String TEST_PRIORITY_STATUS = "High";

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket(TEST_TICKET_ID, TEST_SEAT_NUMBER, TEST_ROW_NUMBER, TEST_HALL,
                TEST_TICKET_TYPE, TEST_ELIGIBLE_FOR_DISCOUNT, TEST_WHEELCHAIR,
                TEST_PRICE, TEST_PRIORITY_STATUS);
    }

    @Test
    void testTicketCreationAndGetters() {
        assertEquals(TEST_TICKET_ID, ticket.getTicketID(), "Ticket ID should match the test value");
        assertNull(ticket.getCustomerID(), "Customer ID should be null (not initialized)");
        assertEquals(TEST_SEAT_NUMBER, ticket.getSeatNumber(), "Seat number should match the test value");
        assertEquals(TEST_ROW_NUMBER, ticket.getRowNumber(), "Row number should match the test value");
        assertEquals(TEST_HALL, ticket.getHall(), "Hall should match the test value");
        assertEquals(TEST_TICKET_TYPE, ticket.getTicketType(), "Ticket type should match the test value");
        assertEquals(TEST_ELIGIBLE_FOR_DISCOUNT, ticket.getEligibleForDiscount(), "Eligible for discount should match the test value");
        assertEquals(TEST_WHEELCHAIR, ticket.getIsWheelchair(), "Wheelchair accessibility should match the test value");
        assertEquals(TEST_PRICE, ticket.getPrice(), "Price should match the test value");
        assertEquals(TEST_PRIORITY_STATUS, ticket.getPriorityStatus(), "Priority status should match the test value");
    }
}
