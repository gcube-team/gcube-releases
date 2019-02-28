package org.gcube.data.tmf.api;

/**
 * A {@link Plugin} extension with callbacks made by the service at key points of its lifecycle.
 * 
 * 
 * @author Fabio Simeoni
 * @see Plugin
 *
 */
public interface PluginLifecycle extends Plugin {

	/**
	 * Invoked by the service when plugin is first loaded.
	 * @param environment the deployment environment of the plugin.
	 */
	void start(Environment environment) throws Exception;
	
	
	/**
	 * Invoked by the service when the plugin, or the service, are stopped.
	 * @param environment the deployment environment of the plugin.
	 */
	void stop(Environment environment);
}
