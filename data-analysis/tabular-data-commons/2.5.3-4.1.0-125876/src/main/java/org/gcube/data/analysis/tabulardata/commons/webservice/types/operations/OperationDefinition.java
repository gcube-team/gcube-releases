package org.gcube.data.analysis.tabulardata.commons.webservice.types.operations;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;

public class OperationDefinition {

	@XmlElement	
	private long operationId;
	
	@XmlElement	
	private String name;
	
	@XmlElement	
	private String description;
	
	@XmlElement	
	private List<Parameter> parameters;
	
	
	public OperationDefinition(long operationId, String name,
			String description, List<Parameter> parameters) {
		super();
		this.operationId = operationId;
		this.name = name;
		this.description = description;
		this.parameters = parameters;
	}


	protected OperationDefinition() {}


	/**
	 * @return the operationId
	 */
	public long getOperationId() {
		return operationId;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OperationDefinition [operationId=" + operationId + ", name="
				+ name + ", description=" + description + ", parameters="
				+ parameters + "]";
	}
	
	
}
