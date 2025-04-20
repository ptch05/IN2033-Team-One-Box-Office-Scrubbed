package com.teamoneboxoffice.entities;

public class Ticket
{

    private String ticketID;
    private String customerID;
    private int seatNumber;
    private int rowNumber;
    private String hall;
    private String ticketType;
    private boolean eligibleForDiscount ;
    private boolean wheelchair;
    private double price;
    private String priorityStatus;

    public Ticket(String ticketID, int seatNumber, int rowNumber, String hall, String ticketType, boolean eligibleForDiscount, boolean isWheelchair, double price, String priorityStatus)
    {
        this.ticketID = ticketID;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.hall = hall;
        this.ticketType = ticketType;
        this.eligibleForDiscount = eligibleForDiscount;
        this.wheelchair = isWheelchair;
        this.price = price;
        this.priorityStatus = priorityStatus;
    }



    public String getTicketID() {
        return ticketID;
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public int getSeatNumber() {
        return seatNumber;
    }
    
    public int getRowNumber() {
        return rowNumber;
    }
    
    public String getHall() {
        return hall;
    }

    public String getTicketType() {return ticketType; }

    public boolean getEligibleForDiscount() {
        return eligibleForDiscount;
    }
    
    public boolean getIsWheelchair() {
        return wheelchair;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getPriorityStatus() {
        return priorityStatus;
    }

}
