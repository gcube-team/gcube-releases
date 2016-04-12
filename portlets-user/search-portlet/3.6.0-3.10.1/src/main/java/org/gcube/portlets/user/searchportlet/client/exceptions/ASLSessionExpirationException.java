package org.gcube.portlets.user.searchportlet.client.exceptions;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * An exception that is thrown when the ASL exception has expired
 * 
 * @author Panagiota Koltsida
 *
 */
public class ASLSessionExpirationException extends Exception implements Serializable {

	private static final long serialVersionUID = 9018581526710473992L;

	@SuppressWarnings("unused")
	private ASLSessionExpirationException() {}
	
	public ASLSessionExpirationException(String message) {
		super(message);
	}
}
