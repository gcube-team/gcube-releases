package org.gcube.vremanagement.vremodel.cl.stubs.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name="JobNotValidFault")
public class InvalidJobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidJobException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidJobException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidJobException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidJobException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
