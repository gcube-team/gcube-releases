package org.gcube.spatial.data.sdi.engine;

import java.util.List;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.GeoServiceDescriptor;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;

public interface GeoServiceManager<T extends GeoServiceDescriptor,E extends ServiceDefinition> {

	public T getDescriptorByHostname(String hostname) throws ConfigurationNotFoundException;
	public List<T> getAvailableInstances() throws ConfigurationNotFoundException;
	public List<T> getSuggestedInstances() throws ConfigurationNotFoundException;
	public String registerService(E toRegister) throws ServiceRegistrationException;
	public String importHostFromToken(String sourceToken, String hostname) throws ServiceRegistrationException;
	public ServiceHealthReport getHealthReport();
}
