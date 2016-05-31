/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.threads;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public interface TaskExecutionListener {

	/**
	 * Called when a task has completed its execution
	 * @param taskUID the UID of the completed task
	 */
	public void onTaskExecutionCompleted(String taskUID);

}
