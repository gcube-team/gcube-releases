package org.gcube.common.homelibrary.jcr;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.ExternalResourceLinkPlugin;
import org.gcube.common.homelibrary.home.workspace.exceptions.ExternalResourcePluginNotFoundException;

public class JCRExternalResourcePluginManager {
	
	 private static Map<String,ExternalResourceLinkPlugin> plugins;
	 
	 public synchronized static void initialize() throws InternalErrorException {
		  
		 if(plugins == null) {
			  plugins = new HashMap<String, ExternalResourceLinkPlugin>();
			  ServiceLoader<ExternalResourceLinkPlugin> serviceLoader = ServiceLoader.load(ExternalResourceLinkPlugin.class);
			  for(ExternalResourceLinkPlugin plugin : serviceLoader) {
				  
				  if(plugins.containsKey(plugin.getPluginName()))
					  throw new InternalErrorException("Plugin" 
							  + plugin.getPluginName() + "already loaded");
				  plugins.put(plugin.getPluginName(), plugin);
			  }
		  }
	 }
	 
	 public static ExternalResourceLinkPlugin getPlugin(String name) throws ExternalResourcePluginNotFoundException {
		 ExternalResourceLinkPlugin plugin = plugins.get(name);
		 if (plugin == null)
			 throw new ExternalResourcePluginNotFoundException("Plugin " + name + " not foud");
		 return plugin;
	 }
}
