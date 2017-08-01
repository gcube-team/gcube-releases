package org.gcube.spatial.data.gis.is.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.gis.Configuration;
import org.gcube.spatial.data.gis.is.CachedGeoServerDescriptor;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ISGeoServerCache extends GeoServerCache<CachedGeoServerDescriptor> {

	private static final ConcurrentHashMap<String,ConcurrentSkipListSet<CachedGeoServerDescriptor>> scopedMap=new ConcurrentHashMap<String,ConcurrentSkipListSet<CachedGeoServerDescriptor>>();

	private static final ConcurrentHashMap<String,Long> scopeLastUpdate=new ConcurrentHashMap<String,Long>();

	@Override
	protected synchronized SortedSet<CachedGeoServerDescriptor> getTheCache(Boolean forceUpdate) {
		String scope=ScopeUtils.getCurrentScope();
		if(forceUpdate || 
				! scopedMap.containsKey(scope) || 
				System.currentTimeMillis()-getLastUpdate()>Configuration.getTTL(Configuration.IS_CACHE_TTL)){
			try{
				log.debug("Going to retrieve information from IS. Scope is {} ",scope);
				List<CachedGeoServerDescriptor> retrieved=queryforGeoServer();
				scopedMap.put(scope, new ConcurrentSkipListSet<CachedGeoServerDescriptor>(retrieved));
				log.trace("Retrieved {} instances in {}",retrieved.size(),scope);
				setUpdated();
			}catch(IOException e){
				log.error("Unable to query IS ",e);
			}
		}
		return scopedMap.get(scope);
	}


	private static synchronized Long getLastUpdate(){
		String scope=ScopeUtils.getCurrentScope();
		log.debug("Accessing lastUpdate in scope {} ",scope);
		return scopeLastUpdate.containsKey(scope)?scopeLastUpdate.get(scope):0l;
	}

	private static synchronized void setUpdated(){
		String scope=ScopeUtils.getCurrentScope();
		log.debug("Setting update for scope {} ",scope);
		scopeLastUpdate.put(scope, System.currentTimeMillis());
	}

	private static List<CachedGeoServerDescriptor> queryforGeoServer() throws IOException{
		List<CachedGeoServerDescriptor> toReturn=new ArrayList<CachedGeoServerDescriptor>();
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		String category=Configuration.get().getProperty(Configuration.IS_SERVICE_PROFILE_CATEGORY);
		String name=Configuration.get().getProperty(Configuration.IS_SERVICE_PROFILE_PLATFORM_NAME);

		log.debug("Querying IS for service profiles category {} , name {} ",category,name);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+name+"'")
		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);

		List<AccessPoint> accesspoints = client.submit(query);

		for (AccessPoint point : accesspoints) {
			try{
				toReturn.add(new CachedGeoServerDescriptor(point.address(),point.username(),StringEncrypter.getEncrypter().decrypt(point.password())));
			}catch(Exception e){
				log.warn("Skipping Geoserver at {}",point.address(),e); 
			}

		}
		return toReturn; 
	}
}
