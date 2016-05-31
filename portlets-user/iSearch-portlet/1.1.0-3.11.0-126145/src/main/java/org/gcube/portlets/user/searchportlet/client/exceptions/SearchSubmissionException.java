package org.gcube.portlets.user.searchportlet.client.exceptions;

import java.io.Serializable;

/**
 * Exception for server side exceptions from search submission queries
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SearchSubmissionException extends Exception implements Serializable {

	private static final long serialVersionUID = 9018581526710473992L;

	public SearchSubmissionException() {}
	
	public SearchSubmissionException(String message) {
		super(message);
	}
}
