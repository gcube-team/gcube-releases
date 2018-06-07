package org.gcube.datapublishing.sdmx.security;

import java.util.Iterator;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.is.ISReader;
import org.gcube.datapublishing.sdmx.is.InformationSystemLabelConstants;
import org.gcube.datapublishing.sdmx.is.SDMXCategoryConstants;
import org.gcube.datapublishing.sdmx.model.Registry;
import org.gcube.datapublishing.sdmx.model.impl.RegistryImpl;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISRegistryDataReader extends ISReader<AccessPoint>  implements InformationSystemLabelConstants, SDMXCategoryConstants{

	private final String 	REGISTRY = "SDMXRegistry",
							ENDPOINT = "AccessPoint/Interface/Endpoint",
							RESULTS = "$resource/Profile/AccessPoint";
	
	private Logger logger;
	
	public ISRegistryDataReader() {
		this.logger = LoggerFactory.getLogger(ISRegistryDataReader.class);
	}
	
	
	public List<AccessPoint> getAccessPoints (String endpoint)
	{
		this.logger.debug("Getting access point "+endpoint);
		super.newQuery(ServiceEndpoint.class);
		super.addCondition(CATEGORY_LABEL, TYPE_SDMX);
		super.addCondition(NAME_LABEL, REGISTRY);
		
		if (endpoint != null) super.addCondition(ENDPOINT, endpoint.trim());

		super.setResults(RESULTS);

		return super.submit(AccessPoint.class);

	
	}
	
	public Registry getRegistry (String protocol)
	{
		RegistryImpl response = null;
		this.logger.debug("Getting access point for protocol "+protocol);
		AccessPoint accessPoint = getAccessPoint(null, protocol);
		
		if (accessPoint != null)
		{
			this.logger.debug("Access point found "+accessPoint.address());
			response = new RegistryImpl(accessPoint.address());
			response.setCredentials(getCredentials(accessPoint));
		}
		else
		{
			this.logger.debug("Access point not found");
		}
		
		return response;
	}
	
	private AccessPoint getAccessPoint (String endpoint, String protocol)
	{
		logger.debug("Getting access points list for "+endpoint+ " and protocol "+protocol);
		AccessPoint response = null;
		List<AccessPoint> accessPoints = getAccessPoints(endpoint);
		
		if (accessPoints == null) 
		{
			logger.warn("No access point found on IS!!!");
		}
		else
		{
			Iterator<AccessPoint> accessPointIterator = accessPoints.iterator();
			
			while (accessPointIterator.hasNext() && response == null)
			{
				AccessPoint accessPoint = accessPointIterator.next();
				String accessPointProtocol = accessPoint.name();
				logger.debug("Access point for "+accessPointProtocol);
				
				if (protocol.equalsIgnoreCase(accessPointProtocol))
				{
					logger.debug("Protocol found");
					response = accessPoint;
				}
			}
		
			logger.debug("Access point "+response);

		}

		
		return response;
	}
	
	private BasicCredentials getCredentials (AccessPoint accessPoint)
	{
		if (accessPoint == null || accessPoint.username() == null || accessPoint.username().trim().length() == 0)
		{
			this.logger.debug("No credentials present");
			return new BasicCredentials(null, null);
		}
		else
		{
			this.logger.debug("Username "+accessPoint.username());
			
			try
			{
				
				return new BasicCredentials(accessPoint.username(), decryptPassword(accessPoint.password()));
			} catch (Exception e)
			{
				logger.error("Unable to decrypt password",e);
				return new BasicCredentials(null, null);

			}
		}
	}
	
	public BasicCredentials getCredentials (String endpoint,String protocol)
	{
		this.logger.debug("Getting access points list for "+endpoint+ " and protocol "+protocol);
		return getCredentials(getAccessPoint(endpoint, protocol));

	}

	
	private String decryptPassword (String originalPassword) throws Exception
	{
		logger.debug("Encrypted password "+originalPassword);
		String response = StringEncrypter.getEncrypter().decrypt(originalPassword);
		return response;
	}
	
	
	

	public static void main(String[] args) {
		

		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		try
		{
			List<AccessPoint> aps = new ISRegistryDataReader().getAccessPoints("http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
			
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
