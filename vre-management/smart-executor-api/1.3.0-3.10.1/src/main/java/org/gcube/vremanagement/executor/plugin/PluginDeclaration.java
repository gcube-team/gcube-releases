/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import java.util.Map;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface PluginDeclaration {

	/**
	 * This method is used by executor to ask to Plugin to initialize itself.
	 * In some cases the plugin does not need the initialization. In that case
	 * the plugin can just implement an empty method.
	 */
	public void init() throws Exception;
	
	/**
	 * This method is used by executor to retrieve the name of the Plugin
	 * @return the name of the plugin used by the Executor to refer to the
	 * plugin implementation class.
	 */
	public String getName();
	
	/**
	 * This method is used by executor to retrieve an human oriented description
	 * and it will be published on the ServiceEndpoint created by the executor
	 * @return the plugin description.
	 */
	public String getDescription();
	
	/**
	 * This method is used by executor to retrieve the plugin version.
	 * It will be published on the ServiceEndpoint created by the executor
	 * @return the plugin version
	 */
	public String getVersion();
	
	/**
	 * This method is used by the Executor to get a key-value {@link Map}
	 * to be published on IS (on Generic Resource), so a client which want to 
	 * launch a Plugin only under certain condition can query the Generic 
	 * Resource in the proper way to obtain its own filtered list.
	 * @return the {@link Map} with the supported capabilities
	 */
	public Map<String,String> getSupportedCapabilities();
	
	/**
	 * Used to retrieve the class which run the plugin 
	 * @return the class which run the plugin
	 */
	public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation();

	/* Waiting for Java 8 where this method should be declarable
	public String toString(){
		return String.format("%s : %s - %s - %s - %s - %s", 
				this.getClass().getSimpleName(), 
				getName(), getVersion(), getDescription(), 
				getSupportedCapabilities(), 
				getPluginImplementation().getClass().getSimpleName());
	}
	*/
}
