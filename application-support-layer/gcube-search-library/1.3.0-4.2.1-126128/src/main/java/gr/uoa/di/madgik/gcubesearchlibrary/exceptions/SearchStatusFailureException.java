package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class SearchStatusFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public SearchStatusFailureException() {}

	public SearchStatusFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SearchStatusFailureException(String message) {
		super(message);
	}

}
