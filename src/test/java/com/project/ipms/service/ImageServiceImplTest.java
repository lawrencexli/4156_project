package com.project.ipms.service;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import com.project.ipms.util.ImageFileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

public class ImageServiceImplTest {

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
     * Test for Transparency.
     */
    @Test
    public void testImageTransparency() {
        BufferedImage img1;
        BufferedImage test1;
        BufferedImage img2;
        BufferedImage test2;
        BufferedImage testResultPng;
        BufferedImage testResultJpg;
        try {
            File f1 = ResourceUtils.getFile("src/test/resources/apple.png");
            File f2 = ResourceUtils.getFile("src/test/resources/trans_apple.png");
            File f3 = ResourceUtils.getFile("src/test/resources/tree.jpg");
            File f4 = ResourceUtils.getFile("src/test/resources/trans_tree.jpg");

            img1 = ImageIO.read(f1);
            test1 = ImageIO.read(f2);
            img2 = ImageIO.read(f3);
            test2 = ImageIO.read(f4);

            // Do image transparency
            String format = ".png";
            BufferedImage transImg1 = imageService.imageTransparency(img1, 0.5F, format);

            String format2 = ".jpg";
            BufferedImage transImg2 = imageService.imageTransparency(img2, 0.5F, format2);

            // Specify the file and path destination
            String pngTestResult = "src/test/resources/trans_apple_test.png";
            String jpgTestResult = "src/test/resources/trans_tree_test.jpg";

            // Write the result to file
            File outputFile1 = new File(pngTestResult);
            ImageIO.write(transImg1, "png", outputFile1);

            // To write a jpg image to file, we need to convert the color format to RGB.
            // This is because the original transImg2 has RGBA as color format,
            // which is incompatible with jpg file type.
            BufferedImage jpgImage = new BufferedImage(
                    transImg2.getWidth(),
                    transImg2.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            jpgImage.createGraphics().drawImage(transImg2, 0, 0, null);
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
            throw new RuntimeException("Image transparency failed: " + e.getMessage());
        }
    }

    /**
     * Test for Cropping.
     */
    @Test
    public void testImageCropping() {
        BufferedImage img1;
        BufferedImage test1;
        BufferedImage img2;
        BufferedImage test2;
        BufferedImage testResultPng;
        BufferedImage testResultJpg;
        try {
            File f1 = ResourceUtils.getFile("src/test/resources/apple.png");
            File f2 = ResourceUtils.getFile("src/test/resources/apple_cropped.png");
            File f3 = ResourceUtils.getFile("src/test/resources/tree.jpg");
            File f4 = ResourceUtils.getFile("src/test/resources/tree_cropped.jpg");

            img1 = ImageIO.read(f1);
            test1 = ImageIO.read(f2);
            img2 = ImageIO.read(f3);
            test2 = ImageIO.read(f4);

            // Do image transparency
            String format = ".png";
            BufferedImage croppedImg1 = imageService.imageCropping(img1, 0, 0, 1150, 893, format);

            String format2 = ".jpg";
            BufferedImage croppedImg2 = imageService.imageCropping(img2, 0, 0, 403, 422, format2);

            // Specify the file and path destination
            String pngTestResult = "src/test/resources/cropped_apple_test.png";
            String jpgTestResult = "src/test/resources/cropped_tree_test.jpg";

            // Write the result to file
            File outputFile1 = new File(pngTestResult);
            ImageIO.write(croppedImg1, "png", outputFile1);

            // To write a jpg image to file, we need to convert the color format to RGB.
            // This is because the original transImg2 has RGBA as color format,
            // which is incompatible with jpg file type.
            BufferedImage jpgImage = new BufferedImage(
                    croppedImg2.getWidth(),
                    croppedImg2.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            jpgImage.createGraphics().drawImage(croppedImg2, 0, 0, null);
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
            throw new RuntimeException("Image transparency failed: " + e.getMessage());
        }
    }

    /**
     * Test image saturation.
     */
    @Test
    public void testImageSaturation() {
        BufferedImage test1;
        BufferedImage check1;
        try {
            File test1File = new File("src/test/resources/courtroom-entrance.png");
            File check1File = new File("src/test/resources/sat-courtroom-entrance.png");

            test1 = ImageIO.read(test1File);
            check1 = ImageIO.read(check1File);

            BufferedImage output1 = imageService.imageSaturation(test1, 0, ".png");
            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(check1, output1));
        } catch (Exception e) {
            throw new RuntimeException("Image saturation failed" + e.getMessage());
        }
    }
}
