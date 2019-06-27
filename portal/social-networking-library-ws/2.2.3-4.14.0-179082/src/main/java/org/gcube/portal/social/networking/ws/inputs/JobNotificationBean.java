package org.gcube.portal.social.networking.ws.inputs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import org.gcube.portal.databook.shared.JobStatusType;
import org.gcube.portal.databook.shared.RunningJob;
import org.gcube.portal.social.networking.ws.providers.JobStatusTypeDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * The job notification bean class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
@ApiModel(description="A job notification object", value="JobNotification")
public class JobNotificationBean {

	@JsonProperty("recipient")
	@NotNull(message="recipient cannot be missing")
	@ApiModelProperty(value="The recipient of the notification",
	required=true,
	example="andrea.rossi",
	name="recipient")
	private String recipient;

	@JsonProperty("job_id")
	@ApiModelProperty(value="The job's identifier, i.e. very likely a UUID",
	required=true,
	example="88055f18-558a-4246-942d-a43012528c92",
	name="job_id")
	@NotNull(message="job_id cannot be missing")
	private String jobId;

	@JsonProperty("job_name")
	@ApiModelProperty(value="The job's name",
	required=true,
	example="SocialDataIndexer Plugin",
	name="job_name")
	@NotNull(message="job_name cannot be missing")
	private String jobName;
	
	@JsonProperty("service_name")
	@ApiModelProperty(value="The name of the service running the job",
	required=true,
	example="SmartExecutor",
	name="service_name")
	@NotNull(message="service_name cannot be missing")
	private String serviceName;

	@JsonProperty("status")
	@ApiModelProperty(value="The status of the job",
	required=true,
	example="FAILED",
	name="status",
	allowableValues = "CANCELLED,CANCELLING,DELETED,DELETING,EXECUTING,FAILED,NEW,SUBMITTED,SUCCEEDED,TIMED_OUT,WAITING"
	)
	@JsonDeserialize(using=JobStatusTypeDeserializer.class)
	@NotNull(message="status cannot be missing")
	private JobStatusType status;

	@JsonProperty("status_message")
	@ApiModelProperty(value="A message reporting the reason of the current status",
	required=false,
	example="An error occurred while ...",
	name="status_message")
	private String statusMessage;
	
	public JobNotificationBean() {
		super();
	}

	/**
	 * @param recipient
	 * @param jobId
	 * @param jobName
	 * @param serviceName
	 * @param status
	 * @param statusMessage
	 */
	public JobNotificationBean(String recipient, String jobId, String jobName,
			String serviceName, JobStatusType status, String statusMessage) {
		super();
		this.recipient = recipient;
		this.jobId = jobId;
		this.jobName = jobName;
		this.serviceName = serviceName;
		this.status = status;
		this.statusMessage = statusMessage;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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
	
	public RunningJob getRunningJob(){
		
		return new RunningJob(jobId, jobName, status, statusMessage, serviceName);
		
	}

	@Override
	public String toString() {
		return "JobNotificationBean ["
				+ (recipient != null ? "recipient=" + recipient + ", " : "")
				+ (jobId != null ? "jobId=" + jobId + ", " : "")
				+ (jobName != null ? "jobName=" + jobName + ", " : "")
				+ (serviceName != null ? "serviceName=" + serviceName + ", "
						: "")
				+ (status != null ? "status=" + status + ", " : "")
				+ (statusMessage != null ? "statusMessage=" + statusMessage
						: "") + "]";
	}
}
