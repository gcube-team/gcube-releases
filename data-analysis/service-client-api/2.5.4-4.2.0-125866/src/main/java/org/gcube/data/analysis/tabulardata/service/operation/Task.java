package org.gcube.data.analysis.tabulardata.service.operation;

import java.util.Date;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo.TaskType;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

public interface Task {

	public TaskId getId();
	
	public float getProgress();
	
	public TaskStatus getStatus();
	
	public String getSubmitter();
		
	public OperationExecution getInvocation();
	
	public TabularResourceId getTabularResourceId();
	
	public TaskResult getResult();
	
	public Throwable getErrorCause();

	public List<Job> getTaskJobs();

	public Date getStartTime();

	public Date getEndTime();
	
	public void abort();
	
	public TaskType getTaskType();

}
