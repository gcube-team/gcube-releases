package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.managers;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.AgencyLoader;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.exceptions.AgencyException;

public interface AgencyManager 
{
	final String 	REGISTRY_URL = "registryURL",
					REGISTRY_USER_NAME = "registryUserName",
					REGISTRY_PASSWORD = "registryPassword";
	
	public String execute(Map<String, String> parameters,AgencyLoader agencyLoader) throws AgencyException;
	
	public String getId ();
	
	public void setId (String id);

}
