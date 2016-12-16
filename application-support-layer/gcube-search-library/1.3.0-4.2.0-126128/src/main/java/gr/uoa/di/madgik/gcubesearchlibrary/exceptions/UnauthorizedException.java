package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class UnauthorizedException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public UnauthorizedException() {}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UnauthorizedException(String message) {
		super(message);
	}

}
