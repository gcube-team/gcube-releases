package org.gcube.rest.search.commons;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SearchDiscoverer implements SearchDiscovererAPI {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchDiscoverer.class);
	
	private RunningInstancesDiscoverer riDiscoverer;
	
	@Inject
	public SearchDiscoverer(RunningInstancesDiscoverer riDiscoverer) {
		this.riDiscoverer = riDiscoverer;
	}
	
	public Set<String> discoverSearchSystemRunninInstances(String scope) {
		Set<String> ris = this.riDiscoverer.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
		logger.info("found search system RIs : " + ris);
		
		return ris;
	}
	
}
