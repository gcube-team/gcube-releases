package org.gcube.search.sru.search.adapter.service.inject;


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
import org.gcube.search.sru.search.adapter.commons.Constants;
import org.gcube.search.sru.search.adapter.commons.discoverer.SruSearchAdapterDiscoverer;
import org.gcube.search.sru.search.adapter.commons.discoverer.SruSearchAdapterDiscovererAPI;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;
import org.gcube.search.sru.search.adapter.service.resources.SruSearchAdapterResourceFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class SruSearchAdapterServiceModule implements Module  {
	private String resourcesFoldername = "/tmp/resources";
	
	public SruSearchAdapterServiceModule() {
		super();
		this.resourcesFoldername = Constants.DEFAULT_RESOURCES_FOLDERNAME;
	}

	public SruSearchAdapterServiceModule(String resourcesFoldername) {
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
			.bind(new TypeLiteral<Class<SruSearchAdapterResource>>(){})
				.annotatedWith(StatefulResourceClass.class)
				.toInstance(SruSearchAdapterResource.class);
		
		binder
			.bind(new TypeLiteral<SruSearchAdapterDiscovererAPI<SruSearchAdapterResource>>(){})
			.to(SruSearchAdapterDiscoverer.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFilter<SruSearchAdapterResource>>(){})
			.to(new TypeLiteral<ResourceFilter<SruSearchAdapterResource>>(){})
			.asEagerSingleton();
	
		binder
			.bind(new TypeLiteral<IResourceFileUtils<SruSearchAdapterResource>>(){})
			.to(new TypeLiteral<ResourceFileUtilsJSON<SruSearchAdapterResource>>(){})
			.asEagerSingleton();
	
		binder
			.bind(new TypeLiteral<ResourcePublisher<SruSearchAdapterResource>>(){})
			.to(new TypeLiteral<PublisherISimpl<SruSearchAdapterResource>>(){})
			.asEagerSingleton();

		binder
			.bind(new TypeLiteral<IResourceHarvester<SruSearchAdapterResource>>(){})
			.to(new TypeLiteral<ResourceHarvester<SruSearchAdapterResource>>(){})
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<ResourceFactory<SruSearchAdapterResource>>(){})
			.to(new TypeLiteral<SruSearchAdapterResourceFactory>(){})
			.asEagerSingleton();
	
	}
}
