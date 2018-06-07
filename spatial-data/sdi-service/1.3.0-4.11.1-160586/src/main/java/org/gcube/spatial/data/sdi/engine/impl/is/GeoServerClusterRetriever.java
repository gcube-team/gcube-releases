package org.gcube.spatial.data.sdi.engine.impl.is;

import java.util.Collections;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceDefinitionException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.health.Status;
import org.gcube.spatial.data.sdi.model.service.GeoServerDescriptor;
import org.gcube.spatial.data.sdi.model.service.Version;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoServerClusterRetriever extends  AbstractISModule{

	
//	@Override
//	public List<ServiceEndpoint> ge throws ConfigurationNotFoundException {
//		//TODO skip library
//				//TODO use both GCoreEndpoints and ServiceEndpoint
//		try {
//			ArrayList<GeoServerDescriptor> availableInstances=new ArrayList<>();
//			for(ServiceEndpoint ep: getServiceEndpoints()) {
//				try{
//					availableInstances.add(translate(ep));
//				}catch(Throwable t) {
//					log.warn("Unable to translate ServiceEndpoint [ID : {}].",ep.id(),t);
//				}
//			}
//		}catch(Throwable e){
//			log.warn("Unable to gather geoserver cluster configuration on scope "+ScopeUtils.getCurrentScope(),e);
//			throw new ConfigurationNotFoundException("Unable to gather geoserver cluster configuration. Please ontact administrator.",e);
//		}
//		
//		log.info("Retrieving GeoServer cluster configuration under scope {}",ScopeUtils.getCurrentScope());
//		try{
//			GISInterface gis=GISInterface.get();
//		ArrayList<GeoServerDescriptor> availableInstances=new ArrayList<>();
//		for(AbstractGeoServerDescriptor desc: gis.getCurrentCacheElements(true)){
//			try{
//				availableInstances.add(translate(desc));
//			}catch(Throwable t){
//				log.warn("Unable to translate descriptor for endpoint"+desc.getUrl(),t);
//			}
//		}
//		
//		return new GeoServerCluster(availableInstances);
//		}catch(Exception e){
//			log.warn("Unable to gather geoserver cluster configuration on scope "+ScopeUtils.getCurrentScope(),e);
//			throw new ConfigurationNotFoundException("Unable to gather geoserver cluster configuration. Please ontact administrator.",e);
//		}
//	}
	
	
	@Override
	protected boolean isSmartGearsMandatory() {
		return LocalConfiguration.getFlag(LocalConfiguration.GEOSERVER_MANDATORY_SG);
	}
	
	
	
	private static final GeoServerDescriptor translate(AbstractGeoServerDescriptor desc){
		Version version=new Version(2,1,2);
		String baseEndpoint=desc.getUrl();
		List<Credentials> accessibleCredentials=Collections.singletonList(new Credentials(desc.getUser(), desc.getPassword(), AccessType.ADMIN));
		String confidentialWorkspace=null;
		String contextVisibilityWorkspace=null;
		String sharedWorkspace=null;
		String publicWorkspace=null;
		return new GeoServerDescriptor(version, baseEndpoint, accessibleCredentials, confidentialWorkspace, contextVisibilityWorkspace, sharedWorkspace, publicWorkspace);
	}
	
	@Override
	protected String getGCoreEndpointServiceClass() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEOSERVER_GE_SERVICE_CLASS);
	}
	@Override
	protected String getGCoreEndpointServiceName() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEOSERVER_GE_SERVICE_NAME);
	}
	@Override
	protected String getManagedServiceType() {
		return "GeoServer";
	}
	
	@Override
	protected String getServiceEndpointAccessPointName() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEOSERVER_SE_ENDPOINT_NAME);
	}
	
	@Override
	protected String getServiceEndpointCategory() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEOSERVER_SE_CATEGORY);
	}
	@Override
	protected String getServiceEndpointPlatformName() {
		return LocalConfiguration.getProperty(LocalConfiguration.GEOSERVER_SE_PLATFORM);
	}
	@Override
	protected List<Status> performInstanceCheck(ServiceEndpoint se) {
		return Collections.EMPTY_LIST;
	}
	
	@Override
	protected void checkDefinitionForServiceType(ServiceDefinition definition)
			throws InvalidServiceDefinitionException {
		// Contact GN
		// try to login with credentials
	}
	@Override
	protected void checkDefinitionType(ServiceDefinition definition) throws InvalidServiceDefinitionException {
		if(!definition.getType().equals(Type.GEOSERVER)||!(definition instanceof GeoServerDefinition)) 
			throw new InvalidServiceDefinitionException("Invalid service type [expected "+Type.GEOSERVER+"]. Definition was "+definition);
	}
	
	
}
