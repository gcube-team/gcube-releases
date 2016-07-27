package org.gcube.portlets.user.dataminermanager.client.bean;

import java.io.Serializable;
import java.util.Date;

import org.gcube.portlets.user.dataminermanager.shared.exception.DataMinerServiceException;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationStatus implements Serializable {

	private static final long serialVersionUID = -1943128398882978439L;

	public enum Status {
		ACCEPTED, RUNNING, COMPLETE, FAILED, CANCELLED;
	};

	private double percentage;
	private Status status;
	private Date endDate;
	private String message;
	private DataMinerServiceException error;

	public ComputationStatus() {
		this.status = Status.ACCEPTED;
	}

	public ComputationStatus(double percentage) {
		super();
		this.percentage = percentage;
		this.status = Status.RUNNING;
	}

	public ComputationStatus(Status status, double percentage) {
		super();
		this.percentage = percentage;
		this.status = status;
	}

	public ComputationStatus(DataMinerServiceException error) {
		super();
		this.percentage = 100f;
		this.status = Status.FAILED;
		this.error = error;
	}

	public boolean isComplete() {
		return this.status == Status.COMPLETE;
	}

	public boolean isFailed() {
		return this.status == Status.FAILED;
	}

	public boolean isRunning() {
		return this.status == Status.RUNNING;
	}

	public boolean isAccepted() {
		return this.status == Status.ACCEPTED;
	}

	public boolean isCancelled() {
		return this.status == Status.CANCELLED;
	}

	public boolean isPaused() {
		return false;
		// return this.status == Status.FAILED;
	}

	public boolean isTerminated() {
		return status == Status.COMPLETE || status == Status.FAILED
				|| status == Status.CANCELLED;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public DataMinerServiceException getError() {
		return error;
	}

	public void setError(DataMinerServiceException error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "ComputationStatus [percentage=" + percentage + ", status="
				+ status + ", endDate=" + endDate + ", message=" + message
				+ ", error=" + error + "]";
	}

}
