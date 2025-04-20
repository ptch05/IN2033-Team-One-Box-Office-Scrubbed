package com.teamoneboxoffice.interfaces.JARServiceImpl.operations;

import com.teamoneboxoffice.entities.Seat;
import com.teamoneboxoffice.entities.SeatingConfig;
import com.teamoneboxoffice.entities.Section;

import java.util.ArrayList;

public interface ISeatingConfig {


  // Get the full SeatingConfig object (if needed)
  SeatingConfig getSeatingConfiguration(int seatingConfigID);

  // Get and set section names


  // Retrieve seats from a section
  ArrayList<Seat> getSeats(Section section);

  ArrayList<Seat> getRestrictedSeats(Section section);

  // Modify seat status
  void setSeatBooked(Seat seat, boolean booked);

  void setSeatVip(Seat seat, boolean vip);

  void setSeatRestrictedView(Seat seat, boolean restricted);
}

