package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import com.project.ipms.service.ImageService;
import com.project.ipms.util.ImageFileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest
class FileControllerTest {

    /**
     * Mock GCB file service interface.
     */
    @Mock
    private FileService fakeFileService;

    /**
     * Mock MongoDB service interface.
     */
    @Mock
    private MongoDBService fakeMongoDBService;

    /**
     * Mock Image file service interface.
     */
    @Mock
    private ImageService fakeImageFileService;

    /**
     * File controller.
     */
    @InjectMocks
    private FileController testFileController;

    /**
     * Bad alpha value 1.
     */
    private static final float BAD_ALPHA_VALUE1 = -0.034F;

    /**
     * Bad alpha value 2.
     */
    private static final float BAD_ALPHA_VALUE2 = 1.12F;

    /**
     * Good alpha value.
     */
    private static final float GOOD_ALPHA_VALUE = 0.87F;

    /**
     * Test image file.
     */
    private byte[] dummyImageByte;

    @BeforeEach
    void setUp() {
        try {
            testFileController = new FileController(
                    fakeFileService,
                    fakeMongoDBService,
                    fakeImageFileService);
            BufferedImage dummyImageBuffer = ImageIO.read(
                    ResourceUtils.getFile("src/test/resources/test1.jpg")
            );
            ByteArrayOutputStream dummyImageStream = new ByteArrayOutputStream();
            ImageIO.write(dummyImageBuffer, "jpg", dummyImageStream);
            dummyImageByte = dummyImageStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Controller test set up failed: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        testFileController = null;
    }

    @Test
    void testUploadFile1() throws IOException {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                dummyImageByte
        );

        String fakeID = "ace-attorney-7";

        ApiResponse apiResponse = testFileController.uploadFile(testMultipartFile, fakeID);
        assertEquals(apiResponse.getResponseMessage(), "File uploaded successfully");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.OK.value());
    }

    @Test
    void testUploadFile2() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                dummyImageByte
        );

        String fakeID = "     ";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(testMultipartFile, fakeID));

        String expectedMessage = "Client ID is missing or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUploadFile3() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                dummyImageByte
        );

        String fakeID = "";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(testMultipartFile, fakeID));

        String expectedMessage = "Client ID is missing or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUploadFile4() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                dummyImageByte
        );

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(testMultipartFile, null));

        String expectedMessage = "Client ID is missing or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUploadFile5() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                new byte[0]
        );

        String fakeID = "ace-attorney-7";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(testMultipartFile, fakeID));

        String expectedMessage = "File has no content or is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUploadFile6() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                (byte[]) null
        );

        String fakeID = "ace-attorney-7";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(testMultipartFile, fakeID));

        String expectedMessage = "File has no content or is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile1() {
        String testImageFile = "maya-fey.jpg";
        String testID = "ace-attorney-6";

        Mockito.when(fakeFileService.downloadFile(testImageFile)).
                thenReturn(new ByteArrayResource(dummyImageByte));

        ResponseEntity<Resource> content = testFileController.downloadFile(testImageFile, testID);
        String expected = "<200 OK OK,[Content-Type:\"application/octet-stream\", "
                + "Content-Disposition:\"attachment; filename=\"maya-fey.jpg\"\"]>";
        assertEquals(content.toString(), expected);
    }

    @Test
    void testDownloadFile2() {
        String testImageFile = "";
        String testID = "ace-attorney-6";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Filename is empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile3() {
        String testID = "ace-attorney-6";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(null, testID));

        String expectedMessage = "Filename is empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile4() {
        String testImageFile = "       ";
        String testID = "ace-attorney-6";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Filename is empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile5() {
        String testImageFile = "maya-fey";
        String testID = "";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Client ID is missing or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile6() {
        String testImageFile = "maya-fey";
        String testID = "  ";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Client ID is missing or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile7() {
        String testImageFile = "maya-fey";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, null));

        String expectedMessage = "Client ID is missing or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGenerateClientID() {
        Mockito.when(fakeMongoDBService.generateNewKey()).
                thenReturn("miles-edgeworth");

        ApiResponse apiResponse = testFileController.generateClientID();
        assertEquals(apiResponse.getResponseMessage(), "miles-edgeworth");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.OK.value());
    }

    @Test
    void testImageTransparent1() throws IOException {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.png";
        float alpha = GOOD_ALPHA_VALUE;

        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTarget = mock(BufferedImage.class);
        BufferedImage mockResult = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).
                thenReturn(mockResource);

        Mockito.when(fakeImageFileService.imageTransparency(mockTarget, alpha, ".png")).
                thenReturn(mockResult);

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTarget);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testTarget)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testResult)).
                    thenReturn(".png");

            ApiResponse response = testFileController.imageTransparent(testTarget, testResult, testID, alpha);
            assertEquals(response.getResponseMessage(), "Operation success");
            assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        }
    }

    @Test
    void testImageTransparent2() {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.png";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, BAD_ALPHA_VALUE1));

        String expectedMessage = "The alpha value should be in the range of 0 to 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, BAD_ALPHA_VALUE2));

        String expectedMessage2 = "The alpha value should be in the range of 0 to 1";
        String actualMessage2 = exception2.getMessage();

        assertTrue(actualMessage2.contains(expectedMessage2));
    }

    @Test
    void testImageTransparent3() {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.jpg";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, GOOD_ALPHA_VALUE));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageCrop1() throws IOException {
        // Arrange
        String target = "target.png";
        String result = "result.png";
        String id = "clientID";
        int x = 0;
        int y = 0;
        int width = 100;
        int height = 100;

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);
        BufferedImage mockResultImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(id + "/" + target)).thenReturn(mockResource);
        Mockito.when(fakeImageFileService.imageCropping(mockTargetImage,
                x,
                y,
                width,
                height,
                "png"
        )).thenReturn(mockResultImage);

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(target)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(result)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            // Act
            ApiResponse response = testFileController.imageCrop(target, result, id, x, y, width, height);

            //Assert
            assertEquals(response.getResponseMessage(), "Operation success");
            assertEquals(response.getStatusCode(), 200);
        }
    }

    @Test
    void testImageCrop2() {
        // Arrange
        String testTarget = "target.png";
        String testResult = "result.png";
        String testID = "clientID";
        int x = -1;
        int y = 0;
        int width = 100;
        int height = 100;

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).thenReturn(mockResource);
        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testTarget)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testResult)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

            String expectedMessage = "The x value should be in the range of 0 to the width of the target image";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            int x2 = 700;
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x2, y, width, height));

            String expectedMessage2 = "The x value should be in the range of 0 to the width of the target image";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageCrop3() {
        // Arrange
        String testTarget = "target.png";
        String testResult = "result.png";
        String testID = "clientID";
        int x = 0;
        int y = -1;
        int width = 100;
        int height = 100;

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).thenReturn(mockResource);
        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testTarget)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testResult)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

            String expectedMessage = "The y value should be in the range of 0 to the height of the target image";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            int y2 = 601;
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y2, width, height));

            String expectedMessage2 = "The y value should be in the range of 0 to the height of the target image";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageCrop4() {
        // Arrange
        String testTarget = "target.png";
        String testResult = "result.png";
        String testID = "clientID";
        int x = 0;
        int y = 0;
        int width = -1;
        int height = 100;

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).thenReturn(mockResource);
        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testTarget)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testResult)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

            String expectedMessage = "The width value should be from 0 to (target image's width - x)";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            int width2 = 601;
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width2, height));

            String expectedMessage2 = "The width value should be from 0 to (target image's width - x)";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageCrop5() {
        // Arrange
        String testTarget = "target.png";
        String testResult = "result.png";
        String testID = "clientID";
        int x = 0;
        int y = 0;
        int width = 100;
        int height = -1;

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).thenReturn(mockResource);
        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testTarget)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testResult)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

            String expectedMessage = "The height value should be from 0 to (target image's height - y)";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            int height2 = 601;
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height2));

            String expectedMessage2 = "The height value should be from 0 to (target image's height - y)";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageCrop6() {
        String testTarget = "target.png";
        String testResult = "result.jpg";
        String testID = "clientID";
        int x = 0;
        int y = 0;
        int width = 100;
        int height = 100;

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
