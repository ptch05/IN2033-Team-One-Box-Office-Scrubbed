package com.teamoneboxoffice.entities;

import java.time.LocalDate;

/**
 * Represents a member of the 'Friends of Lancaster' program.
 * This is distinct from the general Customer entity.
 */
public class Friends_Of_Lancaster {

    private int friendID;
    private String name;
    private String email;
    private String phoneNumber;

    public Friends_Of_Lancaster(int friendID, String name, String email, String phoneNumber) {
        this.friendID = friendID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getFriendID() {
        return friendID;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return "Friends_Of_Lancaster{" +
                "friendID=" + friendID +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
