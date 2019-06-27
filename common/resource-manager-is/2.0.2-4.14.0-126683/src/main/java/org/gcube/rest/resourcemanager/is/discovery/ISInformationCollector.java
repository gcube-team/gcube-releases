package org.gcube.rest.resourcemanager.is.discovery;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;

@Singleton
public class ISInformationCollector implements InformationCollector {
	public ISInformationCollector() {
	}
	
	@Override
	public List<Resource> getGenericResourcesByID(String id, String scope) {
		return ISHelper.getGenericResourcesByID(id, scope);
	}

	@Override
	public List<Resource> getGenericResourcesByName(String name, String scope) {
		return ISHelper.getGenericResourcesByName(name, scope);
	}

	@Override
	public List<Resource> getGenericResourcesByType(String type, String scope) {
		return ISHelper.getGenericResourcesByType(type, scope);
	}

	@Override
	public List<Resource> getGenericResourcesByTypeAndName(String type, String name, String scope) {
		return ISHelper.getGenericResourcesByTypeAndName(type, name, scope);
	}

	@Override
	public List<String> listGenericResourceIDsByType(String type, String scope) {
		return ISHelper.listGenericResourceIDsByType(type, scope);
	}

	@Override
	public Set<RunInstance> discoverRunningInstancesFilteredByEndopointKey(String serviceName, String serviceClass, String endpointKey, String scope) {
		return ISHelper.discoverRunningInstancesFilteredByEndopointKey(serviceName, serviceClass, endpointKey, scope);
	}

	@Override
	public Set<RunInstance> discoverRunningInstances(String serviceName, String serviceClass, String scope) {
		return ISHelper.discoverRunningInstances(serviceName, serviceClass, scope);
	}

	@Override
	public List<SerInstance> discoverServiceInstances(String serviceName, String serviceClass, String scope) {
		return ISHelper.discoverServiceInstances(serviceName, serviceClass, scope);
	}

	@Override
	public List<HostNode> discoverHostingNodes(String scope) {
		return ISHelper.discoverHostingNodes(scope);
	}
}
