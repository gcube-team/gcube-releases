/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.util.List;



/**
 * The Interface BaseTaskConfiguration.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 2, 2018
 */
public interface BaseTaskConfiguration {

	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	String getTaskId();


	/**
	 * Gets the task name.
	 *
	 * @return the task name
	 */
	String getTaskName();

	/**
	 * Gets the workspace item id where execute the Task.
	 *
	 * @return the workspace item id
	 */
	String getWorkspaceItemId();


	/**
	 * Gets the task description.
	 *
	 * @return the task description
	 */
	String getTaskDescription();


	/**
	 * Gets the list parameters.
	 *
	 * @return the list parameters
	 */
	List<TaskParameter> getListParameters();


	/**
	 * Gets the access key to retrieve this configuration from gcube properties saved in the workspace.
	 *
	 * @return the access key
	 */
	String getConfigurationKey();


	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	String getOwner();


}
