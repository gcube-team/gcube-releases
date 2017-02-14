/**
 * 
 */
package org.gcube.vremanagement.executor.configuration;

import java.util.List;
import java.util.UUID;

import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ScheduledTaskConfiguration {

	public static final String SCOPE = "scope";
	
	/**
	 * Retrieve from the #SmartExecutorPersistenceConnector the orphaned
	 * Scheduled tasks
	 * @return the list of orphaned Scheduled
	 * @throws SchedulePersistenceException if fails
	 */
	public List<LaunchParameter> getAvailableScheduledTasks() throws SchedulePersistenceException;
	
	
	/**
	 * Return the Scheduled Task if any, null otherwise
	 * @param uuid which identify the Scheduled Task
	 * @return LaunchParameter of the Scheduled task if any, null otherwise
	 * @throws SchedulePersistenceException if fails
	 */
	public LaunchParameter getScheduledTask(UUID uuid)  throws SchedulePersistenceException;
	
	
	/**
	 * Create a Scheduled Task on persistence
	 * @param uuid the uuid which (will) identify the task on the SmartExecutor instance
	 * @param parameter
	 * @throws SchedulePersistenceException if fails
	 */
	public void addScheduledTask(UUID uuid, String consumerID, LaunchParameter parameter) throws SchedulePersistenceException;
	
	/**
	 * Reserve an orphan Scheduled tasks
	 * @param uuid the uuid which (will) identify the task on the SmartExecutor instance
	 * @throws SchedulePersistenceException if fails
	 */
	public void reserveScheduledTask(UUID uuid, String consumerID) throws SchedulePersistenceException;
	
	/**
	 * Remove from persistence the Scheduled Task.
	 * @param uuid the uuid which (will) identify the task on the SmartExecutor instance
	 * @param parameter
	 * @throws SchedulePersistenceException
	 */
	public void removeScheduledTask(UUID uuid)throws SchedulePersistenceException;
	
	/**
	 * Release the Scheduled Task leaving it as orphan on persistence
	 * @param uuid the uuid which (will) identify the task on the SmartExecutor
	 * instance
	 * @throws SchedulePersistenceException
	 */
	public void releaseScheduledTask(UUID uuid) throws SchedulePersistenceException;
	
}
