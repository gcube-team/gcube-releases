package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;

public interface GISManager {

	public GeoServerClusterConfiguration getConfiguration() throws ConfigurationNotFoundException;
	public ServiceHealthReport getHealthReport();
	public String registerService(GeoServerDefinition definition)throws ServiceRegistrationException;
	String importHostFromToken(String sourceToken, String hostname) throws ServiceRegistrationException;
}
