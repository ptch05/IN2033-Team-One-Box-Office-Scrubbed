package com.teamoneboxoffice.entities;

/**
 * Represents a member of the 'Friends of Lancaster' program.
 * This is distinct from the general Customer entity.
 * Matches the structure of the 'Friends_Of_Lancaster' database table.
 */
public class Friends_Of_Lancaster {

    private int friendID;
    private String name;
    private String email;
    private String phoneNumber;

    /**
     * Constructs a new Friends_Of_Lancaster object.
     *
     * @param friendID    The unique identifier for the 'Friends of Lancaster' member.
     * @param name        The name of the member.
     * @param email       The email address of the member.
     * @param phoneNumber The phone number of the member.
     */
    public Friends_Of_Lancaster(
            int friendID,
            String name,
            String email,
            String phoneNumber
    ) {
        this.friendID = friendID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the friend ID.
     *
     * @return The friend ID.
     */
    public int getFriendID() {
        return friendID;
    }

    /**
     * Gets the name of the member.
     *
     * @return The member's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the email address of the member.
     *
     * @return The member's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the phone number of the member.
     *
     * @return The member's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns a string representation of the Friends_Of_Lancaster object.
     *
     * @return A string containing the friend ID, name, and email.
     */
    @Override
    public String toString() {
        return (
                "Friends_Of_Lancaster{" +
                "friendID=" +
                friendID +
                ", name='" +
                name +
                '\'' +
                ", email='" +
                email +
                '\'' +
                '}'
        );
    }
}
