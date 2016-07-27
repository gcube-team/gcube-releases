package org.gcube.data.spd.stubs.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name="QueryNotValidFault")
public class QueryNotValidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryNotValidException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QueryNotValidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public QueryNotValidException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public QueryNotValidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}



	
}
