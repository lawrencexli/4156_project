package com.project.ipms.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ApiResponseTest {

    /**
     * Test API response object.
     */
    private ApiResponse testResponse;

    @BeforeEach
    void setUp() {
        testResponse = new ApiResponse();
        testResponse.setResponseMessage("Test message");
        testResponse.setStatusCode(HttpStatus.OK.value());
    }

    @Test
    void getResponseMessage() {
        assertEquals(testResponse.getResponseMessage(), "Test message");
    }

    @Test
    void getStatusCode() {
        assertEquals(testResponse.getStatusCode(), HttpStatus.OK.value());
    }

    @Test
    void testEquals() {
        ApiResponse dummyResponse = new ApiResponse();
        dummyResponse.setResponseMessage("Test message");
        dummyResponse.setStatusCode(HttpStatus.OK.value());
        assertEquals(testResponse, dummyResponse);

        dummyResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        assertNotEquals(testResponse, dummyResponse);

        dummyResponse.setResponseMessage("Test 2");
        assertNotEquals(testResponse, dummyResponse);

        dummyResponse.setStatusCode(HttpStatus.OK.value());
        assertNotEquals(testResponse, dummyResponse);
    }

    @Test
    void testHashCode() {
        assertDoesNotThrow(() -> testResponse.hashCode());
    }

    @Test
    void testToString() {
        String result = testResponse.toString();
        assertEquals(result, "ApiResponse(responseMessage=Test message, statusCode=200)");
    }
}
