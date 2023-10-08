package com.project.ipms.exception;

import com.project.ipms.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import java.time.ZonedDateTime;

@ControllerAdvice
public final class ApiExceptionHandler {

    /**
     * Handle InvalidFileTypeException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseBody
    public ApiResponse handleInvalidFileTypeException(final InvalidFileTypeException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.toString());
        response.setStatusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        response.setZonedDateTime(ZonedDateTime.now());
        return response;
    }

    /**
     * Handle BadRequestException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ApiResponse handleBadRequestException(final BadRequestException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.toString());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setZonedDateTime(ZonedDateTime.now());
        return response;
    }

    /**
     * Handle BadRequestException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ApiResponse handleMultipartException(final MultipartException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.toString());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setZonedDateTime(ZonedDateTime.now());
        return response;
    }
}
