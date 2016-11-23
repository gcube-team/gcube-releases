package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BatchExecuteRequest {

	private BatchOption option;
	private List<OperationExecution> invocations;
	private long targetTabularResourceId;
	
	
	@SuppressWarnings("unused")
	private BatchExecuteRequest(){}
			
	public BatchExecuteRequest(long targetTabularResourceId, List<OperationExecution>  invocations, BatchOption option){
		this.invocations = invocations;
		this.option = option;
		this.targetTabularResourceId = targetTabularResourceId;
	}

	public BatchOption getOption() {
		return option;
	}

	public List<OperationExecution> getInvocations() {
		return invocations;
	}

	public long getTargetTabularResourceId() {
		return targetTabularResourceId;
	}
	
	
	
}
