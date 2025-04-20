package com.teamoneboxoffice.entities;

public class Customer {
    private String customerID;
    private boolean optIN;
    private String  paymentType;
    private String  gender;
    private String  postalCode;
    private String  emailAddress;
    private String  phoneNumber;

    public String getCustomerID() {return customerID;}
    public boolean isOptIN() {return optIN;}
    public String getPaymentType() {return paymentType;}
    public String getGender() {return gender;}
    public String getPostalCode() {return postalCode;}
    public String getEmailAddress() {return emailAddress;}
    public String getPhoneNumber() {return phoneNumber;}

    public void setCustomerID(String customerID) {this.customerID = customerID;}
    public void setOptIN(boolean optIN) {this.optIN = optIN;}
    public void setPaymentType(String paymentType) {this.paymentType = paymentType;}
    public void setGender(String gender) {this.gender = gender;}
    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
    public void setEmailAddress(String emailAddress) {this.emailAddress = emailAddress;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

    public Customer(String customerID, boolean optIN, String paymentType, String gender, String postalCode, String emailAddress, String phoneNumber) {
        this.customerID = customerID;
        this.optIN = optIN;
        this.paymentType = paymentType;
        this.gender = gender;
        this.postalCode = postalCode;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }
}
