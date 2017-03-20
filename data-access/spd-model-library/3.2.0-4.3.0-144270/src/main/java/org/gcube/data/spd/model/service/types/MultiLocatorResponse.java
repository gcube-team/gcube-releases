package org.gcube.data.spd.model.service.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MultiLocatorResponse {

	private String inputLocator;
	private String outputLocator;
	private String endpointId;
	
	protected MultiLocatorResponse(){}
	
	public MultiLocatorResponse(String inputLocator, String outputLocator, String endpointId) {
		super();
		this.inputLocator = inputLocator;
		this.outputLocator = outputLocator;
		this.endpointId = endpointId;
	}
	public String getInputLocator() {
		return inputLocator;
	}
	public String getOutputLocator() {
		return outputLocator;
	}

	public String getEndpointId() {
		return endpointId;
	}

	@Override
	public String toString() {
		return "MultiLocatorResponse [inputLocator=" + inputLocator
				+ ", outputLocator=" + outputLocator + ", endpointId="
				+ endpointId + "]";
	}
	
	
}
