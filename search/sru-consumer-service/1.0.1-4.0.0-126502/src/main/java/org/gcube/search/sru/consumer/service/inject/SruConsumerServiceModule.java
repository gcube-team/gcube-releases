package org.gcube.search.sru.consumer.service.inject;

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
import org.gcube.search.sru.consumer.common.Constants;
import org.gcube.search.sru.consumer.common.discoverer.SruConsumerDiscoverer;
import org.gcube.search.sru.consumer.common.discoverer.SruConsumerDiscovererAPI;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.gcube.search.sru.consumer.service.resources.SruConsumerResourceFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class SruConsumerServiceModule implements Module {
	private String resourcesFoldername = "/tmp/resources";

	public SruConsumerServiceModule() {
		super();
		this.resourcesFoldername = Constants.DEFAULT_RESOURCES_FOLDERNAME;
	}

	public SruConsumerServiceModule(String resourcesFoldername) {
		super();
		this.resourcesFoldername = resourcesFoldername;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(String.class).annotatedWith(ResourcesFoldername.class)
				.toInstance(this.resourcesFoldername);

		binder.bind(new TypeLiteral<Class<SruConsumerResource>>() {
		}).annotatedWith(StatefulResourceClass.class)
				.toInstance(SruConsumerResource.class);

		binder
			.bind(new TypeLiteral<SruConsumerDiscovererAPI<SruConsumerResource>>() {})
			.to(SruConsumerDiscoverer.class).asEagerSingleton();

		binder.bind(new TypeLiteral<IResourceFilter<SruConsumerResource>>() {
		}).to(new TypeLiteral<ResourceFilter<SruConsumerResource>>() {
		}).asEagerSingleton();

		binder.bind(new TypeLiteral<IResourceFileUtils<SruConsumerResource>>() {
		}).to(new TypeLiteral<ResourceFileUtilsJSON<SruConsumerResource>>() {
		}).asEagerSingleton();

		binder.bind(new TypeLiteral<ResourcePublisher<SruConsumerResource>>() {
		}).to(new TypeLiteral<PublisherISimpl<SruConsumerResource>>() {
		}).asEagerSingleton();

		binder.bind(new TypeLiteral<IResourceHarvester<SruConsumerResource>>() {
		}).to(new TypeLiteral<ResourceHarvester<SruConsumerResource>>() {
		}).asEagerSingleton();

		binder.bind(RunningInstancesDiscoverer.class)
				.to(RIDiscovererISimpl.class).asEagerSingleton();

		binder.bind(new TypeLiteral<ResourceFactory<SruConsumerResource>>() {
		}).to(new TypeLiteral<SruConsumerResourceFactory>() {
		}).asEagerSingleton();

	}
}
