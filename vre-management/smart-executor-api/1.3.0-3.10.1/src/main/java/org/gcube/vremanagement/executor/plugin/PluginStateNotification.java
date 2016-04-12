/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface PluginStateNotification {

	/**
	 * Persist the new state of plugin
	 * @param pluginStateEvolution the PluginStateEvolution record to persist
	 * @throws Exception if fails
	 */
	public abstract void pluginStateEvolution(PluginStateEvolution pluginStateEvolution) 
			throws Exception;
}
