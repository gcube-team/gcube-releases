package org.gcube.portlets.user.searchportlet.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionRetrievalException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8190335116062686336L;

	public CollectionRetrievalException() {
		super("Failed to retrieve the available collections. An internal error occurred");
	}
	
	public CollectionRetrievalException(String message) {
		super(message);
	}
}
