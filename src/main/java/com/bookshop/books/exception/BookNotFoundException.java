package com.bookshop.books.exception;

import java.util.UUID;

/**
 * Exception thrown when a book cannot be found.
 *
 * <p>
 * Indicates that no book exists with the specified identifier.
 * When propagated to the REST layer, this exception is translated
 * into an HTTP 404 (Not Found) response.
 * </p>
 */
public class BookNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BookNotFoundException(UUID id) {
        super("Book not found with id: " + id);
    }
}