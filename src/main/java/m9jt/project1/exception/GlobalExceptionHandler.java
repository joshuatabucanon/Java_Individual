package m9jt.project1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       Domain Exceptions
       ========================= */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }

    /* =========================
       Validation Errors (400)
       ========================= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        message
                ));
    }

    /* =========================
       Spring Security Errors
       ========================= */

    /**
     * 401 Unauthorized
     * Thrown when user is not authenticated (missing / invalid credentials)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleUnauthorized(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized"
                ));
    }

    /**
     * 403 Forbidden
     * Thrown when user is authenticated but lacks required role
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiError(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden"
                ));
    }

    /* =========================
       Fallback (Safety Net)
       ========================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
 //       ex.printStackTrace(); // TEMPORARY: useful during development

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage() == null
                                ? "Unexpected server error"
                                : ex.getMessage()
                ));
    }
}