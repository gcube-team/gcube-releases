package org.gcube.data.spd.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.stubs.UnsupportedCapabilityFault;
import org.gcube.data.spd.stubs.UnsupportedPluginFault;

public class PluginUtils {

	public static Collection<AbstractPlugin> getPluginsSubList(Collection<String> pluginsName, Map<String, AbstractPlugin> plugins) throws UnsupportedPluginFault{
		Set<AbstractPlugin> selectedPlugins = new HashSet<AbstractPlugin>(); 
		for (String pluginName: pluginsName)
			if (plugins.containsKey(pluginName)) selectedPlugins.add(plugins.get(pluginName));
			else throw new UnsupportedPluginFault();
		return selectedPlugins;
	}
	
	public static Collection<AbstractPlugin> getExtenderPlugins(Collection<AbstractPlugin> plugins) throws UnsupportedCapabilityFault{
		Set<AbstractPlugin> selectedPlugins = new HashSet<AbstractPlugin>(); 
		for (AbstractPlugin plugin: plugins)
			if (plugin.getExpansionInterface()!=null) selectedPlugins.add(plugin);
		if (selectedPlugins.size()==0) throw new  UnsupportedCapabilityFault();
		return selectedPlugins;
	}
	
	public static Collection<AbstractPlugin> getResolverPlugins(Collection<AbstractPlugin> plugins) throws UnsupportedCapabilityFault{
		Set<AbstractPlugin> selectedPlugins = new HashSet<AbstractPlugin>(); 
		for (AbstractPlugin plugin: plugins)
			if (plugin.getMappingInterface()!=null) selectedPlugins.add(plugin);
		if (selectedPlugins.size()==0) throw new  UnsupportedCapabilityFault();
		return selectedPlugins;
	}
	
}
