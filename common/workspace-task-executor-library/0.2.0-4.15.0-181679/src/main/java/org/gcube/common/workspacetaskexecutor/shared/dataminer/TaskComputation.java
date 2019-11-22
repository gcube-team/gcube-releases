/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared.dataminer;

import java.io.Serializable;

import org.gcube.common.workspacetaskexecutor.shared.BaseTaskComputation;


/**
 * The Class TaskComputation.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 3, 2018
 */
public class TaskComputation implements BaseTaskComputation, Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = 7987759965326087786L;
	/** The id. */
	private String id;
	private String urlId;
	private String operatorId;
	private String operatorName;
	private String equivalentRequest;
	private Long startTime;
	private Long endTime;

	/**
	 * Instantiates a new DM computation id.
	 */
	public TaskComputation() {
	}


	/**
	 * @param id
	 * @param urlId
	 * @param operatorId
	 * @param operatorName
	 * @param equivalentRequest
	 * @param startTime
	 * @param endTime
	 */
	public TaskComputation(
		String id, String urlId, String operatorId, String operatorName,
		String equivalentRequest, Long startTime, Long endTime) {

		super();
		this.id = id;
		this.urlId = urlId;
		this.operatorId = operatorId;
		this.operatorName = operatorName;
		this.equivalentRequest = equivalentRequest;
		this.startTime = startTime;
		this.endTime = endTime;
	}



	/**
	 * @return the id
	 */
	public String getId() {

		return id;
	}



	/**
	 * @return the urlId
	 */
	public String getUrlId() {

		return urlId;
	}



	/**
	 * @return the operatorId
	 */
	public String getOperatorId() {

		return operatorId;
	}



	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {

		return operatorName;
	}



	/**
	 * @return the equivalentRequest
	 */
	public String getEquivalentRequest() {

		return equivalentRequest;
	}



	/**
	 * @return the startTime
	 */
	public Long getStartTime() {

		return startTime;
	}



	/**
	 * @return the endTime
	 */
	public Long getEndTime() {

		return endTime;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}



	/**
	 * @param urlId the urlId to set
	 */
	public void setUrlId(String urlId) {

		this.urlId = urlId;
	}



	/**
	 * @param operatorId the operatorId to set
	 */
	public void setOperatorId(String operatorId) {

		this.operatorId = operatorId;
	}



	/**
	 * @param operatorName the operatorName to set
	 */
	public void setOperatorName(String operatorName) {

		this.operatorName = operatorName;
	}



	/**
	 * @param equivalentRequest the equivalentRequest to set
	 */
	public void setEquivalentRequest(String equivalentRequest) {

		this.equivalentRequest = equivalentRequest;
	}



	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Long startTime) {

		this.startTime = startTime;
	}



	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Long endTime) {

		this.endTime = endTime;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskComputation [id=");
		builder.append(id);
		builder.append(", urlId=");
		builder.append(urlId);
		builder.append(", operatorId=");
		builder.append(operatorId);
		builder.append(", operatorName=");
		builder.append(operatorName);
		builder.append(", equivalentRequest=");
		builder.append(equivalentRequest);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append("]");
		return builder.toString();
	}


}