package org.gcube.rest.index.service.inject;

import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.filter.ResourceFilter;
import org.gcube.rest.commons.inject.ResourcesFoldername;
import org.gcube.rest.commons.inject.StatefulResourceClass;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.commons.resourcefile.ResourceFileUtilsJSON;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
import org.gcube.rest.index.service.IndexClientWrapper;
import org.gcube.rest.index.service.IndexService;
import org.gcube.rest.index.service.resources.IndexResourceFactory;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class IndexServiceModule implements Module {
	
	private String resourcesFoldername = "/tmp/resources";
	
	public IndexServiceModule() {
		super();
		this.resourcesFoldername = Constants.DEFAULT_RESOURCES_FOLDERNAME;
	}

	public IndexServiceModule(String resourcesFoldername) {
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
			.bind(new TypeLiteral<Class<IndexResource>>(){})
			.annotatedWith(StatefulResourceClass.class)
			.toInstance(IndexResource.class);
		
		binder
			.bind(new TypeLiteral<IndexDiscovererAPI<IndexResource>>(){})
			.to(IndexDiscoverer.class)
			.asEagerSingleton();
		
		//binder.bind(new TypeLiteral<Class<IndexResource>>(){}).toInstance(IndexResource.class);
//		binder.bind(new TypeLiteral<IndexDiscovererAPI<IndexResource>>(){})
//			.toProvider(IndexDiscovererClientProvider.class)
//			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFilter<IndexResource>>(){})
			.to(new TypeLiteral<ResourceFilter<IndexResource>>(){})
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFileUtils<IndexResource>>(){})
			.to(new TypeLiteral<ResourceFileUtilsJSON<IndexResource>>(){})
			.asEagerSingleton();
		
		
		binder
			.bind(new TypeLiteral<ResourceFactory<IndexResource>>(){})
			.to(new TypeLiteral<IndexResourceFactory>(){})
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<ResourcePublisher<IndexResource>>(){})
			.to(new TypeLiteral<PublisherISimpl<IndexResource>>(){})
			.asEagerSingleton();
	
		binder
			.bind(new TypeLiteral<IResourceHarvester<IndexResource>>(){})
			.to(new TypeLiteral<ResourceHarvester<IndexResource>>(){})
			.asEagerSingleton();
		
		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(IndexClient.Builder.class)
			.toProvider(IndexClientBuilderProvider.class);
		
		binder
			.bind(IndexClientWrapper.class)
			.toProvider(IndexClientWrapperProvider.class);
		
		binder
			.bind(IndexResourceFactory.class)
			.asEagerSingleton();
		
		binder
			.bind(IndexService.class)
			.asEagerSingleton();
	}

}
