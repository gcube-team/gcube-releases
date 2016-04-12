package org.gcube.portlets.user.searchportlet.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NoCollectionsAvailableException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8190335116062686336L;

	public NoCollectionsAvailableException() {
		super("No Collections available of that type");
	}
	
	public NoCollectionsAvailableException(String message) {
		super(message);
	}
}
