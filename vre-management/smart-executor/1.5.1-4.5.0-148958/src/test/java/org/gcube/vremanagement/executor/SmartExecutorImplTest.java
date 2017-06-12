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
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SmartExecutorImplTest {
	
	@Test
	public void helloWorldTest() throws Exception{
		Map<String, Object> inputs = new HashMap<String, Object>();
		long sleepTime = 10000;
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		Plugin<? extends PluginDeclaration> runnablePlugin = PluginManager.instantiatePlugin(HelloWorldPluginDeclaration.NAME);
		runnablePlugin.launch(inputs);
	}
	
	@Test
	public void helloWorldFullTest() throws Exception{
		Map<String, Object> inputs = new HashMap<String, Object>();
		long sleepTime = 10000;
		inputs.put(HelloWorldPlugin.SLEEP_TIME, sleepTime);
		Plugin<? extends PluginDeclaration> runnablePlugin = PluginManager.instantiatePlugin(HelloWorldPluginDeclaration.NAME);
		runnablePlugin.launch(inputs);
	}
	
}
