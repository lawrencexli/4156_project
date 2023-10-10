package com.project.ipms.exception;

public class BadRequestException extends RuntimeException {

    /**
     * Throws BadRequestException.
     * @param message Message showing the type of bad request
     */
    public BadRequestException(final String message) {
        super(message);
    }
}
