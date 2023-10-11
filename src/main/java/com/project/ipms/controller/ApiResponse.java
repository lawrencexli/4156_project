package com.project.ipms.controller;

import lombok.Data;

@Data
public class ApiResponse {
    /**
     * Response message.
     */
    private String responseMessage;

    /**
     * Status code.
     */
    private Integer statusCode;
}
