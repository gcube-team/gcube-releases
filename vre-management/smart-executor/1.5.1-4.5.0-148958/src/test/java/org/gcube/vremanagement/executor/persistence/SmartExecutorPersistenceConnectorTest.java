/**
 * 
 */
package org.gcube.vremanagement.executor.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.acme.HelloWorldPluginDeclaration;
import org.gcube.vremanagement.executor.ScopedTest;
import org.gcube.vremanagement.executor.persistence.orientdb.OrientDBPersistenceConnector;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTaskPersistence;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTaskPersistenceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SmartExecutorPersistenceConnectorTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorPersistenceConnectorTest.class);
	
	@Test
	public void getConnectionTest() throws Exception {
		SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		Assert.assertNotNull(persistenceConnector);
		Assert.assertEquals(OrientDBPersistenceConnector.class, persistenceConnector.getClass());
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
			
			PluginStateEvolution pse = persistenceConnector.getPluginInstanceState(uuid, 1);
			PluginState ps = pse.getPluginState();
			Assert.assertEquals(states[i], ps);
		}
		
		PluginStateEvolution pse = persistenceConnector.getLastPluginInstanceState(uuid);
		PluginState ps = pse.getPluginState();
		Assert.assertEquals(states[states.length-1], ps);
		
		SmartExecutorPersistenceFactory.closePersistenceConnector();
	}
	
	@Test
	public void getAvailableScheduledTasksTest() throws Exception {
		ScheduledTaskPersistence stc = ScheduledTaskPersistenceFactory.getScheduledTaskPersistence();
		Assert.assertNotNull(stc);
		Assert.assertEquals(OrientDBPersistenceConnector.class, stc.getClass());
		
		List<ScheduledTask> lc = stc.getOrphanScheduledTasks(null);
		
		logger.debug("Available Scheduled Tasks : {}", lc);
	}
	
	
}
