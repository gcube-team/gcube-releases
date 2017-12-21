package org.gcube.spatial.data.sdi.engine.impl.is;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;

public interface ISModule {

	public List<ServiceEndpoint> getISInformation()throws ConfigurationNotFoundException;
	public ServiceHealthReport getHealthReport();
	public String registerService(ServiceDefinition definition) throws ServiceRegistrationException;
	public String importHostFromToken(String sourceToken,String host)throws ServiceRegistrationException;	
}
