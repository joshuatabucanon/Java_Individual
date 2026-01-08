package java_M4_Activity5;

public class InvalidAccountFormatException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAccountFormatException(String message) {
        super(message);
    }

    public InvalidAccountFormatException() {
    	
    }

}
