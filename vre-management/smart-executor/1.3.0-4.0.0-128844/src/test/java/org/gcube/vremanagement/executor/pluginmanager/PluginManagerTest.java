package org.gcube.vremanagement.executor.pluginmanager;

import java.util.UUID;

import org.acme.HelloWorldPluginDeclaration;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class PluginManagerTest {

	@Test
	public void getInstance(){
		PluginManager pluginManager = PluginManager.getInstance();
		Assert.assertNotNull(pluginManager);
	}
	
	@Test
	public void getHelloWorldPlugin(){
		PluginManager pluginManager = PluginManager.getInstance();
		Assert.assertNotNull(pluginManager);
		Assert.assertEquals(HelloWorldPluginDeclaration.class, pluginManager.getPlugin(HelloWorldPluginDeclaration.NAME).getClass());
		Assert.assertNull(pluginManager.getPlugin(UUID.randomUUID().toString()));
	}
	
}
