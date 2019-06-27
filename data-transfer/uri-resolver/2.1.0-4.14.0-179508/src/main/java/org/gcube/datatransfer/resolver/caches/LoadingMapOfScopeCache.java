/**
 *
 */
package org.gcube.datatransfer.resolver.caches;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datatransfer.resolver.catalogue.resource.GetAllInfrastructureScopes;
import org.gcube.datatransfer.resolver.init.UriResolverSmartGearManagerInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;


/**
 * The Class LoadingMapOfScopeCache.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 13, 2019
 */
public class LoadingMapOfScopeCache {

	private static Logger logger = LoggerFactory.getLogger(LoadingMapOfScopeCache.class);
	private static LoadingCache<String, ScopeBean> scopeNamesToFullScopes;

	static{

		CacheLoader<String, ScopeBean> loader = new CacheLoader<String, ScopeBean>(){

			@Override
			public ScopeBean load(String scopeName)
				throws Exception {

				logger.info("Loading the cache for scope: {}",scopeName);
				ScopeBean fullScope = loadFullScopeforScopeName(scopeName);
				logger.info("Returning {} for the Scope name: {}",ScopeBean.class.getSimpleName(), scopeName);
				return fullScope;
			}

		};

		RemovalListener<String, ScopeBean> removalListener = new RemovalListener<String, ScopeBean>() {

			@Override
			public void onRemoval(RemovalNotification<String, ScopeBean> arg0) {

				logger.info("cache expired");
				//prePopulateCache();

			}
		};

		scopeNamesToFullScopes = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(
			1, TimeUnit.DAYS).removalListener(removalListener).
			build(loader);


		//Populating the cache at init stage
		populateTheCache();
		logger.info("Pre-Loaded VRE to Scope cache with: "+scopeNamesToFullScopes.asMap().size()+" item/s");
	}


	/**
	 * Populate the cache.
	 */
	private static void populateTheCache(){
		try{
			//POPULATE THE CACHE READING THE RESOURCE "CATALOGUE-RESOLVER"
			logger.info("Trying to pre-populate the cache with mapping (Scope Name, Full Scope)");
		    ScopeProvider.instance.set(UriResolverSmartGearManagerInit.getRootContextScope());
			Map<String, String> map = GetAllInfrastructureScopes.loadMapOfScopeNameToFullScope(UriResolverSmartGearManagerInit.getRootContextScope());
			
			for (String scopeName : map.keySet()) {
				scopeNamesToFullScopes.asMap().put(scopeName, new ScopeBean(map.get(scopeName)));
			}
			
			logger.info("Cache populated with: "+scopeNamesToFullScopes.asMap().toString());
			//logger.info("Pre-Loaded CatalogueApplicationProfiles cache is: "+catalogueApplicationProfiles.asMap().toString());

//			if(UriResolverSmartGearManagerInit.getRootContextScope().compareTo("/gcube")==0){
//				logger.warn("HARD-CABLING PARTHENOS_Registry scope to resolve PARTHENOS_REGISTRY Links in dev environment");
//				scopeNamesToFullScopes.asMap().put("PARTHENOS_Registry", "/d4science.research-infrastructures.eu/ParthenosVO/PARTHENOS_Registry");
//			}
			

		}catch(Exception e){
			//SILENT
		}finally{

		}
	}

	
	/**
	 * Gets the.
	 *
	 * @param scopeName the scope name
	 * @return the scope bean
	 * @throws ExecutionException the execution exception
	 */
	public static ScopeBean get(String scopeName) throws ExecutionException{

		return scopeNamesToFullScopes.get(scopeName);
	}

	
	/**
	 * Load full scopefor scope name.
	 *
	 * @param scopeName the scope name
	 * @return the scope bean
	 */
	protected static ScopeBean loadFullScopeforScopeName(String scopeName){

		//THIS CHECK SHOULD BE NOT NEEDED
		ScopeBean fullScope = scopeNamesToFullScopes.getIfPresent(scopeName);

		if(fullScope==null){
			populateTheCache();
			fullScope = scopeNamesToFullScopes.getIfPresent(scopeName);
		}

		return fullScope;
	}
	

	/**
	 * Gets the cache.
	 *
	 * @return the cache
	 */
	public LoadingCache<String, ScopeBean> getCache(){
		return scopeNamesToFullScopes;
	}

}
