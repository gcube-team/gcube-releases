package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStepClassifier;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.ThrowableAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationStep implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8793235698772500674L;
	
	@XmlElement
	protected WorkerStatus status = WorkerStatus.PENDING;
	
	@XmlElement
	protected String humanReadableStatus="";
	
	@XmlElement
	private TaskStepClassifier classifier = TaskStepClassifier.PREPROCESSING;
			
	@XmlElement
	private String executionDescription;
	
	@XmlElement
	protected float progress;
	
	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	protected Throwable errorMessage;
				
	/**
	 * @return the status
	 */
	public WorkerStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(WorkerStatus status) {
		this.status = status;
	}
		
	/**
	 * @return the classifier
	 */
	public TaskStepClassifier getClassifier() {
		return classifier;
	}

	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(TaskStepClassifier classifier) {
		this.classifier = classifier;
	}
	
	/**
	 * @return the progress
	 */
	public float getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(float progress) {
		this.progress = progress;
	}

	public String getHumanReadableStatus() {
		return humanReadableStatus;
	}
	
	/**
	 * @return the error
	 */
	public Throwable getError() {
		return errorMessage;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Throwable error) {
		this.errorMessage = error;
	}

	public void setExecutionDescription(String executionDescription) {
		this.executionDescription = executionDescription;
	}

	public String getExecutionDescription() {
		return executionDescription;
	}

	@Override
	public String toString() {
		return "ValidationStep [status=" + status + ", humanReadableStatus="
				+ humanReadableStatus + ", executionDescription="
				+ executionDescription + ", progress=" + progress + "]";
	}
		
}
