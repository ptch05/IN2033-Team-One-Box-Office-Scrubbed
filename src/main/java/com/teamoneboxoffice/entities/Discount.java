package com.teamoneboxoffice.entities;

/**
 * Represents a discount code with its percentage and reason.
 * Matches the structure of the 'Discount' database table.
 */
public class Discount {
    private String code;
    private int percentage;
    private String reason;

    /**
     * Constructs a new Discount object.
     *
     * @param code       The unique discount code.
     * @param percentage The discount percentage.
     * @param reason     The reason for the discount.
     */
    public Discount(String code, int percentage, String reason) {
        this.code = code;
        this.percentage = percentage;
        this.reason = reason;
    }

    /**
     * Gets the discount code.
     *
     * @return The discount code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the discount percentage.
     *
     * @return The discount percentage.
     */
    public int getPercentage() {
        return percentage;
    }

    /**
     * Gets the reason for the discount.
     *
     * @return The reason for the discount.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns a string representation of the Discount object.
     *
     * @return A string containing the discount code, percentage, and reason.
     */
    @Override
    public String toString() {
        return (
                "Discount{" +
                "code='" +
                code +
                '\'' +
                ", percentage=" +
                percentage +
                ", reason='" +
                reason +
                '\'' +
                '}'
        );
    }
}
