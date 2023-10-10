package com.project.ipms.controller;

import lombok.Data;
import java.time.ZonedDateTime;

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

    /**
     * Zoned datetime.
     */
    private ZonedDateTime zonedDateTime;
}
