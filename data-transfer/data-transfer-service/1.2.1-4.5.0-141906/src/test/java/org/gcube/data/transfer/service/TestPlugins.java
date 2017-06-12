package org.gcube.data.transfer.service;

import java.util.Map;

import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.service.transfers.engine.impl.PluginManagerImpl;
import org.junit.Assert;
import org.junit.Test;

public class TestPlugins {

	@Test
	public void testPlugins(){		
		Map<String,PluginDescription> plugins=new PluginManagerImpl().getInstalledPlugins();
		Assert.assertNotNull(plugins);
		System.out.println(plugins);
	}
	
	
}
