package com.teamoneboxoffice.util;

import com.teamoneboxoffice.entities.User;

/**
 * Manages the current user session, providing access to the logged-in user
 * and their role.
 */
public class SessionManager {
    private static User currentUser = null;

    /**
     * Sets the role of the currently logged-in user. This method is primarily
     * for internal use or compatibility and it's recommended to use
     * {@link #setCurrentUser(User)} with a full User object.
     * A warning is printed if setting only the role or if the provided role
     * doesn't match the current user's role.
     *
     * @param role The role string ("Manager", "Deputy", "Staff").
     */
    public static void setCurrentUserRole(String role) {
        if (currentUser == null && role != null) {
            System.out.println(
                    "Warning: Setting only role in SessionManager. Full user object preferred."
            );
        } else if (currentUser != null && !role.equals(currentUser.getRole())) {
            System.out.println("Warning: Role mismatch in SessionManager.");
        }
    }

    /**
     * Sets the currently logged-in user for the session. This should be called
     * after successful user authentication.
     *
     * @param user The authenticated User object.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println(
                "Session user set to: " + (user != null ? user.getUserName() : "null")
        );
    }

    /**
     * Gets the currently logged-in user object.
     *
     * @return The User object, or null if no user is logged in.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the role of the currently logged-in user.
     *
     * @return The role string ("Manager", "Deputy", "Staff") or null if no
     *     user is logged in.
     */
    public static String getCurrentUserRole() {
        return (currentUser != null) ? currentUser.getRole() : null;
    }

    /**
     * Clears the current user session data. This method should be called upon
     * user logout.
     */
    public static void clearSession() {
        currentUser = null;
        System.out.println("Session cleared.");
    }

    /**
     * Checks if the currently logged-in user has the "Manager" role.
     *
     * @return True if the current user is a Manager, false otherwise.
     */
    public static boolean isManager() {
        return "Manager".equals(getCurrentUserRole());
    }

    /**
     * Checks if the currently logged-in user has the "Deputy" role.
     *
     * @return True if the current user is a Deputy, false otherwise.
     */
    public static boolean isDeputy() {
        return "Deputy".equals(getCurrentUserRole());
    }

    /**
     * Checks if the currently logged-in user has the "Staff" role.
     *
     * @return True if the current user is Staff, false otherwise.
     */
    public static boolean isStaff() {
        return "Staff".equals(getCurrentUserRole());
    }
}
