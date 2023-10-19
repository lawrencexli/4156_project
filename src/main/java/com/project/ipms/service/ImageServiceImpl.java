package com.project.ipms.service;

import org.springframework.stereotype.Service;

// import com.google.type.Color;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.Color;

@Service
public class ImageServiceImpl implements ImageService {
    /**
     * Make image transparent.
     * @param inputImage Image input
     * @param alpha      Alpha value for transparency
     * @param format     Image file format
     * @return Processed image in BufferedImage format
     */
    @Override
    public BufferedImage imageTransparency(final BufferedImage inputImage,
                                           final float alpha,
                                           final String format) {
        BufferedImage transImg = new BufferedImage(
                inputImage.getWidth(null),
                inputImage.getHeight(null),
                Transparency.TRANSLUCENT);
        Graphics2D g2d = transImg.createGraphics();
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();

        if (format.equals(".jpg") || format.equals(".jpeg")) {
            BufferedImage newImage = new BufferedImage(
                    transImg.getWidth(),
                    transImg.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = newImage.createGraphics();
            g.drawImage(transImg, 0, 0, null);
            g.dispose();
            return newImage;
        } else {
            return transImg;
        }
    }

    /**
    * Changes image's saturation.
    * @param inputImage Image input
    * @param saturationCoeff    Coefficient to multiply saturation by (0-255)
    * @param format     Image file format
    * @return Processed image in BufferedImage format
    */
    @Override
    public BufferedImage imageSaturation(final BufferedImage inputImage,
                                        final float saturationCoeff,
                                        final String format) {
        for (int x = 0; x < inputImage.getWidth(); x++) {
            for (int y = 0; y < inputImage.getHeight(); y++) {
                // Iterate through pixels
                int pixel = inputImage.getRGB(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = (pixel) & 0xFF;

                // Convert to (hue, saturation, brightness)
                float[] hsb = Color.RGBtoHSB(r, g, b, null);

                // Modify saturation and set pixel to new value
                int newRGB = Color.HSBtoRGB(hsb[0], saturationCoeff * hsb[1], hsb[2]);
                inputImage.setRGB(x, y, newRGB);
            }
        }
        if (format.equals(".jpg") || format.equals(".jpeg")) {
            BufferedImage newImage = new BufferedImage(
                    inputImage.getWidth(),
                    inputImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = newImage.createGraphics();
            g.drawImage(inputImage, 0, 0, null);
            g.dispose();
            return newImage;
        } else {
            return inputImage;
        }
    }
}
