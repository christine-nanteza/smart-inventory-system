package com.christine.smartinventorysystem.interfaces;

/**
 * Interface for manageable entities.
 * Any class that manages data must implement these operations.
 */
public interface Manageable {
    void add() throws Exception;
    void update() throws Exception;
    void delete() throws Exception;
    void view();
    void search();
}