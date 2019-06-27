package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CollectorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectorsManagerImpl implements CollectorsManager {

	
	private static final Logger log= LoggerFactory.getLogger(CollectorsManagerImpl.class);
	
	
	private ServiceLoader<CollectorPlugin> collectorPluginsLoader   = null;
	
	private ConcurrentHashMap<String,CollectorPlugin> availablePlugins=new ConcurrentHashMap<>();
	
	@Inject 
	private Infrastructure infrastructure;
	
	
	@PostConstruct
	public void post() {
		//load plugins
		log.debug("Loading collector plugins...");
		collectorPluginsLoader=ServiceLoader.load(CollectorPlugin.class);
		for(CollectorPlugin plugin:collectorPluginsLoader) {			
			log.debug("Loading {} ",plugin.getClass());
			log.debug("Descriptor {} ",plugin.getDescriptor());
			availablePlugins.put(plugin.getDescriptor().getName(), plugin);
		}
		log.trace("Loaded {} collector plugins ",availablePlugins.size());	
		
		log.trace("Static initialization...");
		for(Entry<String,CollectorPlugin> entry:availablePlugins.entrySet()) {
			log.debug("Static initialization for : {} ",entry.getKey());
			try {
				entry.getValue().init();
			}catch(Throwable t) {
				log.error("Unexpected exception while initializing {} ",entry.getKey(),t);
			}
		}
	}
	
	@Override
	public Set<String> getAvailableCollectors() {
		return availablePlugins.keySet();
	}

	@Override
	public CollectorPlugin<?> getPluginById(String collectorId) throws CollectorNotFound {
		if(availablePlugins.containsKey(collectorId)) return availablePlugins.get(collectorId);
		else throw new CollectorNotFound("Collector plugin "+collectorId+" not available.");
	}

	
	
	
	@Override
	public void initInScope() throws InternalError {
		log.trace("Initialization under scope {} ",infrastructure.getCurrentContext());
		for(Entry<String,CollectorPlugin> entry:availablePlugins.entrySet()) {
			log.debug("Scope initialization for : {} ",entry.getKey());
			try {
				entry.getValue().initInScope();
			}catch(Throwable t) {
				log.error("Unexpected exception while initializing {} ",entry.getKey(),t);
			}
		}
	}

}
