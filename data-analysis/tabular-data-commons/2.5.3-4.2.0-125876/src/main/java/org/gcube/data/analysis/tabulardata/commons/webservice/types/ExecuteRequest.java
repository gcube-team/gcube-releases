package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecuteRequest {

	//TODO: add onErrorBehaviour for batch execution
	
	@XmlElement
	OperationExecution invocation;
	
	@XmlElement
	long targetTabularResourceId;

		
	protected ExecuteRequest() {
		super();
	}

	public ExecuteRequest(long targetTabularResourceId, OperationExecution invocation) {
		super();
		if (invocation==null) throw new IllegalArgumentException("empty invocations not accepted");
		this.invocation = invocation;
		this.targetTabularResourceId = targetTabularResourceId;
	}

	/**
	 * @return the invocations
	 */
	public OperationExecution getInvocation() {
		return invocation;
	}

	
	/**
	 * @return the targetTabularResourceId
	 */
	public long getTargetTabularResourceId() {
		return targetTabularResourceId;
	}

	
	
	
}
