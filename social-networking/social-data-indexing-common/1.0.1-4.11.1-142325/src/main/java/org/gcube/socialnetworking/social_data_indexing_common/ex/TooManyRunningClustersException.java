package org.gcube.socialnetworking.social_data_indexing_common.ex;

/**
 * Too many clusters in this scope exception.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class TooManyRunningClustersException extends Exception {

	private static final long serialVersionUID = -4112724774153676227L;

	private static final String DEFAULT_MESSAGE = "Too many ElasticSearch cluster instances for this scope!";

	public TooManyRunningClustersException(){
		super(DEFAULT_MESSAGE);
	}

	public TooManyRunningClustersException(String message) {
		super(message);
	}

}
