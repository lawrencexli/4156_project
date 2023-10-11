package com.project.ipms.exception;

public class CriticalServerException extends RuntimeException {

    /**
     * Throws InvalidFileTypeException.
     * @param message Message showing that the file extension is invalid
     */
    public CriticalServerException(final String message) {
        super(message);
    }
}
