package org.gcube.search.sru.db.client.inject;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.gcube.search.sru.db.common.discoverer.SruDBDiscoverer;
import org.gcube.search.sru.db.common.discoverer.SruDBDiscovererAPI;
import org.gcube.search.sru.db.common.resources.SruDBResource;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class SruDBClientModule implements Module {

	public SruDBClientModule() {
	}
	
	public void configure(Binder binder) {

		binder
			.bind(new TypeLiteral<SruDBDiscovererAPI<SruDBResource>>() {})
			.to(SruDBDiscoverer.class)
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceHarvester<SruDBResource>>() {})
			.to(new TypeLiteral<ResourceHarvester<SruDBResource>>() {})
			.asEagerSingleton();
		
		binder
			.bind(InformationCollector.class)
			.to(ISInformationCollector.class)
			.asEagerSingleton();

	}
}
