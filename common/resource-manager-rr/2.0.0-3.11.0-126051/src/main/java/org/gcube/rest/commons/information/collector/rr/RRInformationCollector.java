package org.gcube.rest.commons.information.collector.rr;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.gcube.rest.commons.db.dao.app.ResourceModelDao;
import org.gcube.rest.commons.db.dao.app.RunInstanceModelDao;
import org.gcube.rest.commons.db.dao.app.SerInstanceModelDao;
import org.gcube.rest.commons.db.model.app.ResourceModel;
import org.gcube.rest.commons.db.model.app.RunInstanceModel;
import org.gcube.rest.commons.db.model.app.SerInstanceModel;
import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;

import com.google.inject.Singleton;

@Singleton
public class RRInformationCollector implements InformationCollector {

	private final ResourceModelDao resourceModelDao;
	private final RunInstanceModelDao runInstanceModelDao;
	private final SerInstanceModelDao serInstanceModelDao;
	
	@Inject
	public RRInformationCollector(ResourceModelDao resourceModelDao,
			RunInstanceModelDao runInstanceModelDao,
			SerInstanceModelDao serInstanceModelDao) {
		super();
		this.resourceModelDao = resourceModelDao;
		this.runInstanceModelDao = runInstanceModelDao;
		this.serInstanceModelDao = serInstanceModelDao;
	}

	@Override
	public List<Resource> getGenericResourcesByID(String id, String scope) {
		
		List<ResourceModel> resources = resourceModelDao.getGenericResourcesByID(id, scope);
		
		return ResourceModelDao.convertToResourceList(resources);
	}

	@Override
	public List<Resource> getGenericResourcesByName(String name, String scope) {
		List<ResourceModel> resources = resourceModelDao.getGenericResourcesByName(name, scope);
		return ResourceModelDao.convertToResourceList(resources);
		
	}

	@Override
	public List<Resource> getGenericResourcesByType(String type, String scope) {
		List<ResourceModel> resources = resourceModelDao.getGenericResourcesByType(type, scope);
		return ResourceModelDao.convertToResourceList(resources);
	}

	@Override
	public List<Resource> getGenericResourcesByTypeAndName(String type,
			String name, String scope) {
		List<ResourceModel> resources = resourceModelDao.getGenericResourcesByTypeAndName(type, name, scope);
		return ResourceModelDao.convertToResourceList(resources);
	}

	@Override
	public List<String> listGenericResourceIDsByType(String type, String scope) {
		List<String> ids = resourceModelDao.listGenericResourceIDsByType(type, scope);
		return ids;
	}

	@Override
	public Set<RunInstance> discoverRunningInstances(String serviceName,
			String serviceClass, String scope) {
		
		List<RunInstanceModel> results = runInstanceModelDao.getByServiceClassAndServiceNameAndScope(serviceClass, serviceName, scope);
		
		return RunInstanceModelDao.convertToRunInstanceSet(results);
	}

	@Override
	public Set<RunInstance> discoverRunningInstancesFilteredByEndopointKey(
			String serviceName, String serviceClass, String endpointKey,
			String scope) {
		
		List<RunInstanceModel> results = runInstanceModelDao.getByServiceClassAndServiceNameAndScopeAndEndpointKey(serviceClass, serviceName, scope, endpointKey);
		
		return RunInstanceModelDao.convertToRunInstanceSet(results);
	}

	@Override
	public List<SerInstance> discoverServiceInstances(String serviceName,
			String serviceClass, String scope) {
		
		List<SerInstanceModel> results = serInstanceModelDao.getByServiceClassAndServiceNameAndScope(serviceClass, serviceName, scope);
		
		return SerInstanceModelDao.convertToSerInstanceList(results);
	}

	@Override
	public List<HostNode> discoverHostingNodes(String scope) {
		// TODO Auto-generated method stub
		return null;
	}

}
