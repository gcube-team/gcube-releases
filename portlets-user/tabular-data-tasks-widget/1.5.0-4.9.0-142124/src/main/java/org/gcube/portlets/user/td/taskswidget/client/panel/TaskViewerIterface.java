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
public interface TaskViewerIterface {
	
	boolean isTaskCompleted();
	
	/**
	 * 
	 * @param bool
	 */
	void setTaskCompleted(boolean bool);
	
	String getTaskId();

	/**
	 * @return
	 */
	JobsManager getJobsManager();

	/**
	 * @return
	 */
	TaskManager getTaskInfo();

	/**
	 * @return
	 */
	ResultsManager getResultManager();

}
