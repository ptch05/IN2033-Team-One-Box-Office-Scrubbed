package com.teamoneboxoffice.entities;

/**
 * Represents a discount code with its percentage and reason.
 * Matches the structure of the 'Discount' database table.
 */
public class Discount {
    private String code;
    private int percentage;
    private String reason;


    public Discount(String code, int percentage, String reason) {
        this.code = code;
        this.percentage = percentage;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }
    public int getPercentage() {
        return percentage;
    }
    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "code='" + code + '\'' +
                ", percentage=" + percentage +
                ", reason='" + reason + '\'' +
                '}';
    }
}
