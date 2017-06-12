package org.gcube.spatial.data.sdi.engine.impl.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.gis.GISInterface;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;
import org.gcube.spatial.data.sdi.model.service.Version;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoNetworkRetriever implements ObjectRetriever<GeoNetworkConfiguration> {

	@Override
	public GeoNetworkConfiguration getObject() throws ConfigurationNotFoundException {
		//TODO skip library
		//TODO use both GCoreEndpoints and ServiceEndpoint
		
		
//		log.info("Gathering geonetwork information under scope {} ",ScopeUtils.getCurrentScope());
//		LocalConfiguration config=LocalConfiguration.get();
//		String category=config.getProperty(LocalConfiguration.GEONETWORK_SE_CATEGORY);
//		String platformName=config.getProperty(LocalConfiguration.GEONETWORK_SE_PLATFORM);
//		String priorityProperty=config.getProperty(LocalConfiguration.GEONETWORK_SE_PRIORITY);
//		String endpointName=config.getProperty(LocalConfiguration.GEONETWORK_SE_ENDPOINT_NAME);
//		ServiceEndpoint se=getTheRightServiceEndpoint(ISUtils.queryForServiceEndpoints(category, platformName), endpointName, priorityProperty);
//		AccessPoint access=getTheRightAccessPoint(se, endpointName, priorityProperty);
//		
		
		try{
		//INIT LIB
		GISInterface gis=GISInterface.get();
		GeoNetworkAdministration gnAdmin=(GeoNetworkAdministration) gis.getGeoNewtorkPublisher();
		Configuration config=gnAdmin.getConfiguration();
		
		Version version=config.getGeoNetworkVersion().equals(ServerAccess.Version.TRE)?new Version(3,0,0):new Version(2,6,0);
		String baseEndpoint=config.getGeoNetworkEndpoint();
		ScopeConfiguration scopeConfig=config.getScopeConfiguration();
		List<Credentials> accessibleCredentials=new ArrayList();
		for(Account acc: scopeConfig.getAccounts().values()){
			accessibleCredentials.add(fromGeoNetworkAccount(acc));
		}
		
		Credentials adminCredentials=fromGeoNetworkAccount(config.getAdminAccount());
		// GN Lib doesn't expose ADMIN account type
		adminCredentials.setAccessType(AccessType.ADMIN);
		accessibleCredentials.add(adminCredentials);
		return new GeoNetworkConfiguration(version, baseEndpoint, accessibleCredentials, scopeConfig.getPrivateGroup()+"", scopeConfig.getPublicGroup()+"", "3");
		}catch(Exception e){
			log.warn("Unable to gather geonetwork information",e);
			throw new ConfigurationNotFoundException("Unable to gather information on geonetwork. Please contact administrator.",e);
		}
	}

	
	
	protected static final Credentials fromGeoNetworkAccount(Account toTranslate){
		switch(toTranslate.getType()){
		case CKAN : return new Credentials(toTranslate.getUser(),toTranslate.getPassword(),AccessType.CKAN);
		case SCOPE : return new Credentials(toTranslate.getUser(),toTranslate.getPassword(),AccessType.CONTEXT_USER);
		default : throw new RuntimeException("Unrecognized account type "+toTranslate);
		}
		
	}
	
	
	
	
	protected static final ServiceEndpoint getTheRightServiceEndpoint(List<ServiceEndpoint>resources, String endpointName,String priorityProperty){
		ServiceEndpoint toReturn=null;
		int priority=1000;
		for(ServiceEndpoint resource: resources){		
			Iterator<AccessPoint> points=resource.profile().accessPoints().iterator();

			while(points.hasNext()){
				AccessPoint point= points.next();
				log.debug(point.toString());
				if(point.name().equals(endpointName)){
					Map<String, Property> properties=point.propertyMap();
					if(properties.containsKey(priorityProperty)){
						int currentPriority=Integer.parseInt(properties.get(priorityProperty).value());
						if(toReturn==null||(currentPriority<priority)){
							toReturn=resource;
							priority=currentPriority;
						}
					}
				}
			}

		}
		return toReturn;
	}
	

	/**
	 * look for the access point compliant with configured endpointName and maxPriority (1)
	 * 
	 * @return null if not present
	 */
	protected static final AccessPoint getTheRightAccessPoint(ServiceEndpoint resource,String endpointName,String priorityProperty){
		AccessPoint toReturn=null;
		int priority=1000;
		
			Iterator<AccessPoint> points=resource.profile().accessPoints().iterator();

			while(points.hasNext()){
				AccessPoint point= points.next();
				log.debug(point.toString());
				if(point.name().equals(endpointName)){
					Map<String, Property> properties=point.propertyMap();
					if(properties.containsKey(priorityProperty)){
						int currentPriority=Integer.parseInt(properties.get(priorityProperty).value());
						if(toReturn==null||(currentPriority<priority)){
							toReturn=point;
							priority=currentPriority;
						}
					}
				}
			}

		
		return toReturn;
	}
}
