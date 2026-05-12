package com.christine.smartinventorysystem.exceptions;

public class InvalidInputException extends Exception {

    private String fieldName;

    public InvalidInputException(String fieldName, String message) {
        super("Invalid input for '" + fieldName + "': " + message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}