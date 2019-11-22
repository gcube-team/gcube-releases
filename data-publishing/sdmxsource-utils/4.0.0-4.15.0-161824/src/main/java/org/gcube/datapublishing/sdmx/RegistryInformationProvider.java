package org.gcube.datapublishing.sdmx;

import org.gcube.datapublishing.sdmx.model.Registry;
import org.gcube.datapublishing.sdmx.security.ISRegistryDataReader;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryInformationProvider {

	public static final String SDMX_PROTOCOL = "RESTV2_1";
	
	public static BasicCredentials retrieveCredentials (String endpoint)
	{
		Logger log = LoggerFactory.getLogger(RegistryInformationProvider.class);
		ISRegistryDataReader isReader = new ISRegistryDataReader();
		BasicCredentials credentials = isReader.getCredentials(endpoint, SDMX_PROTOCOL);
		log.debug(credentials.toString());
		return credentials;
		
	}
	
	
	public static Registry getRegistry ()
	{
		Logger log = LoggerFactory.getLogger(RegistryInformationProvider.class);
		ISRegistryDataReader isReader = new ISRegistryDataReader();
		Registry registry  = isReader.getRegistry(SDMX_PROTOCOL);
		log.debug(registry.toString());
		return registry;
	}
	
}
