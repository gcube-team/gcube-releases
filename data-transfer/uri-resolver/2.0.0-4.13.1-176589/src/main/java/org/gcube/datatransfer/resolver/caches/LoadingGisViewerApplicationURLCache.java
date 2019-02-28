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
 * The Class LoadingGisViewerApplicationURLCache.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 5, 2018
 */
public class LoadingGisViewerApplicationURLCache {

	private static Logger logger = LoggerFactory.getLogger(LoadingGisViewerApplicationURLCache.class);

	//A cache (Scope, GisViewerApplication-URL)
	private static LoadingCache<String, String> gisViewerApplicationURLCache;

	static {

		CacheLoader<String, String> loader = new CacheLoader<String, String> () {
			@Override
			public String load(String scope)
				throws Exception {
				logger.info("Loading the cache for scope: "+scope);
				return loadGisViewerApplicationURL(scope);
			}
		};

		RemovalListener<String, String> removalListener = new RemovalListener<String, String>() {
			  public void onRemoval(RemovalNotification<String, String> removal) {
				  logger.info("cache expired");
			  }
		};

		gisViewerApplicationURLCache =
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

		return gisViewerApplicationURLCache.get(scope);
	}



	/**
	 * Load gis viewer application url.
	 *
	 * @param scope the scope
	 * @return the string
	 */
	protected static String loadGisViewerApplicationURL(String scope){

		if (scope == null || scope.isEmpty())
			logger.warn("Scope is null or ermpty, skipping loadGisViewerApplicationURL");

		ApplicationProfileReader reader = new ApplicationProfileReader(scope, UriResolverSmartGearManagerInit.getGisViewerProfile().getGenericResource(), UriResolverSmartGearManagerInit.getGisViewerProfile().getAppId(), false);

		if(reader.getApplicationProfile()==null){
			logger.error("NO Appllication Profile "+UriResolverSmartGearManagerInit.getGisViewerProfile().getAppId()+" found in the scope: "+scope+", returning null!");
			return null;
		}
		String url = reader.getApplicationProfile().getUrl();
		logger.info("With scope "+scope+" loaded the GisViewer Application URL "+url);
		return url;

	}
}
