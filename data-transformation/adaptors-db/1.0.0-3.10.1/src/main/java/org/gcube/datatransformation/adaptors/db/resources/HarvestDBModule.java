package org.gcube.datatransformation.adaptors.db.resources;
/*package org.gcube.application.framework.harvesting.db.resources;

import org.gcube.application.framework.harvesting.common.db.discover.DBPropsDiscoverer;
import org.gcube.application.framework.harvesting.common.db.discover.DBPropsDiscovererAPI;
import org.gcube.application.framework.harvesting.common.db.xmlobjects.DBProps;
import org.gcube.application.framework.harvesting.db.HarvestDB;
import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.filter.ResourceFilter;
import org.gcube.rest.commons.inject.EndpointKey;
import org.gcube.rest.commons.inject.GcubeScope;
import org.gcube.rest.commons.inject.ResourceClass;
import org.gcube.rest.commons.inject.ResourceNamePref;
import org.gcube.rest.commons.inject.ResourcesFoldername;
import org.gcube.rest.commons.inject.ServiceClass;
import org.gcube.rest.commons.inject.ServiceName;
import org.gcube.rest.commons.inject.StatefulResourceClass;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.commons.resourcefile.ResourceFileUtilsJSON;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.search.sru.db.service.inject.SruDBDiscoverer;
import org.gcube.search.sru.db.service.inject.SruDBDiscovererAPI;
import org.gcube.search.sru.db.service.inject.SruDBResource;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class HarvestDBModule implements Module {
	
	private String scope = "DEFAULT-SCOPE";
	private String resourcesFoldername = "/tmp/resources";
	
	public HarvestDBModule() {
		super();
		this.resourcesFoldername = "/tmp/resources";
	}

	public HarvestDBModule(String scope, String resourcesFoldername) {
		super();
		this.scope = scope;
		this.resourcesFoldername = resourcesFoldername;
	}



	@Override
	public void configure(Binder binder) {
		binder
		.bind(String.class)
			.annotatedWith(ResourcesFoldername.class)
			.toInstance(this.resourcesFoldername);
		
		binder
		.bind(new TypeLiteral<Class<DBProps>>(){})
			.annotatedWith(StatefulResourceClass.class)
			.toInstance(DBProps.class);
		
		binder
			.bind(new TypeLiteral<DBPropsDiscovererAPI<DBProps>>(){})
			.to(DBPropsDiscoverer.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceFilter<SruDBResource>>(){})
			.to(new TypeLiteral<ResourceFilter<SruDBResource>>(){})
			.asEagerSingleton();
		
//		binder.bind(String.class)
//			.annotatedWith(ResourceClass.class)
//			.toInstance(Constants.RESOURCE_CLASS);
//	
//		binder.bind(String.class)
//			.annotatedWith(ResourceNamePref.class)
//			.toInstance(Constants.RESOURCE_CLASS);
//		
//		binder.bind(String.class)
//			.annotatedWith(EndpointKey.class)
//			.toInstance(Constants.ENDPOINT_KEY);
//		
//		binder.bind(String.class)
//			.annotatedWith(ServiceClass.class)
//			.toInstance(Constants.SERVICE_CLASS);
//		
//		binder.bind(String.class)
//			.annotatedWith(ServiceName.class)
//			.toInstance(Constants.SERVICE_NAME);
		
		binder.bind(String.class)
			.annotatedWith(ResourcesFoldername.class)
			.toInstance(this.resourcesFoldername);
	
		
		binder.bind(new TypeLiteral<Class<DBProps>>(){})
			.annotatedWith(StatefulResourceClass.class)
			.toInstance(DBProps.class);
		
		//binder.bind(new TypeLiteral<Class<IndexResource>>(){}).toInstance(IndexResource.class);
		
//		binder.bind(new TypeLiteral<IndexDiscovererAPI<DBProps>>(){})
//			.to(IndexDiscoverer.class)
//			.asEagerSingleton();
		
//		binder.bind(new TypeLiteral<IndexDiscovererAPI<IndexResource>>(){})
//			.toProvider(IndexDiscovererClientProvider.class)
//			.asEagerSingleton();
		
		binder.bind(new TypeLiteral<IResourceFilter<DBProps>>(){})
			.to(new TypeLiteral<ResourceFilter<DBProps>>(){})
			.asEagerSingleton();
		
		binder.bind(new TypeLiteral<IResourceFileUtils<DBProps>>(){})
			.to(new TypeLiteral<ResourceFileUtilsJSON<DBProps>>(){})
			.asEagerSingleton();
		
		
		binder.bind(new TypeLiteral<ResourceFactory<DBProps>>(){})
			.to(new TypeLiteral<DBPropsFactory>(){})
			.asEagerSingleton();
		
//		binder.bind(new TypeLiteral<ResourcePublisher<DBProps>>(){})
//			.to(new TypeLiteral<PublisherISimpl<DBProps>>(){})
//			.asEagerSingleton();
	
		binder.bind(new TypeLiteral<IResourceHarvester<DBProps>>(){})
			.to(new TypeLiteral<ResourceHarvester<DBProps>>(){})
			.asEagerSingleton();
		
//		binder.bind(RunningInstancesDiscoverer.class)
//			.to(RIDiscovererISimpl.class)
//			.asEagerSingleton();
		
//		binder.bind(IndexClient.Builder.class).toProvider(IndexClientBuilderProvider.class);
//		binder.bind(IndexClientWrapper.class).toProvider(IndexClientWrapperProvider.class);
		
		binder.bind(DBPropsFactory.class).asEagerSingleton();
		binder.bind(HarvestDB.class).asEagerSingleton();
	}

}

*/