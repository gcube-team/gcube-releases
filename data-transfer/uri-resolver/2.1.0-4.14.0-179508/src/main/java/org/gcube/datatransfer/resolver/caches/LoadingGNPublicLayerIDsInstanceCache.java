/**
 *
 */

package org.gcube.datatransfer.resolver.caches;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;



/**
 * The Class LoadingGNPublicLayerIDsInstanceCache.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 21, 2019
 */
public class LoadingGNPublicLayerIDsInstanceCache {

	private static Logger logger = LoggerFactory.getLogger(LoadingGNPublicLayerIDsInstanceCache.class);

	private static LoadingCache<String, List<String>> gnPublicLayersCache;

	static {

		CacheLoader<String, List<String>> loader = new CacheLoader<String, List<String>> () {
			@Override
			public List<String> load(String geonetworkEndPoint)
				throws Exception {
				//logger.info("Loading public layer IDS for GN endpoint: "+geonetworkEndPoint);
				return loadGNPublicLayersID(geonetworkEndPoint);
			}
		};

		RemovalListener<String, List<String>> removalListener = new RemovalListener<String, List<String>>() {
			  public void onRemoval(RemovalNotification<String, List<String>> removal) {
				  logger.info("cache expired");
			  }
		};

		gnPublicLayersCache =
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
	public static List<String> get(String scope) throws ExecutionException{

		return gnPublicLayersCache.get(scope);
	}


	/**
	 * Load GN public layers ID.
	 *
	 * @param geonetworkEndPoint the geonetwork end point
	 * @return the list
	 * @throws Exception the exception
	 */
	protected static List<String> loadGNPublicLayersID(String geonetworkEndPoint)
		throws Exception {

		if (geonetworkEndPoint == null || geonetworkEndPoint.isEmpty()){
			logger.warn("geonetworkEndPoint is null or empty, returning null");
			return null;
		}
		
		List<String> foundPublicIds = new ArrayList<String>();
		try {
			logger.info("Loading Public Layers ID for GN endpoint: {}",geonetworkEndPoint);
			GeoNetworkAdministration reader = GeoNetwork.get();
			final GNSearchRequest req=new GNSearchRequest();
			req.addParam(GNSearchRequest.Param.any,"");
			GNSearchResponse resp=reader.query(req);

			Iterator<GNMetadata> iterator=resp.iterator();
			while(iterator.hasNext()){
				foundPublicIds.add(iterator.next().getUUID());
			}
			logger.info("Public Layers ID are: "+foundPublicIds.size());
		}catch (Exception e) {
			logger.error("Error during sending GNSearchRequest: ",e);
		}
		
		return foundPublicIds;
	}
}
