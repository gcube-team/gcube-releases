package org.gcube.data.analysis.tabulardata.service.impl.operation.tasks;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo.TaskType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationStep;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.operation.Job;
import org.gcube.data.analysis.tabulardata.service.operation.JobClassifier;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.operation.TaskResult;
import org.gcube.data.analysis.tabulardata.service.operation.ValidationJob;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskObject implements Task, TaskObserver, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static TaskManagerProxy taskManager = tasks().build();

	private static Logger logger = LoggerFactory.getLogger(TaskObject.class);
	private TaskId taskId;
	private TaskUpdater updater;
	private TaskInfo taskInfo;
	private List<Job> jobs = null;

	protected TaskObject(TaskInfo taskInfo){
		super();
		this.taskInfo = taskInfo;
		this.taskId = new TaskId(taskInfo.getIdentifier());
	}


	/**
	 * @return the taskId
	 */
	public TaskId getTaskId() {
		return taskId;
	}


	private int getFinishedSubTasks(){
		int started = 0;
		for (Job job: this.getTaskJobs())
			if (job.getStatus()==WorkerStatus.SUCCEDED)
				started++;
		return started;
	}

	public float getProgress() {
		checkUpdate();
		logger.debug("task info size "+taskInfo.getTaskSteps().size());
		if (taskInfo.getTaskSteps().size()==1) return taskInfo.getTaskSteps().get(0).getProgress();
		int finishedSubTask = getFinishedSubTasks();
		logger.debug("startedSubTask "+finishedSubTask);
		if (finishedSubTask==taskInfo.getTaskSteps().size()) return 1;
		else{
			float singleJobPercentage = 1/(float)taskInfo.getTaskSteps().size();
			float basePercentage = (float)finishedSubTask/(float)taskInfo.getTaskSteps().size();
			return (basePercentage+singleJobPercentage*taskInfo.getTaskSteps().get(finishedSubTask).getProgress()); 
		}
	}




	public TaskId getId() {
		return getTaskId();
	}



	public Date getStartTime() {
		checkUpdate();
		if (taskInfo.getStartTime()==null)
			return null;
		else return taskInfo.getStartTime().getTime();
	}

	public Date getEndTime() {
		checkUpdate();
		if(taskInfo.getEndTime()==null) return null;
		else return taskInfo.getEndTime().getTime();
	}


	public void abort() {
		try {
			taskManager.abort(this.getId().getValue());
		} catch (NoSuchTaskException e) {
			logger.error("error aborting task",e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public TabularResourceId getTabularResourceId() {
		return new TabularResourceId(taskInfo.getTabularResourceId());
	}


	public void notify(TaskInfo task) {
		if (task==null) return;
		this.taskInfo = task;
		List<Job> tempJobs= new ArrayList<Job>();

		if (taskInfo.getTaskSteps()!=null)
			for (TaskStep step: taskInfo.getTaskSteps())
				tempJobs.add(new JobImpl(step));
		this.jobs = tempJobs;

		if(this.getStatus().isFinal() && updater!=null)
			this.updater.unregisterObserver(this.getObserverIdentifier());
	}

	public String getObserverIdentifier() {
		return this.getId().getValue();
	}

	public TaskStatus getStatus() {
		checkUpdate();
		return taskInfo.getStatus();
	}

	public OperationExecution getInvocation() {
		return null;
	}

	public TaskResult getResult() {
		checkUpdate();
		return new TaskResult() {

			public Table getPrimaryTable() {
				if (taskInfo.getResult()!=null)
					return taskInfo.getResult().getResultTable();
				else return null;
			}

			public List<TableId> getCollateralTables() {
				if (taskInfo.getResult()!=null)
					return taskInfo.getResult().getCollateralTables();
				else return Collections.emptyList();
			}
		};
	}

	public List<Job> getTaskJobs() {
		if (checkUpdate() || jobs==null){
			List<Job> tempJobs= new ArrayList<Job>();
			if (taskInfo.getTaskSteps()!=null)
				for (TaskStep step: taskInfo.getTaskSteps())
					tempJobs.add(new JobImpl(step));
			this.jobs = tempJobs;
		}
		return jobs;
	}




	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.Task#getSubmitter()
	 */
	public String getSubmitter() {
		return taskInfo.getSubmitter();
	}

	public TaskType getTaskType() {
		return taskInfo.getType();
	}

	public Throwable getErrorCause() {
		return taskInfo.getErrorCause();
	}

	/**
	 * @param updater the updater to set
	 */
	protected void setUpdater(TaskUpdater updater) {
		this.updater = updater;
		this.updater.registerObserver(this);
	}



	@Override
	public String toString() {
		return "TaskObject [taskId=" + taskId + ", updater=" + updater
				+ ", taskInfo=" + taskInfo + ", jobs=" + jobs + "]";
	}


	private boolean checkUpdate(){
		if (updater==null) return false;
		else
			return updater.checkUpdate();

	}

	class ValidationJobImpl implements ValidationJob{

		private ValidationStep step;

		public ValidationJobImpl(ValidationStep step) {
			super();
			this.step = step;
		}

		public float getProgress() {
			return step.getProgress();
		}

		public WorkerStatus getStatus() {
			return step.getStatus();
		}

		public Throwable getErrorMessage() {
			return step.getError();
		}

		@Override
		public String getHumaReadableStatus() {
			return step.getHumanReadableStatus();
		}

		@Override
		public String getDescription() {
			return step.getExecutionDescription();
		}

		@Override
		public String toString() {
			return "ValidationJob [" + step + "]";
		}

	}


	class JobImpl implements Job {

		private TaskStep step;

		public JobImpl(TaskStep step) {
			super();
			this.step = step;
		}

		public float getProgress() {
			return step.getProgress();
		}

		public WorkerStatus getStatus() {
			return step.getStatus();
		}

		public OperationExecution getInvocation() {
			return step.getSourceInvocation();
		}

		public Throwable getErrorMessage() {
			return step.getError();
		}

		@Override
		public String getHumaReadableStatus() {
			return step.getHumanReadableStatus();
		}

		@Override
		public String getDescription() {
			return step.getExecutionDescription();
		}

		@Override
		public List<ValidationDescriptor> getValidations() {
			return step.getValidations();
		}

		@Override
		public String toString() {
			return "JobImpl [step=" + step + "]";
		}

		@Override
		public List<ValidationJob> getValidationJobs() {
			List<ValidationJob> validationJobs = new ArrayList<ValidationJob>();
			if (step.getValidationSteps()!=null)
				for (ValidationStep validationStep: step.getValidationSteps())
					validationJobs.add(new ValidationJobImpl(validationStep));
			return validationJobs;
		}

		@Override
		public JobClassifier getJobClassifier() {
			switch (step.getClassifier()) {
			case DATAVALIDATION:
				return JobClassifier.DATAVALIDATION;
			case PROCESSING:
				return JobClassifier.PROCESSING;
			case PREPROCESSING:
				return JobClassifier.PREPROCESSING;
			case POSTPROCESSING:
				return JobClassifier.POSTPROCESSING;
			default:
				break;
			}
			return null;
		}

	}


}
