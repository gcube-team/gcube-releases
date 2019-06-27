package org.gcube.data.publishing.gCatFeeder.service;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ConnectionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ExecutionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.FeederEngine;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.LocalConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Storage;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.CatalogueControllersManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.CollectorsManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.ExecutionManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.FeederEngineImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.InfrastructureUtilsImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.LiveEnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.LocalConfigurationImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.ConnectionManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.PersistenceManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.StorageImpl;
import org.gcube.data.publishing.gCatFeeder.service.rest.Capabilities;
import org.gcube.data.publishing.gCatFeeder.service.rest.Executions;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath(ServiceConstants.SERVICE_NAME)
public class GCatFeeder extends ResourceConfig{

	
	public GCatFeeder() {
		super();
		AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {                    
              bind(FeederEngineImpl.class).to(FeederEngine.class);
              bind(CatalogueControllersManagerImpl.class).to(CatalogueControllersManager.class).in(Singleton.class);
              bind(CollectorsManagerImpl.class).to(CollectorsManager.class).in(Singleton.class);
              bind(ExecutionManagerImpl.class).to(ExecutionManager.class).in(Singleton.class);
              bind(InfrastructureUtilsImpl.class).to(Infrastructure.class);
              bind(StorageImpl.class).to(Storage.class);
              bind(PersistenceManagerImpl.class).to(PersistenceManager.class);
              bind(ConnectionManagerImpl.class).to(ConnectionManager.class).in(Singleton.class);
              bind(LocalConfigurationImpl.class).to(LocalConfiguration.class).in(Singleton.class);
              bind(LiveEnvironmentConfiguration.class).to(EnvironmentConfiguration.class);
              
            }
        };
		register(binder);
		register(JacksonFeature.class);
		registerClasses(Executions.class);
		registerClasses(Capabilities.class); 
	}
	
	
	public GCatFeeder(AbstractBinder binder) {
		super();
		register(binder);
		register(JacksonFeature.class);
		registerClasses(Executions.class);
		registerClasses(Capabilities.class); 
	}
}
