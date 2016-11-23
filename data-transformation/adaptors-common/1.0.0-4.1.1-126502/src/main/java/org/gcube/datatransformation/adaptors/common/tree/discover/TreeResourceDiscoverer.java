package org.gcube.datatransformation.adaptors.common.tree.discover;

import java.util.Set;

import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.xmlobjects.TreeResource;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;

public class TreeResourceDiscoverer extends Discoverer <TreeResource> implements TreeResourceDiscovererAPI<TreeResource> {

	public TreeResourceDiscoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<TreeResource> harvester) {
		super(riDiscoverer, harvester);
	}


	@Override
	public Set<String> discoverTreeServiceRunningInstances(String scope) {
		return this.discoverRunningInstances(ConstantNames.SERVICE_CLASS, ConstantNames.SERVICE_NAME_TREE, ConstantNames.ENDPOINT_KEY, scope);
	}
	
}
