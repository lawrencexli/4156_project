package com.project.ipms.util;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.exception.InvalidFileTypeException;

import java.awt.image.BufferedImage;

public final class ImageFileUtil {

    /**
     * List of supported file extensions for the image service.
     */
    private static final String[] SUPPORTED_EXTENSIONS = {".jpeg", ".jpg", ".png"};

    private ImageFileUtil() { }

    /**
     * Check if the original filename is valid and is a supported file.
     * @param fileName the original filename as String
     * @return the image file extension
     */
    public static String checkFileValid(final String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("Filename is empty or null");
        }

        if (!fileName.contains(".")) {
            throw new BadRequestException("Filename is missing file extension");
        }

        if (fileName.startsWith(".")) {
            throw new BadRequestException("Filename cannot start with a dot '.'");
        }

        for (String extension: SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith(extension)) {
                return extension;
            }
        }

        throw new InvalidFileTypeException(
                "Not a supported file type. Currently, we support the following image file types: jpg, jpeg, png."
        );
    }

    /**
     * Compare 2 images and see if they are equal.
     * @param img1 First image
     * @param img2 Second image
     * @return True if 2 images are identical, False otherwise
     */
    public static boolean compareImagesEqual(final BufferedImage img1, final BufferedImage img2) {
        int w1 = img1.getWidth();
        int w2 = img2.getWidth();
        int h1 = img1.getHeight();
        int h2 = img2.getHeight();
        if ((w1 != w2) || (h1 != h2)) {
            return false;
        } else {
            for (int j = 0; j < h1; j++) {
                for (int i = 0; i < w1; i++) {
                    // Getting the RGB values of a pixel
                    int pixel1 = img1.getRGB(i, j);
                    int pixel2 = img2.getRGB(i, j);
                    // compare RGB of each pixel
                    if (pixel1 != pixel2) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
