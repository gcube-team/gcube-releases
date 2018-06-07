package org.gcube.data.spd.model.service.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubmitJobResponse {

	private String inputLocator;
	private String jobId;
	private String endpointId;
	
	protected SubmitJobResponse(){}
	
	public SubmitJobResponse(String inputLocator, String jobId, String endpointId) {
		super();
		this.inputLocator = inputLocator;
		this.jobId = jobId;
		this.endpointId = endpointId;
	}
	public String getInputLocator() {
		return inputLocator;
	}
	public String getJobId() {
		return jobId;
	}

	public String getEndpointId() {
		return endpointId;
	}
	
}
