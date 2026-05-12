package com.christine.smartinventorysystem.managers;

import com.christine.smartinventorysystem.exceptions.InvalidInputException;
import com.christine.smartinventorysystem.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all user accounts and authentication.
 * Handles login, registration and user administration.
 */
public class UserManager {

    // ── User list + currently logged in user ──────────────
    private List<User> users = new ArrayList<>();
    private User currentUser = null;

    // ── Constructor: create default admin on startup ──────
    public UserManager() {
        // Default admin account always exists
        users.add(new User("admin", "admin123",
                "System Administrator", User.Role.ADMIN));
        // Default cashier account for testing
        users.add(new User("cashier", "cash123",
                "Default Cashier", User.Role.CASHIER));
    }

    // ── Login ─────────────────────────────────────────────
    public User login(String username, String password)
            throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("username", "Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("password", "Password cannot be empty");
        }

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)
                    && u.checkPassword(password)
                    && u.isActive()) {
                currentUser = u;
                return u;
            }
        }
        return null; // login failed
    }

    // ── Logout ────────────────────────────────────────────
    public void logout() {
        currentUser = null;
    }

    // ── Get currently logged in user ──────────────────────
    public User getCurrentUser() {
        return currentUser;
    }

    // ── Check if anyone is logged in ──────────────────────
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // ── Register new user (admin only) ───────────────────
    public void registerUser(String username, String password,
                             String fullName, User.Role role)
            throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("username", "Username cannot be empty");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidInputException("password",
                    "Password must be at least 4 characters");
        }
        if (findUserByUsername(username) != null) {
            throw new InvalidInputException("username",
                    "Username '" + username + "' already exists");
        }
        users.add(new User(username, password, fullName, role));
    }

    // ── Find user by username ─────────────────────────────
    public User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    // ── Get all users ─────────────────────────────────────
    public List<User> getAllUsers() {
        return users;
    }

    // ── Deactivate user ───────────────────────────────────
    public void deactivateUser(String username)
            throws InvalidInputException {
        User user = findUserByUsername(username);
        if (user == null) {
            throw new InvalidInputException("username",
                    "User '" + username + "' not found");
        }
        if (user.getUsername().equalsIgnoreCase("admin")) {
            throw new InvalidInputException("username",
                    "Cannot deactivate the main admin account");
        }
        user.setActive(false);
    }

    // ── Remove user ───────────────────────────────────────
    public void removeUser(String username)
            throws InvalidInputException {
        User user = findUserByUsername(username);
        if (user == null) {
            throw new InvalidInputException("username",
                    "User '" + username + "' not found");
        }
        if (user.getUsername().equalsIgnoreCase("admin")) {
            throw new InvalidInputException("username",
                    "Cannot remove the main admin account");
        }
        users.remove(user);
    }

    // ── Get total number of users ─────────────────────────
    public int getTotalUsers() {
        return users.size();
    }
}