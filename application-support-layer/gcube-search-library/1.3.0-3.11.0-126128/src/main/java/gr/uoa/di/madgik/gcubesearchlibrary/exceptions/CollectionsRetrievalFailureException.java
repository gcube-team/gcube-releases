package gr.uoa.di.madgik.gcubesearchlibrary.exceptions;

import java.io.Serializable;


public class CollectionsRetrievalFailureException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1175451378335167042L;

	public CollectionsRetrievalFailureException() {}

	public CollectionsRetrievalFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CollectionsRetrievalFailureException(String message) {
		super(message);
	}

}
