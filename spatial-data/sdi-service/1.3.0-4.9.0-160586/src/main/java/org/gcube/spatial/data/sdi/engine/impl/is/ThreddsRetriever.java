package org.gcube.spatial.data.sdi.engine.impl.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.NetUtils;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceDefinitionException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.health.Level;
import org.gcube.spatial.data.sdi.model.health.Status;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.gcube.spatial.data.sdi.model.service.Version;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition.Type;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ThreddsRetriever extends AbstractISModule {

//	@Override
//	public ThreddsDescriptor getObject() throws ConfigurationNotFoundException{
//		log.info("Loading Thredds information from IS. Current Scope is {} ",ScopeUtils.getCurrentScope());
//
//		// Try to look for GCore Endpoints first
//		
////		List<GCoreEndpoint> gCoreEndpoints=getGcoreEndpoints();
////		if(gCoreEndpoints!=null&&!gCoreEndpoints.isEmpty()){
////			log.debug("Found {} GCore Endpoints ",gCoreEndpoints.size());
////			for(int i=0;i<gCoreEndpoints.size();i++){
////				GCoreEndpoint endpoint=gCoreEndpoints.get(i);
////				try{
////					log.debug("Checking element {}, ID {} ",i,endpoint.id());
////					ThreddsConfiguration toReturn=translate(endpoint);
////					if(toReturn==null) throw new Exception("Translated configuration was null");
////					return toReturn;
////				}catch(Throwable t){
////					log.warn("Unable to read retrieved gCore endpoint ID "+endpoint.id(),t);
////				}			
////			}
////		}
//
//		// Code is executed only if no configuration has been retrieved from gCore endpoints
//		
//		List<ServiceEndpoint> threddsSE=getServiceEndpoints();
//		if(threddsSE!=null&&!threddsSE.isEmpty()){
//			log.debug("Found {} Service Endpoints ",threddsSE.size());
//			for(int i=0;i<threddsSE.size();i++){
//				ServiceEndpoint endpoint=threddsSE.get(i);
//				try{
//					log.debug("Checking element {}, ID {} ",i,endpoint.id());
//					ThreddsDescriptor toReturn=translate(endpoint);
//					if(toReturn==null) throw new Exception("Translated configuration was null");
//					return toReturn;
//				}catch(Throwable t){
//					log.warn("Unable to read retrieved service endpoint ID "+endpoint.id(),t);
//				}	
//			}
//		}
//
//		throw new ConfigurationNotFoundException("Thredds has not been found in current scope "+ScopeUtils.getCurrentScope());
//
//	}

	
//	@Override
//	public ServiceHealthReport getHealthReport() {	
//		List<Status> checkStatuses=new ArrayList<>();
//		try {
//		
//		log.trace("Checking Thredds heatlh under context {} ",ScopeUtils.getCurrentScope());
//		//Check if existing
//		List<GCoreEndpoint> gCoreEndpoints=getGcoreEndpoints();
//		List<ServiceEndpoint> serviceEndpoints=getServiceEndpoints();
//		log.debug("Found {} GC Endpoints and {} SE Endpoints",gCoreEndpoints.size(),serviceEndpoints.size());
//		
//		if(serviceEndpoints.isEmpty())			
//			if(gCoreEndpoints.isEmpty())checkStatuses.add(new Status("No Thredds service found in context "+ScopeUtils.getCurrentScope(),Level.ERROR));
//			else checkStatuses.add(new Status("Unregistered Thredds instances found. Check following messages",Level.ERROR));
//		
//		//For each GC check for missing SE 
//		for(GCoreEndpoint gc:gCoreEndpoints) {
//			String hostname= gc.profile().endpoints().iterator().next().uri().getHost();
//			if(ISUtils.getGCEByHostname(hostname, serviceEndpoints)==null) {
//				String msg="Found unregistered Thredds hosted on "+hostname;
//				log.debug(msg);
//				checkStatuses.add(new Status(msg,Level.WARNING));
//			}
//		}
//		
//		for(ServiceEndpoint se: serviceEndpoints) {
//			
//		}
//		}catch(Throwable t) {
//			log.error("Unable to perform checks", t);
//			checkStatuses.add(new Status("Internal error while checking Thredds Status.",Level.ERROR));
//		}
//		return new ServiceHealthReport(checkStatuses);
//	}
	
	@Override
	protected String getGCoreEndpointServiceClass() {		
		return LocalConfiguration.getProperty(LocalConfiguration.THREDDS_GE_SERVICE_CLASS);
	}
	@Override
	protected String getGCoreEndpointServiceName() {
		return LocalConfiguration.getProperty(LocalConfiguration.THREDDS_GE_SERVICE_NAME);
	}
	
	@Override
	protected String getManagedServiceType() {
		return "THREDDS";
	}
	
	@Override
	protected String getServiceEndpointCategory() {
		return LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_CATEGORY);
	}
	@Override
	protected String getServiceEndpointPlatformName() {
		return LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_PLATFORM);
	}
	@Override
	protected String getServiceEndpointAccessPointName() {
		return LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_ENDPOINT_NAME);
	}
	
	@Override
	protected boolean isSmartGearsMandatory() {
		return LocalConfiguration.getFlag(LocalConfiguration.THREDDS_MANDATORY_SG);
	}
	
	@Override
	protected List<Status> performInstanceCheck(ServiceEndpoint se) {
		ArrayList<Status> toReturn=new ArrayList<Status>();
		
		String hostname=se.profile().runtime().hostedOn();
		try {
		log.trace("Checking thredds hosted on {} ",hostname);
		String publicCatalogUrl="www."+hostname+"/thredds/catalog/public/netcdf/catalog.html";
		if(!NetUtils.isUp(publicCatalogUrl))
			toReturn.add(new Status("Unreachable default THREDDS catalog at "+publicCatalogUrl,Level.ERROR));
		else {
			
//			
//			
//			DataTransferClient client=DataTransferClient.getInstanceByEndpoint(hostname);
//			//check SIS plugin presence
//			boolean found=false;
//			for(PluginDescription desc: client.getDestinationCapabilities().getAvailablePlugins())
//				if(desc.getId().equals("SIS/GEOTK")) {
//					found=true;
//					break;
//				}
//			if(!found) toReturn.add(new Status("SIS/GEOTK plugin for DataTransfer service not found on "+hostname, Level.ERROR));
				
		}			
		}catch(IOException e) {
			String msg="Unable to check thredds instance hosted on "+hostname;
			log.warn(msg);
			log.debug("Exception was ",e);
			toReturn.add(new Status(msg,Level.WARNING));
//		} catch (DataTransferException e) {
//			String msg="DataTransfer not found in host "+hostname;
//			log.warn(msg);
//			log.debug("Exception was ",e);
//			toReturn.add(new Status(msg,Level.ERROR));
		} 
		return toReturn;
	}
	
	

//	private static final ThreddsConfiguration translate(GCoreEndpoint toTranslate){
////
////		ThreddsConfiguration toReturn=new ThreddsConfiguration(version, baseEndpoint, accessibleCredentials);
//		return null;
//	}

	private static final ThreddsDescriptor translate(ServiceEndpoint toTranslate){
		Platform platform=toTranslate.profile().platform();
		Version version=new Version(platform.version(),platform.minorVersion(),platform.revisionVersion());
		AccessPoint access=toTranslate.profile().accessPoints().iterator().next();
		Credentials credentials=new Credentials(access.username(),access.password(),AccessType.ADMIN);		
		return new ThreddsDescriptor(version, access.address(), Collections.singletonList(credentials));
	}
	
	
	@Override
	protected void checkDefinitionForServiceType(ServiceDefinition definition)
			throws InvalidServiceDefinitionException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void checkDefinitionType(ServiceDefinition definition) throws InvalidServiceDefinitionException {
		if(!definition.getType().equals(Type.THREDDS)||!(definition instanceof ThreddsDefinition)) 
			throw new InvalidServiceDefinitionException("Invalid service type [expected "+Type.THREDDS+"]. Definition was "+definition);
	}
	
}
