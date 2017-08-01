package org.gcube.data.tm.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.tmf.api.Plugin;
import org.gcube.data.tmf.api.PluginLifecycle;

/**
 * Manages service plugins.
 * 
 * @author Fabio Simeoni
 *
 */
public class PluginManager {

	private static GCUBELog log = new GCUBELog(PluginManager.class);
	
	private static ServiceLoader<Plugin> loader;
	private static Map<String,Plugin> plugins = new HashMap<String, Plugin>();
	
	/**
	 * Creates a new instance, installing all the plugins found on the classpath.
	 */
	public PluginManager() {
		
		if (loader ==null) {
			
			loader=ServiceLoader.load(Plugin.class);
			
			for (Plugin plugin : loader)
				register(plugin);
		}
	}
	
	public void register(Plugin plugin) {
		
		if (plugin.name()==null)
			log.error("plugin "+plugin.getClass().getSimpleName()+" has a null name");
		
		if (plugin.name()==null)
			log.warn("plugin "+plugin.getClass().getSimpleName()+" has a null description");
		
		if (plugin.binder()==null)
			log.error("plugin "+plugin.name()+" binder is null");
		
		log.info("registering plugin "+plugin.name());
		
		if (plugin instanceof PluginLifecycle)
		try {
			((PluginLifecycle) plugin).start(new PluginEnvironment());
		}
		catch(Throwable e) {
			log.error("cannot initialise plugin",e);
		}
		
		plugins.put(plugin.name(),plugin);
	}
	
	/**
	 * Returns the installed plugins, indexed by name.
	 * @return the plugins
	 */
	public Map<String,Plugin> plugins() {
		return new HashMap<String, Plugin>(plugins);
	}
	
	/**
	 * Stops all plugins.
	 */
	public void stop() {
	
		PluginManager manager = new PluginManager();
		for (Plugin plugin : manager.plugins().values())
			stop(plugin);
	
	}
	
	/**
	 * Stops a given plugin
	 * 
	 * @param plugin the plugin
	 */
	public void stop(Plugin plugin) {
		
			if (plugin instanceof PluginLifecycle)
				((PluginLifecycle) plugin).stop(new PluginEnvironment());	
	}
	
}
