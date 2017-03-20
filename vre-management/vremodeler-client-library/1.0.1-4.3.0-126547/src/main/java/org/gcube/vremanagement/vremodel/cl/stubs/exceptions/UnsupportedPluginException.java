package org.gcube.vremanagement.vremodel.cl.stubs.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name="UnsupportedPluginFault")
public class UnsupportedPluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedPluginException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UnsupportedPluginException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedPluginException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedPluginException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}


	
}
