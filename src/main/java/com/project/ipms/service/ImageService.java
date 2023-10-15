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
}
