package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class SetVREFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public SetVREFailureException() {}

	public SetVREFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SetVREFailureException(String message) {
		super(message);
	}

}
