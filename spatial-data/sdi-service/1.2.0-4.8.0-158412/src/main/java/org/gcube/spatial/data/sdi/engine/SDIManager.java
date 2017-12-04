package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.ScopeConfiguration;
import org.gcube.spatial.data.sdi.model.health.HealthReport;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;

public interface SDIManager {

	public ScopeConfiguration getContextConfiguration();
	
	public HealthReport getHealthReport();
	
	public String registerService(ServiceDefinition definition) throws ServiceRegistrationException; 
	
	public String importService(String sourceToken,String host,ServiceDefinition.Type expectedType)throws ServiceRegistrationException;
	
	public GeoNetworkManager getGeoNetworkManager();
	public ThreddsManager getThreddsManager();
	public GISManager getGeoServerManager();
}
