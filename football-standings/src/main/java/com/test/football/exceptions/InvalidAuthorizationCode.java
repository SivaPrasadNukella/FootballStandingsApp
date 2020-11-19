package com.test.football.exceptions;

public class InvalidAuthorizationCode extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAuthorizationCode(String message) {
		super(message);
	}
}
