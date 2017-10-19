package org.gcube.portal.databook.shared;

import java.io.Serializable;

/**
 * The RunningJob class.
 * @author Massimiliano Assante, ISTI-CNR (massimiliano.assante@isti.cnr.it)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class RunningJob implements Serializable {
	
	private String jobId;
	private String jobName;
	private JobStatusType status;
	private String message;
	private String serviceName; // i.e., Dataminer, SmartExecutor..
	
	public RunningJob() {
		super();
	}
	
	/** Buind a RunningJob object.
	 * @param jobId
	 * @param jobName
	 * @param status
	 * @param message
	 * @param serviceName
	 */
	public RunningJob(String jobId, String jobName, JobStatusType status,
			String message, String serviceName) {
		super();
		this.jobId = jobId;
		this.jobName = jobName;
		this.status = status;
		this.message = message;
		this.serviceName = serviceName;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String toString() {
		return "RunningJob [" + (jobId != null ? "jobId=" + jobId + ", " : "")
				+ (jobName != null ? "jobName=" + jobName + ", " : "")
				+ (status != null ? "status=" + status + ", " : "")
				+ (message != null ? "message=" + message + ", " : "")
				+ (serviceName != null ? "serviceName=" + serviceName : "")
				+ "]";
	}
}
