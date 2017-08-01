package org.gcube.data.analysis.tabulardata.operation.sdmx.security;

import java.util.Iterator;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISManager  {

	private final String 	CATEGORY ="Category",	
							SDMX ="SDMX",
							NAME = "Name",
							REGISTRY = "SDMXRegistry",
							ENDPOINT = "AccessPoint/Interface/Endpoint",
							RESULTS = "$resource/Profile/AccessPoint";
	
	private Logger logger;
	
	public ISManager() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public List<AccessPoint> getAccessPoints (String endpoint)
	{
		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
		String queryCondition1 = generateQuery(CATEGORY,SDMX);
		logger.debug("Query condition 1: "+queryCondition1 );
		String queryCondition2 = generateQuery(NAME, REGISTRY);
		logger.debug("Query condition 2: "+queryCondition2 );
		String endpointCondition = generateQuery(ENDPOINT, endpoint.trim());
		logger.debug("Endpoint condition: "+endpointCondition );
		query.addCondition(queryCondition1);
		query.addCondition(queryCondition2);
		query.addCondition(endpointCondition);
		query.setResult(RESULTS);

		DiscoveryClient<AccessPoint>  client = ICFactory.clientFor(AccessPoint.class);
		List<AccessPoint> response = client.submit(query);
		return response;
	
	}
	
	
	public Credentials getCredentials (String endpoint,String protocol)
	{
		logger.debug("Getting access points list for "+endpoint+ " and protocol "+protocol);
		List<AccessPoint> accessPoints = getAccessPoints(endpoint);
		Credentials response = new Credentials(null, null);
		
		if (accessPoints == null) 
		{
			logger.warn("No access point found on IS!!!");
		}
		else
		{
			boolean found = false;
			Iterator<AccessPoint> accessPointIterator = accessPoints.iterator();
			
			while (accessPointIterator.hasNext() && !found)
			{
				AccessPoint accessPoint = accessPointIterator.next();
				String accessPointProtocol = accessPoint.name();
				logger.debug("Access point for "+accessPointProtocol);
				
				if (protocol.equalsIgnoreCase(accessPointProtocol))
				{
					logger.debug("Protocol found");
					found = true;
					String userName = accessPoint.username();
					logger.debug("Username "+userName);
					
					if (userName != null && userName.trim().length()>0)
					{
						logger.debug("Credentials found");
						
						try
						{
							response = new Credentials(userName, decryptPassword(accessPoint.password()));
						} catch (Exception e)
						{
							logger.error("Unable to decrypt password",e);
						}
						
						
					}
					else logger.debug("Credentials not present");
				}
			}
		
			if (!found) logger.warn("Credentials not found for the selected protocol");

		}

		
		return response;
		
	}

	
	private String decryptPassword (String originalPassword) throws Exception
	{
		logger.debug("Encrypted password "+originalPassword);
		String response = StringEncrypter.getEncrypter().decrypt(originalPassword);
		return response;
	}
	
	
	
	private String generateQuery(String element, String name)
	{	
		StringBuilder builder = new StringBuilder();
		builder.append("$resource/Profile/");
		builder.append(element).append("/text() eq '");
		builder.append(name);
		builder.append("'");
		return builder.toString();
		
	}

	public static void main(String[] args) {
		

		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		try
		{
			List<AccessPoint> aps = new ISManager().getAccessPoints("http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
			
			for (AccessPoint ap : aps)
			{
				System.out.println(ap.address());
				System.out.println(ap.name());
				System.out.println(ap.username());
				System.out.println(ap.password());
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
