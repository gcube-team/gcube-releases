package org.gcube.spatial.data.sdi.engine.impl.is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.gis.GISInterface;
import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceDefinitionException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.health.Status;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.model.service.GeoServerConfiguration;
import org.gcube.spatial.data.sdi.model.service.Version;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoServerClusterRetriever extends  AbstractISModule<GeoServerClusterConfiguration>{

	
	@Override
	public GeoServerClusterConfiguration getObject() throws ConfigurationNotFoundException {
		//TODO skip library
				//TODO use both GCoreEndpoints and ServiceEndpoint
		try {
			ArrayList<GeoServerConfiguration> availableInstances=new ArrayList<>();
			for(ServiceEndpoint ep: getServiceEndpoints()) {
				try{
					availableInstances.add(translate(ep));
				}catch(Throwable t) {
					log.warn("Unable to translate ServiceEndpoint [ID : {}].",ep.id(),t);
				}
			}
		}catch(Throwable e){
			log.warn("Unable to gather geoserver cluster configuration on scope "+ScopeUtils.getCurrentScope(),e);
			throw new ConfigurationNotFoundException("Unable to gather geoserver cluster configuration. Please ontact administrator.",e);
		}
		
		
		
		
		log.info("Retrieving GeoServer cluster configuration under scope {}",ScopeUtils.getCurrentScope());
		try{
			GISInterface gis=GISInterface.get();
		ArrayList<GeoServerConfiguration> availableInstances=new ArrayList<>();
		for(AbstractGeoServerDescriptor desc: gis.getCurrentCacheElements(true)){
			try{
				availableInstances.add(translate(desc));
			}catch(Throwable t){
				log.warn("Unable to translate descriptor for endpoint"+desc.getUrl(),t);
			}
		}
		
		return new GeoServerClusterConfiguration(availableInstances);
		}catch(Exception e){
			log.warn("Unable to gather geoserver cluster configuration on scope "+ScopeUtils.getCurrentScope(),e);
			throw new ConfigurationNotFoundException("Unable to gather geoserver cluster configuration. Please ontact administrator.",e);
		}
	}
	
	private final GeoServerConfiguration translate(ServiceEndpoint ep) {
		GeoServerConfiguration toReturn=new GeoServerConfiguration();
		
		Profile profile=ep.profile();
		AccessPoint point=null;		
		for(AccessPoint declaredPoint:profile.accessPoints().asCollection()) {
			if(declaredPoint.name().equals(getServiceEndpointAccessPointName())) {
				point=declaredPoint;
				break;
			}
		}
		toReturn.setBaseEndpoint(point.address());
		

		
		
		String scopeName=ScopeUtils.getCurrentScopeName();
		
		//Getting Scope credentials
		List<Credentials> accessibleCredentials=toReturn.getAccessibleCredentials();
		//Admin credentials
		accessibleCredentials.add(new Credentials(point.username(),ISUtils.decryptString(point.password()),AccessType.ADMIN));
		
		Map<String,Property> pointProperties=point.propertyMap();
		for(AccessType toLookForType:AccessType.values()) {
			String userNameProperty=toLookForType+"_u_"+scopeName;
			String passwordProperty=toLookForType+"_u_"+scopeName;
			if(pointProperties.containsKey(userNameProperty)) {
				String user=pointProperties.get(userNameProperty).value();
				String password=ISUtils.decryptString(pointProperties.get(passwordProperty).value());
				accessibleCredentials.add(new Credentials(user,password,toLookForType));
			}
		}
		
		//Getting scope data spaces
		String confidentialProperty="confidential_"+scopeName;
		if(pointProperties.containsKey(confidentialProperty))
			toReturn.setConfidentialWorkspace(pointProperties.get(confidentialProperty).value());
		String contextProperty="context_"+scopeName;
		if(pointProperties.containsKey(contextProperty))
			toReturn.setContextVisibilityWorkspace(pointProperties.get(contextProperty).value());	
		String sharedProperty="shared_"+scopeName;
		if(pointProperties.containsKey(sharedProperty))
			toReturn.setSharedWorkspace(pointProperties.get(sharedProperty).value());
		String publicProperty="public_"+scopeName;
		if(pointProperties.containsKey(publicProperty))
			toReturn.setPublicWorkspace(pointProperties.get(publicProperty).value());
		
		// Getting version
		Platform platform=profile.platform();
		toReturn.setVersion(new Version(platform.version(),platform.minorVersion(),platform.revisionVersion()));
		return toReturn;
		
	}
	
	private static final GeoServerConfiguration translate(AbstractGeoServerDescriptor desc){
		Version version=new Version(2,1,2);
		String baseEndpoint=desc.getUrl();
		List<Credentials> accessibleCredentials=Collections.singletonList(new Credentials(desc.getUser(), desc.getPassword(), AccessType.ADMIN));
		String confidentialWorkspace=null;
		String contextVisibilityWorkspace=null;
		String sharedWorkspace=null;
		String publicWorkspace=null;
		return new GeoServerConfiguration(version, baseEndpoint, accessibleCredentials, confidentialWorkspace, contextVisibilityWorkspace, sharedWorkspace, publicWorkspace);
	}
	
	@Override
	protected String getGCoreEndpointServiceClass() {
		return LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_GE_SERVICE_CLASS);
	}
	@Override
	protected String getGCoreEndpointServiceName() {
		return LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_GE_SERVICE_NAME);
	}
	@Override
	protected String getManagedServiceType() {
		return "GeoServer";
	}
	
	@Override
	protected String getServiceEndpointAccessPointName() {
		return LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_SE_ENDPOINT_NAME);
	}
	
	@Override
	protected String getServiceEndpointCategory() {
		return LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_SE_CATEGORY);
	}
	@Override
	protected String getServiceEndpointPlatformName() {
		return LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_SE_PLATFORM);
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
