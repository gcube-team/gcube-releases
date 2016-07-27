package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class NotFoundException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public NotFoundException() {}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public NotFoundException(String message) {
		super(message);
	}

}
