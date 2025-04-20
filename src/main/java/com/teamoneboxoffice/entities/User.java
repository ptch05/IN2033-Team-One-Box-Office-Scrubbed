package com.teamoneboxoffice.entities;


public class User {

    private long id;
    private String userName;
    private String password;
    private String role;
    private boolean isActive;


    public long getId() {return id;}
    public String getUserName() {return userName;}
    public String getPassword() {return password;}
    public String getRole() {return role;}
    public boolean isActive() {return isActive;}

    public void setUserName(String userName) {this.userName = userName;}
    public void setPassword(String password) {this.password = password;}
    public void setRole(String role) {this.role = role;}
    public void setActive(boolean isActive) {this.isActive = isActive;}

    public User(long id, String userName, String password, String role, boolean isActive) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

}
