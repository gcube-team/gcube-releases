package org.gcube.socialnetworking.social_data_indexing_common.ex;

/**
 * BulkInsertionFailedException class: such an error is thrown if the index process fails.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class BulkInsertionFailedException extends Exception {
	
	private static final long serialVersionUID = -7707293515649267047L;
	
	private static final String DEFAULT_MESSAGE = "Unable to insert data into the index";
	
	public BulkInsertionFailedException(){
		super(DEFAULT_MESSAGE);
	}

	public BulkInsertionFailedException(String message) {
		super(message);
	}

}
