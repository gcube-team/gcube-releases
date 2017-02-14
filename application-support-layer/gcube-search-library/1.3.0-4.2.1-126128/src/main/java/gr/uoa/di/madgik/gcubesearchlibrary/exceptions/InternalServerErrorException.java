package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class InternalServerErrorException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public InternalServerErrorException() {}

	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	

	public InternalServerErrorException(String message) {
		super(message);
	}

}
