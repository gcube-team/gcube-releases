package org.gcube.rest.index.client.inject;

import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class IndexClientModule implements Module {

	public IndexClientModule() {
	}

	@Override
	public void configure(Binder binder) {

		binder
			.bind(new TypeLiteral<IndexDiscovererAPI<IndexResource>>() {})
			.to(IndexDiscoverer.class)
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();

		binder
			.bind(new TypeLiteral<IResourceHarvester<IndexResource>>() {})
			.to(new TypeLiteral<ResourceHarvester<IndexResource>>() {})
			.asEagerSingleton();

	}

}
