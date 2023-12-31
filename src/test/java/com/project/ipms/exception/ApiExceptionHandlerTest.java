package com.project.ipms.exception;

import com.project.ipms.controller.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ApiExceptionHandlerTest {

    /**
     * Handler class.
     */
    private ApiExceptionHandler testHandler;

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
        assertEquals(apiResponse.getStatusCode(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    void handleBadRequestException() {
        BadRequestException e = new BadRequestException("error");
        ApiResponse apiResponse = testHandler.handleBadRequestException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleMultipartException() {
        MultipartException e = new MultipartException("error");
        ApiResponse apiResponse = testHandler.handleMultipartException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleFileNotFoundException() {
        FileNotFoundException e = new FileNotFoundException("error");
        ApiResponse apiResponse = testHandler.handleFileNotFoundException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleIOException() {
        IOException e = new IOException("error");
        ApiResponse apiResponse = testHandler.handleIOException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void handleInvalidCredentialsException() {
        InvalidCredentialsException e = new InvalidCredentialsException("error");
        ApiResponse apiResponse = testHandler.handleInvalidCredentialsException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    void handleCriticalServerException() {
        CriticalServerException e = new CriticalServerException("error");
        ApiResponse apiResponse = testHandler.handleCriticalServerException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void handleFileAlreadyExistsException() {
        FileAlreadyExistsException e = new FileAlreadyExistsException("error");
        ApiResponse apiResponse = testHandler.handleFileAlreadyExistsException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.CONFLICT.value());
    }

    @Test
    void handleMissingServletRequestPartException() {
        MissingServletRequestPartException e = new MissingServletRequestPartException("test_parameter");
        ApiResponse apiResponse = testHandler.handleMissingServletRequestPartException(e);
        assertEquals(apiResponse.getResponseMessage(), "Required part 'test_parameter' is not present.");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleMissingServletRequestParameterException() {
        MissingServletRequestParameterException e = new MissingServletRequestParameterException(
                "test_parameter",
                "String"
        );
        ApiResponse apiResponse = testHandler.handleMissingServletRequestParameterException(e);
        assertEquals(apiResponse.getResponseMessage(), "Required request parameter 'test_parameter' for method "
                + "parameter type String is not present");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleHttpMessageNotReadableException() {
        ApiResponse apiResponse = testHandler.handleHttpMessageNotReadableException();
        assertTrue(apiResponse.getResponseMessage().contains("Required request body is missing or incorrect"));
        assertEquals(apiResponse.getStatusCode(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleNumberFormatException() {
        NumberFormatException e = new NumberFormatException("error");
        ApiResponse apiResponse = testHandler.handleNumberFormatException(e);
        assertEquals(apiResponse.getResponseMessage(), "error");
        assertEquals(apiResponse.getStatusCode(), HttpStatus.BAD_REQUEST.value());
    }
}
