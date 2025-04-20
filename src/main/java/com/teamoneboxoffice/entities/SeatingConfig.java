package com.teamoneboxoffice.entities;

import com.teamoneboxoffice.interfaces.JARServiceImpl.operations.ISeatingConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class SeatingConfig implements ISeatingConfig {
    private static Map<String, Integer> predefinedConfigs = new HashMap<>();
    private int seatingConfigID;
    private int venueID;
    private String eventType;
    private ArrayList<Section> sections;

    // Predefine seating configurations
    static {
        predefinedConfigs.put("1_LivePerformance", 101);// Main Theater - Live Performance
        predefinedConfigs.put("1_Film", 102);           // Main Theater - Film
        predefinedConfigs.put("2_Conference", 201);     // Conference Room
        predefinedConfigs.put("1_Concert", 103);        // Main Theater - Concert
        predefinedConfigs.put("2_SmallEvent", 202);     // Small Hall
    }

    // Retrieve SeatingConfigID based on venueID + eventType
    public static int getSeatingConfigID(int venueID, String eventType) {
        String key = venueID + "_" + eventType;
        return predefinedConfigs.getOrDefault(key, -1);
    }

    // Constructor
    public SeatingConfig(int venueID, String eventType) {
        this.venueID = venueID;
        this.eventType = eventType;
        this.seatingConfigID = getSeatingConfigID(venueID, eventType); // returns -1 if key doesn't exist
        this.sections = generateSeatingLayout(seatingConfigID);
    }

    // Generate Sections for an Event
    private ArrayList<Section> generateSeatingLayout(int seatingConfigID) {
        ArrayList<Section> sections = new ArrayList<>();

        // Based on first image (Main Theater)
        if (seatingConfigID == 101) { // Live Performance in Main Theater
            // Stalls section (main floor)
            Section stalls = new Section("Stalls", 16, 19, 65.0f);
            stalls.configureComplexLayout();

            // Balcony section (upper level)
            Section balcony = new Section("Balcony", 8, 23, 85.0f);
            balcony.configureComplexLayout();

            // Side Balcony Left
            Section sideBalconyLeft = new Section("Side Balcony Left", 5, 20, 55.0f);
            sideBalconyLeft.configureComplexLayout();

            // Side Balcony Right
            Section sideBalconyRight = new Section("Side Balcony Right", 5, 20, 55.0f);
            sideBalconyRight.configureComplexLayout();

            sections.add(stalls);
            sections.add(balcony);
            sections.add(sideBalconyLeft);
            sections.add(sideBalconyRight);

            // Mark restricted view seats
            markRestrictedViewSeats(sections);
        }
        else if (seatingConfigID == 102) { // Film in Main Theater
            // Similar layout as Live Performance but with different pricing
            Section stalls = new Section("Stalls", 16, 19, 45.0f);
            stalls.configureComplexLayout();

            Section balcony = new Section("Balcony", 8, 23, 65.0f);
            balcony.configureComplexLayout();

            Section sideBalconyLeft = new Section("Side Balcony Left", 5, 20, 35.0f);
            sideBalconyLeft.configureComplexLayout();

            Section sideBalconyRight = new Section("Side Balcony Right", 5, 20, 35.0f);
            sideBalconyRight.configureComplexLayout();

            sections.add(stalls);
            sections.add(balcony);
            sections.add(sideBalconyLeft);
            sections.add(sideBalconyRight);

            // Mark restricted view seats
            markRestrictedViewSeats(sections);
        }
        else if (seatingConfigID == 103) { // Concert in Main Theater
            // Similar layout but with standing area and premium pricing
            Section stalls = new Section("Stalls", 16, 19, 85.0f);
            stalls.configureComplexLayout();

            Section balcony = new Section("Balcony", 8, 23, 95.0f);
            balcony.configureComplexLayout();

            Section sideBalconyLeft = new Section("Side Balcony Left", 5, 20, 75.0f);
            sideBalconyLeft.configureComplexLayout();

            Section sideBalconyRight = new Section("Side Balcony Right", 5, 20, 75.0f);
            sideBalconyRight.configureComplexLayout();

            sections.add(stalls);
            sections.add(balcony);
            sections.add(sideBalconyLeft);
            sections.add(sideBalconyRight);

            // Mark VIP seats
            markVIPSeats(sections);
            // Mark restricted view seats
            markRestrictedViewSeats(sections);
        }
        else if (seatingConfigID == 201) { // Conference in Conference Room
            // Based on second image - Small Hall
            Section mainHall = new Section("Conference Hall", 13, 16, 30.0f);
            mainHall.configureRectangularLayout();
            sections.add(mainHall);
        }
        else if (seatingConfigID == 202) { // Small Event in Small Hall
            // Based on second image - Small Hall with different pricing
            Section mainHall = new Section("Small Hall", 13, 16, 25.0f);
            mainHall.configureRectangularLayout();
            sections.add(mainHall);
        }
        else {
            // Default simple layout if config not found
            sections.add(new Section("Default Section", 10, 10, 20.0f));
        }

        return sections;
    }

    // Mark seats with restricted view for the theater layout
    private void markRestrictedViewSeats(ArrayList<Section> sections) {
        for (Section section : sections) {
            ArrayList<Seat> seats = section.getSeats();

            for (Seat seat : seats) {
                // Mark side seats as restricted view
                if (section.getName().equals("Stalls")) {
                    // In rows, first and last 2 seats often have restricted views
                    if (seat.getColumn() <= 2 || seat.getColumn() >= section.getColumns() - 1) {
                        seat.setRestrictedView(true);
                    }
                }
                else if (section.getName().contains("Side Balcony")) {
                    // Some side balcony seats typically have restricted views
                    if (seat.getRow() >= 3) {
                        seat.setRestrictedView(true);
                    }
                }
                else if (section.getName().equals("Balcony")) {
                    // Some balcony seats might have restricted views, especially in the corners
                    if ((seat.getRow() == 1 && (seat.getColumn() <= 2 || seat.getColumn() >= section.getColumns() - 1)) ||
                            (seat.getRow() >= 7 && (seat.getColumn() <= 1 || seat.getColumn() >= section.getColumns()))) {
                        seat.setRestrictedView(true);
                    }
                }
            }
        }
    }

    // Mark VIP seats for concerts
    private void markVIPSeats(ArrayList<Section> sections) {
        for (Section section : sections) {
            ArrayList<Seat> seats = section.getSeats();

            for (Seat seat : seats) {
                // First 3 rows in the stalls are typically VIP for concerts
                if (section.getName().equals("Stalls") && seat.getRow() <= 3) {
                    seat.setVip(true);
                }
                // Center seats in first 2 rows of balcony are also VIP
                else if (section.getName().equals("Balcony") && seat.getRow() <= 2 &&
                        seat.getColumn() >= 5 && seat.getColumn() <= section.getColumns() - 4) {
                    seat.setVip(true);
                }
            }
        }
    }

    // Get the SeatingConfig Object
    @Override
    public SeatingConfig getSeatingConfiguration(int seatingConfigID) {
        return this;
    }

    // Retrieve All Seats in a Section
    @Override
    public ArrayList<Seat> getSeats(Section section) {
        return section.getSeats();
    }

    // Get Restricted View Seats
    @Override
    public ArrayList<Seat> getRestrictedSeats(Section section) {
        ArrayList<Seat> restrictedSeats = new ArrayList<>();
        for (Seat seat : section.getSeats()) {
            if (seat.isRestrictedView()) {
                restrictedSeats.add(seat);
            }
        }
        return restrictedSeats;
    }

    // Get VIP Seats
    public ArrayList<Seat> getVIPSeats(Section section) {
        ArrayList<Seat> vipSeats = new ArrayList<>();
        for (Seat seat : section.getSeats()) {
            if (seat.isVip()) {
                vipSeats.add(seat);
            }
        }
        return vipSeats;
    }

    // Get all sections
    public ArrayList<Section> getSections() {
        return sections;
    }

    // Modify Seat Booking Status
    @Override
    public void setSeatBooked(Seat seat, boolean booked) {
        seat.setBooked(booked);
    }

    @Override
    public void setSeatVip(Seat seat, boolean vip) {
        seat.setVip(vip);
    }

    @Override
    public void setSeatRestrictedView(Seat seat, boolean restricted) {
        seat.setRestrictedView(restricted);
    }
}
