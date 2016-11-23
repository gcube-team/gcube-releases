package org.gcube.portlets.user.statisticalmanager.client.bean;

import java.io.Serializable;
import java.util.Date;

import org.gcube.portlets.user.statisticalmanager.client.bean.output.Resource;


public class ComputationStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1943128398882978439L;
	
	public enum Status{
		PENDING,
		RUNNING,
		COMPLETE,
		FAILED
	};

	
	private double percentage;
	private Status status;
	private Date endDate;
	private String message;
	private Resource errResource;
	
	
	public ComputationStatus() {
		this.status = Status.PENDING;
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

	public double getPercentage() {
		return percentage;
	}
	
	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
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
	
	public boolean isPending() {
		return this.status == Status.PENDING;
	}
	
	public boolean isTerminated() {
		return status==Status.COMPLETE || status==Status.FAILED;
	}
	
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	public Resource getErrResource()
	{
		return errResource;
	}
	
	public void setErrResource(Resource errRes)
	{
		this.errResource= errRes;
	}
}
