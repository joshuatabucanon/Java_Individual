package com.bookshop.books.exception;

/**
 * Exception thrown when a duplicate ISBN is detected.
 *
 * <p>
 * Indicates a violation of the ISBN uniqueness constraint.
 * When handled by the REST layer, this exception results
 * in an HTTP 409 (Conflict) response.
 * </p>
 */
public class DuplicateIsbnException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateIsbnException(String isbn) {
        super("A book with ISBN already exists: " + isbn);
    }
}