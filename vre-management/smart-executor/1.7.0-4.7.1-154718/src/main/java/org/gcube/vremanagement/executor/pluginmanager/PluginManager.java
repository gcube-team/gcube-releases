package org.gcube.vremanagement.executor.pluginmanager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.persistence.Persistence;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a singleton class which discover on classpath the available plugins
 * and map the plugin name to its implementation class.
 * The plugin implementation class can be retrieved using its name.
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("deprecation")
public class PluginManager {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(PluginManager.class);
	
	/** 
	 * Singleton instance
	 */
	private static PluginManager pluginManager;
	
	/**
	 * Contains mapping between plugin name and the instance of its declaration
	 * class 
	 */
	private Map<String, PluginDeclaration> availablePlugins;
	
	/**
	 * Retrieve the PluginDeclaration class representing the plugin which
	 * have the name provided as input
	 * @param pluginName the name of the plugin
	 * @return the PluginDeclaration
	 * @throws PluginNotFoundException if the plugin is not available
	 */
	public static PluginDeclaration getPluginDeclaration(String pluginName) throws PluginNotFoundException {
		logger.debug(String.format("Trying to instantiate a Plugin named %s",
				pluginName));
		PluginDeclaration pluginDeclaration = PluginManager.getInstance()
				.getPlugin(pluginName);
		if (pluginDeclaration == null) {
			throw new PluginNotFoundException();
		}
		return pluginDeclaration;
	}
	
	public static Plugin<? extends PluginDeclaration> instantiatePlugin(
			String pluginName) throws InputsNullException,
			PluginNotFoundException {

		PluginDeclaration pluginDeclaration = getPluginDeclaration(pluginName);

		// Retrieving the plugin instance class to be run from PluginDeclaration
		Class<? extends Plugin<? extends PluginDeclaration>> plugin = pluginDeclaration
				.getPluginImplementation();
		logger.debug(String.format(
				"The class which will run the execution will be %s",
				plugin.getName()));

		// Retrieve the Constructor of Plugin to instantiate it
		@SuppressWarnings("rawtypes")
		Class[] argTypes = { pluginDeclaration.getClass()};
		
		// Creating the Argument to pass to constructor
		Object[] arguments = { pluginDeclaration};
		
		
		// logger.debug(String.format("Plugin named %s once instantiated will be identified by the UUID %s",
		// name, executionIdentifier));
		Constructor<? extends Plugin<? extends PluginDeclaration>> executorPluginConstructor;
		try {
			executorPluginConstructor = plugin.getDeclaredConstructor(argTypes);
		} catch (Exception e) {
			
			/* Maintaining backward compatibility */
			argTypes = new Class[2];
			argTypes[0] = pluginDeclaration.getClass();
			argTypes[1] = Persistence.class;
			
			try {
				executorPluginConstructor = plugin.getDeclaredConstructor(argTypes);
				arguments = new Object[2];
				arguments[0] = pluginDeclaration;
				arguments[1] = null;
			} catch (Exception e1) {
				throw new PluginNotFoundException();
			}
		}

		// Instancing the plugin
		Plugin<? extends PluginDeclaration> instantiatedPlugin;
		try {
			instantiatedPlugin = executorPluginConstructor
					.newInstance(arguments);
		} catch (Exception e) {
			throw new PluginNotFoundException();
		}
		logger.debug(String
				.format("Plugin named %s has been instantiated", pluginName));

		return instantiatedPlugin;
	}
	
	/**
	 * Get the singleton instance of {@link #PluginManager}.
	 * The first time this function is invoked the instance is null
	 * so it is created. Otherwise the already created instance is returned
	 * @return singleton instance of {@link #PluginManager}
	 */
	public static PluginManager getInstance(){
		if(pluginManager== null){
			pluginManager = new PluginManager();
		}
		return pluginManager;
	}
	
	/**
	 * Used by {@link #getInstance()} function check the available plugin on classpath
	 * and add them on {@link #availablePlugins}
	 */
	protected PluginManager(){
		logger.debug("Loading plugins available on classpath");
		this.availablePlugins = new HashMap<String, PluginDeclaration>();
		ServiceLoader<PluginDeclaration> serviceLoader = ServiceLoader.load(PluginDeclaration.class);
		for (PluginDeclaration pluginDeclaration : serviceLoader) {
			try {
				logger.debug(String.format("%s plugin found", pluginDeclaration.getName()));
				pluginDeclaration.init();
				String name = pluginDeclaration.getName();
				this.availablePlugins.put(name, pluginDeclaration);
			} catch (Exception e) {
				logger.debug(String.format("%s not initialized correctly. It will not be used", pluginDeclaration.getName()));
			}
		}
	}
	
	/**
	 * 
	 * @param name The name of the plugin
	 * @return The plugin declaration if available, null otherwise
	 */
	public PluginDeclaration getPlugin(String name){
		return this.availablePlugins.get(name);
	}
	
	/**
	 * @return the availablePlugins
	 */
	public Map<String, PluginDeclaration> getAvailablePlugins() {
		return availablePlugins;
	}
}
