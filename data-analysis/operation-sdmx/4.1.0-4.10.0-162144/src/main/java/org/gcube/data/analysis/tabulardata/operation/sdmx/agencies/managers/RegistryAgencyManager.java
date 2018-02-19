package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.managers;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.AgencyLoader;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.AgencyProvider;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.exceptions.AgencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryAgencyManager implements AgencyManager {

	private Logger logger;
	private String id;
	
	public RegistryAgencyManager() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public String execute(Map<String, String> parameters,AgencyLoader agencyLoader) throws AgencyException{
		String agency = parameters.get(AgencyProvider.USER_AGENCY);
		this.logger.debug("Registry agency manager for "+agency);
		return agency;
		
	}

	@Override
	public String getId() 
	{
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
