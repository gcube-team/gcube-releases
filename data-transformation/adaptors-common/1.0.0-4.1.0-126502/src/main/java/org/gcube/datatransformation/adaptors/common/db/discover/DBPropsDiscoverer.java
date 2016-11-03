package org.gcube.datatransformation.adaptors.common.db.discover;

import java.util.Set;

import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;

public class DBPropsDiscoverer extends Discoverer <DBProps> implements DBPropsDiscovererAPI<DBProps> {

	public DBPropsDiscoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<DBProps> harvester) {
		super(riDiscoverer, harvester);
	}

	@Override
	public Set<String> discoverDBServiceRunningInstances(String scope) {
		return this.discoverRunningInstances(ConstantNames.SERVICE_CLASS, ConstantNames.SERVICE_NAME_DB, ConstantNames.ENDPOINT_KEY, scope);
	}
	
}
