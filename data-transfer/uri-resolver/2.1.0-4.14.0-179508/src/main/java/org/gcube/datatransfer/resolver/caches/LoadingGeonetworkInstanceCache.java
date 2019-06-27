/**
 *
 */

package org.gcube.datatransfer.resolver.caches;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter;
import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.AccountType;
import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.GeonetworkLoginLevel;
import org.gcube.datatransfer.resolver.gis.GeonetworkInstance;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;


/**
 * The Class LoadingGeonetworkInstanceCache.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 5, 2018
 */
public class LoadingGeonetworkInstanceCache {

	private static Logger logger = LoggerFactory.getLogger(LoadingGeonetworkInstanceCache.class);

	//A cache (Scope, GeonetworkInstance)
	private static LoadingCache<String, GeonetworkInstance> geonetworkInstancesCache;

	static {

		CacheLoader<String, GeonetworkInstance> loader = new CacheLoader<String, GeonetworkInstance> () {
			@Override
			public GeonetworkInstance load(String scope)
				throws Exception {
				logger.info("Loading the cache for scope: "+scope);
				return loadGeonetworkInstance(scope);
			}
		};

		RemovalListener<String, GeonetworkInstance> removalListener = new RemovalListener<String, GeonetworkInstance>() {
			  public void onRemoval(RemovalNotification<String, GeonetworkInstance> removal) {
				  logger.info("cache expired");
			  }
		};

		geonetworkInstancesCache =
		CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(
			1, TimeUnit.DAYS).removalListener(removalListener).
			build(loader);

		logger.info("cache instancied");
	}


	/**
	 * Gets the.
	 *
	 * @param scope the scope
	 * @return the geonetwork instance
	 * @throws ExecutionException the execution exception
	 */
	public static GeonetworkInstance get(String scope) throws ExecutionException{

		return geonetworkInstancesCache.get(scope);
	}


	/**
	 * Load geonetwork instance.
	 *
	 * @param scope the scope
	 * @return the geonetwork instance
	 * @throws GeonetworkInstanceException the geonetwork instance exception
	 */
	protected static GeonetworkInstance loadGeonetworkInstance(String scope)
		throws GeonetworkInstanceException {

		if (scope == null || scope.isEmpty()){
			logger.warn("Scope is null or empty, returning GeonetworkInstance as null");
			return null;
		}

		GeonetworkAccessParameter gntwAccess = new GeonetworkAccessParameter(scope);
		/*
		 * 
		 * 
		 * !!!!!!!DOES NOT AUTHENTICATE NEVER!!!!!!
		 * 
		 */
		GeonetworkInstance geoInstance = gntwAccess.getGeonetworkInstance(false, GeonetworkLoginLevel.CKAN, AccountType.CKAN);
		logger.info("Loaded "+geoInstance+" for scope: " + scope);
		return geoInstance;
	}
}
