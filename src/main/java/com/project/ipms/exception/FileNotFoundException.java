package com.project.ipms.exception;

public class FileNotFoundException extends RuntimeException {

    /**
     * Throws FileNotFoundException.
     * @param message error message
     */
    public FileNotFoundException(final String message) {
        super(message);
    }
}
