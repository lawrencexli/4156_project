package com.project.ipms.service;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ImageTransparency {
    private float alpha;
    private BufferedImage img;

    public ImageTransparency(BufferedImage img, float alpha){
        this.alpha = alpha;
        this.img = img;
    }

    private BufferedImage doDrawing(String format) {
        BufferedImage trans_img = new BufferedImage(img.getWidth(null), img.getHeight(null), java.awt.Transparency.TRANSLUCENT);
        Graphics2D g2d = trans_img.createGraphics();

        AlphaComposite acomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(acomp);
        g2d.drawImage(img, 0, 0, null);

        g2d.dispose();

        if(format.equals("jpg") || format.equals("jpeg")){
            BufferedImage newImage = new BufferedImage(trans_img.getWidth(), trans_img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = newImage.createGraphics();
            g.drawImage(trans_img, 0, 0, null);
            g.dispose();
            return newImage;
        }else{
            return trans_img;
        }
    }

    public static void main(String[] args) {
        BufferedImage img=null, trans_image = null;
        String fileName = "", format="";

        try {
            // File f = new File("C:\\Users\\user\\Desktop\\Columbia\\Advance software engineer\\apple.png");
            // File f = new File("C:\\Users\\user\\Desktop\\Columbia\\Advance software engineer\\tree.jpg");
            File f = new File("C:\\Users\\user\\Desktop\\Columbia\\Advance software engineer\\leaf.jpeg");


            img = ImageIO.read(f);
            fileName = f.getName();
            int index = fileName.lastIndexOf('.');
            format = fileName.substring(index+1);
            ImageTransparency it = new ImageTransparency(img, (float)0.5);
            trans_image = it.doDrawing(format);
        } catch (IOException e) {
            System.out.println("error reading image");
        }
        try {
            File f = new File("C:\\Users\\user\\Desktop\\Columbia\\Advance software engineer\\" + "trans_" + fileName);

            ImageIO.write(trans_image, format, f);
        } catch (IOException e) {
            System.out.println("error saving image");
        }

    }

}
