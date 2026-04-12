package m4_group6project1.exception;

public class InvalidBookIdException extends Exception {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidBookIdException(String message) {
        super(message);
    }
    
}
