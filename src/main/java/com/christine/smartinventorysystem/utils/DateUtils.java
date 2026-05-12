package com.christine.smartinventorysystem.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date formatting and parsing.
 */
public class DateUtils {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // ── Format date to string ─────────────────────────────
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    // ── Format datetime to string ─────────────────────────
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMAT);
    }

    // ── Parse string to date ──────────────────────────────
    public static LocalDate parseDate(String dateStr)
            throws DateTimeParseException {
        return LocalDate.parse(dateStr, DATE_FORMAT);
    }

    // ── Get today as formatted string ─────────────────────
    public static String today() {
        return formatDate(LocalDate.now());
    }

    // ── Check if date is valid ────────────────────────────
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}