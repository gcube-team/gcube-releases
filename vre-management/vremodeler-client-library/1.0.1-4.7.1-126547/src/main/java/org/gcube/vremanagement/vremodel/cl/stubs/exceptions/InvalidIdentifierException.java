package org.gcube.vremanagement.vremodel.cl.stubs.exceptions;

import javax.xml.ws.WebFault;


@WebFault(name="IdNotValidFault")
public class InvalidIdentifierException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidIdentifierException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidIdentifierException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidIdentifierException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidIdentifierException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
	
}
