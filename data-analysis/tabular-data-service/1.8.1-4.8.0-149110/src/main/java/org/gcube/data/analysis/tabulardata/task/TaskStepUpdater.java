package org.gcube.data.analysis.tabulardata.task;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskStepUpdater implements Observer, Serializable {
	
	private static Logger logger = LoggerFactory.getLogger(TaskStepUpdater.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TaskStep step;
	
	public TaskStepUpdater(TaskStep taskStep) {
		this.step = taskStep;
	}

	protected TaskStepUpdater(){}
	
	@Override
	public void update(Observable o, Object obj) {
			
		Worker<?> w = (Worker<?>) obj;
		/*if (w.getResult()!=null)
			this.result =new WorkerResult(w.getResult().getResultTable());*/
		this.step.setStatus(WorkerStatus.valueOf(w.getStatus().name()));
		this.step.setProgress(w.getProgress());	
		this.step.setHumanReadableStatus(w.getHumanReadableStatus());
		if (w.getException()!=null)
			this.step.setErrorMessage(w.getException());
		
		logger.debug("Task updated "+this.step.getHumanReadableStatus()+" "+this.step.getProgress());
	}
	
	
}
