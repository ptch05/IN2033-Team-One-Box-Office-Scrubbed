package com.teamoneboxoffice.entities;

public class Seat {
  private String seatID;
  private int row;
  private int column;
  private float price;
  private boolean isBooked;
  private boolean isVip;
  private boolean isRestrictedView;

  public Seat(String seatID, int row, int column, float price) {
    this.seatID = seatID;
    this.row = row;
    this.column = column;
    this.price = price;
    this.isBooked = false;
    this.isVip = false;
    this.isRestrictedView = false;
  }

  public String getSeatID() {
    return seatID;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public boolean isBooked() {
    return isBooked;
  }

  public void setBooked(boolean booked) {
    isBooked = booked;
  }

  public boolean isVip() {
    return isVip;
  }

  public void setVip(boolean vip) {
    this.isVip = vip;
    if (vip) {
      this.price *= 1.5f;
    }
  }

  public boolean isRestrictedView() {
    return isRestrictedView;
  }

  public void setRestrictedView(boolean restrictedView) {
    this.isRestrictedView = restrictedView;
    if (restrictedView) {
      this.price *= 0.7f;
    }
  }

  public String getStatusString() {
    if (isBooked) {
      return "Booked";
    } else if (isVip) {
      return "VIP";
    } else if (isRestrictedView) {
      return "Restricted View";
    } else {
      return "Available";
    }
  }

  @Override
  public String toString() {
    return seatID + " (" + getStatusString() + ", £" + String.format("%.2f", price) + ")";
  }
}

