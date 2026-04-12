package com.bookshop.books.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response DTO representing validation errors.
 * 
 * <p>
 * Returned when request validation fails due to
 * constraint violations or invalid input data.
 * </p>
 */
public class ValidationErrorResponseDto {

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Short error description.
     */
    private String error;

    /**
     * List of validation error messages.
     */
    private List<String> messages;

    /**
     * Timestamp when the error occurred.
     */
    private OffsetDateTime timestamp;

    /**
     * Constructs a validation error response.
     *
     * @param status HTTP status code
     * @param error short error description
     * @param messages validation error details
     * @param timestamp time when the error occurred
     */
    public ValidationErrorResponseDto(
            int status,
            String error,
            List<String> messages,
            OffsetDateTime timestamp) {

        this.status = status;
        this.error = error;
        this.messages = messages;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getMessages() {
        return messages;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

}
