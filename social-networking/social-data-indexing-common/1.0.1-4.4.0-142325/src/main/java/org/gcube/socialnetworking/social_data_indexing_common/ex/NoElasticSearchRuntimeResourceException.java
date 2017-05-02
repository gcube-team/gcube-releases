package org.gcube.socialnetworking.social_data_indexing_common.ex;

/**
 * No elasticsearch cluster in the infrastructure found exception.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class NoElasticSearchRuntimeResourceException extends Exception {
	
	private static final long serialVersionUID = -40748130477807648L;
	
	private static final String DEFAULT_MESSAGE = "No ElasticSearch cluster instance for this scope!";

	public NoElasticSearchRuntimeResourceException(){
		super(DEFAULT_MESSAGE);
	}

	public NoElasticSearchRuntimeResourceException(String message) {
		super(message);
	}


}
