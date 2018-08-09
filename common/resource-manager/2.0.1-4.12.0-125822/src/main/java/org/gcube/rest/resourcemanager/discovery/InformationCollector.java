package org.gcube.rest.resourcemanager.discovery;

import java.util.List;
import java.util.Set;

import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;

public interface InformationCollector {
	public List<Resource> getGenericResourcesByID(String id, String scope);
	public List<Resource> getGenericResourcesByName(String name, String scope);
	public List<Resource> getGenericResourcesByType(String type, String scope);
	public List<Resource> getGenericResourcesByTypeAndName(String type, String name, String scope);
	public List<String> listGenericResourceIDsByType(String type, String scope);
	public Set<RunInstance> discoverRunningInstances(String serviceName, String serviceClass, String scope);
	public Set<RunInstance> discoverRunningInstancesFilteredByEndopointKey(String serviceName, String serviceClass, String endpointKey, String scope);
	public List<SerInstance> discoverServiceInstances(String serviceName, String serviceClass, String scope);
	public List<HostNode> discoverHostingNodes(String scope);
}
