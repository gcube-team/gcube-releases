/**
 * 
 */
package org.gcube.vremanagement.executor.configuration;

import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ScheduledTaskConfigurationFactory {

	public static ScheduledTaskConfiguration getLaunchConfiguration() throws Exception {
		return (ScheduledTaskConfiguration) SmartExecutorPersistenceFactory.getPersistenceConnector();
	}
	
}
