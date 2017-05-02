package org.gcube.data.analysis.statisticalmanager.persistence;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.exception.ISException;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RuntimeResourceManager {


	private static Logger logger = LoggerFactory.getLogger(RuntimeResourceManager.class);

	


	public static AccessPointDescriptor getDatabaseProfile(DatabaseType type) throws ISException{
		logger.debug("Get Access point for "+type+" under scope"+ScopeUtils.getCurrentScope());
		String rrName=Configuration.getProperty(Configuration.RUNTIME_RESOURCE_DB);
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+rrName+"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		// Not Found
		if (resources == null || resources.isEmpty())
			throw new ISException("Runtime resource "+ rrName + " not found");


		// No Access point
		ServiceEndpoint resource = resources.get(0);
		logger.debug("Checking service point ID "+resource.id());
		if (resource.profile().accessPoints() == null|| resource.profile().accessPoints().isEmpty())
			throw new ISException("No AccessPoint defined in "+ rrName + " not found");

		for (AccessPoint ap : resource.profile().accessPoints()) 			
			if(ap.name().equals(type.getAccessPointName()))
				try{
					return new AccessPointDescriptor(ap.address(),ap.username(),StringEncrypter.getEncrypter().decrypt(ap.password()));
				}catch(Exception e){					
					throw new ISException("Unable to decrypt information",e);
				}

		throw new ISException("AccessPoint not found for "+type);
	}


	public static HashMap<String, String> getServiceEndpointAsMap(String runtimeResource) throws ISException{
		logger.debug("Get ServiceEndpoint "+runtimeResource+" Under scope "+ScopeUtils.getCurrentScope());

		HashMap<String, String> toReturn = new HashMap<String, String>();
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"
				+ runtimeResource + "'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		try{
			ServiceEndpoint resource = client.submit(query).get(0);

			logger.debug("Found resource ID "+resource.id());

			String sc= "";
			for (String scope : resource.scopes()) {
				sc += scope + ";";
			}

			toReturn.put(runtimeResource + "." + "Scopes", sc);
			Profile profile = resource.profile();
			toReturn.put(runtimeResource + "." + "Profile.Category",profile.category());
			Platform platform= profile.platform();

			toReturn.put(runtimeResource + "." + "Profile.Platform.name", platform.name());
			toReturn.put(runtimeResource + "." + "Profile.Platform.version", Integer.toString(platform.version()));
			toReturn.put(runtimeResource + "." + "Profile.Platform.minorVersion",Integer.toString( platform.minorVersion()));
			toReturn.put(runtimeResource + "." + "Profile.Platform.revisionVersion",Integer.toString( platform.revisionVersion()));
			toReturn.put(runtimeResource + "." + "Profile.Platform.buildVersion",Integer.toString( platform.buildVersion()));

			org.gcube.common.resources.gcore.ServiceEndpoint.Runtime runtime= profile.runtime();

			toReturn.put(runtimeResource + "." + "Profile.Runtime.hostedOn", runtime.hostedOn());
			toReturn.put(runtimeResource + "." + "Profile.Runtime.status", runtime.status());
			toReturn.put(runtimeResource + "." + "Profile.Runtime.ghnId", runtime.ghnId());

			if (profile.accessPoints() != null) 
				for (AccessPoint accessPoint :  profile.accessPoints()) {
					toReturn.put(runtimeResource + "."+ "AccessPoint.Description", accessPoint.description());
					toReturn.put(runtimeResource + "." + "AccessPoint.Address",accessPoint.address());
					toReturn.put(runtimeResource + "." + "AccessPoint.Password",
							StringEncrypter.getEncrypter().decrypt(accessPoint.password()));
					toReturn.put(runtimeResource + "." + "AccessPoint.Username",accessPoint.username());


					for (Map.Entry<String, Property> entry : accessPoint.propertyMap().entrySet()) {
						String propertyName = runtimeResource + "."+ entry.getKey().toString();
						String propertyVal = entry.getValue().toString();					
						toReturn.put(propertyName, propertyVal);
					}
					break;
				}

			return toReturn;
		}catch(IndexOutOfBoundsException e){
			throw new ISException("Runtime resource "+ runtimeResource + " not found");
		}catch(Exception e){			
			throw new ISException("Unable to decrypt information", e);
		}
	}

	public static Group<AccessPoint> getRRAccessPoint(String runtimeResource) throws ISException{
		try{
			logger.debug("Get Access Points for ServiceEndpoint "+runtimeResource+" under scope "+ScopeUtils.getCurrentScope());
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"
					+ runtimeResource + "'");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);


			ServiceEndpoint resource = client.submit(query).get(0);
			if (resource.profile().accessPoints() == null	|| resource.profile().accessPoints().isEmpty())
				throw new ISException("Accesspoint in resource "+ runtimeResource + " not found");

			return resource.profile().accessPoints();

		}catch(IndexOutOfBoundsException e){
			throw new ISException("Runtime resource "+ runtimeResource + " not found");
		}
	}

}
