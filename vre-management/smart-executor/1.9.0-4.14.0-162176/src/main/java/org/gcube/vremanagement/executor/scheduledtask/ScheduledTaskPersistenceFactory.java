/**
 * 
 */
package org.gcube.vremanagement.executor.scheduledtask;

import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ScheduledTaskPersistenceFactory {

	public static ScheduledTaskPersistence getScheduledTaskPersistence() throws Exception {
		return (ScheduledTaskPersistence) SmartExecutorPersistenceFactory.getPersistenceConnector();
	}
	
}
