package java_M4_Activity5;

public class InvalidAccountNumberException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAccountNumberException(String message) {
        super(message);
    }
    
    public InvalidAccountNumberException() {
    	
    }
}

