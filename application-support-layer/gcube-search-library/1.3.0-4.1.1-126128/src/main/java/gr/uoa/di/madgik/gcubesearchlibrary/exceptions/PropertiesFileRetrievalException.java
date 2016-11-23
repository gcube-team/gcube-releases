package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class PropertiesFileRetrievalException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public PropertiesFileRetrievalException() {}

	public PropertiesFileRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PropertiesFileRetrievalException(String message) {
		super(message);
	}

}
