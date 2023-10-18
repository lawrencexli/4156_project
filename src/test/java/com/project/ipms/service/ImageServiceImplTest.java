package com.project.ipms.service;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

public class ImageServiceImplTest {

    ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl();
    }

    @AfterEach
    void tearDown() {
        imageService = null;
    }

    @Test
    public void testImageCompare() {
        BufferedImage img1 = null, img2 = null, img3 = null;
        try {
            File f1 = ResourceUtils.getFile("objection.png");
            File f2 = ResourceUtils.getFile("objection_copy.png");
            File f3 = ResourceUtils.getFile("miles-edgeworth.png");

            img1 = ImageIO.read(f1);
            img2 = ImageIO.read(f2);
            img3 = ImageIO.read(f3);

            boolean value1 = imageService.compare(img1, img2);
            boolean value2 = imageService.compare(img1, img3);

            Assertions.assertEquals(true, value1);
            Assertions.assertEquals(true, value2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void testImageTransparency() {
        BufferedImage img1 = null, test1 = null, img2 = null, test2 = null;
        try {
            File f1 = ResourceUtils.getFile("apple.png");
            File f2 = ResourceUtils.getFile("trans_apple.png");
            File f3 = ResourceUtils.getFile("tree.jpg");
            File f4 = ResourceUtils.getFile("trans_tree.jpg");

            img1 = ImageIO.read(f1);
            test1 = ImageIO.read(f2);
            img2 = ImageIO.read(f3);
            test2 = ImageIO.read(f4);

            String fileName = f1.getName();
            String format = fileName.substring(fileName.lastIndexOf('.')+1);
            BufferedImage trans_img1 = imageService.imageTransparency(img1, (float)0.5, format);

            fileName = f3.getName();
            format = fileName.substring(fileName.lastIndexOf('.')+1);
            BufferedImage trans_img2 = imageService.imageTransparency(img2, (float)0.5, format);

            boolean value1 = imageService.compare(trans_img1, test1);
            boolean value2 = imageService.compare(trans_img2, test2);

            Assertions.assertEquals(true, value1);
            Assertions.assertEquals(true, value2);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
