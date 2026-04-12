package com.bookshop.books.exception;

/**
 * Exception thrown when a book deletion violates business rules.
 *
 * <p>
 * This exception is used when deletion is not allowed due to
 * domain constraints such as age-based restrictions.
 * When handled at the REST layer, it results in an HTTP 400
 * (Bad Request) response.
 * </p>
 */
public class BookDeletionNotAllowedException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TOO_NEW =
            "Book can only be deleted after it is at least 7 days old.";

    public static final String TOO_OLD =
            "Book older than 1 year cannot be deleted.";

    public BookDeletionNotAllowedException(String message) {
        super(message);
    }
}
