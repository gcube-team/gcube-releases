package org.gcube.rest.resourcemanager.is.discoverer.ri.icclient;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RIDiscovererISimpl implements RunningInstancesDiscoverer {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RIDiscovererISimpl.class);
	
	
	@Inject
	public RIDiscovererISimpl() {
	}
	
	@Override
	public Set<String> discoverRunningInstances(String serviceClass, String serviceName, String endpointKey, String scope){
		return RIDiscovererISHelper.discoverRunningInstances(serviceName, serviceClass, endpointKey, scope);
	}
}
