package com.teamoneboxoffice.entities;


/**
 * Represents a User entity in the system.
 * This class contains basic user information including identification, credentials, role and status.
 */
public class User {

    /** Unique identifier for the user */
    private long id;
    
    /** Username for login purposes */
    private String userName;
    
    /** User's password */
    private String password;
    
    /** User's role in the system (e.g., staff, deputy, manager) */
    private String role;
    
    /** Flag indicating if the user account is active */
    private boolean isActive;
    
    /**
     * Gets the user's unique identifier.
     * @return the user ID
     */
    public long getId() {return id;}
    
    /**
     * Gets the username.
     * @return the username
     */
    public String getUserName() {return userName;}
    

    /**
     * Gets the user's password.
     * @return the password
     */
    public String getPassword() {return password;}


    /**
     * Gets the user's role.
     * @return the role
     */
    public String getRole() {return role;}


    /**
     * Checks if the user account is active.
     * @return true if the account is active, false otherwise
     */
    public boolean isActive() {return isActive;}

    
    /**
     * Sets the username.
     * @param userName the username to set
     */
    public void setUserName(String userName) {this.userName = userName;}


    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password) {this.password = password;}


    /**
     * Sets the user's role.
     * @param role the role to set
     */
    public void setRole(String role) {this.role = role;}

    
    /**
     * Sets the active status of the user account.
     * @param isActive true to activate the account, false to deactivate
     */
    public void setActive(boolean isActive) {this.isActive = isActive;}

    
    /**
     * Constructs a new User with specified parameters.
     * 
     * @param id the unique identifier for the user
     * @param userName the username for the user
     * @param password the password for the user
     * @param role the role assigned to the user
     * @param isActive the active status of the user account
     */
    public User(long id, String userName, String password, String role, boolean isActive) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }
}
