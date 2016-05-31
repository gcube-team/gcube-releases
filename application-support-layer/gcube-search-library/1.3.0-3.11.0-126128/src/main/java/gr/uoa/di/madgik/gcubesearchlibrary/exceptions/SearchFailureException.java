package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class SearchFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public SearchFailureException() {}

	public SearchFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SearchFailureException(String message) {
		super(message);
	}

}
