package m9jt.project1.exception;

public abstract class DomainException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}