package com.project.ipms.util;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.exception.InvalidFileTypeException;

public final class ImageFileUtil {

    /**
     * List of supported file extensions for the image service.
     */
    private static final String[] SUPPORTED_EXTENSIONS = {".jpeg", ".jpg", ".png"};

    private ImageFileUtil() { }

    /**
     * Check if the original filename is valid and is a supported file.
     * @param fileName the original filename as String
     */
    public static void checkFileValid(final String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("Original file name is empty or null");
        }

        if (!fileName.contains(".")) {
            throw new BadRequestException("Original file name is missing file extension");
        }

        for (String extension: SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith(extension)) {
                return;
            }
        }

        throw new InvalidFileTypeException("Not a supported file type");
    }
}
