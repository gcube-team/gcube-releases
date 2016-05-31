package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class ServletURLRetrievalException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public ServletURLRetrievalException() {}

	public ServletURLRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ServletURLRetrievalException(String message) {
		super(message);
	}

}
