package com.project.ipms.exception;

public class FileAlreadyExistsException extends RuntimeException {

    /**
     * Throws InvalidFileTypeException.
     * @param message Message showing that the file extension is invalid
     */
    public FileAlreadyExistsException(final String message) {
        super(message);
    }
}
