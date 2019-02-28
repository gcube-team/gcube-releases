/**
 *
 */
package org.gcube.datatransfer.resolver.caches;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.catalogue.resource.GetAllInfrastructureVREs;
import org.gcube.datatransfer.resolver.init.UriResolverSmartGearManagerInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;



/**
 * The Class LoadingVREsScopeCache.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 8, 2018
 */
public class LoadingVREsScopeCache {

	private static Logger logger = LoggerFactory.getLogger(LoadingVREsScopeCache.class);
	private static LoadingCache<String, String> vresNameToScope;

	static{

		CacheLoader<String, String> loader = new CacheLoader<String, String>(){

			@Override
			public String load(String vreName)
				throws Exception {

				logger.info("Loading the cache for vreName: "+vreName);
				String fullScope = loadFullScopeforVreName(vreName);
				logger.info("Returning fullScope: "+fullScope+ " for the VRE name: "+vreName);
				return fullScope;
			}

		};

		RemovalListener<String, String> removalListener = new RemovalListener<String, String>() {

			@Override
			public void onRemoval(RemovalNotification<String, String> arg0) {

				logger.info("cache expired");
				//prePopulateCache();

			}
		};

		vresNameToScope = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(
			1, TimeUnit.DAYS).removalListener(removalListener).
			build(loader);


		//Populating the cache at init stage
		populateTheCache();
		logger.info("Pre-Loaded VRE to Scope cache with: "+vresNameToScope.asMap().size()+" item/s");
	}


	/**
	 * Populate the cache.
	 */
	private static void populateTheCache(){
		try{
			//POPULATE THE CACHE READING THE RESOURCE "CATALOGUE-RESOLVER"
			logger.info("Trying to pre-populate the cache with mapping (VRE Name, VRE Full Scope)");
		    ScopeProvider.instance.set(UriResolverSmartGearManagerInit.getRootContextScope());
			Map<String, String> map = GetAllInfrastructureVREs.loadMapOFVreNameToScope(UriResolverSmartGearManagerInit.getRootContextScope());
			vresNameToScope.asMap().putAll(map);
			logger.info("Cache populated with: "+vresNameToScope.asMap().toString());
			//logger.info("Pre-Loaded CatalogueApplicationProfiles cache is: "+catalogueApplicationProfiles.asMap().toString());

			if(UriResolverSmartGearManagerInit.getRootContextScope().compareTo("/gcube")==0){
				logger.warn("HARD-CABLING PARTHENOS_Registry scope to resolve PARTHENOS_REGISTRY Links in dev environment");
				vresNameToScope.asMap().put("PARTHENOS_Registry", "/d4science.research-infrastructures.eu/ParthenosVO/PARTHENOS_Registry");
			}

		}catch(Exception e){

		}finally{

		}
	}


	/**
	 * Gets the.
	 *
	 * @param vreName the vre name
	 * @return the string
	 * @throws ExecutionException
	 */
	public static String get(String vreName) throws ExecutionException{

		return vresNameToScope.get(vreName);
	}


	/**
	 * Load application profiles.
	 *
	 * @param vreName the vre name
	 * @return the string
	 */
	protected static String loadFullScopeforVreName(String vreName){

		//THIS CHECK SHOULD BE NOT NEEDED
		String fullScope = vresNameToScope.getIfPresent(vreName);

		if(fullScope==null){
			populateTheCache();
			fullScope = vresNameToScope.getIfPresent(vreName);
		}

		return fullScope;
	}

}
