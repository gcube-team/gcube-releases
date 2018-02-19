package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;

@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)

public class SMComputationConfig {
	@XmlElement(namespace = TYPES_NAMESPACE)
	private SMEntries parameters;
	
	@XmlElement()
	private String algorithm;
	
	 public SMComputationConfig() {
	    }

	    public SMComputationConfig(
	           String algorithm,
	           SMEntries parameters) {
	           this.parameters = parameters;
	           this.algorithm = algorithm;
	    }
	
	
	

	public SMEntries parameters() {
		return parameters;
	}

	
	public void parameters(SMEntries parameters) {
		this.parameters = parameters;
	}
	
	
	
	
	public String algorithm() {
		return algorithm;
	}

	
	public void algorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	
	
}
