package com.christine.smartinventorysystem.models;

/**
 * Represents a system user (Admin or Cashier).
 * Controls login and access levels.
 */
public class User {

    //  Enum: only these two roles are allowed
    public enum Role {
        ADMIN, CASHIER
    }

    //  Fields
    private String username;
    private String password;
    private String fullName;
    private Role role;
    private boolean isActive;

    //  Constructor
    public User(String username, String password, String fullName, Role role){
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role     = role;
        this.isActive = true;
    }

    //  Getters
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getFullName()  { return fullName; }
    public Role getRole()        { return role; }
    public boolean isActive()    { return isActive; }

    //  Setters
    public void setPassword(String password)  { this.password = password; }
    public void setFullName(String fullName)  { this.fullName = fullName; }
    public void setRole(Role role)            { this.role = role; }
    public void setActive(boolean active)     { this.isActive = active; }

    //  Check if user is admin
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    //  Check password
    public boolean checkPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }

    //  Display
    @Override
    public String toString() {
        return String.format("%-15s %-20s %-10s %s",
                username, fullName, role,
                isActive ? "✔ ACTIVE" : "✗ INACTIVE");
    }
}