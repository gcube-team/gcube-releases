package org.gcube.datacatalogue.grsf_manage_widget.shared.ex;

import org.gcube.datacatalogue.common.enums.Status;


/**
 * The Class GRSFRecordAlreadyManagedStatusException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Mar 21, 2019
 */
public class GRSFRecordAlreadyManagedStatusException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2034802685606929315L;
	private Status status;
	
	/**
	 * Instantiates a new GRSF record already managed status exception.
	 *
	 * @param theStatus the the status
	 * @param arg0 the arg 0
	 */
	public GRSFRecordAlreadyManagedStatusException(Status theStatus, String arg0){
		super(arg0);
		this.status = theStatus;
		
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

}
