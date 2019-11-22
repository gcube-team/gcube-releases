package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;


/**
 * The Enum Status.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 27, 2018
 */
public enum TaskStatus implements Serializable{
	INITIALIZING("INITIALIZING"),
	ONGOING("ONGOING"),
	ACCEPTED("ACCEPTED"),
	CANCELLED("CANCELLED"),
	FAILED("FAILED"),
	COMPLETED("COMPLETED");

	/**
	 * Instantiates a new status.
	 */
	TaskStatus(){}


    private String label;

	/**
	 * Instantiates a new status.
	 *
	 * @param label the label
	 */
	TaskStatus(String label){
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