package com.project.ipms.util;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.exception.InvalidFileTypeException;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

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
     * Copy Buffered Image.
     * Source: "<a href="https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage">...</a>"
     * @param bi Original buffered image
     * @return New identical copy of original buffered image
     */
    public static BufferedImage copyBufferedImage(final BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
