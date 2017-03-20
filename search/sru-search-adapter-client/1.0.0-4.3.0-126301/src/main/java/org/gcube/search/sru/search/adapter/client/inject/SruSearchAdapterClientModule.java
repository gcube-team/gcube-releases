package org.gcube.search.sru.search.adapter.client.inject;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.gcube.search.sru.search.adapter.commons.discoverer.SruSearchAdapterDiscoverer;
import org.gcube.search.sru.search.adapter.commons.discoverer.SruSearchAdapterDiscovererAPI;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class SruSearchAdapterClientModule implements Module {

	public SruSearchAdapterClientModule() {
	}
	
	public void configure(Binder binder) {

		binder
			.bind(new TypeLiteral<SruSearchAdapterDiscovererAPI<SruSearchAdapterResource>>() {})
			.to(SruSearchAdapterDiscoverer.class)
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceHarvester<SruSearchAdapterResource>>() {})
			.to(new TypeLiteral<ResourceHarvester<SruSearchAdapterResource>>() {})
			.asEagerSingleton();
		
		binder
			.bind(InformationCollector.class)
			.to(ISInformationCollector.class)
			.asEagerSingleton();

	}
}
