/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.caches.LoadingGeoExplorerApplicationURLCache;
import org.gcube.datatransfer.resolver.caches.LoadingGeonetworkInstanceCache;
import org.gcube.datatransfer.resolver.caches.LoadingGisViewerApplicationURLCache;
import org.gcube.datatransfer.resolver.gis.GeonetworkInstance;
import org.gcube.datatransfer.resolver.gis.MetadataConverter;
import org.gcube.datatransfer.resolver.gis.entity.GisLayerItem;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.datatransfer.resolver.init.UriResolverSmartGearManagerInit;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheLoader.InvalidCacheLoadException;



/**
 * The Class GisResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 14, 2018
 */
@Path("gis")
public class GisResolver {

	private static Logger logger = LoggerFactory.getLogger(GisResolver.class);

	public static String help = "https://wiki.gcube-system.org/gcube/URI_Resolver#GIS_Resolver";

	public static final String UTF_8 = "UTF-8";

	public static final String GIS_UUID = "gis-UUID";
	public static final String SCOPE = "scope";
	public static final String GEO_EXPLORER_LAYER_UUID = "geo-exp";


	/**
	 * Submit get.
	 *
	 * @param req the req
	 * @param scope the scope
	 * @param gisUUID the gis uuid
	 * @param geoExplorerUUID the geo explorer uuid
	 * @return the response
	 * @throws WebApplicationException the web application exception
	 */
	@GET
	@Path("")
	public Response submitGet(@Context HttpServletRequest req, @
		QueryParam(SCOPE) String scope,
		@QueryParam(GIS_UUID) String gisUUID,
		@QueryParam(GEO_EXPLORER_LAYER_UUID) String geoExplorerUUID) throws WebApplicationException{

		logger.info(this.getClass().getSimpleName()+" GET starts...");

		try{
			boolean isGisLink = false;
			boolean isGeoExplorerLink = false;

			if(scope==null || scope.isEmpty()){
				logger.error("Query Parameter 'scope' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory query parameter 'scope'", this.getClass(), help);
			}

			if(gisUUID==null || gisUUID.isEmpty()){
				logger.error("Path Parameter 'gis-UUID' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory query parameter 'gis-UUID'", this.getClass(), help);
			}else
				isGisLink = true;

			logger.info(SCOPE +" is: " + scope);
			logger.info(GIS_UUID +" is: " + gisUUID);

			if (geoExplorerUUID == null || geoExplorerUUID.isEmpty()) {
				logger.debug(GEO_EXPLORER_LAYER_UUID+ " not found");
			}else
				isGeoExplorerLink = true;

			logger.info(GEO_EXPLORER_LAYER_UUID +" is: " + geoExplorerUUID);

			if(!isGisLink && !isGeoExplorerLink){
				String err = GIS_UUID+" or "+GEO_EXPLORER_LAYER_UUID+" not found or empty in the query string";
				logger.error(err);
				throw ExceptionManager.badRequestException(req, err, this.getClass(), help);
			}



			if(isGisLink){
				ScopeProvider.instance.set(scope);
				//ServerParameters geonetworkParams = getCachedServerParameters(scope);
				GisLayerItem gisLayerItem = getGisLayerForLayerUUID(req, scope, gisUUID);

				logger.info("wms url is: " + gisLayerItem.getFullWmsUrlRequest());
				String wmsRequest = URLEncoder.encode(gisLayerItem.getFullWmsUrlRequest(), UTF_8);
				logger.info("encoded WMS url is: " + wmsRequest);

				String layerTitle = null;
					if(gisLayerItem.getCitationTitle()!=null && !gisLayerItem.getCitationTitle().isEmpty())
						layerTitle = URLEncoder.encode(gisLayerItem.getCitationTitle(), UTF_8);

				logger.info("layer Title encoded is: " + layerTitle);
				String gisViewerPortletUrl = null;
				try{
					gisViewerPortletUrl = LoadingGisViewerApplicationURLCache.get(scope);
				}catch(ExecutionException | InvalidCacheLoadException e){
					logger.error("Error on getting the GisViewer Applicaton URL from cache for scope "+scope, e);
					throw ExceptionManager.wrongParameterException(req, "Error on getting the GisViewer Applicaton URL from cache for scope "+scope+".\nIs the Application Profile with APPID "+UriResolverSmartGearManagerInit.getGisViewerProfile().getAppId()+" registered for this scope: "+scope+"?", this.getClass(), help);
				}
				//CHECKING IF THE GisViewer Portlet URL is valid
				if(gisViewerPortletUrl==null || gisViewerPortletUrl.isEmpty())
					throw ExceptionManager.notFoundException(req, "GisViewer Portlet URL not found in the scope: "+scope +". Please contact the support", this.getClass(), help);

				logger.info("Gis Viewer Application url is: " + gisViewerPortletUrl);
				gisViewerPortletUrl+="?rid="+new Random().nextLong()
								+"&wmsrequest="+wmsRequest
								+"&uuid="+URLEncoder.encode(gisUUID, UTF_8);

				if(layerTitle!=null)
					gisViewerPortletUrl+="&layertitle="+layerTitle;

				logger.info("Redirecting to: "+gisViewerPortletUrl);
				return Response.seeOther(new URI(gisViewerPortletUrl)).build();

			}

			if(isGeoExplorerLink){

				ScopeProvider.instance.set(scope);
				String geoExplorerPortletUrl = null;
				try{
					geoExplorerPortletUrl = LoadingGeoExplorerApplicationURLCache.get(scope);
				}catch(ExecutionException e){
					logger.error("Error on getting the GeoExplorer Applicaton URL from cache for scope "+scope, e);
					throw ExceptionManager.wrongParameterException(req, "Error on getting the GeoExplorer Applicaton URL from cache for scope "+scope+".\nIs the Application Profile with APPID "+UriResolverSmartGearManagerInit.getGeoExplorerProfile().getAppId()+" registered for this scope: "+scope+"?", this.getClass(), help);
				}
				//CHECKING IF THE GeoExplorer Portlet URL is valid
				if(geoExplorerPortletUrl==null || geoExplorerPortletUrl.isEmpty())
					throw ExceptionManager.notFoundException(req, "GeoExplorer Portlet URL not found in the scope: "+scope +". Please contact the support", this.getClass(), help);

				logger.info("GeoExplorer Application url is: " + geoExplorerPortletUrl);
				geoExplorerPortletUrl+="?rid="+new Random().nextLong()
								+"&luuid="+URLEncoder.encode(geoExplorerUUID, UTF_8);
				//urlRedirect(req, resp, geoExplorerPortletUrl);
				return Response.seeOther(new URI(geoExplorerPortletUrl)).build();
			}

			throw ExceptionManager.badRequestException(req, GIS_UUID+" or "+GEO_EXPLORER_LAYER_UUID+" not found or empty in the query string", this.getClass(), help);

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Sorry, an error occurred on resolving request with UUID "+gisUUID+" and scope "+scope+". Please, contact support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), help);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}

	}


	/**
	 * Gets the gis layer for layer uuid.
	 *
	 * @param req the req
	 * @param scope the scope
	 * @param gisUUID the gis uuid
	 * @return the gis layer for layer uuid
	 * @throws Exception the exception
	 */
	protected GisLayerItem getGisLayerForLayerUUID(HttpServletRequest req, String scope, String gisUUID) throws Exception{

		try {
			GeonetworkInstance gi = null;
			try {
				gi = LoadingGeonetworkInstanceCache.get(scope);
			}catch(ExecutionException | InvalidCacheLoadException e){
				logger.error("Error on getting the Geonetwork Instance from cache for scope "+scope, e);
				throw ExceptionManager.wrongParameterException(req, "Error on getting the Geonetwork Instance from cache for scope "+scope+". Is it registered for this scope: "+scope+"?", this.getClass(), help);
			}
			if(gi==null)
				throw new Exception("GeonetworkInstance not instanciable in the scope: "+scope);

			GisLayerItem gisLayerItem = MetadataConverter.getWMSOnLineResource(gi, gisUUID);
			return gisLayerItem;
			//TODO CREATE A BEAN ADDING WMS REQUEST AND LAYER TITLE MetadataConverter.
		}catch (GeonetworkInstanceException e){
			logger.error("An error occurred when instancing geonetowrk gis layer with UUID "+gisUUID, e);
			throw new IllegalArgumentException("Sorry, An error occurred when instancing geonetwork with UUID: "+gisUUID);
		} catch (Exception e) {
			logger.error("An error occurred when retrieving gis layer with UUID "+gisUUID, e);
			throw new IllegalArgumentException("Sorry, An error occurred when retrieving gis layer with UUID "+gisUUID);
		}
	}


}
