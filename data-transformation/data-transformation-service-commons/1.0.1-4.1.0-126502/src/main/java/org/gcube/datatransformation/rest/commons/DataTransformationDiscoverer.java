package org.gcube.datatransformation.rest.commons;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DataTransformationDiscoverer implements DataTransformationDiscovererAPI {

	private static final Logger logger = LoggerFactory.getLogger(DataTransformationDiscoverer.class);

	private RunningInstancesDiscoverer riDiscoverer;

	@Inject
	public DataTransformationDiscoverer(RunningInstancesDiscoverer riDiscoverer) {
		this.riDiscoverer = riDiscoverer;
	}

	public Set<String> discoverDataTransformationRunninInstances(String scope) {
		Set<String> ris = this.riDiscoverer.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY,
				scope);
		logger.info("found data transformation system RIs : " + ris);

		return ris;
	}
}
