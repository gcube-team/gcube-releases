package org.gcube.datacatalogue.ckanutillibrary.server.exceptions;

/**
 * Exception thrown when it is not possible retrieve information from the ServiceEndpoint
 * related to the Data Catalogue
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ServiceEndPointException extends Exception {
	
	private static final long serialVersionUID = 7057074369001221035L;
	private static final String DEFAULT_MESSAGE = "Unable to retrieve information from Data Catalogue endpoint!";

	public ServiceEndPointException(){
		super(DEFAULT_MESSAGE);
	}
	public ServiceEndPointException(String string) {
		super(string);
	}
}
