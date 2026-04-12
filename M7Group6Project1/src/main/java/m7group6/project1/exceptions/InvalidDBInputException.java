package m7group6.project1.exceptions;

/**
 * Thrown when values intended for DB INSERT/UPDATE violate
 * schema-level constraints (e.g., max length).
 *
 * Unchecked to align with the project's DataAccessException usage.
 */
public class InvalidDBInputException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidDBInputException() {
        super();
    }

    public InvalidDBInputException(String message) {
        super(message);
    }

    public InvalidDBInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDBInputException(Throwable cause) {
        super(cause);
    }
}

