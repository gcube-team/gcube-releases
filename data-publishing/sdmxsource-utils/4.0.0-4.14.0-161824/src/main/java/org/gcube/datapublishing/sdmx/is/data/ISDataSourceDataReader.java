package org.gcube.datapublishing.sdmx.is.data;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.is.ISReader;
import org.gcube.datapublishing.sdmx.is.InformationSystemLabelConstants;
import org.gcube.datapublishing.sdmx.is.SDMXCategoryConstants;
import org.gcube.datapublishing.sdmx.model.DataSource;
import org.gcube.datapublishing.sdmx.model.impl.DataSourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISDataSourceDataReader extends ISReader<Profile>  implements InformationSystemLabelConstants,SDMXCategoryConstants{

	private final String 	RESULTS = "$resource/Profile";
	
	private Logger logger;
	
	public ISDataSourceDataReader() {
		this.logger = LoggerFactory.getLogger(ISDataSourceDataReader.class);
	}
	
	
	public List<Profile> getProfiles ()
	{
		this.logger.debug("Getting SDMX Data Sources in the current VRE");
		super.newQuery(ServiceEndpoint.class);
		super.addCondition(CATEGORY_LABEL, TYPE_SDMX_DATA_SOURCES);
		//super.addCondition(NAME_LABEL, SDMX_DATA_SOURCE);
		super.setResults(RESULTS);

		return super.submit(Profile.class);

	
	}
	
	public List<DataSource> getDataSources ()
	{
		List<DataSource> response = new ArrayList<>();
		this.logger.debug("Getting Data Sources for VRE "+ScopeProvider.instance.get());
		List<Profile> profiles = getProfiles();
		
		if (profiles != null)
		{
			for (Profile profile: profiles)
			{
				this.logger.debug("Data Source found ");
				String name = profile.name();
				this.logger.debug("VM "+name);
				Group<AccessPoint> accessPoints = profile.accessPoints();
				Iterator<AccessPoint> accessPointIterator = accessPoints.iterator();
				
				if (accessPointIterator.hasNext())
				{
					AccessPoint accessPoint = accessPointIterator.next();
					String endpoint = accessPoint.address();
					this.logger.debug("Endpoint "+endpoint);
					response.add(new DataSourceImpl(name, endpoint));
					
				} else
				{
					this.logger.debug("Data source "+name+ " does not have any access point");
				}


			}
		}
		else
		{
			this.logger.debug("Data Sources not found");
		}
		
		return response;
	}
	

	
	
	

	public static void main(String[] args) {
		

		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		try
		{
			List<DataSource> dataSources = new ISDataSourceDataReader().getDataSources();
			
			
			for (DataSource dataSource : dataSources)
			{
				System.out.println(dataSource.getName());
				System.out.println(dataSource.getEndpoint());
				System.out.println("********************");
			}
			
			
		} catch (RuntimeException e)
		{
			SOAPFaultException soap = (SOAPFaultException) e.getCause();
			
			System.out.println(soap.getMessage());
			System.out.println(soap.getFault());
		}
		
		
		
	}

}
