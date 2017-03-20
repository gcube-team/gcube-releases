package org.gcube.opensearch.opensearchdatasource.inject;

import org.gcube.opensearch.opensearchdatasource.resources.OpenSearchDataSourceResourceFactory;
import org.gcube.opensearch.opensearchdatasource.service.OpenSearchOperator;
import org.gcube.opensearch.opensearchdatasource.service.OpenSearchService;
import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.filter.ResourceFilter;
import org.gcube.rest.commons.inject.ResourcesFoldername;
import org.gcube.rest.commons.inject.StatefulResourceClass;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.commons.resourcefile.ResourceFileUtilsJSON;
import org.gcube.rest.opensearch.common.discover.OpenSearchDataSourceDiscoverer;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class OpenSearchServiceModule implements Module {
	
	private String resourcesFoldername = "/tmp/resources";
	
	public OpenSearchServiceModule() {
		super();
		
	}

	public OpenSearchServiceModule(String resourcesFoldername) {
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
			.bind(new TypeLiteral<Class<OpenSearchDataSourceResource>>(){})
			.annotatedWith(StatefulResourceClass.class)
			.toInstance(OpenSearchDataSourceResource.class);
		
		
		binder
			.bind(new TypeLiteral<OpenSearchDiscovererAPI<OpenSearchDataSourceResource>>(){})
			.to(OpenSearchDataSourceDiscoverer.class)
			.asEagerSingleton();
		
//		binder.bind(new TypeLiteral<Class<IndexResource>>(){}).toInstance(IndexResource.class);
		
//		binder.bind(new TypeLiteral<IndexDiscovererAPI<IndexResource>>(){})
//			.toProvider(IndexDiscovererClientProvider.class)
//			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFilter<OpenSearchDataSourceResource>>(){})
			.to(new TypeLiteral<ResourceFilter<OpenSearchDataSourceResource>>(){})
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFileUtils<OpenSearchDataSourceResource>>(){})
			.to(new TypeLiteral<ResourceFileUtilsJSON<OpenSearchDataSourceResource>>(){})
			.asEagerSingleton();
		
		
		binder
			.bind(new TypeLiteral<ResourceFactory<OpenSearchDataSourceResource>>(){})
			.to(new TypeLiteral<OpenSearchDataSourceResourceFactory>(){})
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<ResourcePublisher<OpenSearchDataSourceResource>>(){})
			.to(new TypeLiteral<PublisherISimpl<OpenSearchDataSourceResource>>(){})
			.asEagerSingleton();
	
		binder
			.bind(new TypeLiteral<IResourceHarvester<OpenSearchDataSourceResource>>(){})
			.to(new TypeLiteral<ResourceHarvester<OpenSearchDataSourceResource>>(){})
			.asEagerSingleton();
		
		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
//		binder.bind(IndexClient.Builder.class).toProvider(IndexClientBuilderProvider.class);
//		binder.bind(IndexClientWrapper.class).toProvider(IndexClientWrapperProvider.class);
		
		binder
			.bind(OpenSearchDataSourceResourceFactory.class)
			.asEagerSingleton();
		
		binder
			.bind(OpenSearchOperator.class)
			.asEagerSingleton();
		
		binder
			.bind(OpenSearchService.class)
			.asEagerSingleton();
		
		binder
			.bind(InformationCollector.class)
			.to(ISInformationCollector.class).asEagerSingleton();
		
	}

}

