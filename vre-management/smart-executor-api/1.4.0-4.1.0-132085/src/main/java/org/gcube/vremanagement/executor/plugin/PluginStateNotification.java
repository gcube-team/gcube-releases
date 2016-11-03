/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import java.util.Map;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class PluginStateNotification {

	protected Map<String, String> inputs;
	
	public PluginStateNotification(Map<String, String> inputs){
		this.inputs = inputs;
	}
	
	/**
	 * Persist the new state of plugin
	 * @param pluginStateEvolution the PluginStateEvolution record to persist
	 * @throws Exception if fails
	 */
	public abstract void pluginStateEvolution(PluginStateEvolution pluginStateEvolution, Exception exception) 
			throws Exception;
}
