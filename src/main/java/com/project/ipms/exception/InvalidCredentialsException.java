package com.project.ipms.exception;

public class InvalidCredentialsException extends RuntimeException {

    /**
     * Throws InvalidFileTypeException.
     * @param message Message showing that the file extension is invalid
     */
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
