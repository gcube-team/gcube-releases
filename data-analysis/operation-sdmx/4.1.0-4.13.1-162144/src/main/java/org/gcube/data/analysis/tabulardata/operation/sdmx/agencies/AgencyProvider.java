package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.exceptions.AgencyException;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.managers.AgencyManager;
import org.gcube.data.analysis.tabulardata.operation.sdmx.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgencyProvider {

	private AgencyLoader agencyLoader;
	private Logger logger;
	private static AgencyProvider instance;
	public static final String 	TABMAN_AGENCY = "tabman",
								USER_AGENCY = "user";
	
	private LinkedList<AgencyManager> agencyManagerList;
	private final String AGENCY_MANAGER_MAP ="agency.manager.map";
	
	private AgencyProvider ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.agencyLoader = new AgencyLoader();
		this.agencyManagerList = new LinkedList<>();
		String [] agencyManagerMapString = ConfigurationManager.getInstance().getValue(AGENCY_MANAGER_MAP).split(",");
		
		for (String agencyManagerTag : agencyManagerMapString)
		{
			agencyManagerTag = agencyManagerTag.trim();
			String agencyManagerTagQN = AGENCY_MANAGER_MAP+"."+agencyManagerTag;
			this.logger.debug("Agency manager tag "+agencyManagerTag);
			String agencyManagerClassName = ConfigurationManager.getInstance().getValue(agencyManagerTagQN);
			this.logger.debug("Agency manager class "+agencyManagerClassName);
			
			try
			{
				this.logger.debug("Generating new class by reflection");
				Class<?> agencyManagerClass =   Class.forName(agencyManagerClassName);
				AgencyManager agencyManager = (AgencyManager) agencyManagerClass.newInstance();
				agencyManager.setId(agencyManagerTag);
				this.logger.debug("Class and object generated");
				this.agencyManagerList.add(agencyManager);
				
			} catch (Exception e)
			{
				this.logger.error("Unable to generate agency manager "+agencyManagerTag,e);
			}
			
			
		}
	}
	
	
	public static AgencyProvider getInstance ()
	{
		if (instance == null) instance = new AgencyProvider();
		return instance;
	}
	
	
	public String getAgency (String tabmanAgency, String userAgency, String [] registryParameters) throws AgencyException
	{
		this.logger.debug("Selecting the correct agency...");
		this.logger.debug("Defining operations sequence");
		Map<String, String> parameters = new HashMap<>();
		parameters.put(TABMAN_AGENCY, tabmanAgency);
		parameters.put(USER_AGENCY, userAgency);
		parameters.put(AgencyManager.REGISTRY_URL, registryParameters[0]);
		parameters.put(AgencyManager.REGISTRY_USER_NAME, registryParameters[1]);
		parameters.put(AgencyManager.REGISTRY_PASSWORD, registryParameters[2]);
		this.logger.debug("Executing operations");
		String response = null;
		Iterator<AgencyManager> agencyTagIterator = this.agencyManagerList.iterator();
		int priority = 1;
		
		while (agencyTagIterator.hasNext() && response == null)
		{
			AgencyManager agencyManager = agencyTagIterator.next();
			this.logger.debug("Loading agency manager of priproty "+priority++ +" id "+agencyManager.getId());
			response = agencyManager.execute(parameters, this.agencyLoader);
		}
		
		if (response == null)
		{
			this.logger.error("No suitable agency found");
			throw new AgencyException("No suitable agency found");
		}
		
		return response;
		
	}
	
	
}
