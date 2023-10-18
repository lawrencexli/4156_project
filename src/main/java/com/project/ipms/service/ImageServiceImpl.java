package com.project.ipms.service;

import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

@Service
public class ImageServiceImpl implements ImageService {
    /**
     * Make image transparent.
     *
     * @param inputImage Image input
     * @param alpha      Alpha value for transparency
     * @param format     Image file format
     * @return Processed image in BufferedImage format
     */
    @Override
    public boolean compare(BufferedImage img1, BufferedImage img2) {
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
                    Color color1 = new Color(pixel1, true);
                    int r1 = color1.getRed();
                    int g1 = color1.getGreen();
                    int b1 = color1.getBlue();
                    int pixel2 = img2.getRGB(i, j);
                    Color color2 = new Color(pixel2, true);
                    int r2 = color2.getRed();
                    int g2 = color2.getGreen();
                    int b2 = color2.getBlue();
                    // compare RGB of each pixel
                    if (r1 != r2 || g1 != g2 || b1 != b2) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

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
}
