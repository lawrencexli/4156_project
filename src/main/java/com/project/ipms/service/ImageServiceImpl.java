package com.project.ipms.service;

import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

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
}
