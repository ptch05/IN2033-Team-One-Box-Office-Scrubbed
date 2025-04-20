package com.teamoneboxoffice.interfaces.JARServiceImpl.operations;

import com.teamoneboxoffice.entities.Seat;
import com.teamoneboxoffice.entities.SeatingConfig;
import com.teamoneboxoffice.entities.Section;

import java.util.ArrayList;

/**
 * Interface for managing seating configurations and seat properties within a venue
 * or event.
 */
public interface ISeatingConfig {

    /**
     * Retrieves the full SeatingConfig object based on its ID.
     *
     * @param seatingConfigID The ID of the seating configuration to retrieve.
     * @return The SeatingConfig object, or null if not found.
     */
    SeatingConfig getSeatingConfiguration(int seatingConfigID);

    // Get and set section names - (Commented out in the original code, assuming
    // these were placeholders or not needed)

    /**
     * Retrieves a list of all seats within a specific section.
     *
     * @param section The Section object to retrieve seats from.
     * @return An ArrayList of Seat objects in the specified section.
     */
    ArrayList<Seat> getSeats(Section section);

    /**
     * Retrieves a list of restricted view seats within a specific section.
     *
     * @param section The Section object to retrieve restricted seats from.
     * @return An ArrayList of restricted view Seat objects in the specified section.
     */
    ArrayList<Seat> getRestrictedSeats(Section section);

    /**
     * Sets the booked status of a specific seat.
     *
     * @param seat   The Seat object to update.
     * @param booked The booked status to set (true if booked, false if available).
     */
    void setSeatBooked(Seat seat, boolean booked);

    /**
     * Sets the VIP status of a specific seat.
     *
     * @param seat The Seat object to update.
     * @param vip  The VIP status to set (true if VIP, false otherwise).
     */
    void setSeatVip(Seat seat, boolean vip);

    /**
     * Sets the restricted view status of a specific seat.
     *
     * @param seat       The Seat object to update.
     * @param restricted The restricted view status to set (true if restricted view,
     *                   false otherwise).
     */
    void setSeatRestrictedView(Seat seat, boolean restricted);
}
