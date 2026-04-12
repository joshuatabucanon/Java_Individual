package m8jt.project1.exception;

import java.time.Instant;

public class ApiError {

    private final int status;
    private final String error;
    private final Instant timestamp;

    public ApiError(int status, String error) {
        this.status = status;
        this.error = error;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}