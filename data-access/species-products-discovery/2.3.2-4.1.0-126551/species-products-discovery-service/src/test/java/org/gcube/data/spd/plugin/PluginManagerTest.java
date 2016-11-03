package org.gcube.data.spd.plugin;

import java.util.Map.Entry;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManagerTest {

	private Logger logger = LoggerFactory.getLogger(PluginManagerTest.class);
	
	private static final String scope ="/gcube/devsec";
	
	@Test
	public void retreivePlugins(){
		ScopeProvider.instance.set(scope);
		PluginManager pm = PluginManager.get();
		for (Entry<String, AbstractPlugin> entry: pm.plugins().entrySet())
			logger.trace(entry.getKey()+" - "+entry.getValue().isRemote());
	}
	
}
