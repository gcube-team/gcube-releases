package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStepClassifier;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerResult;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.ThrowableAdapter;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskStep implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	@XmlElement
	protected WorkerResult result;
	
	@XmlElement
	protected WorkerStatus status = WorkerStatus.PENDING;

	@XmlElement
	private List<ValidationDescriptor> validations = new ArrayList<ValidationDescriptor>();
	
	@XmlElement
	protected String humanReadableStatus="";
	
	@XmlElement
	private OperationExecution sourceInvocation;
		
	@XmlElement
	private List<ValidationStep> validationSteps;
	
	@XmlElement
	private TaskStepClassifier classifier = TaskStepClassifier.PROCESSING;
	
	@XmlElement
	private String executionDescription;
	
	@XmlElement
	protected float progress;
	
	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	protected Throwable errorMessage;
	
	protected TaskStep() {}
	
	public TaskStep(OperationExecution execution , TaskStepClassifier classifier){
		this.sourceInvocation = execution;
		this.classifier = classifier;
	}
	
	/**
	 * @return the result
	 */
	public WorkerResult getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(WorkerResult result) {
		this.result = result;
	}

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
	 * @return the sourceInvocation
	 */
	public OperationExecution getSourceInvocation() {
		return sourceInvocation;
	}

	/**
	 * @param sourceInvocation the sourceInvocation to set
	 */
	public void setSourceInvocation(OperationExecution sourceInvocation) {
		this.sourceInvocation = sourceInvocation;
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
		
	public List<ValidationStep> getValidationSteps() {
		return validationSteps;
	}
	
	public void setValidationSteps(List<ValidationStep> validationSteps) {
		this.validationSteps = validationSteps;
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

	public void addValidations(List<ValidationDescriptor> validations){
		this.validations.addAll(validations);
	}
	
	public void addValidation(ValidationDescriptor validation){
		this.validations.add(validation);
	}
	
	public List<ValidationDescriptor> getValidations() {
		return validations;
	}
	
	public void cleanValidations() {
		validations = new ArrayList<ValidationDescriptor>();
	}
	
	
	public void setHumanReadableStatus(String humanReadableStatus) {
		this.humanReadableStatus = humanReadableStatus;
	}
	

	public void setErrorMessage(Throwable errorMessage) {
		this.errorMessage = errorMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaskStep [result=" + result + ", status=" + status 
				+ ", sourceInvocation=" + sourceInvocation + ", classifier="
				+ classifier + ", progress=" + progress + ", errorMessage="
				+ errorMessage + "]";
	}
	

	
}
