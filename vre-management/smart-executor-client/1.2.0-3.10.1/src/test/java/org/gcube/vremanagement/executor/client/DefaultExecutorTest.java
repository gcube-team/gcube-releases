/**
 * 
 */
package org.gcube.vremanagement.executor.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.acme.HelloWorldPlugin;
import org.acme.HelloWorldPluginDeclaration;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class DefaultExecutorTest {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultExecutorTest.class);
	
	private SmartExecutorProxy proxy;
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube");
		proxy = ExecutorPlugin.getExecutorProxy().build();
		Assert.assertNotNull(proxy);
	}
	
	@Test
	public void launch() {
		Map<String, Object> inputs = new HashMap<String, Object>();
		LaunchParameter launchParameter = new LaunchParameter("Test", inputs);
		try {
			proxy.launch(launchParameter);
		} catch (Exception e) {
			Assert.assertEquals(PluginNotFoundException.class, e.getCause().getClass());
		}
		
	}

	@Test
	public void getState() {
		String executionIdentifier = UUID.randomUUID().toString();
		try {
			proxy.getState(executionIdentifier);
		} catch (Exception e) {
			Assert.assertEquals(PluginInstanceNotFoundException.class, e.getCause().getClass());
		}
		
	}
	
	@Test
	public void testOk() throws Exception {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000; // 1000 millisec * 10 = 10 sec
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		LaunchParameter launchParameter = new LaunchParameter(HelloWorldPluginDeclaration.NAME, inputs);
		try {
			String executionIdentifier = proxy.launch(launchParameter);

			Thread.sleep(1000);
			Assert.assertEquals(PluginState.RUNNING, proxy.getState(executionIdentifier));
			
			Thread.sleep(4000);
			Assert.assertEquals(PluginState.RUNNING, proxy.getState(executionIdentifier));
			
			Thread.sleep(6000);
			Assert.assertEquals(PluginState.DONE, proxy.getState(executionIdentifier));
			
		} catch (Exception e) {
			logger.error("testOk Exception", e);
			throw e;
		}
		
	}
	
	
	@Test
	public void testScheduledTaskNotPersisted() throws Exception {
		proxy = ExecutorPlugin.getExecutorProxy((new HelloWorldPluginDeclaration()).getName()).build();
		Assert.assertNotNull(proxy);
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000; // 1000 millisec * 10 = 10 sec
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		// Every 5 minutes, for 12 times (one hour totally).
		Scheduling scheduling = new Scheduling(60*5,12,false);
		LaunchParameter launchParameter = new LaunchParameter(HelloWorldPluginDeclaration.NAME, inputs, scheduling);
		
		try {
			String executionIdentifier = proxy.launch(launchParameter);

			Thread.sleep(1000);
			Assert.assertEquals(PluginState.RUNNING, proxy.getState(executionIdentifier));
			
			Thread.sleep(4000);
			Assert.assertEquals(PluginState.RUNNING, proxy.getState(executionIdentifier));
			
			Thread.sleep(6000);
			Assert.assertEquals(PluginState.DONE, proxy.getState(executionIdentifier));
			
		} catch (Exception e) {
			logger.error("testOk Exception", e);
			throw e;
		}
				
	}
	
	@Test
	public void testScheduledTaskPersisted() throws Exception {
		proxy = ExecutorPlugin.getExecutorProxy((new HelloWorldPluginDeclaration()).getName()).build();
		Assert.assertNotNull(proxy);
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000; // 1000 millisec * 10 = 10 sec
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		// Every 5 minutes, for 12 times (one hour totally).
		Scheduling scheduling = new Scheduling(60*5,12,true);
		LaunchParameter launchParameter = new LaunchParameter(HelloWorldPluginDeclaration.NAME, inputs, scheduling);
		
		try {
			String executionIdentifier = proxy.launch(launchParameter);

			Thread.sleep(1000);
			Assert.assertEquals(PluginState.RUNNING, proxy.getState(executionIdentifier));
			
			Thread.sleep(4000);
			Assert.assertEquals(PluginState.RUNNING, proxy.getState(executionIdentifier));
			
			Thread.sleep(6000);
			Assert.assertEquals(PluginState.DONE, proxy.getState(executionIdentifier));
			
		} catch (Exception e) {
			logger.error("testOk Exception", e);
			throw e;
		}
				
	}

}
