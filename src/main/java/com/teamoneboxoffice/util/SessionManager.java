package com.teamoneboxoffice.util;

import com.teamoneboxoffice.entities.User;

public class SessionManager {
    private static User currentUser = null;

    public static void setCurrentUserRole(String role) {
        if (currentUser == null && role != null) {
            System.out.println("Warning: Setting only role in SessionManager. Full user object preferred.");
        } else if (currentUser != null && !role.equals(currentUser.getRole())) {
            System.out.println("Warning: Role mismatch in SessionManager.");
        }
    }

    /**
     * Sets the currently logged-in user for the session.
     * @param user The authenticated User object.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("Session user set to: " + (user != null ? user.getUserName() : "null"));
    }

    /**
     * Gets the currently logged-in user object.
     * @return The User object, or null if no user is logged in.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the role of the currently logged-in user.
     * @return The role string ("Manager", "Deputy", "Staff") or null.
     */
    public static String getCurrentUserRole() {
        return (currentUser != null) ? currentUser.getRole() : null;
    }

    /**
     * Clears the current user session data. Call on logout.
     */
    public static void clearSession() {
        currentUser = null;
        System.out.println("Session cleared.");
    }

    public static boolean isManager() { return "Manager".equals(getCurrentUserRole()); }
    public static boolean isDeputy() { return "Deputy".equals(getCurrentUserRole()); }
    public static boolean isStaff() { return "Staff".equals(getCurrentUserRole()); }
}
