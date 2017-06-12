package org.gcube.spatial.data.sdi.engine.impl.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.gis.GISInterface;
import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.model.service.GeoServerConfiguration;
import org.gcube.spatial.data.sdi.model.service.Version;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoServerClusterRetriever implements ObjectRetriever<GeoServerClusterConfiguration>{

	
	@Override
	public GeoServerClusterConfiguration getObject() throws ConfigurationNotFoundException {
		//TODO skip library
				//TODO use both GCoreEndpoints and ServiceEndpoint
		
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
}
