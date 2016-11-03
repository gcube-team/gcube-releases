package org.gcube.resourcemanagement.whnmanager.api.exception;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GCUBEUnrecoverableExceptionInfo {
	
	@XmlElement
	 public String details;

	protected GCUBEUnrecoverableExceptionInfo(){}
	
	public GCUBEUnrecoverableExceptionInfo(String details) {
		super();
		this.details = details;
	}

	public String getDetails() {
		return details;
	}
	
	 
}
