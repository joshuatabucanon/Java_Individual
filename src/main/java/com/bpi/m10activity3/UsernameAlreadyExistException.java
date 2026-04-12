package com.bpi.m10activity3;

public class UsernameAlreadyExistException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsernameAlreadyExistException(String message) {
        super(message);
    }
}