/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel;

import org.gcube.portlets.user.td.taskswidget.client.manager.JobsManager;
import org.gcube.portlets.user.td.taskswidget.client.manager.ResultsManager;
import org.gcube.portlets.user.td.taskswidget.client.manager.TaskManager;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 19, 2013
 *
 */
public class TaskViewer implements TaskViewerIterface{
	
	private String taskId;
	private JobsManager jobsManager;
	
	private boolean taskCompleted = false;
	private TaskManager taskManager;
	private ResultsManager resultManager;
	
	/**
	 * @param b 
	 * @param resultManager 
	 * @param jobsManager2 
	 * @param taskManager 
	 * @param string 
	 * 
	 */
	public TaskViewer(String taskId, TaskManager taskManager, JobsManager jobsManager, ResultsManager resultManager, boolean taskCompleted) {
		this.taskId = taskId;
		this.taskManager = taskManager;
		this.jobsManager = jobsManager;
		this.resultManager = resultManager;
		this.taskCompleted = taskCompleted;
	}
	
	
	public String getTaskId() {
		return taskId;
	}

	protected void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public JobsManager getJobsManager() {
		return jobsManager;
	}

	protected void setJobsManager(JobsManager jobsManager) {
		this.jobsManager = jobsManager;
	}


	@Override
	public boolean isTaskCompleted() {
		return taskCompleted;
	}

	@Override
	public void setTaskCompleted(boolean bool) {
		taskCompleted = bool;
		
	}

	@Override
	public TaskManager getTaskInfo() {
		return taskManager;
	}

	@Override
	public ResultsManager getResultManager() {
		return resultManager;
	}
}
