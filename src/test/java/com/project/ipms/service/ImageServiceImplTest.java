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
    public void testImageCompare1() {
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

    }
}
