package org.gcube.search.inject;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.search.commons.SearchDiscoverer;
import org.gcube.rest.search.commons.SearchDiscovererAPI;

import com.google.inject.Binder;
import com.google.inject.Module;

public class SearchClientModule implements Module {

	public SearchClientModule() {
	}
	
	public void configure(Binder binder) {
		binder
			.bind(RunningInstancesDiscoverer.class)
			.to(RIDiscovererISimpl.class)
			.asEagerSingleton();
		
		binder
			.bind(SearchDiscovererAPI.class)
			.to(SearchDiscoverer.class)
			.asEagerSingleton();
		
	}
}
