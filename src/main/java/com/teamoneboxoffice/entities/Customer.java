package com.teamoneboxoffice.entities;

/**
 * Represents a customer.
 * Matches the structure of the 'Customer' database table.
 */
public class Customer {
    private String customerID;
    private boolean optIN;
    private String paymentType;
    private String gender;
    private String postalCode;
    private String emailAddress;
    private String phoneNumber;

    /**
     * Constructs a new Customer object.
     *
     * @param customerID   The unique identifier for the customer.
     * @param optIN        Indicates if the customer has opted in for communications.
     * @param paymentType  The preferred payment type of the customer.
     * @param gender       The gender of the customer.
     * @param postalCode   The postal code of the customer.
     * @param emailAddress The email address of the customer.
     * @param phoneNumber  The phone number of the customer.
     */
    public Customer(
            String customerID,
            boolean optIN,
            String paymentType,
            String gender,
            String postalCode,
            String emailAddress,
            String phoneNumber
    ) {
        this.customerID = customerID;
        this.optIN = optIN;
        this.paymentType = paymentType;
        this.gender = gender;
        this.postalCode = postalCode;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the customer ID.
     *
     * @return The customer ID.
     */
    public String getCustomerID() {
        return customerID;
    }

    /**
     * Gets the opt-in status of the customer.
     *
     * @return True if the customer has opted in, false otherwise.
     */
    public boolean isOptIN() {
        return optIN;
    }

    /**
     * Gets the preferred payment type of the customer.
     *
     * @return The payment type.
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * Gets the gender of the customer.
     *
     * @return The gender.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Gets the postal code of the customer.
     *
     * @return The postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the email address of the customer.
     *
     * @return The email address.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Gets the phone number of the customer.
     *
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the customer ID.
     *
     * @param customerID The customer ID to set.
     */
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    /**
     * Sets the opt-in status of the customer.
     *
     * @param optIN The opt-in status to set.
     */
    public void setOptIN(boolean optIN) {
        this.optIN = optIN;
    }

    /**
     * Sets the preferred payment type of the customer.
     *
     * @param paymentType The payment type to set.
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Sets the gender of the customer.
     *
     * @param gender The gender to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Sets the postal code of the customer.
     *
     * @param postalCode The postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Sets the email address of the customer.
     *
     * @param emailAddress The email address to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param phoneNumber The phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
