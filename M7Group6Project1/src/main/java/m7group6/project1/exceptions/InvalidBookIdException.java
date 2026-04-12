package m7group6.project1.exceptions;

public class InvalidBookIdException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidBookIdException(String message) {
        super(message);
    }
    
}
