package com.project.ipms.service;
import com.project.ipms.util.ImageFileUtil;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ImageTransparency {
    /**
     * Alpha value for transparency.
     */
    private final float alpha;

    /**
     * BufferedImage object.
     */
    private final BufferedImage img;

    /**
     * Image file type format.
     */
    private final String format;

    /**
     * Constructor for image transparency.
     * @param img BufferedImage object
     * @param alpha Alpha value for transparency
     * @param format image file format
     */
    public ImageTransparency(final BufferedImage img,
                             final float alpha,
                             final String format) {
        this.alpha = alpha;
        this.img = ImageFileUtil.copyBufferedImage(img);
        this.format = format;
    }

    /**
     * Method for drawing.
     * @return Processed BufferedImage object.
     */
    public BufferedImage doDrawing() {
        BufferedImage transImg = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                java.awt.Transparency.TRANSLUCENT
        );

        Graphics2D g2d = transImg.createGraphics();
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        if (format.equals(".jpg") || format.equals(".jpeg")) {
            BufferedImage newImage = new BufferedImage(
                    transImg.getWidth(),
                    transImg.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            Graphics g = newImage.createGraphics();
            g.drawImage(transImg, 0, 0, null);
            g.dispose();
            return newImage;
        } else {
            return transImg;
        }
    }

    /**
     * Test Method.
     * @param args default args
     */
    public static void main(final String[] args) {
        BufferedImage img;
        BufferedImage transImage = null;
        String fileName = "";
        String format = "";

        try {
            File f = new File("src/test/test_img/ace-attorney-logo.png");
            img = ImageIO.read(f);
            fileName = f.getName();
            int index = fileName.lastIndexOf('.');
            format = fileName.substring(index + 1);
            ImageTransparency it = new ImageTransparency(img, (float) 0.5, ".png");
            transImage = it.doDrawing();
        } catch (IOException e) {
            System.out.println("error reading image");
        }

        try {
            File f = new File("src/test/test_img/" + "trans-" + fileName);
            assert transImage != null;
            ImageIO.write(transImage, format, f);
        } catch (IOException e) {
            System.out.println("error saving image");
        }
    }
}
