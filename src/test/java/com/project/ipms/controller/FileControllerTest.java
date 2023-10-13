package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


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
     * File controller.
     */
    @InjectMocks
    FileController testFileController;

    @BeforeEach
    void setUp() {
        testFileController = new FileController(fakeFileService, fakeMongoDBService);
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

        String expectedMessage = "Client ID is missing or is null";
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

        String expectedMessage = "Client ID is missing or is null";
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

        String fakeID = null;

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.uploadFile(testMultipartFile, fakeID));

        String expectedMessage = "Client ID is missing or is null";
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

        String expectedMessage = "File has no content or file is null";
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

        String expectedMessage = "File has no content or file is null";
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

        String expectedMessage = "Filename is null or empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile3() {
        String testImageFile = null;
        String testID = "ace-attorney-6";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Filename is null or empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile4() {
        String testImageFile = "       ";
        String testID = "ace-attorney-6";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Filename is null or empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile5() {
        String testImageFile = "maya-fey";
        String testID = "";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Client ID is missing or is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile6() {
        String testImageFile = "maya-fey";
        String testID = "  ";

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Client ID is missing or is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDownloadFile7() {
        String testImageFile = "maya-fey";
        String testID = null;

        Exception exception = assertThrows(BadRequestException.class, () ->
                testFileController.downloadFile(testImageFile, testID));

        String expectedMessage = "Client ID is missing or is null";
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
}