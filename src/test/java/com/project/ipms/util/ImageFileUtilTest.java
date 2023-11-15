package com.project.ipms.util;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.exception.InvalidFileTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.project.ipms.util.ImageFileUtil.checkFileValid;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ImageFileUtilTest {

    @Test
    void checkFileValidTest1() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                checkFileValid(null));

        String expectedMessage = "Filename is empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest2() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                checkFileValid(""));

        String expectedMessage = "Filename is empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest3() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                checkFileValid("abc"));

        String expectedMessage = "Filename is missing file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest4() {
        Exception exception = assertThrows(InvalidFileTypeException.class, () ->
                checkFileValid("abc."));

        String expectedMessage = "Not a supported file type. "
                                 + "Currently, we support the following image file types: jpg, jpeg, png.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest5() {
        Exception exception = assertThrows(InvalidFileTypeException.class, () ->
                checkFileValid("abc.pdf"));

        String expectedMessage = "Not a supported file type. "
                                 + "Currently, we support the following image file types: jpg, jpeg, png.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest6() {
        Exception exception = assertThrows(InvalidFileTypeException.class, () ->
                checkFileValid("test-name.jpg.png.pdfpng"));

        String expectedMessage = "Not a supported file type. "
                                 + "Currently, we support the following image file types: jpg, jpeg, png.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest7() {
        // This should pass
        String extension = checkFileValid("}.:':你好我是?]..{)_8}成步堂}>&%龙一^.,JnjM..ffvf.d.png");
        assertEquals(extension, ".png");
    }

    @Test
    void checkFileValidTest8() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                checkFileValid(".jpg"));

        String expectedMessage = "Filename cannot start with a dot '.'";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkFileValidTest9() {
        // This should pass
        String extension = checkFileValid("ace-attorney.png.jpg");
        assertEquals(extension, ".jpg");
    }

    @Test
    void checkFileValidTest10() {
        // This should pass
        String extension = checkFileValid("ace-attorney.jpg.png.jpeg.jpeg");
        assertEquals(extension, ".jpeg");
    }

    @Test
    void testImageCompare1() {
        BufferedImage img1;
        BufferedImage img2;
        BufferedImage img3;
        try {
            File f1 = ResourceUtils.getFile("src/test/resources/objection.png");
            File f2 = ResourceUtils.getFile("src/test/resources/objection_copy.png");
            File f3 = ResourceUtils.getFile("src/test/resources/miles-edgeworth.png");

            img1 = ImageIO.read(f1);
            img2 = ImageIO.read(f2);
            img3 = ImageIO.read(f3);

            boolean value1 = ImageFileUtil.compareImagesEqual(img1, img2);
            boolean value2 = ImageFileUtil.compareImagesEqual(img1, img3);

            assertTrue(value1);
            assertFalse(value2);
        } catch (Exception e) {
            throw new RuntimeException("Image comparison test failed: " + e.getMessage());
        }
    }

    @Test
    void testImageCompare2() {
        BufferedImage img1;
        BufferedImage img2;

        try {
            File f1 = ResourceUtils.getFile("src/test/resources/trans_tree.jpg");
            File f2 = ResourceUtils.getFile("src/test/resources/trans_tree_alpha_1.jpg");

            img1 = ImageIO.read(f1);
            img2 = ImageIO.read(f2);

            assertFalse(ImageFileUtil.compareImagesEqual(img1, img2));
        } catch (Exception e) {
            throw new RuntimeException("Image comparison test failed: " + e.getMessage());
        }
    }
}
