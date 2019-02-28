/**
 * 
 */
package org.gcube.vremanagement.executor.scheduledtask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ScheduledTaskPersistence {

	public static final String SCOPE = "scope";

	/**
	 * Retrieve from the #SmartExecutorPersistenceConnector the orphaned
	 * Scheduled tasks
	 * @param pluginDeclarations
	 * @return the list of orphaned Scheduled
	 * @throws SchedulePersistenceException
	 *             if fails
	 */
	public List<ScheduledTask> getOrphanScheduledTasks(
			Collection<? extends PluginDeclaration> pluginDeclarations)
			throws SchedulePersistenceException;

	/**
	 * Return the Scheduled Task if any, null otherwise
	 * 
	 * @param uuid
	 *            which identify the Scheduled Task
	 * @return LaunchParameter of the Scheduled task if any, null otherwise
	 * @throws SchedulePersistenceException
	 *             if fails
	 */
	public ScheduledTask getScheduledTask(UUID uuid)
			throws SchedulePersistenceException;

	/**
	 * Create a Scheduled Task on persistence
	 * 
	 * @param scheduledTask to create on persistence
	 * @throws SchedulePersistenceException
	 *             if fails
	 */
	public void addScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException;

	/**
	 * Release the Scheduled Task leaving it as orphan on persistence
	 * 
	 * @param uuid
	 *            the uuid which (will) identify the task on the SmartExecutor
	 *            instance
	 * @throws SchedulePersistenceException
	 */
	public void releaseScheduledTask(UUID uuid)
			throws SchedulePersistenceException;

	/**
	 * Remove from persistence the Scheduled Task.
	 * 
	 * @param scheduledTask
	 * @throws SchedulePersistenceException
	 */
	public void removeScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException;

	/**
	 * Remove from persistence the Scheduled Task.
	 * 
	 * @param uuid
	 *            the uuid which (will) identify the task on the SmartExecutor
	 *            instance
	 * @throws SchedulePersistenceException
	 */
	public void removeScheduledTask(UUID uuid)
			throws SchedulePersistenceException;

	/**
	 * Release the Scheduled Task leaving it as orphan on persistence
	 * 
	 * @param scheduledTask
	 * @throws SchedulePersistenceException
	 */
	public void releaseScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException;

	/**
	 * Reserve an orphan Scheduled tasks
	 * 
	 * @param scheduledTask
	 * @throws SchedulePersistenceException
	 *             if fails
	 */
	public void reserveScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException;
}
