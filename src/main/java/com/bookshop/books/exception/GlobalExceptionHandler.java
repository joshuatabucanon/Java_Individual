package com.bookshop.books.exception;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bookshop.books.dto.response.ValidationErrorResponseDto;

import jakarta.validation.ConstraintViolationException;


/**
 * Global exception handler for REST controllers.
 *
 * <p>
 * Translates domain, validation, and system exceptions
 * into appropriate HTTP responses.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles cases where a requested resource is not found.
     *
     * @param ex thrown exception
     * @return HTTP 404 response
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(BookNotFoundException ex) {
        log.warn("Book not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles duplicate resource creation attempts.
     *
     * @param ex thrown exception
     * @return HTTP 409 response
     */
    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateIsbnException ex) {
        log.warn("Duplicate ISBN error: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles violations of book deletion business rules.
     *
     * @param ex thrown exception
     * @return HTTP 400 response
     */
    @ExceptionHandler(BookDeletionNotAllowedException.class)
    public ResponseEntity<ApiError> handleBookDeletionRule(
            BookDeletionNotAllowedException ex) {
        log.warn("Book deletion rule violated: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles constraint violations from request parameters.
     *
     * @param ex thrown exception
     * @return HTTP 400 response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex) {

        log.warn("Constraint violation: {}", ex.getMessage());

        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                messages
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles request body validation errors.
     *
     * @param ex thrown exception
     * @return validation error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponseDto handleValidationErrors(
            MethodArgumentNotValidException ex) {

        log.warn("Request body validation failed");

        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ValidationErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                messages,
                OffsetDateTime.now()
        );
    }

    /**
     * Handles uncaught exceptions.
     *
     * @param ex thrown exception
     * @return HTTP 500 response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                List.of(message)
        );
        return new ResponseEntity<>(error, status);
    }
}