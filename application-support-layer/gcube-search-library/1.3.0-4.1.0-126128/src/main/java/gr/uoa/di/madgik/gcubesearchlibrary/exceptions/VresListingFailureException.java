package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class VresListingFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public VresListingFailureException() {}

	public VresListingFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public VresListingFailureException(String message) {
		super(message);
	}

}
