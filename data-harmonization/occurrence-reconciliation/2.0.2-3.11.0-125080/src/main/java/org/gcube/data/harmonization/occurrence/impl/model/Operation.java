package org.gcube.data.harmonization.occurrence.impl.model;

import java.util.Calendar;

import org.gcube.data.harmonization.occurrence.impl.model.types.OperationType;
import org.gcube.data.harmonization.occurrence.impl.model.types.Status;

public class Operation {
	private Long operationId;
	private Calendar submissionDate;
	private Calendar completionDate;
	private Status status;
	private String operationDescription;
	private OperationType operationType;

	public Operation() {
	}

	public Operation(Long operationId, Calendar submissionDate,
			Calendar completionDate, Status status,
			String operationDescription, OperationType operationType) {
		super();
		this.operationId = operationId;
		this.submissionDate = submissionDate;
		this.completionDate = completionDate;
		this.status = status;
		this.operationDescription = operationDescription;
		this.operationType = operationType;
	}

	/**
	 * @return the operationId
	 */
	public Long getOperationId() {
		return operationId;
	}

	/**
	 * @param operationId the operationId to set
	 */
	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}

	/**
	 * @return the submissionDate
	 */
	public Calendar getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * @param submissionDate the submissionDate to set
	 */
	public void setSubmissionDate(Calendar submissionDate) {
		this.submissionDate = submissionDate;
	}

	/**
	 * @return the completionDate
	 */
	public Calendar getCompletionDate() {
		return completionDate;
	}

	/**
	 * @param completionDate the completionDate to set
	 */
	public void setCompletionDate(Calendar completionDate) {
		this.completionDate = completionDate;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the operationDescription
	 */
	public String getOperationDescription() {
		return operationDescription;
	}

	/**
	 * @param operationDescription the operationDescription to set
	 */
	public void setOperationDescription(String operationDescription) {
		this.operationDescription = operationDescription;
	}

	/**
	 * @return the operationType
	 */
	public OperationType getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Operation [operationId=");
		builder.append(operationId);
		builder.append(", submissionDate=");
		builder.append(submissionDate);
		builder.append(", completionDate=");
		builder.append(completionDate);
		builder.append(", status=");
		builder.append(status);
		builder.append(", operationDescription=");
		builder.append(operationDescription);
		builder.append(", operationType=");
		builder.append(operationType);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}