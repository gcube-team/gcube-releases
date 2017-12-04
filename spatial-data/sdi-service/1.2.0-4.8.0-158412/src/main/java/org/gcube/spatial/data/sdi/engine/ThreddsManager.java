package org.gcube.spatial.data.sdi.engine;

import java.io.File;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.CatalogDescriptor;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

public interface ThreddsManager extends GeoServiceManager<ThreddsDescriptor, ThreddsDefinition>{

//	public ThreddsDescriptor getConfiguration() throws ConfigurationNotFoundException;
//	public ThreddsDescriptor getConfigurationByHostname(String host) throws ConfigurationNotFoundException; 
//	public ServiceHealthReport getHealthReport();
//	public String registerService(ThreddsDefinition definition)throws ServiceRegistrationException;
//	String importHostFromToken(String sourceToken, String hostname) throws ServiceRegistrationException;
	
	
	public CatalogDescriptor createCatalog(File catalogFile,String catalogReference) throws ConfigurationNotFoundException;
	
}
