package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class ContentInfoFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public ContentInfoFailureException() {}

	public ContentInfoFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ContentInfoFailureException(String message) {
		super(message);
	}

}
