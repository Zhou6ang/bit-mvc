package com.github.zhou6ang.mvc.exception;

public class BitMvcException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4004028771032104926L;

	public BitMvcException(String message) {
		super(message);
	}
	
	public BitMvcException(String message, Throwable cause) {
		super(message,cause);
	}
}
