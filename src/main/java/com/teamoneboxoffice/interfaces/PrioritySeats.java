package com.teamoneboxoffice.interfaces;

import java.util.List;


public interface PrioritySeats {
    /**
     * Reserves priority seating for Lancaster friends
     * @param ticketId Unique ticket identifier
     * @param seatNumber Seat number to be reserved
     * @param priorityStatus Priority status of the reservation
     * @return boolean indicating if reservation was successful
     */

    //--Interface Version 1:

    boolean reservePrioritySeat(String ticketId, int seatNumber, String priorityStatus);

    //--Interface Version 2:

    //List<Ticket> getPriorityTickets();
}

