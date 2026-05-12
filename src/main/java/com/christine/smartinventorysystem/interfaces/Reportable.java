package com.christine.smartinventorysystem.interfaces;

/**
 * Interface for report generation.
 * Any class that generates reports must implement these.
 */
public interface Reportable {
    void generateFullReport();
    void generateLowStockReport();
    void generateExpiredReport();
    void generateSalesReport();
    void generateDailySummary();
}