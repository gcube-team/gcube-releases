package org.gcube.vremanagement.executor.plugin;

import java.util.Map;

import org.gcube.vremanagement.executor.persistence.Persistence;
import org.gcube.vremanagement.executor.persistence.PersistenceConnector;

/**
 * This interface represent the contract for a plugin runnable by the executor.
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class Plugin<T extends PluginDeclaration> {
	
	private T pluginDeclaration;
	
	@Deprecated
	public Plugin(T pluginDeclaration, Persistence<?  extends PersistenceConnector> persistence){
		this.pluginDeclaration = pluginDeclaration;
	}
	
	public Plugin(T pluginDeclaration){
		this.pluginDeclaration = pluginDeclaration;
	}
	
	/**
	 * @return the pluginDeclaration
	 */
	public T getPluginDeclaration() {
		return pluginDeclaration;
	}
	
	/**
	 * Launch the plugin with the provided input.
	 * @param inputs
	 * @throws Exception if the launch fails
	 */
	public abstract void launch(Map<String,Object> inputs) throws Exception;
	
	/**
	 * This function is used to correctly stop the plugin 
	 * @throws Exception if the launch fails
	 */
	protected abstract void onStop() throws Exception;
	
	/**
	 * Invoke onStop() function to allow the plugin to safely stop the execution
	 * @throws Exception
	 */
	public void stop() throws Exception {
		onStop();
	}
	
}
