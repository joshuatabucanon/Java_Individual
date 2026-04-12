package com.bookshop.books.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiError {

    private final OffsetDateTime timestamp = OffsetDateTime.now();
    private int status;
    private String error;
    private List<String> messages;

    public ApiError(int status, String error, List<String> messages) {
        this.status = status;
        this.error = error;
        this.messages = messages;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
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
}