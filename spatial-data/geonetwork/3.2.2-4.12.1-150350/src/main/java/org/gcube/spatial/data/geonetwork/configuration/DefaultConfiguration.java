package org.gcube.spatial.data.geonetwork.configuration;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess.Version;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.EncryptionUtils;
import org.gcube.spatial.data.geonetwork.utils.RuntimeParameters;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DefaultConfiguration implements  Configuration {

	protected static Properties props=null;

	static{
		log.debug("Loading properties");
		try{			
			props=new RuntimeParameters().getProps();
			log.trace("LOADED PROPERTIES : "+props);
			//			// per scope
			//			assignedScopePrefix=props.getProperty(RuntimeParameters.assignedScopePrefix);
			//			scopeUserPrefix=props.getProperty(RuntimeParameters.scopeUserPrefix);
			//			scopePasswordPrefix=props.getProperty(RuntimeParameters.scopePasswordPrefix);
			//			ckanUserPrefix=props.getProperty(RuntimeParameters.ckanUserPrefix);
			//			ckanPasswordPrefix=props.getProperty(RuntimeParameters.ckanPasswordPrefix);
			//			defaultGroupPrefix=props.getProperty(RuntimeParameters.defaultGroupPrefix);
			//			privateGroupPrefix=props.getProperty(RuntimeParameters.privateGroupPrefix);
			//			publicGroupPrefix=props.getProperty(RuntimeParameters.publicGroupPrefix);

			// global
			geonetworkCategory=props.getProperty(RuntimeParameters.geonetworkCategory);
			geonetworkPlatformName=props.getProperty(RuntimeParameters.geonetworkPlatformName);
			endpointName=props.getProperty(RuntimeParameters.geonetworkEndpointName);
			priorityProperty=props.getProperty(RuntimeParameters.priorityProperty);
		}catch(Exception e){
			log.error("Unable to load properties",e);
		}
	}

	//***************************** Properties are loaded at startup
	//	// per scope
	//	private static String assignedScopePrefix=null;
	//	private static String scopeUserPrefix=null;
	//	private static String scopePasswordPrefix=null;
	//	private static String ckanUserPrefix=null;
	//	private static String ckanPasswordPrefix=null;
	//	private static String defaultGroupPrefix=null;
	//	private static String privateGroupPrefix=null;
	//	private static String publicGroupPrefix=null;

	// global
	private static String geonetworkCategory;
	private static String geonetworkPlatformName;
	private static String endpointName;
	private static String priorityProperty;
	//***************************** INSTANCE VALUES
	private String geonetworkUrl=null;
	private String adminUserValue=null;
	private String adminPasswordValue=null;
	private ScopeConfiguration configuration=null;


	public DefaultConfiguration() throws EncryptionException, MissingConfigurationException{
	}









	//**************************** INTERFACE IMPLEMENTATION

	@Override
	public Version getGeoNetworkVersion() throws MissingServiceEndpointException {
		short version=getISServiceEndpoint().profile().platform().version();
		if(version<3) return Version.DUE;
		else return Version.TRE;
	}
	
	
	@Override
	public Account getAdminAccount() throws MissingServiceEndpointException {
		loadFromIs();
		return new Account(adminUserValue,adminPasswordValue,Type.SCOPE);
	}

	@Override
	public String getGeoNetworkEndpoint() throws MissingServiceEndpointException{
		loadFromIs();
		return geonetworkUrl;
	}

	@Override
	public ScopeConfiguration getScopeConfiguration()throws MissingConfigurationException, MissingServiceEndpointException{
		loadFromIs();
		if(configuration==null) throw new MissingConfigurationException("Scope not configured.");
		return configuration;	 
	}

	@Override
	public void createScopeConfiguration(ScopeConfiguration toCreate) throws MissingServiceEndpointException{		
		storeAndReload(toCreate); 
	}

	@Override
	public ScopeConfiguration acquireConfiguration() throws MissingServiceEndpointException, MissingConfigurationException {
		log.debug("Loading existing configurations");
		Set<ScopeConfiguration> existing=getExistingConfigurations();
		log.debug("Found "+existing.size()+" configurations, checking for availability");
		ScopeConfiguration available=ScopeConfigurationUtils.getByScope(existing, ScopeConfiguration.NOT_ASSIGNED);
		available.setAssignedScope(ScopeUtils.getCurrentScopeName());
		storeAndReload(available);
		return configuration;
	}


	@Override
	public Set<ScopeConfiguration> getExistingConfigurations() throws MissingServiceEndpointException{
		return ScopeConfigurationUtils.fromMap(getIsAccessPoint().propertyMap(),props);
	}

	@Override
	public Set<ScopeConfiguration> getParentScopesConfiguration() throws MissingServiceEndpointException{
		List<String> parentScopes=ScopeUtils.getParentScopes();
		HashSet<ScopeConfiguration> toReturn=new HashSet<>();

		if(!parentScopes.isEmpty()) {// in order to avoid loading from IS

			Set<ScopeConfiguration> existing=getExistingConfigurations();
			for(String parent:parentScopes)
				try{
					toReturn.add(ScopeConfigurationUtils.getByScope(existing, parent));
				}catch(MissingConfigurationException e){
					log.warn("Parent scope {} found but no related configuration. Existing {}",parent,existing.toString());
				}
		}
		return toReturn; 
	}

	//****************************** IS LOGIC

	private boolean loaded=false;
	
	
	/**
	 * loads configuration from Is
	 * @throws MissingServiceEndpointException 
	 * 
	 */
	@Synchronized
	protected void loadFromIs() throws MissingServiceEndpointException{
		if(!loaded || 
				((configuration!=null)&&(!configuration.getAssignedScope().equals(ScopeUtils.getCurrentScopeName())))){ // reuse of configuration in different scopes
			log.debug("IS Information  not loaded, doing it now..");
			AccessPoint point=getIsAccessPoint();	
			Map<String, Property> properties=point.propertyMap();
			geonetworkUrl=point.address();
			log.debug("Found master endpoint @ "+point.address()+", loading configuration map");
			adminUserValue=point.username();
			adminPasswordValue=EncryptionUtils.decrypt(point.password());
			try{
				configuration=ScopeConfigurationUtils.getByScope(ScopeConfigurationUtils.fromMap(properties, props), ScopeUtils.getCurrentScopeName());
				log.debug("Loaded config : "+configuration);
				loaded=true;
			}catch(MissingConfigurationException e){				
				log.debug("Configuration not found for current scope "+ScopeUtils.getCurrentScopeName());
				configuration=null;
			}

		}		
	}


	protected AccessPoint getIsAccessPoint() throws MissingServiceEndpointException{
		log.debug("Querying IS for Geonetwork information..");


		List<ServiceEndpoint> endpoints = doTheQuery(geonetworkCategory, geonetworkPlatformName);

		log.debug("Found "+endpoints.size()+" ServiceEndpoints");
		AccessPoint point=getTheRightAccessPoint(endpoints.toArray(new ServiceEndpoint[endpoints.size()]));
		if(point!=null) return point;			
		
		throw new MissingServiceEndpointException("No Resource found under current scope "+ScopeUtils.getCurrentScope());
	}

	
	protected ServiceEndpoint getISServiceEndpoint() throws MissingServiceEndpointException{
		log.debug("Querying IS for Geonetwork information..");


		List<ServiceEndpoint> endpoints = doTheQuery(geonetworkCategory, geonetworkPlatformName);

		log.debug("Found "+endpoints.size()+" ServiceEndpoints");
		ServiceEndpoint se=getTheRightServiceEndpoint(endpoints.toArray(new ServiceEndpoint[endpoints.size()]));
		
		if(se!=null) return se;			
		
		throw new MissingServiceEndpointException("No Resource found under current scope "+ScopeUtils.getCurrentScope());
		
	}
	

	/**
	 * look for the access point compliant with configured endpointName and maxPriority (1)
	 * 
	 * @return null if not present
	 */
	protected static final AccessPoint getTheRightAccessPoint(ServiceEndpoint... resources){
		AccessPoint toReturn=null;
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
							toReturn=point;
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
	protected static final ServiceEndpoint getTheRightServiceEndpoint(ServiceEndpoint... resources){
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
	
	
	protected List<ServiceEndpoint> doTheQuery(String geonetworkCategory, String geonetworkPlatformName){
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+geonetworkCategory+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+geonetworkPlatformName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}



	/**
	 * 		UPDATE existing values and store new ones
	 * 
	 * @param updatedMap
	 * @return
	 * @throws MissingServiceEndpointException 
	 * @throws MissingConfigurationException
	 * @throws EncryptionException
	 */
	protected void storeAndReload(ScopeConfiguration toStore) throws MissingServiceEndpointException {
		log.debug("Going to store : "+toStore);
		ServiceEndpoint toUpdateResource=getISServiceEndpoint();
		Map<String,Property> accessPointConfiguration=getTheRightAccessPoint(toUpdateResource).propertyMap();
		String toUseSuffix=null;
		try{
			toUseSuffix=ScopeConfigurationUtils.getSuffixByConfiguration(toStore, accessPointConfiguration, props);
			log.debug("Configuration was already defined, updateing it");
		}catch(MissingConfigurationException e){
			log.debug("Configuration is new, selecting new suffix");
			toUseSuffix=ScopeConfigurationUtils.generateSuffix(ScopeConfigurationUtils.getExistingSuffixes(accessPointConfiguration,props));
			ScopeConfigurationUtils.insertSuffix(accessPointConfiguration,toUseSuffix,props);
		}
		log.debug("Suffix for configuration is "+toUseSuffix);
		Map<String,Property> toAddValues=ScopeConfigurationUtils.asMap(toStore, toUseSuffix, props);
		accessPointConfiguration.putAll(toAddValues);
		getTheRightAccessPoint(toUpdateResource).properties().addAll(accessPointConfiguration.values());

		update(toUpdateResource);
		loaded=false;
		waitForISUpdate(toAddValues);
	}



	private void waitForISUpdate(Map<String,Property> toLookFor) throws MissingServiceEndpointException{		
		boolean updated=false;
		boolean continuePolling=true;
		long maxWait=Long.parseLong(props.getProperty(RuntimeParameters.isMaxWaitTimeMillis));
		long wait=500;
		long startPollingTime=System.currentTimeMillis();
		while(!updated&&continuePolling){
			try {				
				log.debug("Wating for IS [elapsedTime {}, max wait {}]",(System.currentTimeMillis()-startPollingTime),maxWait);
				Thread.sleep(wait);
			} catch (InterruptedException e) {}
			log.debug("loading IS information");
			ServiceEndpoint toUpdateResource=getISServiceEndpoint();
			Map<String,Property> accessPointConfiguration=getTheRightAccessPoint(toUpdateResource).propertyMap();

			// check if all values are updated
			for(Entry<String,Property> toLookForEntry:toLookFor.entrySet())
				if(accessPointConfiguration.containsKey(toLookForEntry.getKey())&&
						accessPointConfiguration.get(toLookForEntry.getKey()).equals(toLookForEntry.getValue()))
					updated=true;					
				else{
					updated=false;
					break;
				}
			wait=wait*2;
			continuePolling=(System.currentTimeMillis()-startPollingTime)<maxWait;			
		}
		if(!updated) log.warn("Polling timeout reached, IS was not updated");
	}


	protected ServiceEndpoint update(ServiceEndpoint toStore){
		RegistryPublisher rp=RegistryPublisherFactory.create();
		return rp.update(toStore);
	}




}
