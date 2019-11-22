package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;

public class GHNonCloudReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1418965279876312220L;

	private Status state;
	
	private Status[] deployingState;

	public GHNonCloudReport() {
		this.state= Status.Waiting;
	}
	
	
	public Status getStatus() {
		return state;
	}

	public void setStatus(Status status) {
		this.state = status;
	}

	public Status[] getDeployingState() {
		return deployingState;
	}

	public void setDeployingState(Status[] deployingState) {
		this.deployingState = deployingState;
	} 
}
