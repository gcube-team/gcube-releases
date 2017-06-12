package org.gcube.spatial.data.sdi.engine.impl.cache;

import java.util.Collections;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;
import org.gcube.spatial.data.sdi.model.service.Version;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ThreddsRetriever implements ObjectRetriever<ThreddsConfiguration> {

	@Override
	public ThreddsConfiguration getObject() throws ConfigurationNotFoundException{
		log.info("Loading Thredds information from IS. Current Scope is {} ",ScopeUtils.getCurrentScope());


		LocalConfiguration config=LocalConfiguration.get();


		// Try to look for GCore Endpoints first
		String geClass=config.getProperty(LocalConfiguration.THREDDS_GE_SERVICE_CLASS);
		String geName=config.getProperty(LocalConfiguration.THREDDS_GE_SERVICE_NAME);
		List<GCoreEndpoint> gCoreEndpoints=ISUtils.queryForGCoreEndpoint(geClass, geName);
		if(gCoreEndpoints!=null&&!gCoreEndpoints.isEmpty()){
			log.debug("Found {} GCore Endpoints ",gCoreEndpoints.size());
			for(int i=0;i<gCoreEndpoints.size();i++){
				GCoreEndpoint endpoint=gCoreEndpoints.get(i);
				try{
					log.debug("Checking element {}, ID {} ",i,endpoint.id());
					ThreddsConfiguration toReturn=translate(endpoint);
					if(toReturn==null) throw new Exception("Translated configuration was null");
					return toReturn;
				}catch(Throwable t){
					log.warn("Unable to read retrieved gCore endpoint ID "+endpoint.id(),t);
				}			
			}
		}

		// Code is executed only if no configuration has been retrieved from gCore endpoints
		String seCategory=config.getProperty(LocalConfiguration.THREDDS_SE_CATEGORY);
		String sePlatform=config.getProperty(LocalConfiguration.THREDDS_SE_PLATFORM);

		List<ServiceEndpoint> threddsSE=ISUtils.queryForServiceEndpoints(seCategory, sePlatform);
		if(threddsSE!=null&&!threddsSE.isEmpty()){
			log.debug("Found {} Service Endpoints ",threddsSE.size());
			for(int i=0;i<threddsSE.size();i++){
				ServiceEndpoint endpoint=threddsSE.get(i);
				try{
					log.debug("Checking element {}, ID {} ",i,endpoint.id());
					ThreddsConfiguration toReturn=translate(endpoint);
					if(toReturn==null) throw new Exception("Translated configuration was null");
					return toReturn;
				}catch(Throwable t){
					log.warn("Unable to read retrieved service endpoint ID "+endpoint.id(),t);
				}	
			}
		}

		throw new ConfigurationNotFoundException("Thredds has not been found in current scope "+ScopeUtils.getCurrentScope());

	}


	private static final ThreddsConfiguration translate(GCoreEndpoint toTranslate){
//
//		ThreddsConfiguration toReturn=new ThreddsConfiguration(version, baseEndpoint, accessibleCredentials);
		return null;
	}

	private static final ThreddsConfiguration translate(ServiceEndpoint toTranslate){
		Platform platform=toTranslate.profile().platform();
		Version version=new Version(platform.version(),platform.minorVersion(),platform.revisionVersion());
		AccessPoint access=toTranslate.profile().accessPoints().iterator().next();
		Credentials credentials=new Credentials(access.username(),access.password(),AccessType.ADMIN);		
		return new ThreddsConfiguration(version, access.address(), Collections.singletonList(credentials));
	}
}
