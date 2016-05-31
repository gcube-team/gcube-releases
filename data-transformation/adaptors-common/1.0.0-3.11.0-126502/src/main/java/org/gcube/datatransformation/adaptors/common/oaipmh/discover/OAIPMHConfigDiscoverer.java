package org.gcube.datatransformation.adaptors.common.oaipmh.discover;


import java.util.Set;

import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.xmlobjects.OAIPMHConfig;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;

public class OAIPMHConfigDiscoverer extends Discoverer <OAIPMHConfig> implements OAIPMHConfigDiscovererAPI<OAIPMHConfig> {

	public OAIPMHConfigDiscoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<OAIPMHConfig> harvester, Class<OAIPMHConfig> clazz) {
		super(riDiscoverer, harvester);
	}

	@Override
	public Set<String> discoverOAIPMHServiceRunningInstances(String scope) {
		return this.discoverRunningInstances(ConstantNames.SERVICE_CLASS, ConstantNames.SERVICE_NAME_OAIPMH, ConstantNames.ENDPOINT_KEY, scope);
	}


}
