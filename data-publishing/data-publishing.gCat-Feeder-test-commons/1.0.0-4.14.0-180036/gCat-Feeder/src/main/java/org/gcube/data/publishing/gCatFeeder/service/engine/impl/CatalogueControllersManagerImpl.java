package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.catalogues.CataloguePlugin;
import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CataloguePluginNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogueControllersManagerImpl implements CatalogueControllersManager {

	private static final Logger log= LoggerFactory.getLogger(CatalogueControllersManagerImpl.class);
	
	
	private ServiceLoader<CataloguePlugin> cataloguePluginsLoader   = null;
	
	private ConcurrentHashMap<String,CataloguePlugin> availablePlugins=new ConcurrentHashMap<String, CataloguePlugin>();
	
	@Inject 
	private Infrastructure infrastructure;
	
	
	@PostConstruct
	public void post() {
		//load plugins
				log.debug("Loading catalogue plugins...");
				cataloguePluginsLoader=ServiceLoader.load(CataloguePlugin.class);
				for(CataloguePlugin plugin:cataloguePluginsLoader) {			
					log.debug("Loading {} ",plugin.getClass());
					log.debug("Descriptor {} ",plugin.getDescriptor());
					availablePlugins.put(plugin.getDescriptor().getId(), plugin);
				}
				log.trace("Loaded {} catalogue plugins ",availablePlugins.size());
				
				log.trace("Static initialization...");
				for(Entry<String,CataloguePlugin> entry:availablePlugins.entrySet()) {
					log.debug("Static initialization for : {} ",entry.getKey());
					try {
						entry.getValue().init();
					}catch(Throwable t) {
						log.error("Unexpected exception while initializing {} ",entry.getKey(),t);
					}
				}
				
	}
	
	
	
	
	@Override
	public Set<String> getAvailableControllers() {
		return availablePlugins.keySet();
	}

	@Override
	public CataloguePlugin getPluginById(String pluginId) throws CataloguePluginNotFound {
		if(availablePlugins.containsKey(pluginId)) return availablePlugins.get(pluginId);
		else throw new CataloguePluginNotFound("Catalogue plugin "+pluginId+" not available.");
	}

	

	@Override
	public void initInScope() throws InternalError {
		log.trace("Initialization under scope {} ",infrastructure.getCurrentContext());
		for(Entry<String,CataloguePlugin> entry:availablePlugins.entrySet()) {
			log.debug("Scope initialization for : {} ",entry.getKey());
			try {
				entry.getValue().initInScope();
			}catch(Throwable t) {
				log.error("Unexpected exception while initializing {} ",entry.getKey(),t);
			}
		}
	}
	
}
