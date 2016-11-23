package gr.uoa.di.madgik.rr.plugins;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.plugins.Plugin.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager 
{
	private static class PeriodicPluginTask implements Runnable
	{
		private Plugin plugin = null;
		
		
		private static final Logger logger = LoggerFactory
				.getLogger(PluginManager.class);
		
		public PeriodicPluginTask(Plugin plugin)
		{
			this.plugin = plugin;
		}
		
		@Override
		public void run() 
		{
			while(true)
			{
				try
				{
					//TimeUnit.SECONDS.sleep(20); TODO init wait mechanism needed
					plugin.execute(null);
					plugin.getPeriodUnit().sleep(plugin.getPeriod());
				}
				catch(Exception e)
				{
					logger.warn("Could not execute plugin", e);
					try { TimeUnit.SECONDS.sleep(30); } catch(Exception ee) { }
				}
			}
		}
	}
	
	private static class OneOffPluginTask implements Runnable
	{
		private Plugin plugin = null;
		
		private static final Logger logger = LoggerFactory
				.getLogger(OneOffPluginTask.class);
		
		public OneOffPluginTask(Plugin plugin)
		{
			this.plugin = plugin;
		}
		
		@Override
		public void run() 
		{
			boolean success = false;
			while(!success)
			{
				try
				{
					plugin.execute(null);
					success = true;
				}
				catch(Exception e)
				{
					logger.warn("Could not execute plugin", e);
					try { TimeUnit.SECONDS.sleep(30); } catch(Exception ee) { }
				}
			}
		}
}
	
	private static class PluginThreadFactory implements ThreadFactory
	{
		private long i = 0;
		
		@Override
		public Thread newThread(Runnable r) 
		{
			Thread t = new Thread(r, "PluginTask-"+ i++);
			t.setDaemon(true);
			return t;
		}
		
	}
		
	
	private static Map<Plugin.Type, TreeMap<Integer, Map<String, Plugin>>> plugins = new HashMap<Plugin.Type, TreeMap<Integer, Map<String, Plugin>>>();
	
	private static final Logger logger = LoggerFactory
			.getLogger(PluginManager.class);
	
	private static ExecutorService executor= Executors.newCachedThreadPool(new PluginThreadFactory());
	
	public synchronized static String registerPlugin(final Plugin plugin, int order)
	{
		if(plugins.get(plugin.getType()) == null) 
			plugins.put(plugin.getType(), new TreeMap<Integer, Map<String, Plugin>>());
		if(plugins.get(plugin.getType()).get(order) == null)
			plugins.get(plugin.getType()).put(order, new HashMap<String, Plugin>());
		String id = UUID.randomUUID().toString();
		plugins.get(plugin.getType()).get(order).put(id, plugin);
		logger.info( "Registered plugin: " + plugin.getClass().getName() + " of type " + plugin.getType() + ", order #" + order + 
				(plugin.getType() == Type.PERIODIC ? ", period " + plugin.getPeriod() + " " + plugin.getPeriodUnit() : ""));
		if(plugin.getType() == Type.PERIODIC)
			executor.execute(new PeriodicPluginTask(plugin));
		else if(plugin.getType() == Type.ONE_OFF)
			executor.execute(new OneOffPluginTask(plugin));
		return id;
	}
	
	public synchronized static Map<String, Plugin> getPluginsOfType(Plugin.Type type)
	{
		Map<Integer, Map<String, Plugin>> pluginsOfType = plugins.get(type);
		if(pluginsOfType == null) return new HashMap<String, Plugin>();
		LinkedHashMap<String, Plugin> ret = new LinkedHashMap<String, Plugin>();
		for(Map<String, Plugin> p : plugins.get(type).values())
			ret.putAll(p);
		return ret;
	}
	
	public synchronized static Map<String, Plugin> getPlugins()
	{
		Map<String, Plugin> ret = new HashMap<String, Plugin>();
		
		for(Map<Integer, Map<String, Plugin>> pluginsOfType : plugins.values())
		{
			for(Map<String, Plugin> p : pluginsOfType.values())
				ret.putAll(p);
		}
		return ret;
	}
	
	public synchronized static void executePluginsOfType(Plugin.Type type, Set<Class<?>> targets) throws ResourceRegistryException
	{
		Map<Integer, Map<String, Plugin>> pluginsOfType = plugins.get(type);
		if(pluginsOfType == null) return;
		for(Map<String, Plugin> orderedPlugins : plugins.get(type).values())
		{
			for(Plugin p : orderedPlugins.values())
			{
				logger.info( "Executing " + p.getType() + " plugin: " + p.getClass().getName());
				p.executePlugin(targets);
			}
		}
	}
}
