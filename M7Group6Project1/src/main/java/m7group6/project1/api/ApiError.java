package m7group6.project1.api;

public final class ApiError {
    private final int status;
    private final String code;
    private final String message;
    private final String path;

    private ApiError(int status, String code, String message, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public static ApiError of(int status, String code, String message, String path) {
        return new ApiError(status, code, message, path);
    }

    public int getStatus() { 
    	return status; 
    }
    public String getCode() { 
    	return code; 
    }
    public String getMessage() { 
    	return message; 
    }
    public String getPath() {
    	return path; 
    }
}