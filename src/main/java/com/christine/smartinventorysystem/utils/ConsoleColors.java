package com.christine.smartinventorysystem.utils;

/**
 * ANSI color codes for console output styling.
 * Makes the console interface look clean and professional.
 */
public class ConsoleColors {

    // ── Reset ─────────────────────────────────────────────
    public static final String RESET  = "\033[0m";

    // ── Colors ────────────────────────────────────────────
    public static final String RED    = "\033[0;31m";
    public static final String GREEN  = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE   = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN   = "\033[0;36m";
    public static final String WHITE  = "\033[0;37m";

    // ── Bold Colors ───────────────────────────────────────
    public static final String BOLD_RED    = "\033[1;31m";
    public static final String BOLD_GREEN  = "\033[1;32m";
    public static final String BOLD_YELLOW = "\033[1;33m";
    public static final String BOLD_BLUE   = "\033[1;34m";
    public static final String BOLD_CYAN   = "\033[1;36m";
    public static final String BOLD_WHITE  = "\033[1;37m";

    // ── Shortcut print methods ────────────────────────────
    public static void printSuccess(String message) {
        System.out.println(GREEN + "✔ " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "✘ " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(CYAN + "ℹ " + message + RESET);
    }

    public static void printHeader(String title) {
        int width = 44;
        String line = "═".repeat(width);
        System.out.println(BOLD_BLUE + "╔" + line + "╗");
        int padding = (width - title.length()) / 2;
        String padded = " ".repeat(padding) + title;
        System.out.printf("║%-" + width + "s║%n", padded);
        System.out.println("╚" + line + "╝" + RESET);
    }

    public static void printDivider() {
        System.out.println(BLUE +
                "──────────────────────────────────────────────"
                + RESET);
    }
}