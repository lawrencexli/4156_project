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
    public void testImageTransparency() {
        BufferedImage img1, test1, img2, test2, testResultPng, testResultJpg;
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
            String format = f1.getName().substring(1);
            BufferedImage transImg1 = imageService.imageTransparency(img1, 0.5F, format);

            String format2 = f3.getName().substring(1);
            BufferedImage transImg2 = imageService.imageTransparency(img2, 0.5F, format2);

            // Specify the file and path destination
            String png_test_result = "src/test/resources/trans_apple_test.png";
            String jpg_test_result = "src/test/resources/trans_tree_test.jpg";

            // Write the result to file
            File outputFile1 = new File(png_test_result);
            ImageIO.write(transImg1, "png", outputFile1);

            // To write a jpg image to file, we need to convert the color format to RGB.
            // This is because the original transImg2 has RGBA as color format,
            // which is incompatible with jpg file type.
            BufferedImage jpgImage = new BufferedImage(
                    transImg2.getWidth(),
                    transImg2.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            jpgImage.createGraphics().drawImage(transImg2, 0, 0, null);
            File outputFile2 = new File(jpg_test_result);
            ImageIO.write(jpgImage, "jpg", outputFile2);

            // Read back the result and compare with the true images
            File f5 = ResourceUtils.getFile(png_test_result);
            File f6 = ResourceUtils.getFile(jpg_test_result);

            testResultPng = ImageIO.read(f5);
            testResultJpg = ImageIO.read(f6);

            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultPng, test1));
            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultJpg, test2));
        } catch (Exception e) {
            throw new RuntimeException("Image transparency failed: " + e.getMessage());
        }
    }

    @Test
    public void testImageSaturation() {
        BufferedImage test1, check1;
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
