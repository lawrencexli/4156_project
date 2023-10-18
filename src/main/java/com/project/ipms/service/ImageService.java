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
    boolean compare(BufferedImage img1, BufferedImage img2);

    BufferedImage imageTransparency(BufferedImage inputImage, float alpha, String format);
}
