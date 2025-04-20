package com.teamoneboxoffice;

import com.teamoneboxoffice.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private static final String TEST_CUSTOMER_ID = "C12345";
    private static final boolean TEST_OPT_IN = true;
    private static final String TEST_PAYMENT_TYPE = "Credit Card";
    private static final String TEST_GENDER = "Female";
    private static final String TEST_POSTAL_CODE = "12345";
    private static final String TEST_EMAIL_ADDRESS = "test@example.com";
    private static final String TEST_PHONE_NUMBER = "555-1234";

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(TEST_CUSTOMER_ID, TEST_OPT_IN, TEST_PAYMENT_TYPE,
                TEST_GENDER, TEST_POSTAL_CODE, TEST_EMAIL_ADDRESS,
                TEST_PHONE_NUMBER);
    }

    @Test
    void testCustomerCreationAndGetters() {
        assertEquals(TEST_CUSTOMER_ID, customer.getCustomerID(), "Customer ID should match the test value");
        assertEquals(TEST_OPT_IN, customer.isOptIN(), "Opt-in should match the test value");
        assertEquals(TEST_PAYMENT_TYPE, customer.getPaymentType(), "Payment type should match the test value");
        assertEquals(TEST_GENDER, customer.getGender(), "Gender should match the test value");
        assertEquals(TEST_POSTAL_CODE, customer.getPostalCode(), "Postal code should match the test value");
        assertEquals(TEST_EMAIL_ADDRESS, customer.getEmailAddress(), "Email address should match the test value");
        assertEquals(TEST_PHONE_NUMBER, customer.getPhoneNumber(), "Phone number should match the test value");
    }

    @Test
    void testSetters() {
        customer.setCustomerID("C98765");
        customer.setOptIN(false);
        customer.setPaymentType("Debit Card");
        customer.setGender("Male");
        customer.setPostalCode("67890");
        customer.setEmailAddress("newemail@example.com");
        customer.setPhoneNumber("555-6789");

        assertEquals("C98765", customer.getCustomerID(), "Customer ID should be updated");
        assertFalse(customer.isOptIN(), "Opt-in should be updated to false");
        assertEquals("Debit Card", customer.getPaymentType(), "Payment type should be updated");
        assertEquals("Male", customer.getGender(), "Gender should be updated");
        assertEquals("67890", customer.getPostalCode(), "Postal code should be updated");
        assertEquals("newemail@example.com", customer.getEmailAddress(), "Email address should be updated");
        assertEquals("555-6789", customer.getPhoneNumber(), "Phone number should be updated");
    }
}
