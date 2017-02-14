/**
 * 
 */
package org.gcube.vremanagement.executor.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.acme.HelloWorldPluginDeclaration;
import org.gcube.vremanagement.executor.TokenBasedTests;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.configuration.ScheduledTaskConfiguration;
import org.gcube.vremanagement.executor.configuration.ScheduledTaskConfigurationFactory;
import org.gcube.vremanagement.executor.persistence.couchdb.CouchDBPersistenceConnector;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SmartExecutorPersistenceConnectorTest extends TokenBasedTests {
	
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorPersistenceConnectorTest.class);
	
	@Test
	public void getConnectionTest() throws Exception {
		SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		Assert.assertNotNull(persistenceConnector);
		Assert.assertEquals(CouchDBPersistenceConnector.class, persistenceConnector.getClass());
		SmartExecutorPersistenceFactory.closePersistenceConnector();
	}
	
	@Test
	public void getPluginInstanceStateTest() throws Exception {
		SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		UUID uuid  = UUID.randomUUID();
		
		PluginState[] states = PluginState.values();
		
		for(int i=0; i<states.length; i++){
			long timestamp =  new Date().getTime();
			PluginStateEvolution pluginStateEvolution = new PluginStateEvolution(uuid, 1, timestamp, HelloWorldPluginDeclaration.class.newInstance(), states[i], 0);
			persistenceConnector.pluginStateEvolution(pluginStateEvolution, null);
			
			long startTime = Calendar.getInstance().getTimeInMillis();
			long endTime = startTime;
			while(endTime <=  (startTime + 1000)){
				endTime = Calendar.getInstance().getTimeInMillis();
			}
			
			PluginState ps = persistenceConnector.getPluginInstanceState(uuid, 1).getPluginState();
			Assert.assertEquals(states[i], ps);
		}

		SmartExecutorPersistenceFactory.closePersistenceConnector();
	}
	
	@Test
	public void getAvailableScheduledTasksTest() throws Exception {
		ScheduledTaskConfiguration stc = ScheduledTaskConfigurationFactory.getLaunchConfiguration();
		Assert.assertNotNull(stc);
		Assert.assertEquals(CouchDBPersistenceConnector.class, stc.getClass());
		
		List<LaunchParameter> lc = stc.getAvailableScheduledTasks();
		
		logger.debug("Available Scheduled Tasks : {}", lc);
	}
	
	
}
