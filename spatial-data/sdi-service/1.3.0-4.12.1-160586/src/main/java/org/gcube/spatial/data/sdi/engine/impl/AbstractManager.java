package org.gcube.spatial.data.sdi.engine.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.spatial.data.sdi.engine.GeoServiceManager;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoServiceController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.GeoServiceDescriptor;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;

public abstract class AbstractManager<T extends GeoServiceDescriptor,E extends ServiceDefinition, L extends GeoServiceController<T>> implements GeoServiceManager<T,E>{

	
	protected abstract ISModule getRetriever();
	protected abstract AbstractCluster<T,L> getCluster();
	
	@Override
	public T getDescriptorByHostname(String hostname) throws ConfigurationNotFoundException {
		return getCluster().getControllerByHostName(hostname).getDescriptor();
	}

	@Override
	public List<T> getAvailableInstances() throws ConfigurationNotFoundException {
		ArrayList<T> toReturn=new ArrayList<>();
		for(L controller :getCluster().getActualCluster())
			toReturn.add(controller.getDescriptor());
		return toReturn;
	}
	

	@Override
	public String registerService(E toRegister) throws ServiceRegistrationException {
		return getRetriever().registerService(toRegister);
	}

	@Override
	public String importHostFromToken(String sourceToken, String hostname) throws ServiceRegistrationException {
		return getRetriever().importHostFromToken(sourceToken, hostname);
	}

	@Override
	public ServiceHealthReport getHealthReport() {
		return getRetriever().getHealthReport();
	}

}
