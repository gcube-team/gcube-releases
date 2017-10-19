package org.gcube.socialnetworking.social_data_indexing_common.ex;

/**
 * Exception thrown when it is not possible retrieve information from the ServiceEndpoint
 * related to ElasticSearch
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class ServiceEndPointException extends Exception {

	private static final long serialVersionUID = 5378333924429281681L;
	
	private static final String DEFAULT_MESSAGE = "Unable to retrieve information from ElasticSearch endpoint!";

	public ServiceEndPointException(){
		super(DEFAULT_MESSAGE);
	}
	public ServiceEndPointException(String string) {
		super(string);
	}
}
