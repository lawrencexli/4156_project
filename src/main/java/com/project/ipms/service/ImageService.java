package com.project.ipms.service;

import java.awt.image.BufferedImage;

public interface ImageService {
    /**
     * Make image transparent.
     * @param inputImage Image input
     * @param alpha Alpha value for transparency
     * @param format Image file format
     * @return Processed image in BufferedImage format
     */
    BufferedImage imageTransparency(BufferedImage inputImage, float alpha, String format);

    /**
     * Change the saturation.
     * @param inputImage Image input
     * @param saturationCoeff Coefficient to multiply saturation by
     * @param format Image file format
     * @return Processed image in BufferedImage format
     */
    BufferedImage imageSaturation(BufferedImage inputImage, float saturationCoeff, String format);

    /**
     * Crop the image.
     * @param inputImage Image input
     * @param x The X coordinate of the upper-left corner of the specified rectangular region
     * @param y The Y coordinate of the upper-left corner of the specified rectangular region
     * @param width The width of the specified rectangular region
     * @param height The height of the specified rectangular region
     * @param format Image file format
     * @return Processed image in BufferedImage format
     */
    BufferedImage imageCropping(BufferedImage inputImage, int x, int y, int width, int height, String format);

    /**
     * Overlay InputImage1 over InputImage2.
     * @param inputImage1 Image input 1
     * @param inputImage2 Image input 2
     * @param x           The X coordinate of the upper-left corner for the overlay
     * @param y           The Y coordinate of the upper-left corner for the overlay
     * @param format      Image file format
     * @return Processed image in BufferedImage format
     */
    BufferedImage imageOverlay(BufferedImage inputImage1, BufferedImage inputImage2,
                               int x, int y, String format);
}
