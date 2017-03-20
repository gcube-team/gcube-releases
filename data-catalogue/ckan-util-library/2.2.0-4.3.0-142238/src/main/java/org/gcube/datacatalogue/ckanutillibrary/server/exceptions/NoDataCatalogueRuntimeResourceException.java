package org.gcube.datacatalogue.ckanutillibrary.server.exceptions;

/**
 * No Data Catalogue node found.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class NoDataCatalogueRuntimeResourceException extends Exception {
	
	private static final long serialVersionUID = -40748130477807648L;
	
	private static final String DEFAULT_MESSAGE = "No Data Catalogue instance for this scope!";

	public NoDataCatalogueRuntimeResourceException(){
		super(DEFAULT_MESSAGE);
	}

	public NoDataCatalogueRuntimeResourceException(String message) {
		super(message);
	}
}
