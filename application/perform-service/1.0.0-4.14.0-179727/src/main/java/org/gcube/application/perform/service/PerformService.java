package org.gcube.application.perform.service;

import javax.ws.rs.ApplicationPath;

import org.gcube.application.perform.service.engine.Importer;
import org.gcube.application.perform.service.engine.MappingManager;
import org.gcube.application.perform.service.engine.PerformanceManager;
import org.gcube.application.perform.service.engine.impl.ImporterImpl;
import org.gcube.application.perform.service.engine.impl.MappingManagerImpl;
import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.rest.Import;
import org.gcube.application.perform.service.rest.Mappings;
import org.gcube.application.perform.service.rest.Performance;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationPath(ServiceConstants.SERVICE_NAME)
public class PerformService extends ResourceConfig{

	private static final Logger log= LoggerFactory.getLogger(PerformService.class);
	
	
	public PerformService() {
		super();		
		
		
		AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {                    
              bind(MappingManagerImpl.class).to(MappingManager.class);
              bind(ImporterImpl.class).to(Importer.class);
              bind(PerformanceManagerImpl.class).to(PerformanceManager.class);
            }
        };
		register(binder);
		registerClasses(Mappings.class);
		registerClasses(Import.class);
		registerClasses(Performance.class);
//		packages("org.gcube.application.perform.service.rest");
		register(MultiPartFeature.class);
	}
	
	
	 
}
