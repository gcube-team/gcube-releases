package org.gcube.portal.tou.exceptions;

/**
 * Exception thrown when ToU is not available for a group
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ToUNotFoundException extends Exception {

	private static final long serialVersionUID = 4008601593313440997L;
	
	private static final String DEFAULT_ERROR = "Terms of Use is not set for this group!";
	
	public ToUNotFoundException(){
		super(DEFAULT_ERROR);
	}

}
