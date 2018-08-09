package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.MapAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResumeOperationRequest {

	@XmlElement
	private String identifier; 
	
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, Object> currentOperationParameter;

	
	@SuppressWarnings("unused")
	private ResumeOperationRequest(){}
	
	public ResumeOperationRequest(String identifier,
			Map<String, Object> currentOperationParameter) {
		super();
		this.identifier = identifier;
		this.currentOperationParameter = currentOperationParameter;
	}

	public ResumeOperationRequest(String identifier) {
		this(identifier, null);
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public Map<String, Object> getCurrentOperationParameter() {
		return currentOperationParameter;
	}
	
}
