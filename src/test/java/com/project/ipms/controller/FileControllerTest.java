package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import com.project.ipms.service.ImageService;
import com.project.ipms.util.ImageFileUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
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
        dummyImageByte = null;
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
    void testUploadFile7() throws IOException {
        try (InputStream testInputStream = new FileInputStream("src/test/resources/fraud_pdf.png")) {
            MockMultipartFile testMultipartFile = new MockMultipartFile(
                    "test",
                    "test.jpg",
                    "image/jpg",
                    testInputStream
                    );

            String fakeID = "ace-attorney-7";

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.uploadFile(testMultipartFile, fakeID));

            String expectedMessage = "Image file validation failed";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("[testUploadFile7] Test failed. Cannot read dummy image file data.");
        }
    }

    @Test
    void testUploadFile8() {
        String fakeID = "ace-attorney-7";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(null, fakeID));

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
        String alpha = "0.7";

        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTarget = mock(BufferedImage.class);
        BufferedImage mockResult = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).
                thenReturn(mockResource);

        Mockito.when(fakeImageFileService.imageTransparency(mockTarget, Float.parseFloat(alpha), ".png")).
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
                testFileController.imageTransparent(testTarget, testResult, testID, "-0.34"));

        String expectedMessage = "The alpha value should be in the range of 0 to 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, "100"));

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
                testFileController.imageTransparent(testTarget, testResult, testID, "0.8"));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageTransparent4() {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.jpg";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, "0.8"));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageTransparent5() {
        String testTarget = "target.png";
        String testResult = "result.jpg";

        String expectedMessage = "Client ID is missing or null";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, "", "0.8"));

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, null, "0.8"));

        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));
    }

    @Test
    void testImageTransparent6() {
        String testID = "apollo-justice";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(null, "test.jpg", testID, "0.8"));

        String expectedMessage = "Target filename or result filename is empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(" ", "test.jpg", testID, "0.8"));

        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));

        Exception exception3 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent("test.jpg", " ", testID, "0.8"));

        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage));

        Exception exception4 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent("test.jpg", null, testID, "0.8"));

        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage));
    }

    @Test
    void testImageCrop1() throws IOException {
        // Arrange
        String target = "target.png";
        String result = "result.png";
        String id = "clientID";
        String x = "0";
        String y = "0";
        String width = "100";
        String height = "100";

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);
        BufferedImage mockResultImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(id + "/" + target)).thenReturn(mockResource);
        Mockito.when(fakeImageFileService.imageCropping(mockTargetImage,
                Integer.parseInt(x),
                Integer.parseInt(y),
                Integer.parseInt(width),
                Integer.parseInt(height),
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
        String x = "-1";
        String y = "0";
        String width = "100";
        String height = "100";

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

            String x2 = "700";
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
        String x = "0";
        String y = "-1";
        String width = "100";
        String height = "100";

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

            String y2 = "601";
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
        String x = "0";
        String y = "0";
        String width = "-1";
        String height = "100";

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

            String expectedMessage = "The width value should be from 1 to (target image's width - x)";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            String width2 = "601";
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width2, height));

            String expectedMessage2 = "The width value should be from 1 to (target image's width - x)";
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
        String x = "0";
        String y = "0";
        String  width = "100";
        String  height = "0";

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

            String expectedMessage = "The height value should be from 1 to (target image's height - y)";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            String height2 = "601";
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height2));

            String expectedMessage2 = "The height value should be from 1 to (target image's height - y)";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageCrop6() {
        String testTarget = "target.png";
        String testResult = "result.jpg";
        String testID = "clientID";
        String x = "0";
        String y = "0";
        String width = "100";
        String height = "100";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageCrop7() {
        String testID = "clientID";
        String x = "0";
        String y = "0";
        String width = "100";
        String height = "100";

        String expectedMessage = "Target filename or result filename is empty or null";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageCrop("", "test.jpg", testID, x, y, width, height));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageCrop(null, "test.jpg", testID, x, y, width, height));
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));

        Exception exception3 = assertThrows(BadRequestException.class, () ->
                testFileController.imageCrop("test.jpg", "", testID, x, y, width, height));
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage));

        Exception exception4 = assertThrows(BadRequestException.class, () ->
                testFileController.imageCrop("test.jpg", null, testID, x, y, width, height));
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage));
    }

    @Test
    void testImageCrop8() {
        String testTarget = "target.png";
        String testResult = "result.png";
        String testID = "clientID";
        String x = "0";
        String y = "0";
        String width = "100.50";
        String height = "100";

        Exception exception = assertThrows(NumberFormatException.class, () ->
                testFileController.imageCrop(testTarget, testResult, testID, x, y, width, height));

        String expectedMessage = "For input string: \"100.50\"";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageSaturation1() throws IOException {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.png";
        String saturationCoeff = "0";

        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTarget = mock(BufferedImage.class);
        BufferedImage mockResult = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(testID + "/" + testTarget)).
                thenReturn(mockResource);

        Mockito.when(fakeImageFileService.imageTransparency(mockTarget, Float.parseFloat(saturationCoeff), ".png")).
                thenReturn(mockResult);

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTarget);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testTarget)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(testResult)).
                    thenReturn(".png");

            ApiResponse response = testFileController.imageSaturate(testTarget, testResult, testID, saturationCoeff);
            assertEquals(response.getResponseMessage(), "Operation success");
            assertEquals(response.getStatusCode(), 200);
        }
    }

    @Test
    void testImageSaturation2() {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.png";
        String saturationCoeff = "256";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate(testTarget, testResult, testID, saturationCoeff));

        String expectedMessage = "The saturation coefficient should be in the range of 0 to 255";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        String saturationCoeff2 = "-0.034";

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate(testTarget, testResult, testID, saturationCoeff2));

        String actualMessage2 = exception2.getMessage();

        assertTrue(actualMessage2.contains(expectedMessage));
    }

    @Test
    void testImageSaturation3() {
        String testTarget = "target.png";
        String testResult = "result.jpg";
        String testID = "clientID";
        String saturationCoeff = "1";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate(testTarget, testResult, testID, saturationCoeff));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageSaturation4() {
        String testID = "clientID";
        String saturationCoeff = "1";

        String expectedMessage = "Target filename or result filename is empty or null";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate("target.png", "   ", testID, saturationCoeff));

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate("target.png", null, testID, saturationCoeff));

        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));

        Exception exception3 = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate("   ", "result.png", testID, saturationCoeff));

        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage));

        Exception exception4 = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate(null, "result.png", testID, saturationCoeff));

        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage));
    }

    @Test
    void testImageSaturation5() {
        String saturationCoeff = "1";

        String expectedMessage = "Client ID is missing or null";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate("target.png", "result.png", "", saturationCoeff));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageSaturate("target.png", "result.png", null, saturationCoeff));
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));
    }

    @Test
    void testImageOverlay1() throws IOException {
        // Arrange
        String target1 = "target1.png";
        String target2 = "target2.png";
        String result = "result.png";
        String id = "clientID";
        String x = "0";
        String y = "0";

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);
        BufferedImage mockResultImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(id + "/" + target1)).thenReturn(mockResource);
        Mockito.when(fakeFileService.downloadFile(id + "/" + target2)).thenReturn(mockResource);
        Mockito.when(fakeImageFileService.imageOverlay(
                mockTargetImage,
                mockTargetImage,
                Integer.parseInt(x),
                Integer.parseInt(y),
                "png"
        )).thenReturn(mockResultImage);

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(target1)).
                    thenReturn(".png");
            mockUtil.when(() -> ImageFileUtil.checkFileValid(target2)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(result)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            // Act
            ApiResponse response = testFileController.imageOverlay(target1, target2, result, id, x, y);

            //Assert
            assertEquals(response.getResponseMessage(), "Operation success");
            assertEquals(response.getStatusCode(), 200);
        }
    }

    @Test
    void testImageOverlay2() {
        // Arrange
        String target1 = "target1.png";
        String target2 = "target2.png";
        String result = "result.png";
        String id = "clientID";
        String x = "-1";
        String y = "0";

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(id + "/" + target1)).thenReturn(mockResource);
        Mockito.when(fakeFileService.downloadFile(id + "/" + target2)).thenReturn(mockResource);

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(target1)).
                    thenReturn(".png");
            mockUtil.when(() -> ImageFileUtil.checkFileValid(target2)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(result)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.imageOverlay(target1, target2, result, id, x, y));

            String expectedMessage = "The x value should be in the range of 0 to the width of the target image";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            String x2 = "700";
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageOverlay(target1, target2, result, id, x2, y));

            String expectedMessage2 = "The x value should be in the range of 0 to the width of the target image";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageOverlay3() {
        // Arrange
        String target1 = "target1.png";
        String target2 = "target2.png";
        String result = "result.png";
        String id = "clientID";
        String x = "0";
        String y = "-1";

        // Mock behavior for dependencies
        ByteArrayResource mockResource = mock(ByteArrayResource.class);
        BufferedImage mockTargetImage = mock(BufferedImage.class);

        Mockito.when(fakeFileService.downloadFile(id + "/" + target1)).thenReturn(mockResource);
        Mockito.when(fakeFileService.downloadFile(id + "/" + target2)).thenReturn(mockResource);

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class);
             MockedStatic<ImageFileUtil> mockUtil = Mockito.mockStatic(ImageFileUtil.class)) {
            imageIO.when(() -> ImageIO.read(mockResource.getInputStream())).
                    thenReturn(mockTargetImage);

            mockUtil.when(() -> ImageFileUtil.checkFileValid(target1)).
                    thenReturn(".png");
            mockUtil.when(() -> ImageFileUtil.checkFileValid(target2)).
                    thenReturn(".png");

            mockUtil.when(() -> ImageFileUtil.checkFileValid(result)).
                    thenReturn(".png");

            // Set the width and height of mockTargetImage
            Mockito.when(mockTargetImage.getWidth()).thenReturn(600);
            Mockito.when(mockTargetImage.getHeight()).thenReturn(600);

            Exception exception = assertThrows(BadRequestException.class, () ->
                    testFileController.imageOverlay(target1, target2, result, id, x, y));

            String expectedMessage = "The y value should be in the range of 0 to the height of the target image";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            String y2 = "700";
            Exception exception2 = assertThrows(BadRequestException.class, () ->
                    testFileController.imageOverlay(target1, target2, result, id, x, y2));

            String expectedMessage2 = "The y value should be in the range of 0 to the height of the target image";
            String actualMessage2 = exception2.getMessage();

            assertTrue(actualMessage2.contains(expectedMessage2));
        }
    }

    @Test
    void testImageOverlay4() {
        // Arrange
        String target1 = "target1.png";
        String target2 = "target2.png";
        String result = "result.jpg";
        String id = "clientID";
        String x = "0";
        String y = "0";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay(target1, target2, result, id, x, y));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageOverlay5() {
        String target1 = "target1.png";
        String target2 = "target2.jpg";
        String result = "result.png";
        String id = "clientID";
        String x = "0";
        String y = "0";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay(target1, target2, result, id, x, y));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testImageOverlay6() {
        // Arrange
        String id = "clientID";
        String x = "0";
        String y = "0";

        String expectedMessage = "Target filenames or result filename is empty or null";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay("", "test.jpg", "result.jpg", id, x, y));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay(null, "test.jpg", "result.jpg", id, x, y));
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));

        Exception exception3 = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay("test.jpg", "", "result.jpg", id, x, y));
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage));

        Exception exception4 = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay("test.jpg", null, "result.jpg", id, x, y));
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage));

        Exception exception5 = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay("test.jpg", "test.jpg", "", id, x, y));
        String actualMessage5 = exception5.getMessage();
        assertTrue(actualMessage5.contains(expectedMessage));

        Exception exception6 = assertThrows(BadRequestException.class, () ->
                testFileController.imageOverlay("test.jpg", "test.jpg", null, id, x, y));
        String actualMessage6 = exception6.getMessage();
        assertTrue(actualMessage6.contains(expectedMessage));
    }
}
