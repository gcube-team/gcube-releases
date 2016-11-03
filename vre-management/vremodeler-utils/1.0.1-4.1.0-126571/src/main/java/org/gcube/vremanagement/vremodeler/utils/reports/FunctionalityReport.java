package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;

public class FunctionalityReport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1104477953274508866L;

	private Status status;
	
	private int functionalityId;
	private String functionalityName;
	
	public FunctionalityReport(){
		this.status=Status.Waiting;
	}
	
	public Status getState() {
		return status;
	}



	public void setStatus(Status status) {
		this.status = status;
	}



	public int getFunctionalityId() {
		return functionalityId;
	}



	public void setFunctionalityId(int functionalityId) {
		this.functionalityId = functionalityId;
	}



	public String getFunctionalityName() {
		return functionalityName;
	}



	public void setFunctionalityName(String functioanlityName) {
		this.functionalityName = functioanlityName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + functionalityId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionalityReport other = (FunctionalityReport) obj;
		if (functionalityId != other.functionalityId)
			return false;
		return true;
	}
}