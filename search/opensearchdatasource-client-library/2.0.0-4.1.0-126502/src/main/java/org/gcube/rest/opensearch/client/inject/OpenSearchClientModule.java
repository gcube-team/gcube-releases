package org.gcube.rest.opensearch.client.inject;

import org.gcube.rest.opensearch.common.discover.OpenSearchDataSourceDiscoverer;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class OpenSearchClientModule implements Module {

	public OpenSearchClientModule() {
	}

	@Override
	public void configure(Binder binder) {

		binder
			.bind(new TypeLiteral<OpenSearchDiscovererAPI<OpenSearchDataSourceResource>>() {})
			.to(OpenSearchDataSourceDiscoverer.class)
			.asEagerSingleton();

		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(new TypeLiteral<IResourceHarvester<OpenSearchDataSourceResource>>() {})
			.to(new TypeLiteral<ResourceHarvester<OpenSearchDataSourceResource>>() {})
			.asEagerSingleton();
		
		binder
			.bind(InformationCollector.class)
			.to(ISInformationCollector.class)
			.asEagerSingleton();

	}

}
