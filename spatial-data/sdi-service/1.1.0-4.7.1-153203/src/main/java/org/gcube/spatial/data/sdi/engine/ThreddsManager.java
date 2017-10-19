package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

public interface ThreddsManager {

	public ThreddsConfiguration getConfiguration() throws ConfigurationNotFoundException;
	public ServiceHealthReport getHealthReport();
	public String registerService(ThreddsDefinition definition)throws ServiceRegistrationException;
	String importHostFromToken(String sourceToken, String hostname) throws ServiceRegistrationException;
}
