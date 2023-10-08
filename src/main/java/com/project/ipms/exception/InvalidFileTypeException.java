package com.project.ipms.exception;

public class InvalidFileTypeException extends RuntimeException {

    /**
     * Throws InvalidFileTypeException.
     * @param message Message showing that the file extension is invalid
     */
    public InvalidFileTypeException(final String message) {
        super(message);
    }
}
