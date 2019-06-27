/**
 *
 */

package org.gcube.datatransfer.resolver.caches;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileReader;
import org.gcube.datatransfer.resolver.init.UriResolverSmartGearManagerInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;



/**
 * The Class LoadingGeoExplorerApplicationURLCache.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 5, 2018
 */
public class LoadingGeoExplorerApplicationURLCache {

	private static Logger logger = LoggerFactory.getLogger(LoadingGeoExplorerApplicationURLCache.class);

	//A cache (Scope, GeoExplorer-URL)
	private static LoadingCache<String, String> geoExplorerApplicationURLCache;

	static {

		CacheLoader<String, String> loader = new CacheLoader<String, String> () {
			@Override
			public String load(String scope)
				throws Exception {
				logger.info("Loading the cache for scope: "+scope);
				return loadGeoExplorerApplicationURL(scope);
			}
		};

		RemovalListener<String, String> removalListener = new RemovalListener<String, String>() {
			  public void onRemoval(RemovalNotification<String, String> removal) {
				  logger.info("cache expired");
			  }
		};

		geoExplorerApplicationURLCache =
		CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(
			1, TimeUnit.DAYS).removalListener(removalListener).
			build(loader);

		logger.info("cache instancied");
	}

	/**
	 * Gets the.
	 *
	 * @param scope the scope
	 * @return the string
	 * @throws ExecutionException the execution exception
	 */
	public static String get(String scope) throws ExecutionException{

		return geoExplorerApplicationURLCache.get(scope);
	}



	/**
	 * Load geo explorer application url.
	 *
	 * @param scope the scope
	 * @return the string
	 */
	protected static String loadGeoExplorerApplicationURL(String scope){

		if (scope == null || scope.isEmpty())
			logger.warn("Scope is null or ermpty, skipping loadGisViewerApplicationURL");

		ApplicationProfileReader reader = new ApplicationProfileReader(scope, UriResolverSmartGearManagerInit.getGeoExplorerProfile().getGenericResource(), UriResolverSmartGearManagerInit.getGeoExplorerProfile().getAppId(), false);
		String url = reader.getApplicationProfile().getUrl();
		logger.info("With scope "+scope+" loaded the GeoExplorer Application URL "+url);
		return url;
	}
}
