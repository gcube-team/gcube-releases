/*
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.util.List;

import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotExecutableException;
import org.gcube.common.workspacetaskexecutor.shared.exception.TaskConfigurationNotFoundException;



/**
 * The Interface ExecutableItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 3, 2018
 * @param <T> the generic type must extends {@link BaseTaskConfiguration}
 */
public interface ExecutableItem<T extends BaseTaskConfiguration> {

	/**
	 * Checks if is item executable.
	 *
	 * @param itemId the item id
	 * @return the boolean
	 * @throws Exception the exception
	 */
	Boolean isItemExecutable(String itemId) throws Exception;


	/**
	 * Sets the task configuration.
	 *
	 * @param config the config
	 * @return the boolean
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws Exception the exception
	 */
	void setTaskConfiguration(T config) throws ItemNotExecutableException, Exception;


	/**
	 * Removes the task configuration.
	 *
	 * @param config the config
	 * @return the boolean
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws Exception the exception
	 */
	Boolean removeTaskConfiguration(T config) throws ItemNotExecutableException, Exception;


	/**
	 * Gets the task configuration.
	 *
	 * @param itemId the item id
	 * @param configurationKey the configuration key
	 * @return the task configuration
	 * @throws TaskConfigurationNotFoundException the task configuration not found exception
	 * @throws Exception
	 */
	T getTaskConfiguration(String itemId, String configurationKey) throws TaskConfigurationNotFoundException, Exception;


	/**
	 * Gets the list of task configurations.
	 *
	 * @param itemId the item id
	 * @return the list of executable configurations. It is the list of its {@link BaseTaskConfiguration}
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws Exception the exception
	 */
	List<T> getListOfTaskConfigurations(String itemId) throws ItemNotExecutableException, Exception;


	/**
	 * Erase all task configurations.
	 *
	 * @param itemId the item id
	 * @return the boolean
	 * @throws ItemNotExecutableException the item not executable exception
	 * @throws Exception the exception
	 */
	Boolean eraseAllTaskConfigurations(String itemId) throws ItemNotExecutableException, Exception;



}
