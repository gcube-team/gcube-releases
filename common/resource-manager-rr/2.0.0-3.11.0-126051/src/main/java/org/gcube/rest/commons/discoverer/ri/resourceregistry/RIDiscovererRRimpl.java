package org.gcube.rest.commons.discoverer.ri.resourceregistry;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.commons.db.dao.app.RunInstanceModelDao;
import org.gcube.rest.commons.db.model.app.RunInstanceModel;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RIDiscovererRRimpl implements RunningInstancesDiscoverer {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RIDiscovererRRimpl.class);
	
	private final RunInstanceModelDao runInstanceModelDao;
	
	@Inject
	public RIDiscovererRRimpl(RunInstanceModelDao runInstanceModelDao){
		this.runInstanceModelDao = runInstanceModelDao;
	}
	
	@Override
	public Set<String> discoverRunningInstances(String serviceClass, String serviceName, String endpointKey, String scope){
		List<RunInstanceModel> list = runInstanceModelDao.getByServiceClassAndServiceNameAndScopeAndEndpointKey(serviceClass, serviceName, scope, endpointKey);
		return RunInstanceModelDao.convertToResourceIDsSet(list);
	}
}
