package org.gcube.spatial.data.sdi.engine.impl.is;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceDefinitionException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.health.Level;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.health.Status;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractISModule implements ISModule {

	protected abstract String getGCoreEndpointServiceClass();
	protected abstract String getGCoreEndpointServiceName();
	protected abstract String getServiceEndpointAccessPointName();
	protected abstract String getServiceEndpointCategory();
	protected abstract String getServiceEndpointPlatformName();

	protected abstract String getManagedServiceType();

	protected abstract boolean isSmartGearsMandatory();

	@Override
	public List<ServiceEndpoint> getISInformation() throws ConfigurationNotFoundException {
		log.trace("Getting current information from IS. Scope {} ",ScopeUtils.getCurrentScope());
		ArrayList<ServiceEndpoint> toReturn=new ArrayList<>();
		log.debug("MANDATORY SG FOR {} is {} ",getManagedServiceType(),isSmartGearsMandatory());
		if(isSmartGearsMandatory()) {
			List<GCoreEndpoint> GCs=queryGcoreEndpoints();
			for(ServiceEndpoint ep: queryServiceEndpoints()) {
				String host=ISUtils.getHost(ep);
				log.debug("Checking if ServiceEndpoint at {} is SmartGears .. ",host);
				try{
					if(ISUtils.getByHostnameInCollection(host, GCs)!=null) {
						log.debug("Found GC. Service Endpoint {} seems valid. ",ep.profile().name());
						toReturn.add(ep);
					}
				}catch(UnknownHostException e) {
					log.warn("Unexpected Exception while checking hostnames. Please check configuration.",e);
				}
			}
		}else {
			toReturn.addAll(queryServiceEndpoints());
		}
		return toReturn;
	}


	@Override
	public ServiceHealthReport getHealthReport() {
		List<Status> checkStatuses=new ArrayList<>();
		try {

			log.trace("Checking {} heatlh under context {} ",getManagedServiceType(),ScopeUtils.getCurrentScope());
			//Check if existing
			List<GCoreEndpoint> gCoreEndpoints=queryGcoreEndpoints();
			List<ServiceEndpoint> serviceEndpoints=queryServiceEndpoints();
			log.debug("Found {} GC Endpoints and {} SE Endpoints",gCoreEndpoints.size(),serviceEndpoints.size());

			if(serviceEndpoints.isEmpty())			
				if(gCoreEndpoints.isEmpty())checkStatuses.add(new Status("No "+getManagedServiceType()+" found in context "+ScopeUtils.getCurrentScope(),Level.ERROR));
				else checkStatuses.add(new Status("Unregistered "+getManagedServiceType()+" instances found. Check following messages",Level.ERROR));

			//For each GC check for missing SE 
			for(GCoreEndpoint gc:gCoreEndpoints) {
				String hostname= gc.profile().endpoints().iterator().next().uri().getHost();
				if(ISUtils.getByHostnameInCollection(hostname, serviceEndpoints)==null) {
					String msg="Found unregistered "+getManagedServiceType()+" hosted on "+hostname;
					log.debug(msg);
					checkStatuses.add(new Status(msg,Level.WARNING));
				}
			}


			for(ServiceEndpoint se : serviceEndpoints) {
				try {
					//check if GC up & running
					String hostname=se.profile().runtime().hostedOn();
					GCoreEndpoint found=ISUtils.getByHostnameInCollection(hostname, gCoreEndpoints);

					if(found==null)
						checkStatuses.add(new Status("Service endpoint [name = "+se.profile().name()+", host = "+hostname+" ID = "+se.id()+"] found but no related GC is present.",Level.ERROR));
					else {
						String status=found.profile().deploymentData().status();
						switch(status) {
						case "unreachable" :
						case "down" : checkStatuses.add(new Status("GCoreEndpoint [ID "+found.id()+"] for instance hosted on "+hostname+" has status : "+status,Level.ERROR));
						break;
						default : 
						}
					}


					// perform specific checks
					checkStatuses.addAll(performInstanceCheck(se));
				}catch(Throwable t) {
					log.error("Unable to perform checks on SE "+se.id(), t);
					checkStatuses.add(new Status("Internal error while checking "+getManagedServiceType()+" [SE ID : "+se.id()+"]."+t.getMessage(),Level.ERROR));
				}
			}			

		}catch(Throwable t) {
			log.error("Unable to perform checks", t);
			checkStatuses.add(new Status("Internal error while checking "+getManagedServiceType()+" Status.",Level.ERROR));
		}
		return new ServiceHealthReport(checkStatuses);
	}

	protected abstract List<Status> performInstanceCheck(ServiceEndpoint se);


	protected List<GCoreEndpoint> queryGcoreEndpoints(){		
		String geClass=getGCoreEndpointServiceClass();
		String geName=getGCoreEndpointServiceName();
		return ISUtils.queryForGCoreEndpoint(geClass, geName);
	}


	protected List<ServiceEndpoint> queryServiceEndpoints(){		
		String seCategory=getServiceEndpointCategory();
		String sePlatform=getServiceEndpointPlatformName();
		return ISUtils.queryForServiceEndpoints(seCategory, sePlatform);
	}



	@Override
	public String importHostFromToken(String sourceToken, String host) throws ServiceRegistrationException {

		log.trace("Importing host {} from token {} ",host,sourceToken);
		String callerScope=ScopeUtils.getCurrentScope();
		String callerToken=SecurityTokenProvider.instance.get();
		try {
			//Checking if already present
			List<ServiceEndpoint> existingSEs=ISUtils.querySEByHostname(getServiceEndpointCategory(), getServiceEndpointPlatformName(), host);
			if(existingSEs.size()>0) {				
				throw new ServiceRegistrationException("HOST "+host+" is already registered in current scope with ID : "+existingSEs.get(0).id());
			}

			// Getting from sourceToken..
			SecurityTokenProvider.instance.set(sourceToken);		
			log.debug("Source token {} is from scope {}.",sourceToken,ScopeUtils.getCurrentScope());
			List<ServiceEndpoint> foundSEs=ISUtils.querySEByHostname(getServiceEndpointCategory(), getServiceEndpointPlatformName(), host);
			if(foundSEs.size()>1) throw new ServiceRegistrationException("Too many ServiceEndpoints found with hostname "+host);
			else if(foundSEs.isEmpty()) throw new ServiceRegistrationException("No ServiceEndpoints found with hostname "+host);

			ServiceEndpoint toImportSE= foundSEs.get(0);
			try {
				GCoreEndpoint toImportGC = ISUtils.getByHostnameInCollection(host, queryGcoreEndpoints());
				if(toImportGC==null) throw new ServiceRegistrationException("No GCoreEndpoint found for hostname "+host);

				log.debug("Registering resources to caller scope {} ",callerScope);
				return ISUtils.addToScope(toImportSE, toImportGC,callerScope);
			}catch(Exception e) {
				throw new ServiceRegistrationException("Unable to register resources",e);
			}


		}finally {
			if(!SecurityTokenProvider.instance.get().equals(callerToken))
				SecurityTokenProvider.instance.set(callerToken);
		}
	}

	@Override
	@Synchronized
	public String registerService(ServiceDefinition definition) throws ServiceRegistrationException {
		log.info("Registering {} ",definition);
		log.debug("Checking definition type..");
		checkDefinitionType(definition);
		log.debug("Checking IS ..");		
		checkDefinition(definition);
		log.debug("Performing type specific checks..");
		checkDefinitionForServiceType(definition);
		log.debug("Preparing ServiceEndpoint.. ");
		ServiceEndpoint ep=prepareEndpoint(definition);
		log.debug("Publishing resource..");
		String id=ISUtils.registerService(ep);

		List<String> registered=null;
		long registrationTime=System.currentTimeMillis();
		long timeout=Long.parseLong(LocalConfiguration.getProperty(LocalConfiguration.IS_REGISTRATION_TIMEOUT));
		do{
			log.debug("Waiting for IS to update. Passed {} ms.",(System.currentTimeMillis()-registrationTime));
			try{Thread.sleep(500);
			}catch(Exception e) {}

			registered=ISUtils.queryById(id);
		}while(registered.isEmpty()&&((System.currentTimeMillis()-registrationTime)<=timeout));
		if(registered.isEmpty()) {
			log.warn("Registered resource [ID :{}] was not found before Timeout of {} ms. Returning id. ",id,timeout);
			return id;
		}else 	return registered.get(0);
	}


	protected abstract void checkDefinitionForServiceType(ServiceDefinition definition) throws InvalidServiceDefinitionException; 
	protected abstract void checkDefinitionType(ServiceDefinition definition) throws InvalidServiceDefinitionException;

	protected void checkDefinition(ServiceDefinition definition) throws ServiceRegistrationException {		
		try{
			String hostname=definition.getHostname();
			List<ServiceEndpoint> serviceEndpoints=queryServiceEndpoints();
			ServiceEndpoint existing=ISUtils.getByHostnameInCollection(hostname, serviceEndpoints);
			if(existing!=null) {
				throw new ServiceRegistrationException("Service is already registered");
			}		
			List<GCoreEndpoint> gCoreNodes=queryGcoreEndpoints();
			GCoreEndpoint running=ISUtils.getByHostnameInCollection(hostname, gCoreNodes);
			if(running==null) throw new ServiceRegistrationException("No GCoreEndpoint found for "+definition); 
		}catch(ServiceRegistrationException e) {
			throw e;
		}catch(Throwable t) {
			throw new ServiceRegistrationException("Unexpected exception while trying to register "+definition, t); 
		}
	}

	protected ServiceEndpoint prepareEndpoint(ServiceDefinition definition) throws ServiceRegistrationException {
		try{
			ServiceEndpoint toCreate=new ServiceEndpoint();
			Profile profile=toCreate.newProfile();
			profile.category(getServiceEndpointCategory());
			profile.description(definition.getDescription());
			Platform platform=profile.newPlatform();
			platform.name(getServiceEndpointPlatformName()).
			version(definition.getMajorVersion()).
			minorVersion(definition.getMinorVersion()).
			revisionVersion(definition.getReleaseVersion());

			org.gcube.common.resources.gcore.ServiceEndpoint.Runtime runtime=profile.newRuntime();
			runtime.hostedOn(definition.getHostname());

			GCoreEndpoint relatedGHN=ISUtils.getByHostnameInCollection(definition.getHostname(), queryGcoreEndpoints());

			runtime.ghnId(relatedGHN.id());
			runtime.status("READY");

			return toCreate;
		}catch(Throwable t) {
			throw new ServiceRegistrationException("Unexpected exception while trying to register "+definition, t); 
		}
	}

}
