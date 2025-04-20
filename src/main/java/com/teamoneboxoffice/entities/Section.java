package com.teamoneboxoffice.entities;

import java.util.ArrayList;

public class Section {
  private String name;
  private int rows;
  private int columns;
  private float basePrice;
  private ArrayList<Seat> seats;

  public Section(String name, int rows, int columns, float basePrice) {
    this.name = name;
    this.rows = rows;
    this.columns = columns;
    this.basePrice = basePrice;
    this.seats = new ArrayList<>();

    generateBasicLayout();
  }

  // Generate basic rectangular layout
  private void generateBasicLayout() {
    for (int row = 1; row <= rows; row++) {
      for (int col = 1; col <= columns; col++) {
        // Create a new seat with unique ID (e.g., "Stalls-A-12")
        String rowLabel = getRowLabel(row);
        String seatID = name + "-" + rowLabel + "-" + col;

        Seat seat = new Seat(seatID, row, col, basePrice);
        seats.add(seat);
      }
    }
  }

  public void configureComplexLayout() {
    seats.clear();

    if (name.equals("Stalls")) {
      configureStallsLayout();
    }
    else if (name.equals("Balcony")) {
      configureBalconyLayout();
    }
    else if (name.contains("Side Balcony")) {
      configureSideBalconyLayout();
    }
    else {
      generateBasicLayout();
    }
  }

  public void configureRectangularLayout() {
    seats.clear();

    for (int row = 1; row <= rows; row++) {
      for (int col = 1; col <= columns; col++) {
        String rowLabel = getRowLabel(row);
        String seatID = name + "-" + rowLabel + "-" + col;

        Seat seat = new Seat(seatID, row, col, basePrice);

        if (row <= 3) {
          seat.setPrice(basePrice * 1.2f);
        }
        else if (row >= rows - 2) {
          seat.setPrice(basePrice * 0.9f);
        }
        seats.add(seat);
      }
    }
  }

  private void configureStallsLayout() {
    int[] seatsInRow = {
            11, 11, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16
    };

    for (int row = 1; row <= seatsInRow.length; row++) {
      String rowLabel = getRowLabel(row);
      int numSeats = seatsInRow[row - 1];
      int startCol = (19 - numSeats) / 2 + 1;

      for (int col = 0; col < numSeats; col++) {
        int actualCol = startCol + col;
        String seatID = name + "-" + rowLabel + "-" + actualCol;

        Seat seat = new Seat(seatID, row, actualCol, basePrice);

        if (actualCol >= 5 && actualCol <= 15) {
          seat.setPrice(basePrice * 1.15f);
        }
        else if (actualCol <= 3 || actualCol >= 17) {
          seat.setPrice(basePrice * 0.9f);
        }

        if (row >= 3 && row <= 8) {
          seat.setPrice(seat.getPrice() * 1.1f);
        }

        seats.add(seat);
      }
    }
  }

  private void configureBalconyLayout() {

    int[] seatsInRow = {
            8, 23, 23, 23, 23, 23, 23, 23  // Based on image 1
    };

    for (int row = 1; row <= seatsInRow.length; row++) {
      String rowLabel = (row == 1) ? "CC" : "AA" + (row - 1);
      int numSeats = seatsInRow[row - 1];

      int startCol = (23 - numSeats) / 2 + 1;

      for (int col = 0; col < numSeats; col++) {
        int actualCol = startCol + col;
        String seatID = name + "-" + rowLabel + "-" + actualCol;

        Seat seat = new Seat(seatID, row, actualCol, basePrice);

        if (actualCol >= 8 && actualCol <= 16) {
          seat.setPrice(basePrice * 1.2f);
        }
        else if (actualCol <= 3 || actualCol >= 21) {
          seat.setPrice(basePrice * 0.85f);
        }

        seats.add(seat);
      }
    }
  }

  private void configureSideBalconyLayout() {
    boolean isLeft = name.contains("Left");

    for (int row = 1; row <= 5; row++) {
      String rowLabel = String.valueOf(row);

      for (int col = 1; col <= 20; col++) {
        int actualCol = isLeft ? col : 21 - col;
        String seatID = name + "-" + rowLabel + "-" + actualCol;

        Seat seat = new Seat(seatID, row, actualCol, basePrice);

        if (row <= 2) {
          seat.setPrice(basePrice * 1.1f);
        }
        if ((isLeft && col >= 15) || (!isLeft && col <= 5)) {
          seat.setPrice(seat.getPrice() * 0.9f);
        }

        seats.add(seat);
      }
    }
  }

  private String getRowLabel(int row) {
    if (row <= 26) {
      return String.valueOf((char)('A' + row - 1));
    } else {
      // For rows beyond Z
      return "A" + (row - 26);
    }
  }

  public String getName() {
    return name;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public float getBasePrice() {
    return basePrice;
  }

  public ArrayList<Seat> getSeats() {
    return seats;
  }
}
