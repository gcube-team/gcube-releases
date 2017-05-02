package org.gcube.vremanagement.executor.pluginmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.acme.HelloWorldPlugin;
import org.acme.HelloWorldPluginDeclaration;
import org.gcube.vremanagement.executor.ScopedTest;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.InvalidInputsException;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class RunnablePluginTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorSchedulerTest.class);
	
	@Test
	public void launchNullInputsTest() throws Exception {
		logger.debug("Testing Null inputs");
		UUID uuid = UUID.randomUUID();
		HelloWorldPluginDeclaration hwpd = new HelloWorldPluginDeclaration();
		SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		List<PluginStateNotification> pluginStateNotifications = new ArrayList<PluginStateNotification>();
		pluginStateNotifications.add(persistenceConnector);
		HelloWorldPlugin helloWorldPlugin = new HelloWorldPlugin(hwpd);
		try {
			RunnablePlugin<HelloWorldPlugin> runnablePlugin = new RunnablePlugin<HelloWorldPlugin>(helloWorldPlugin, null, uuid, 1, pluginStateNotifications);
			runnablePlugin.run();
		} catch(Exception e){
			Assert.assertEquals(InputsNullException.class, e.getCause().getClass());
		}
	}
	
	@Test
	public void launchEmptyInputsTest() throws Exception {
		logger.debug("Testing Empty inputs");
		Map<String, Object> inputs = new HashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		HelloWorldPluginDeclaration hwpd = new HelloWorldPluginDeclaration();
		SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		List<PluginStateNotification> pluginStateNotifications = new ArrayList<PluginStateNotification>();
		pluginStateNotifications.add(persistenceConnector);
		HelloWorldPlugin helloWorldPlugin = new HelloWorldPlugin(hwpd);
		
		RunnablePlugin<HelloWorldPlugin> pt = new RunnablePlugin<HelloWorldPlugin>(helloWorldPlugin, inputs, uuid, 1, pluginStateNotifications);
		try {
			pt.run();
		} catch(RuntimeException e) {
			Assert.assertEquals(InvalidInputsException.class, e.getCause().getClass());
		}
		
	}
	
	@Test
	public void launchValidInputsTest() throws Exception {
		logger.debug("Testing Some inputs");
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Test", "Test");
		long sleepTime = 10000;
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		
		UUID uuid = UUID.randomUUID();
		HelloWorldPluginDeclaration hwpd = new HelloWorldPluginDeclaration();
		SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		List<PluginStateNotification> pluginStateNotifications = new ArrayList<PluginStateNotification>();
		pluginStateNotifications.add(persistenceConnector);
		HelloWorldPlugin helloWorldPlugin = new HelloWorldPlugin(hwpd);
		RunnablePlugin<HelloWorldPlugin> rp = new RunnablePlugin<HelloWorldPlugin>(helloWorldPlugin, inputs, uuid, 1, pluginStateNotifications);
		long startTime = Calendar.getInstance().getTimeInMillis();
		long endTime = startTime;
		while(endTime <=  (startTime + 1000)){
			endTime = Calendar.getInstance().getTimeInMillis();
		}
		Assert.assertTrue(PluginState.CREATED == persistenceConnector.getPluginInstanceState (uuid, 1).getPluginState());
		
		rp.run();
		
		startTime = Calendar.getInstance().getTimeInMillis();
		endTime = startTime;
		while(endTime <=  (startTime + 1000)){
			endTime = Calendar.getInstance().getTimeInMillis();
		}
		Assert.assertTrue(PluginState.DONE == persistenceConnector.getPluginInstanceState(uuid, 1).getPluginState());
		
	}

}
