package com.project.ipms.exception;

import com.project.ipms.controller.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApiExceptionHandlerTest {

    /**
     * Handler class.
     */
    ApiExceptionHandler testHandler;

    @BeforeEach
    void setUp() {
        testHandler = new ApiExceptionHandler();
    }

    @AfterEach
    void tearDown() {
        testHandler = null;
    }

    @Test
    void testHandleInvalidFileTypeException() {
        InvalidFileTypeException e = new InvalidFileTypeException("error");
        ApiResponse apiResponse = testHandler.handleInvalidFileTypeException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 415);
    }

    @Test
    void handleBadRequestException() {
        BadRequestException e = new BadRequestException("error");
        ApiResponse apiResponse = testHandler.handleBadRequestException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 400);
    }

    @Test
    void handleMultipartException() {
        MultipartException e = new MultipartException("error");
        ApiResponse apiResponse = testHandler.handleMultipartException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 400);
    }

    @Test
    void handleFileNotFoundException() {
        FileNotFoundException e = new FileNotFoundException("error");
        ApiResponse apiResponse = testHandler.handleFileNotFoundException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 404);
    }

    @Test
    void handleIOException() {
        IOException e = new IOException("error");
        ApiResponse apiResponse = testHandler.handleIOException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 500);
    }

    @Test
    void handleInvalidCredentialsException() {
        InvalidCredentialsException e = new InvalidCredentialsException("error");
        ApiResponse apiResponse = testHandler.handleInvalidCredentialsException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 403);
    }

    @Test
    void handleCriticalServerException() {
        CriticalServerException e = new CriticalServerException("error");
        ApiResponse apiResponse = testHandler.handleCriticalServerException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 500);
    }

    @Test
    void handleFileAlreadyExistsException() {
        FileAlreadyExistsException e = new FileAlreadyExistsException("error");
        ApiResponse apiResponse = testHandler.handleFileAlreadyExistsException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), 409);
    }
}