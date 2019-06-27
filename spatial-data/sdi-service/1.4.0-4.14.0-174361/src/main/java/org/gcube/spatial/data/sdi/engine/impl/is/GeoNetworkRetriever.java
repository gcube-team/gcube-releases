package org.gcube.spatial.data.sdi.engine.impl.is;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceDefinitionException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.gn.Account;
import org.gcube.spatial.data.sdi.model.health.Status;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoNetworkRetriever extends AbstractISModule{

//	@Override
//	public GeoNetworkDescriptor getObject() throws ConfigurationNotFoundException {
//		//TODO skip library
//		//TODO use both GCoreEndpoints and ServiceEndpoint
//		
//		
////		log.info("Gathering geonetwork information under scope {} ",ScopeUtils.getCurrentScope());
////		LocalConfiguration config=LocalConfiguration.get();
////		String category=config.getProperty(LocalConfiguration.GEONETWORK_SE_CATEGORY);
////		String platformName=config.getProperty(LocalConfiguration.GEONETWORK_SE_PLATFORM);
////		String priorityProperty=config.getProperty(LocalConfiguration.GEONETWORK_SE_PRIORITY);
////		String endpointName=config.getProperty(LocalConfiguration.GEONETWORK_SE_ENDPOINT_NAME);
////		ServiceEndpoint se=getTheRightServiceEndpoint(ISUtils.queryForServiceEndpoints(category, platformName), endpointName, priorityProperty);
////		AccessPoint access=getTheRightAccessPoint(se, endpointName, priorityProperty);
////		
//		
//		try{
//		//INIT LIB
//		GISInterface gis=GISInterface.get();
//		GeoNetworkAdministration gnAdmin=(GeoNetworkAdministration) gis.getGeoNewtorkPublisher();
//		Configuration config=gnAdmin.getConfiguration();
//		
//		Version version=config.getGeoNetworkVersion().equals(ServerAccess.Version.TRE)?new Version(3,0,0):new Version(2,6,0);
//		String baseEndpoint=config.getGeoNetworkEndpoint();
//		ScopeConfiguration scopeConfig=config.getScopeConfiguration();
//		List<Credentials> accessibleCredentials=new ArrayList();
//		for(Account acc: scopeConfig.getAccounts().values()){
//			accessibleCredentials.add(fromGeoNetworkAccount(acc));
//		}
//		
//		Credentials adminCredentials=fromGeoNetworkAccount(config.getAdminAccount());
//		// GN Lib doesn't expose ADMIN account type
//		adminCredentials.setAccessType(AccessType.ADMIN);
//		accessibleCredentials.add(adminCredentials);
//		return new GeoNetworkDescriptor(version, baseEndpoint, accessibleCredentials, scopeConfig.getPrivateGroup()+"", scopeConfig.getPublicGroup()+"", "3");
//		}catch(Exception e){
//			log.warn("Unable to gather geonetwork information",e);
//			throw new ConfigurationNotFoundException("Unable to gather information on geonetwork. Please contact administrator.",e);
//		}
//	}

	
	
	protected static final Credentials fromGeoNetworkAccount(Account toTranslate){
		switch(toTranslate.getType()){
		case CKAN : return new Credentials(toTranslate.getUser(),toTranslate.getPassword(),AccessType.CKAN);
		case SCOPE : return new Credentials(toTranslate.getUser(),toTranslate.getPassword(),AccessType.CONTEXT_USER);
		default : throw new RuntimeException("Unrecognized account type "+toTranslate);
		}
		
	}
	
	@Override
	protected boolean isSmartGearsMandatory() {
		return LocalConfiguration.getFlag(LocalConfiguration.GEONETWORK_MANDATORY_SG);
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
	
	
	@Override
	protected String getGCoreEndpointServiceClass() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_GE_SERVICE_CLASS);
	}
	
	@Override
	protected String getGCoreEndpointServiceName() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_GE_SERVICE_NAME);
	}

	@Override
	protected String getManagedServiceType() {
		return "GeoNetwork";
	}
	
	@Override
	protected String getServiceEndpointCategory() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_CATEGORY);
	}
	@Override
	protected String getServiceEndpointPlatformName() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_PLATFORM);
	}
	
	@Override
	protected String getServiceEndpointAccessPointName() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_ENDPOINT_NAME);
	}
	
	
	
	@Override
	protected List<Status> performInstanceCheck(ServiceEndpoint se) {
		return Collections.EMPTY_LIST;
	}
	
	@Override
	protected void checkDefinitionForServiceType(ServiceDefinition definition) {
		log.info("Checking geonetwork for {} ",definition);
		// Contact GN
		// try to login with credentials
		// check priority of other GNs against the defined one
	}
	
	@Override
	protected ServiceEndpoint prepareEndpoint(ServiceDefinition definition) throws ServiceRegistrationException {
		ServiceEndpoint toReturn= super.prepareEndpoint(definition);
		GeoNetworkServiceDefinition gnDefinition=(GeoNetworkServiceDefinition) definition;
		
		AccessPoint point=new AccessPoint();
		point.address("http://"+definition.getHostname()+"/geonetwork");
		point.credentials(ISUtils.encryptString(definition.getAdminPassword()), "admin");
		point.description("Main Access point");
		point.name(getServiceEndpointAccessPointName());
		
		// Priority property
		Property priorityProperty=new Property();
		priorityProperty.nameAndValue("priority", gnDefinition.getPriority()+"");
		point.properties().add(priorityProperty);
		// Suffixes property
		Property suffixesProperty=new Property();
		suffixesProperty.nameAndValue("suffixes", "");
		point.properties().add(suffixesProperty);
		
		toReturn.profile().accessPoints().add(point);
		
		return toReturn;
	}
	
	
	@Override
	protected void checkDefinitionType(ServiceDefinition definition) throws InvalidServiceDefinitionException {
		if(!definition.getType().equals(Type.GEONETWORK)||!(definition instanceof GeoNetworkServiceDefinition)) 
			throw new InvalidServiceDefinitionException("Invalid service type [expected "+Type.GEONETWORK+"]. Definition was "+definition);
	}
}
