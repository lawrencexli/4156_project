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
     * @param y The X coordinate of the upper-left corner of the specified rectangular region
     * @param width The width of the specified rectangular region
     * @param height The height of the specified rectangular region
     * @param format Image file format
     * @return Processed image in BufferedImage format
     */
    BufferedImage imageCropping(BufferedImage inputImage, int x, int y, int width, int height, String format);
}
