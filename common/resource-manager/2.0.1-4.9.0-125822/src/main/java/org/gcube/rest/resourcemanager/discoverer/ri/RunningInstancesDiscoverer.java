package org.gcube.rest.resourcemanager.discoverer.ri;

import java.util.Set;

public interface RunningInstancesDiscoverer {
	public Set<String> discoverRunningInstances(String serviceClass, String serviceName, String endpointKey, String scope);
}
