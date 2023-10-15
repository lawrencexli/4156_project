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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


@SpringBootTest
class FileControllerTest {

    /**
     * Mock GCB file service interface.
     */
    @Mock
    FileService fakeFileService;

    /**
     * Mock MongoDB service interface.
     */
    @Mock
    MongoDBService fakeMongoDBService;

    /**
     * Mock Image file service interface.
     */
    @Mock
    ImageService fakeImageFileService;

    /**
     * File controller.
     */
    @InjectMocks
    FileController testFileController;

    @BeforeEach
    void setUp() {
        testFileController = new FileController(
                fakeFileService,
                fakeMongoDBService,
                fakeImageFileService);
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
                "image-content-byte".getBytes()
        );

        String fakeID = "ace-attorney-7";

        ApiResponse apiResponse = testFileController.uploadFile(testMultipartFile, fakeID);
        assertEquals(apiResponse.getResponseMessage(), "File uploaded successfully");
        assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test
    void testUploadFile2() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                "image-content-byte".getBytes()
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
                "image-content-byte".getBytes()
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
                "image-content-byte".getBytes()
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
                "".getBytes()
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
                thenReturn(new ByteArrayResource("image-file-content".getBytes()));

        ResponseEntity<Resource> content = testFileController.downloadFile(testImageFile, testID);
        String expected = "<200 OK OK,[Content-Type:\"application/octet-stream\", " +
                "Content-Disposition:\"attachment; filename=\"maya-fey.jpg\"\"]>";
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
        assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test
    void testImageTransparent1() throws IOException {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.png";
        float alpha = 0.5F;

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
            assertEquals(response.getStatusCode(), 200);
        }
    }

    @Test
    void testImageTransparent2() {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.png";
        float alpha = 1.2F;

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, alpha));

        String expectedMessage = "The alpha value should be in the range of 0 to 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        float alpha2 = -0.034F;

        Exception exception2 = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, alpha2));

        String expectedMessage2 = "The alpha value should be in the range of 0 to 1";
        String actualMessage2 = exception2.getMessage();

        assertTrue(actualMessage2.contains(expectedMessage2));
    }

    @Test
    void testImageTransparent3() {
        String testID = "apollo-justice";
        String testTarget = "target.png";
        String testResult = "result.jpg";
        float alpha = 0.8F;

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.imageTransparent(testTarget, testResult, testID, alpha));

        String expectedMessage = "Target file extension is different from result file extension";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}