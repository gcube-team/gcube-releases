package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class XMLParsingException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public XMLParsingException() {}

	public XMLParsingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public XMLParsingException(String message) {
		super(message);
	}

}
