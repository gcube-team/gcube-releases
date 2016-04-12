package org.gcube.search.sru.db.service.inject;


import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.filter.ResourceFilter;
import org.gcube.rest.commons.inject.ResourcesFoldername;
import org.gcube.rest.commons.inject.StatefulResourceClass;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.commons.resourcefile.ResourceFileUtilsJSON;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.search.sru.db.common.Constants;
import org.gcube.search.sru.db.common.discoverer.SruDBDiscoverer;
import org.gcube.search.sru.db.common.discoverer.SruDBDiscovererAPI;
import org.gcube.search.sru.db.common.resources.SruDBResource;
import org.gcube.search.sru.db.service.resources.SruDBResourceFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class SruDBServiceModule implements Module  {
private String resourcesFoldername = "/tmp/resources";
	
	public SruDBServiceModule() {
		super();
		this.resourcesFoldername = Constants.DEFAULT_RESOURCES_FOLDERNAME;
	}

	public SruDBServiceModule(String resourcesFoldername) {
		super();
		this.resourcesFoldername = resourcesFoldername;
	}
	
	@Override
	public void configure(Binder binder) {
		binder
			.bind(String.class)
				.annotatedWith(ResourcesFoldername.class)
				.toInstance(this.resourcesFoldername);

	
		binder
			.bind(new TypeLiteral<Class<SruDBResource>>(){})
				.annotatedWith(StatefulResourceClass.class)
				.toInstance(SruDBResource.class);
		
		binder
			.bind(new TypeLiteral<SruDBDiscovererAPI<SruDBResource>>(){})
			.to(SruDBDiscoverer.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFilter<SruDBResource>>(){})
			.to(new TypeLiteral<ResourceFilter<SruDBResource>>(){})
			.asEagerSingleton();
	
		binder
			.bind(new TypeLiteral<IResourceFileUtils<SruDBResource>>(){})
			.to(new TypeLiteral<ResourceFileUtilsJSON<SruDBResource>>(){})
			.asEagerSingleton();
	
		binder
			.bind(new TypeLiteral<ResourcePublisher<SruDBResource>>(){})
			.to(new TypeLiteral<PublisherISimpl<SruDBResource>>(){})
			.asEagerSingleton();

		binder
			.bind(new TypeLiteral<IResourceHarvester<SruDBResource>>(){})
			.to(new TypeLiteral<ResourceHarvester<SruDBResource>>(){})
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<ResourceFactory<SruDBResource>>(){})
			.to(new TypeLiteral<SruDBResourceFactory>(){})
			.asEagerSingleton();
	
	}
}
