package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.managers;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.AgencyLoader;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.AgencyProvider;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.exceptions.AgencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabmanAgencyManager implements AgencyManager {

	private Logger logger;
	private AgencyLoader agencyLoader;
	private String id;
	
	public TabmanAgencyManager() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public String execute(Map<String, String> parameters,AgencyLoader agencyLoader) throws AgencyException
	{
		String agency = parameters.get(AgencyProvider.TABMAN_AGENCY);
		
		this.logger.debug("Tabman agency manager");
		
		if (agency == null)
		{
			this.logger.debug("Agency null");
			return agency;
		}
		
		else
		{
			String registryURL = parameters.get(REGISTRY_URL);
			String registryUserName = parameters.get(REGISTRY_USER_NAME);
			String registryPassword = parameters.get(REGISTRY_PASSWORD);
			this.logger.debug("Checking the agency provided by tabman "+agency);
			List<String> agencyList = this.agencyLoader.getAgencies(registryURL, registryUserName, registryPassword);
			
			if (agencyList != null && agencyList.contains(agency)) 
			{
				this.logger.debug("Agency "+agency+" found");
			}
			else 
			{
				this.logger.debug("Agency not found: adding the new one");
				
				if(this.agencyLoader.addAgency(registryURL, registryUserName, registryPassword, agency))
				{
					this.logger.debug("New agency added");
				}
				else 
				{
					this.logger.error("Unable to add the new agency");
					throw new AgencyException("Unable to add the new agency");
				}
				
			}

			return agency;
		}

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
