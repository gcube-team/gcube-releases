package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.inject.Singleton;

import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.AbstractPlugin;
import org.gcube.data.transfer.plugin.AbstractPluginFactory;
import org.gcube.data.transfer.plugin.ExecutionReport;
import org.gcube.data.transfer.plugin.fails.PluginException;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginExecutionException;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginNotFoundException;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PluginManagerImpl implements PluginManager {

	private static ServiceLoader<AbstractPluginFactory> abstractFactoryLoader   = null;
	
	private static Map<String,PluginDescription> installedPlugins=null;
	
	
	static{
		abstractFactoryLoader=ServiceLoader.load(AbstractPluginFactory.class);
		
		
		
	}
	
	@Override
	public Map<String, PluginDescription> getInstalledPlugins() {
		return init();
	}
	
	
	@Synchronized
	private static Map<String, PluginDescription> init() {
		if(installedPlugins==null){
			Map<String,PluginDescription> toSet=new HashMap<String,PluginDescription>(); 
			log.trace("Loading plugins descriptors..");
			for(AbstractPluginFactory factory:abstractFactoryLoader){
				log.debug("loading {}, {} ",factory.getID(),factory.getDescription());
				toSet.put(factory.getID(), new PluginDescription(factory.getID(), factory.getDescription(), factory.getParameters()));
			}
			installedPlugins=toSet;
		}
		
		return installedPlugins;
	}

	@Override
	public ExecutionReport execute(PluginInvocation invocation)
			throws PluginNotFoundException, PluginExecutionException {
		log.debug("Executing invocation {} ",invocation);
				
		AbstractPluginFactory factory=getFactory(invocation.getPluginId());
		log.debug("Loaded factory {} ",factory.getClass());
		log.debug("Checkign invocation .. ");
		try{
			factory.checkInvocation(invocation);
			AbstractPlugin plugin=factory.createWorker(invocation);
			return plugin.execute();
		}catch(PluginException e){
			throw new PluginExecutionException(e.getMessage(),e);
		}
	}

	
	private AbstractPluginFactory getFactory(String pluginId) throws PluginNotFoundException{
		for(AbstractPluginFactory factory:abstractFactoryLoader){
			if(factory.getID().equals(pluginId)) return factory;
		}
		throw new PluginNotFoundException("Plugin with ID "+pluginId+" not found");
	}
	
}
