package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Enum Status.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 16, 2018
 */
public enum Status implements Serializable{
	INITIALIZING("INITIALIZING"), // initial status
	ONGOING("ONGOING"), // synch in progress 
	WARNINGS("WARNINGS"),   // errors occurred
	STOPPED("STOPPED"),  // STOP received, waiting for request to finish
	COMPLETED("COMPLETED");
	
	/**
	 * Instantiates a new status.
	 */
	Status(){}
	

    private String label;
	
	/**
	 * Instantiates a new status.
	 *
	 * @param label the label
	 */
	Status(String label){
		this.label = label;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
}