package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.shared.tr.RefColumn;

/**
 * 
 * @author Giancarlo Panichi
 * 
 */
public class InvocationS implements Serializable {

	private static final long serialVersionUID = -7112348181058962879L;
	
	protected Integer jobNumber;//Internal job number from 1 to n
	protected String columnId;
	protected long operationId;
	protected Long identifier;
	protected HashMap<String, Object> parameters;
	protected String taskId;
	protected RefColumn refColumn;
	

	public InvocationS() {

	}

	public InvocationS(Integer jobNumber, String columnId, long operationId, Long identifier,
			HashMap<String, Object> parameters, String taskId, RefColumn refColumn) {
		this.jobNumber=jobNumber;
		this.columnId = columnId;
		this.operationId = operationId;
		this.identifier = identifier;
		this.parameters = parameters;
		this.taskId=taskId;
		this.refColumn=refColumn;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public long getOperationId() {
		return operationId;
	}

	public void setOperationId(long operationId) {
		this.operationId = operationId;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public RefColumn getRefColumn() {
		return refColumn;
	}

	public void setRefColumn(RefColumn refColumn) {
		this.refColumn = refColumn;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Integer getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(Integer jobNumber) {
		this.jobNumber = jobNumber;
	}

	@Override
	public String toString() {
		return "InvocationS [jobNumber=" + jobNumber + ", columnId=" + columnId
				+ ", operationId=" + operationId + ", identifier=" + identifier
				+ ", parameters=" + parameters + ", taskId=" + taskId
				+ ", refColumn=" + refColumn + "]";
	}

	

	
	

}
