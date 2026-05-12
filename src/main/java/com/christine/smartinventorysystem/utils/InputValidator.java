package com.christine.smartinventorysystem.utils;

import com.christine.smartinventorysystem.exceptions.InvalidInputException;
import java.util.Scanner;

/**
 * Handles all user input from the console.
 * Validates input and prevents crashes from bad data.
 */
public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);

    // ── Read a non-empty string ───────────────────────────
    public static String readString(String prompt)
            throws InvalidInputException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            throw new InvalidInputException("input", "Input cannot be empty");
        }
        return input;
    }

    // ── Read an integer ───────────────────────────────────
    public static int readInt(String prompt)
            throws InvalidInputException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("input",
                    "'" + input + "' is not a valid number");
        }
    }

    // ── Read a positive integer ───────────────────────────
    public static int readPositiveInt(String prompt)
            throws InvalidInputException {
        int value = readInt(prompt);
        if (value <= 0) {
            throw new InvalidInputException("input",
                    "Value must be greater than zero");
        }
        return value;
    }

    // ── Read a double ─────────────────────────────────────
    public static double readDouble(String prompt)
            throws InvalidInputException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("input",
                    "'" + input + "' is not a valid price");
        }
    }

    // ── Read a positive double ────────────────────────────
    public static double readPositiveDouble(String prompt)
            throws InvalidInputException {
        double value = readDouble(prompt);
        if (value <= 0) {
            throw new InvalidInputException("input",
                    "Value must be greater than zero");
        }
        return value;
    }

    // ── Read a menu choice ────────────────────────────────
    public static int readMenuChoice(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // invalid choice
        }
    }

    // ── Read a date string ────────────────────────────────
    public static String readDate(String prompt)
            throws InvalidInputException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (!DateUtils.isValidDate(input)) {
            throw new InvalidInputException("date",
                    "Invalid date format. Use dd-MM-yyyy (e.g. 25-12-2026)");
        }
        return input;
    }

    // ── Read yes or no ────────────────────────────────────
    public static boolean readYesNo(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
}