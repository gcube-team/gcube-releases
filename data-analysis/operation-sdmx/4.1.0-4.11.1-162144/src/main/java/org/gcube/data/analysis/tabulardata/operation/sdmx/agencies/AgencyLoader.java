package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies;

import java.util.List;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.configuration.ConfigurationManager;
import org.gcube.datapublishing.sdmx.api.model.versioning.Version;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AgencyBean;
import org.sdmxsource.sdmx.api.model.beans.base.AgencySchemeBean;
import org.sdmxsource.sdmx.api.model.mutable.base.AgencyMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.AgencySchemeMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.AgencyMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgencyLoader 
{
	private AgenciesMap agenciesMap;
	private Logger logger;
	private long timeout;
	private final String TIMEOUT_CONFIGURATION = "agency.loader.timeout";
	private final long DEFAULT_TIMEOUT = 300000;
	
	public AgencyLoader() 
	{
		this.agenciesMap = new AgenciesMap();
		this.logger = LoggerFactory.getLogger(this.getClass());
		try
		{
			this.timeout = Long.parseLong(ConfigurationManager.getInstance().getValue(TIMEOUT_CONFIGURATION))*1000;
			
		} catch (Exception e)
		{
			this.logger.warn ("Timeout configuration value not present or wrong: using default value",e);
			this.timeout = DEFAULT_TIMEOUT;
		}
		this.logger.debug("Timeout value "+this.timeout);
	}
	

	public List<String> getAgencies (String registryUrl, String registryUserName, String registryPassword)
	{
		this.logger.debug("Getting agencies of registry "+registryUrl);
		AgenciesList agencies = this.agenciesMap.get(registryUrl);
		
		if (agencies == null)
		{
			this.logger.debug("Valid agency list not found in the local cache");
			
			try
			{
				agencies = loadAgencies(registryUrl,registryUserName,registryPassword); 
			} catch (Exception e)
			{
				this.logger.error("Unable to load agency data");
			}

			this.agenciesMap.put(registryUrl, agencies);
		}
		
		return agencies;
		
	}
	
	public boolean addAgency (String registryUrl, String registryUserName, String registryPassword, String agency)
	{
		this.logger.debug("Adding new agency "+agency);
		boolean response;
		
		try
		{	
			AgencySchemeBean agencyScheme = getAgencySchemeBean(registryUrl, registryUserName, registryPassword);
			AgencySchemeMutableBean mutableInstance = agencyScheme.getMutableInstance();
			this.logger.debug("Generating agency bean");
			AgencyMutableBean agencyBean = new AgencyMutableBeanImpl();
			agencyBean.setId(agency);
			agencyBean.addName("en", agency);
			mutableInstance.addItem(agencyBean);
			this.logger.debug("Starting publication");
			SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(registryUrl,registryUserName,registryPassword);
			registryClient.publish(mutableInstance.getImmutableInstance());
			this.logger.debug("Publication completed");
			response = true;
			
		} catch (Exception e)
		{
			this.logger.error("Unable to get root agencyscheme from the server",e);
			response = false;
		}
		
		this.logger.debug("Operation completed with result "+response);
		return response;
		
	}
	
	private AgencySchemeBean getAgencySchemeBean (String registryUrl, String registryUserName, String registryPassword) throws SDMXRegistryClientException
	{
		this.logger.debug("Loading agencies from the registry");
		SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(registryUrl,registryUserName,registryPassword);
		SdmxBeans sdmxBeansResponse = registryClient.getAgencyScheme(AgencySchemeBean.DEFAULT_SCHEME, "", Version.LATEST, Detail.allstubs, References.none);
		Set<AgencySchemeBean> agencySchemes = sdmxBeansResponse.getAgenciesSchemes();
		AgencySchemeBean response = null;
		
		if (agencySchemes != null && agencySchemes.size()>0)
		{
			response = agencySchemes.iterator().next();
			this.logger.debug("Downloaded agency scheme "+response.getAgencyId());
			
		}
		else logger.debug("Agencies not found");
		
		return response;
	}
	
	private AgenciesList loadAgencies (String registryUrl, String registryUserName, String registryPassword) throws Exception
	{
		this.logger.debug("Loading agencies from the registry");
		AgencySchemeBean agencyScheme = getAgencySchemeBean(registryUrl, registryUserName, registryPassword);
		AgenciesList response = null;
		
		if (agencyScheme != null)
		{
			response =  new AgenciesList(this.timeout);
			this.logger.debug("Downloaded agency scheme "+agencyScheme.getAgencyId());
			List<AgencyBean> agencies = agencyScheme.getItems();
			
			for (AgencyBean agency : agencies)
			{
				String id = agency.getId();
				this.logger.debug("Got agency "+id);
				response.add(id);
			}
			
		}
		else logger.debug("Agencies not found");
		
		return response;
		
	}

}
