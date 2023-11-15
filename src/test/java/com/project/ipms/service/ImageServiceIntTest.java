package com.project.ipms.service;

import com.project.ipms.util.ImageFileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageServiceIntTest {

    /**
     * An image processing service object for calling methods.
     */
    private ImageServiceImpl imageService;

    @BeforeEach
    final void setUp() {
        imageService = new ImageServiceImpl();
    }

    @AfterEach
    final void tearDown() {
        imageService = null;
    }

    /**
     * Internal integration test for all functionalities.
     */
    @Test
    public void integrationTest1() {
        BufferedImage img1;
        BufferedImage test1;
        BufferedImage img2;
        BufferedImage test2;
        BufferedImage testResultPng;
        BufferedImage testResultJpg;
        try {
            File f1 = ResourceUtils.getFile("src/test/resources/apple.png");
            File f2 = ResourceUtils.getFile("src/test/resources/apple_result_1.png");
            File f3 = ResourceUtils.getFile("src/test/resources/tree.jpg");
            File f4 = ResourceUtils.getFile("src/test/resources/tree_result_1.jpg");

            img1 = ImageIO.read(f1);
            test1 = ImageIO.read(f2);
            img2 = ImageIO.read(f3);
            test2 = ImageIO.read(f4);

            String format = ".png";
            BufferedImage transImg1 = imageService.imageTransparency(img1, 0.5F, format);
            BufferedImage croppedImg1 = imageService.imageCropping(transImg1, 500, 500, 600, 600, format);
            BufferedImage output1 = imageService.imageSaturation(croppedImg1, 0.5F, format);

            String format2 = ".jpg";
            BufferedImage transImg2 = imageService.imageTransparency(img2, 0.5F, format2);
            BufferedImage croppedImg2 = imageService.imageCropping(transImg2, 100, 100, 200, 200, format);
            BufferedImage output2 = imageService.imageSaturation(croppedImg2, 0.9F, format);

            String pngTestResult = "src/test/resources/apple_result_1_test.png";
            String jpgTestResult = "src/test/resources/tree_result_1_test.jpg";

            // Write the result to file
            File outputFile1 = new File(pngTestResult);
            ImageIO.write(output1, "png", outputFile1);

            // To write a jpg image to file, we need to convert the color format to RGB.
            // This is because the original transImg2 has RGBA as color format,
            // which is incompatible with jpg file type.
            BufferedImage jpgImage = new BufferedImage(
                    croppedImg2.getWidth(),
                    croppedImg2.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            jpgImage.createGraphics().drawImage(output2, 0, 0, null);
            File outputFile2 = new File(jpgTestResult);
            ImageIO.write(jpgImage, "jpg", outputFile2);

            // Read back the result and compare with the true images
            File f5 = ResourceUtils.getFile(pngTestResult);
            File f6 = ResourceUtils.getFile(jpgTestResult);

            testResultPng = ImageIO.read(f5);
            testResultJpg = ImageIO.read(f6);

            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultPng, test1));
            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultJpg, test2));
        } catch (Exception e) {
            throw new RuntimeException("Image transparency test failed: " + e.getMessage());
        }
    }

    /**
     * Internal integration test 2.
     */
    @Test
    public void integrationTest2() {
        BufferedImage img1;
        try {
            File f1 = ResourceUtils.getFile("src/test/resources/apple.png");
            img1 = ImageIO.read(f1);

            String format = ".png";
            BufferedImage transImg1 = imageService.imageTransparency(img1, 0.5F, format);
            BufferedImage croppedImg1 = imageService.imageCropping(transImg1, 9999, 9999, 600, 600, format);
            imageService.imageSaturation(croppedImg1, 0.5F, format);
        } catch (Exception e) {
            if (!e.getMessage().contains("(x + width) is outside raster")) {
                throw new RuntimeException("Image transparency test failed: " + e.getMessage());
            }
        }
    }

    /**
     * Internal integration test 2.
     */
    @Test
    public void integrationTest3() {
        BufferedImage img1;
        try {
            File f1 = ResourceUtils.getFile("src/test/resources/apple.png");
            img1 = ImageIO.read(f1);

            String format = ".png";
            BufferedImage transImg1 = imageService.imageTransparency(img1, 3F, format);
            BufferedImage croppedImg1 = imageService.imageCropping(transImg1, 9999, 9999, 600, 600, format);
            imageService.imageSaturation(croppedImg1, 0.5F, format);
        } catch (Exception e) {
            if (!e.getMessage().contains("alpha value out of range")) {
                throw new RuntimeException("Image transparency test failed: " + e.getMessage());
            }
        }
    }
}
