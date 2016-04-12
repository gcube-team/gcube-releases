/**
 * 
 */
package org.gcube.vremanagement.executor.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.HelloWorldPlugin;
import org.acme.HelloWorldPluginDeclaration;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.ListEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.RandomEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.SpecificEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.client.util.Tuple;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class QueriedClientTest {
	
	private static Logger logger = LoggerFactory.getLogger(QueriedClientTest.class);
	
	private void launchTest(SmartExecutorProxy  proxy) throws Exception {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000;
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		LaunchParameter launchParameter = new LaunchParameter(HelloWorldPluginDeclaration.NAME, inputs);
		try {
			String executionIdentifier = proxy.launch(launchParameter);
			proxy.getState(executionIdentifier);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Test
	public void testNoConditions() throws Exception {
		ScopeProvider.instance.set("/gcube");
		SmartExecutorProxy  proxy = ExecutorPlugin.getExecutorProxy(HelloWorldPluginDeclaration.NAME).build();
		Assert.assertNotNull(proxy);
		
		try {
			launchTest(proxy);
		} catch (Exception e) {
			logger.error("testNoConditions Exception", e);
			throw e;
		}

	}

	@Test
	public void testWithSingleRighConditions() throws Exception {
		ScopeProvider.instance.set("/gcube");
		HelloWorldPluginDeclaration helloWorldPluginDeclaration = new HelloWorldPluginDeclaration();
		Map<String,String> map = helloWorldPluginDeclaration.getSupportedCapabilities();
		Tuple<String, String> tuple = new Tuple<String, String>();
		for(String key : map.keySet()){
			tuple = new Tuple<String, String>(key, map.get(key));
			break; // Get only the first
		}
		SmartExecutorProxy  proxy = ExecutorPlugin.getExecutorProxy(HelloWorldPluginDeclaration.NAME, tuple).build();
		Assert.assertNotNull(proxy);
		
		try {
			launchTest(proxy);
		} catch (Exception e) {
			logger.error("testWithSingleRighConditions Exception", e);
			throw e;
		}
	}
	
	@Test
	public void testWithMultipleRighConditions() throws Exception {
		ScopeProvider.instance.set("/gcube");
		HelloWorldPluginDeclaration helloWorldPluginDeclaration = new HelloWorldPluginDeclaration();
		Map<String,String> map = helloWorldPluginDeclaration.getSupportedCapabilities();
		
		@SuppressWarnings("unchecked")
		Tuple<String, String>[] tuples = new Tuple[map.size()+1];
		int i = 0;
		for(String key : map.keySet()){
			tuples[i] = new Tuple<String, String>(key, map.get(key));
			++i;
		}
		
		tuples[i] = new Tuple<String, String>("Version", helloWorldPluginDeclaration.getVersion());

		SmartExecutorProxy  proxy = ExecutorPlugin.getExecutorProxy(HelloWorldPluginDeclaration.NAME, tuples).build();
		Assert.assertNotNull(proxy);
		
		try {
			launchTest(proxy);
		} catch (Exception e) {
			logger.error("testWithMultipleRighConditions Exception", e);
			throw e;
		}
	}
	
	@Test
	public void testWithUnsatisfiedConditions() {
		ScopeProvider.instance.set("/gcube");
		Tuple<String, String> tuple = new Tuple<String, String>("Unsatisfied", "Condition");
		SmartExecutorProxy proxy = ExecutorPlugin.getExecutorProxy(HelloWorldPluginDeclaration.NAME, tuple).build();
		Assert.assertNotNull(proxy);
		try {
			launchTest(proxy);
		} catch (Exception e) {
			Assert.assertEquals(DiscoveryException.class, e.getClass());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWithPersonalfilters() throws Exception {
		ScopeProvider.instance.set("/gcube");
		
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		HelloWorldPluginDeclaration helloWorldPluginDeclaration = new HelloWorldPluginDeclaration();
		Map<String,String> map = helloWorldPluginDeclaration.getSupportedCapabilities();
		Tuple<String, String> tuple = new Tuple<String, String>();
		for(String key : map.keySet()){
			tuple = new Tuple<String, String>(key, map.get(key));
			break; // Get only the first
		}

		query.addConditions(HelloWorldPluginDeclaration.NAME, tuple);
		query.setServiceEndpointQueryFilter(null);
		query.setEndpointDiscoveryFilter(null);
		
		SmartExecutorProxy proxy = new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(executorPlugin, query).build();
		
		try {
			launchTest(proxy);
		} catch (Exception e) {
			logger.error("testWithPersonalfilters Exception", e);
			throw e;
		}
	}
	
	@Test
	public void testManagedPersonalfilters() throws Exception {
		ScopeProvider.instance.set("/gcube");
		HelloWorldPluginDeclaration helloWorldPluginDeclaration = new HelloWorldPluginDeclaration();
		Map<String,String> map = helloWorldPluginDeclaration.getSupportedCapabilities();
		@SuppressWarnings("unchecked")
		Tuple<String, String>[] tuples = new Tuple[map.size()];
		int i = 0;
		for(String key : map.keySet()){
			tuples[i] = new Tuple<String, String>(key, map.get(key));
			++i;
		}
		
		SmartExecutorProxy  proxy = ExecutorPlugin.getExecutorProxy(HelloWorldPluginDeclaration.NAME, tuples, null, new RandomEndpointDiscoveryFilter()).build();
		Assert.assertNotNull(proxy);
		
		try {
			launchTest(proxy);
		} catch (Exception e) {
			logger.error("testManagedPersonalfilters Exception", e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWithListfilters() throws Exception {
		ScopeProvider.instance.set("/gcube");
		
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		HelloWorldPluginDeclaration helloWorldPluginDeclaration = new HelloWorldPluginDeclaration();
		Map<String,String> map = helloWorldPluginDeclaration.getSupportedCapabilities();
		Tuple<String, String> tuple = new Tuple<String, String>();
		for(String key : map.keySet()){
			tuple = new Tuple<String, String>(key, map.get(key));
			break; // Get only the first
		}

		query.addConditions(HelloWorldPluginDeclaration.NAME, tuple);
		query.setServiceEndpointQueryFilter(null);
		query.setEndpointDiscoveryFilter(new ListEndpointDiscoveryFilter());
		
		SmartExecutorProxy proxy = new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(executorPlugin, query).build();
		
		try {
			launchTest(proxy);
		} catch (Exception e) {
			logger.error("testWithListfilters Exception", e);
			throw e;
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWithSpecificSelection() throws Exception {
		ScopeProvider.instance.set("/gcube");
		
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		HelloWorldPluginDeclaration helloWorldPluginDeclaration = new HelloWorldPluginDeclaration();
		Map<String,String> map = helloWorldPluginDeclaration.getSupportedCapabilities();
		Tuple<String, String> tuple = new Tuple<String, String>();
		for(String key : map.keySet()){
			tuple = new Tuple<String, String>(key, map.get(key));
			break; // Get only the first
		}

		query.addConditions(HelloWorldPluginDeclaration.NAME, tuple);
		query.setServiceEndpointQueryFilter(null);
		List<String> endpoints = query.discoverEndpoints(new ListEndpointDiscoveryFilter());
		
		for(String endpoint : endpoints){
			
			ExecutorPlugin runExecutorPlugin = new ExecutorPlugin();
			SmartExecutorPluginQuery runQuery = new SmartExecutorPluginQuery(runExecutorPlugin);
			runQuery.addConditions(HelloWorldPluginDeclaration.NAME, tuple);
			
			SpecificEndpointDiscoveryFilter sedf = new SpecificEndpointDiscoveryFilter(endpoint);
			runQuery.setEndpointDiscoveryFilter(sedf);
			SmartExecutorProxy proxy = new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(runExecutorPlugin, runQuery).build();
			
			try {
				launchTest(proxy);
			} catch (Exception e) {
				logger.error("testWithSpecificSelection Exception", e);
				throw e;
			}
		}
	}
	
}
