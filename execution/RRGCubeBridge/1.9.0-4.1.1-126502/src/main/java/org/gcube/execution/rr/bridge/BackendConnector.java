package org.gcube.execution.rr.bridge;

//import com.google.inject.persist.jpa.JpaPersistModule;
//import org.gcube.rest.commons.db.app.ApplicationInitializer;
//import org.gcube.rest.commons.db.dao.app.ResourceModelDao;
//import org.gcube.rest.commons.db.dao.app.RunInstanceModelDao;
//import org.gcube.rest.commons.db.dao.app.SerInstanceModelDao;
//import org.gcube.rest.commons.information.collector.rr.RRInformationCollector;
//import org.gcube.rest.commons.publisher.resourceregistry.PublisherRRimpl;
import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class BackendConnector {

	private static Injector injector;
	
	static {
		injector = Guice.createInjector(
//				new JpaPersistModule("myapp-db"),
				new AbstractModule() {
//					
//					@Override
//					protected void configure() {
//						bind(ResourceModelDao.class);
//						bind(RunInstanceModelDao.class);
//						bind(SerInstanceModelDao.class);
//						bind(InformationCollector.class).to(RRInformationCollector.class);
//						
//						bind(new TypeLiteral<ResourcePublisher<GeneralResource>>(){})
//							.to(new TypeLiteral<PublisherRRimpl<GeneralResource>>(){});
//					}
					
					@Override
					protected void configure() {
						bind(InformationCollector.class).to(ISInformationCollector.class);
						
						bind(new TypeLiteral<ResourcePublisher<GeneralResource>>(){})
							.to(new TypeLiteral<PublisherISimpl<GeneralResource>>(){});
					}
				}
				);
		
//		ApplicationInitializer ai = injector.getInstance(ApplicationInitializer.class);
		
	}
	
	static InformationCollector newICollector(){
		return injector.getInstance(InformationCollector.class);
	}
	
	static ResourcePublisher<GeneralResource> newPublisher(){
		return injector.getInstance(Key.get(new TypeLiteral<ResourcePublisher<GeneralResource>>(){}));
	}
}
