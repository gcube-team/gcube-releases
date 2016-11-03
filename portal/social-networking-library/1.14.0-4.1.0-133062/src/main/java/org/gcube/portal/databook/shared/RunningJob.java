package org.gcube.portal.databook.shared;

import java.io.Serializable;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Dec 2012
 *
 */
@SuppressWarnings("serial")
public class RunningJob implements Serializable {
	
	private String jobId;
	private String jobName;
	private JobStatusType status;
	
	public RunningJob() {
		super();
	}
	
	public RunningJob(String jobId, String jobName, JobStatusType status) {
		super();
		this.jobId = jobId;
		this.jobName = jobName;
		this.status = status;
	}
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public JobStatusType getStatus() {
		return status;
	}
	public void setStatus(JobStatusType status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "RunningJob [jobId=" + jobId + ", jobName=" + jobName
				+ ", status=" + status + "]";
	}
	
}
