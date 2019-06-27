package org.gcube.portlets.user.performfish.bean;

import com.liferay.portal.kernel.json.JSONObject;

public class PublishAnonymisedJob {
	//STATUSES ACCEPTED, RUNNING, COMPLETE, FAILED, CANCELLED
	public static Long EPOCH_TIME_JOB_NOTFINISHED = 3050596467L; //September 1, 2066, Likely retired or dead by then
	
	private String jobId;
	private String farmId;
	private String batch_type;
	private String sourceUrl;
	private String status;
	private String submitterIdentity;
	private Long endTimeepochSecond;


	public PublishAnonymisedJob(JSONObject cData) {
		this.jobId=cData.getString("id");
		this.farmId=cData.getString("farmId");
		this.batch_type=cData.getString("batch_type");
		this.sourceUrl=cData.getString("sourceUrl");
		this.status=cData.getString("status");
		this.submitterIdentity=cData.getString("submitterIdentity");
		if (cData.getJSONObject("endTime") != null) //if there is no end time the status is not COMPLETE
			this.endTimeepochSecond=cData.getJSONObject("endTime").getLong("epochSecond");
		else 
			this.endTimeepochSecond = EPOCH_TIME_JOB_NOTFINISHED;
	}


	public String getJobId() {
		return jobId;
	}


	public String getFarmId() {
		return farmId;
	}


	public String getBatch_type() {
		return batch_type;
	}


	public String getSourceUrl() {
		return sourceUrl;
	}


	public String getStatus() {
		return status;
	}


	public String getSubmitterIdentity() {
		return submitterIdentity;
	}


	public Long getEndTimeEpochSecond() {
		return endTimeepochSecond;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PublishAnonymisedJob [jobId=");
		builder.append(jobId);
		builder.append(", farmId=");
		builder.append(farmId);
		builder.append(", batch_type=");
		builder.append(batch_type);
		builder.append(", sourceUrl=");
		builder.append(sourceUrl);
		builder.append(", status=");
		builder.append(status);
		builder.append(", submitterIdentity=");
		builder.append(submitterIdentity);
		builder.append(", endTime=");
		builder.append(endTimeepochSecond);
		builder.append("]");
		return builder.toString();
	}

	
}
