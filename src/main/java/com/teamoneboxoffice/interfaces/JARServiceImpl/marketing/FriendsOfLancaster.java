package com.teamoneboxoffice.interfaces.JARServiceImpl.marketing;//Interface for managing Lancaster's priority customers ("Friends of Lancaster")

import java.util.List;

public interface FriendsOfLancaster {

    List<String> getfriends(); //list of friends custmomers

    boolean checkList (String userID); //checks if user is a friend

    void getReservedPrioritySeats(String Event, int reservedSeats); //returns reservied priority seats
}
