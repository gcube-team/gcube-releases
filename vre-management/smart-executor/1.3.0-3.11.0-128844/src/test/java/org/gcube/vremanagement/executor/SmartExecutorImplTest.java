/**
 * 
 */
package org.gcube.vremanagement.executor;


import java.util.HashMap;
import java.util.Map;

import org.acme.HelloWorldPlugin;
import org.acme.HelloWorldPluginDeclaration;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.pluginmanager.PluginManager;
import org.junit.Test;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SmartExecutorImplTest {
	
	/*
	@Test
	public void createServiceEndpointTest() {
		TODO Redesign this test
		ServiceEndpoint serviceEndpoint = SmartExecutorInitalizator.createServiceEndpoint();
		
		Profile profile = serviceEndpoint.profile();
		Assert.assertEquals(SmartExecutor.WEB_SERVICE_SERVICE_NAME, profile.category());
		Assert.assertEquals(SmartExecutor.class.getSimpleName(), profile.name());
		Group<AccessPoint> accessPoints = profile.accessPoints();
		Assert.assertEquals(1, accessPoints.size());
		HelloWorldPluginDeclaration hwpd = new HelloWorldPluginDeclaration();
		Map<String, String> supportedCapabilities = hwpd.getSupportedCapabilities();
		for(AccessPoint accessPoint : accessPoints){
			Assert.assertEquals(hwpd.getName(),accessPoint.name());
			Group<Property> properties = accessPoint.properties();
			Assert.assertEquals(supportedCapabilities.size(), properties.size());
			for(Property property : properties){
				String propertyName = property.name();
				Assert.assertTrue(supportedCapabilities.containsKey(propertyName));
				Assert.assertEquals(supportedCapabilities.get(propertyName), property.value());
			}
		}
		
	}
	*/
	
	@Test
	public void helloWorldTest() throws Exception{
		Map<String, Object> inputs = new HashMap<String, Object>();
		long sleepTime = 10000;
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		Plugin<? extends PluginDeclaration> runnablePlugin = PluginManager.instantiatePlugin(HelloWorldPluginDeclaration.NAME);
		runnablePlugin.launch(inputs);
	}
	
}
