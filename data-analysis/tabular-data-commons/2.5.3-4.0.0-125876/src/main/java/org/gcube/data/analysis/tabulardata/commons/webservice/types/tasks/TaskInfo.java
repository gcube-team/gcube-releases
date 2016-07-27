package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerResult;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.ThrowableAdapter;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value={TemplateTaskInfo.class, OperationTaskInfo.class, RollbackTaskInfo.class})
public abstract class TaskInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum TaskType{
		TEMPLATE,
		OPERATION, 
		ROLLBACK
	}
			
	@XmlElement
	private String identifier;
	
	@XmlElement
	private String submitter;
	
	@XmlElement
	private Calendar startTime;
	
	@XmlElement
	private Calendar endTime;
	
	@XmlElement
	private long tabularResourceId;
	
	
	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	private Throwable errorCause;
	
	@XmlElement
	private List<TaskStep> taskSteps;
	
	@XmlElement
	private TaskStatus status = TaskStatus.INITIALIZING;
	
	@XmlElement
	private WorkerResult result;
	
	protected TaskInfo(){}
	
	public TaskInfo(String submitter, long tabularResourceId){
		this.submitter = submitter;
		this.setStartTime(Calendar.getInstance());
		this.identifier = UUID.randomUUID().toString();
		this.tabularResourceId = tabularResourceId;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	public boolean isAborted(){
		return this.getStatus()== TaskStatus.ABORTED;
	}
		
	/**
	 * @return the startTime
	 */
	public Calendar getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Calendar getEndTime() {
		return endTime;
	}
	
	
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public Throwable getErrorCause() {
		return errorCause;
	}

	public void setErrorCause(Throwable errorCause) {
		this.errorCause = errorCause;
	}
	
	/**
	 * @return the taskSteps
	 */
	public List<TaskStep> getTaskSteps() {
		return taskSteps;
	}

	/**
	 * @param taskSteps the taskSteps to set
	 */
	public void setTaskSteps(List<TaskStep> taskSteps) {
		this.taskSteps = taskSteps;
	}

	
	/**
	 * @return the submitter
	 */
	public String getSubmitter() {
		return submitter;
	}

	/**
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	/**
	 * @return the result
	 */
	public WorkerResult getResult() {
		return result;
	}
		
	
	public abstract boolean isResubmittable(); 
		
	

	/**
	 * @return the type
	 */
	public abstract TaskType getType();

	/**
	 * @param result the result to set
	 */
	public void setResult(WorkerResult result) {
		this.result = result;
	}

	public long getTabularResourceId(){
		return tabularResourceId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaskInfo [type=" + getType() + ", identifier=" + identifier
				+ ", submitter=" + submitter + ", errorCause=" + errorCause
				+ ", taskSteps=" + taskSteps + ", status=" + status
				+ ", result=" + result + "]";
	}

	


	
}
