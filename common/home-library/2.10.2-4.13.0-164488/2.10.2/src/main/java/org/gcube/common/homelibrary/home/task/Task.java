/**
 * 
 */
package org.gcube.common.homelibrary.home.task;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Task {
	
	/**
	 * Return this task id.
	 * @return the task id.
	 */
	public String getId();
	
	/**
	 * Return this task state.
	 * @return the task state.
	 */
	public TaskState getState();

	/**
	 * Result this task result.
	 * @return the task result.
	 */
	public TaskResult getResult();
	
	
	/**
	 * Return this task progress.
	 * @return the task progress.
	 */
	public TaskProgress getProgress();
}
