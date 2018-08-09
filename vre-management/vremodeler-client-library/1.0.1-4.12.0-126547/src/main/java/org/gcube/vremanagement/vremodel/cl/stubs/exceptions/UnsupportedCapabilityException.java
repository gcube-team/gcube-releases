package org.gcube.vremanagement.vremodel.cl.stubs.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name="UnsupportedCapabilityFault")
public class UnsupportedCapabilityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedCapabilityException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UnsupportedCapabilityException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedCapabilityException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedCapabilityException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
	
}
