package org.gcube.data.spd.stubs.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name="InputNotValidFault")
public class InvalidInputException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
	
}
