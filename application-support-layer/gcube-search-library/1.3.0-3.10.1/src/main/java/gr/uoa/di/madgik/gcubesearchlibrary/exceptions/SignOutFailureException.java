package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class SignOutFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public SignOutFailureException() {}

	public SignOutFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SignOutFailureException(String message) {
		super(message);
	}

}
