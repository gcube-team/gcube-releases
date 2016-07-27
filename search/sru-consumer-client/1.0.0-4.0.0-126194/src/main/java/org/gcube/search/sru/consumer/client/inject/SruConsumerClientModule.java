package org.gcube.search.sru.consumer.client.inject;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.gcube.search.sru.consumer.common.discoverer.SruConsumerDiscoverer;
import org.gcube.search.sru.consumer.common.discoverer.SruConsumerDiscovererAPI;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class SruConsumerClientModule implements Module {

	public SruConsumerClientModule() {
	}
	
	public void configure(Binder binder) {

		binder
			.bind(new TypeLiteral<SruConsumerDiscovererAPI<SruConsumerResource>>() {})
			.to(SruConsumerDiscoverer.class)
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceHarvester<SruConsumerResource>>() {})
			.to(new TypeLiteral<ResourceHarvester<SruConsumerResource>>() {})
			.asEagerSingleton();
		
		binder
			.bind(InformationCollector.class)
			.to(ISInformationCollector.class)
			.asEagerSingleton();

	}
}
