package com.project.ipms.exception;

import com.project.ipms.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;

@ControllerAdvice
public final class ApiExceptionHandler {

    /**
     * Handle InvalidFileTypeException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public ApiResponse handleInvalidFileTypeException(final InvalidFileTypeException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        return response;
    }

    /**
     * Handle BadRequestException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleBadRequestException(final BadRequestException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return response;
    }

    /**
     * Handle BadRequestException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleMultipartException(final MultipartException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return response;
    }

    /**
     * Handle FileNotFoundException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleFileNotFoundException(final FileNotFoundException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        return response;
    }

    /**
     * Handle IOException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleIOException(final IOException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return response;
    }

    /**
     * Handle InvalidCredentialsException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ApiResponse handleInvalidCredentialsException(final InvalidCredentialsException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.FORBIDDEN.value());
        return response;
    }

    /**
     * Handle CriticalServerException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(CriticalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleCriticalServerException(final CriticalServerException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return response;
    }

    /**
     * Handle FileAlreadyExistsException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiResponse handleFileAlreadyExistsException(final FileAlreadyExistsException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.CONFLICT.value());
        return response;
    }

    /**
     * Handle MissingServletRequestPartException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleMissingServletRequestPartException(final MissingServletRequestPartException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return response;
    }

    /**
     * Handle MissingServletRequestParameterException.
     * @param e error message
     * @return exception json output
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return response;
    }
}
