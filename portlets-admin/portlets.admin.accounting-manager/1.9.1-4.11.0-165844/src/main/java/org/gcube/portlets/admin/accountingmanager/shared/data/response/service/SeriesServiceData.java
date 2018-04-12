package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SeriesServiceData implements Serializable {

	private static final long serialVersionUID = -903768352060298831L;
	private Date date;
	private Long operationCount;
	private Long duration;
	private Long maxInvocationTime;
	private Long minInvocationTime;

	public SeriesServiceData() {
		super();
	}

	public SeriesServiceData(Date date, Long operationCount, Long duration,
			Long maxInvocationTime, Long minInvocationTime) {
		super();
		this.date = date;
		this.operationCount = operationCount;
		this.duration = duration;
		this.maxInvocationTime = maxInvocationTime;
		this.minInvocationTime = minInvocationTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getOperationCount() {
		return operationCount;
	}

	public void setOperationCount(Long operationCount) {
		this.operationCount = operationCount;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Long getMaxInvocationTime() {
		return maxInvocationTime;
	}

	public void setMaxInvocationTime(Long maxInvocationTime) {
		this.maxInvocationTime = maxInvocationTime;
	}

	public Long getMinInvocationTime() {
		return minInvocationTime;
	}

	public void setMinInvocationTime(Long minInvocationTime) {
		this.minInvocationTime = minInvocationTime;
	}

	@Override
	public String toString() {
		return "SeriesServiceData [date=" + date + ", operationCount="
				+ operationCount + ", duration=" + duration
				+ ", maxInvocationTime=" + maxInvocationTime
				+ ", minInvocationTime=" + minInvocationTime + "]";
	}

}
