/**
 *
 */
package org.gcube.datatransfer.resolver.gis;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileReader;
import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.GeonetworkLoginLevel;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.datatransfer.resolver.gis.exception.IllegalArgumentException;
import org.gcube.datatransfer.resolver.gis.property.ApplicationProfileGenericResourcePropertyReader;
import org.gcube.datatransfer.resolver.gis.property.PropertyFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GisResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 7, 2016
 */
public class GisResolver extends HttpServlet{

	private static final String UTF_8 = "UTF-8";
	private static final String TEXT_PLAIN = "text/plain";
	public static final String PARAM_SEPARATOR_REPLACEMENT_VALUE = "%%";
	public static final String PARAM_SEPARATOR_REPLACEMENT_KEY = "separtor";
	private static final long serialVersionUID = 5605107730993617579L;

	public static final String GIS_UUID = "gis-UUID";
	public static final String SCOPE = "scope";
	public static final String GEO_EXPLORER_LAYER_UUID = "geo-exp";

	protected static final String GIS_VIEWER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES = "gisviewerappgenericresource.properties";
	protected static final String GEO_EXPLORER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES = "geoexplorerappgenericresource.properties";


	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GisResolver.class);

	protected Map<String, GeonetworkInstance> cachedGeonetworkInstances; //A cache: scope - geonetwork instances
	protected Map<String, String> cachedGisViewerApplHostname; //A cache: scope -  GisViewerApp hostname
	protected Map<String, String> cachedGeoExplorerApplHostname; //A cache: scope -  GisViewerApp hostname

	private Timer timer;
	private ApplicationProfileGenericResourcePropertyReader gisViewerAppPropertyReader;
	private ApplicationProfileGenericResourcePropertyReader geoEplorerAppPropertyReader;

	//THIRTY MINUTES
	public static final long CACHE_RESET_TIME = 30*60*1000;

	//TEN MINUTES
	public static final long CACHE_RESET_DELAY = 10*60*1000;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Resetting cache...");
				reseCacheServerParameters();
				resetGisViewerAppEndPoint();
				reseCacheGisViewerApplicationHostname();
				reseCacheGeoExplorerApplicationHostname();
			}
		}, CACHE_RESET_DELAY, CACHE_RESET_TIME);
	}



	/**
	 * Gets the cached geonetwork instance.
	 *
	 * @param scope the scope
	 * @return the cached geonetwork instance
	 * @throws Exception the exception
	 */
	protected GeonetworkInstance getCachedGeonetworkInstance(String scope) throws Exception{

		if(cachedGeonetworkInstances==null)
			reseCacheServerParameters();

		logger.info("Attempt to get the GeonetworkInstance from cache by scope: "+scope);
		GeonetworkInstance geonInstance = cachedGeonetworkInstances.get(scope);

		if(geonInstance==null){
			logger.info("GeonetworkInstance is null in cache, reading from library...");
			try {
				geonInstance = discoveryGeonetworkInstance(scope);
				cachedGeonetworkInstances.put(scope, geonInstance);
				logger.info("Updated GeonetworkInstance cache! Scope "+scope+" linking "+geonInstance);
			} catch (Exception e) {
				logger.error("An error occurred on getting GeonetworkInstance for scope: "+scope, e);
				throw new Exception("Sorry, An error occurred on getting GeonetworkInstance for scope: "+scope);
			}
		}else
			logger.info("GeonetworkInstance is not null using it");

		logger.info("returning GeonetworkInstance: "+geonInstance);

		return geonInstance;
	}


	/**
	 * Discovery geonetwork instance.
	 *
	 * @param scope the scope
	 * @return the geonetwork instance
	 * @throws GeonetworkInstanceException the geonetwork instance exception
	 */
	private GeonetworkInstance discoveryGeonetworkInstance(String scope) throws GeonetworkInstanceException{

		GeonetworkAccessParameter gntwAccess = new GeonetworkAccessParameter(scope);

		if(cachedGeonetworkInstances==null)
			reseCacheServerParameters();

		return gntwAccess.getGeonetworkInstance(true, GeonetworkLoginLevel.ADMIN);
	}


	/**
	 * Rese cache server parameters.
	 */
	private void reseCacheServerParameters(){
		cachedGeonetworkInstances = new HashMap<String, GeonetworkInstance>();
		logger.info("Cache of GeonetworkInstances reset!");
	}

	/**
	 * Rese cache gis viewer application hostname.
	 */
	private void reseCacheGisViewerApplicationHostname(){
		cachedGisViewerApplHostname = new HashMap<String, String>();
		logger.info("Cache of Gis Viewer Hostname reset!");
	}

	/**
	 * Rese cache geo explorer application hostname.
	 */
	private void reseCacheGeoExplorerApplicationHostname() {
		cachedGeoExplorerApplHostname = new HashMap<String, String>();
		logger.info("Cache of Geo Explorer Hostname reset!");
	}

	/**
	 * Reset gis viewer app end point.
	 */
	private void resetGisViewerAppEndPoint(){
		try {
			gisViewerAppPropertyReader = new ApplicationProfileGenericResourcePropertyReader(GIS_VIEWER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES);
			logger.info("GisViewerApp end point updated!");
		} catch (PropertyFileNotFoundException e) {
			logger.error("Error on reset GisViewerAppEndPoint ",e);
		}
	}


	/**
	 * Reset geo explorer app end point.
	 */
	private void resetGeoExplorerAppEndPoint(){
		try {
			geoEplorerAppPropertyReader = new ApplicationProfileGenericResourcePropertyReader(GEO_EXPLORER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES);
			logger.info("GeoExplorer end point updated!");
		} catch (PropertyFileNotFoundException e) {
			logger.error("Error on reset GeoExplorerEndPoint ",e);
		}
	}

	/**
	 * Gets the gis viewer application url.
	 *
	 * @param scope the scope
	 * @return the gis viewer application url
	 * @throws Exception the exception
	 */
	protected String getGisViewerApplicationURL(String scope) throws Exception{

		if(cachedGisViewerApplHostname==null)
			reseCacheGisViewerApplicationHostname();

		logger.info("Tentative of recovering gis viewer application hostname from cache for scope: "+scope);
		String gisViewerAppHostname = cachedGisViewerApplHostname.get(scope);
		if(gisViewerAppHostname==null){
			logger.info("Gis viewer application hostname is null, reading from application profile..");
			if(gisViewerAppPropertyReader==null)
				resetGisViewerAppEndPoint();

			ApplicationProfileReader reader = new ApplicationProfileReader(scope, gisViewerAppPropertyReader.getGenericResource(), gisViewerAppPropertyReader.getAppId(), true);
			String url = reader.getApplicationProfile().getUrl();
			cachedGisViewerApplHostname.put(scope, url);
			logger.info("Updated GisViewerApplication cache! Scope "+scope+" linking "+url);
			return url;
		}else
			logger.info("Cache for GisViewerApplication end point is not null using it");

		return gisViewerAppHostname;
	}


	/**
	 * Gets the geo explorer application url.
	 *
	 * @param scope the scope
	 * @return the geo explorer application url
	 * @throws Exception the exception
	 */
	protected String getGeoExplorerApplicationURL(String scope) throws Exception{

		if(cachedGeoExplorerApplHostname==null)
			reseCacheGeoExplorerApplicationHostname();

		logger.info("Tentative of recovering geo explorer application hostname from cache for scope: "+scope);
		String geoExplorerApplicationHostname = cachedGeoExplorerApplHostname.get(scope);
		if(geoExplorerApplicationHostname==null){
			logger.info("GeoExplorer application hostname is null, reading from application profile..");
			if(geoEplorerAppPropertyReader==null)
				resetGeoExplorerAppEndPoint();

			ApplicationProfileReader reader = new ApplicationProfileReader(scope, geoEplorerAppPropertyReader.getGenericResource(), geoEplorerAppPropertyReader.getAppId(), true);
			String url = reader.getApplicationProfile().getUrl();
			cachedGeoExplorerApplHostname.put(scope, url);
			logger.info("Updated GeoExplorerApplication cache! Scope "+scope+" linking "+url);
			return url;
		}else
			logger.info("Cache for GeoExplorerApplication end point is not null using it");

		return geoExplorerApplicationHostname;


	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String originalScope =  ScopeProvider.instance.get();

		logger.info("The http session id is: " + req.getSession().getId());
		String scope = req.getParameter(SCOPE);

		if (scope == null || scope.isEmpty()) {
			logger.error(SCOPE+" not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, SCOPE+" not found or empty");
			return;
		}
		logger.info("SCOPE is: " + scope);

		boolean isGisLink = false;
		boolean isGeoExplorerLink = false;
		String gisUUID = req.getParameter(GIS_UUID);

		if (gisUUID == null || gisUUID.isEmpty()) {
			logger.debug(GIS_UUID+" not found");
		}else
			isGisLink = true;

		logger.info(GIS_UUID +" is: " + gisUUID);

		String geoExplorerUUID = req.getParameter(GEO_EXPLORER_LAYER_UUID);

		if (geoExplorerUUID == null || geoExplorerUUID.isEmpty()) {
			logger.debug(GEO_EXPLORER_LAYER_UUID+ " not found");
		}else
			isGeoExplorerLink = true;

		logger.info(GEO_EXPLORER_LAYER_UUID +" is: " + geoExplorerUUID);

		if(!isGisLink && !isGeoExplorerLink){
			String err = GIS_UUID+" and "+GEO_EXPLORER_LAYER_UUID+" not found or empty";
			logger.error(err);
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, err);
			return;
		}

		try {

			if(isGisLink){
				//ScopeProvider.instance.set(scope);
				//ServerParameters geonetworkParams = getCachedServerParameters(scope);

				String wmsRequest = getLayerWmsRequest(scope, gisUUID);
				logger.info("wms url is: " + wmsRequest);
				wmsRequest = URLEncoder.encode(wmsRequest, UTF_8);
				logger.info("encoded WMS url is: " + wmsRequest);

				String gisViewerPortletUrl = getGisViewerApplicationURL(scope);
				logger.info("Gis Viewer Application url is: " + gisViewerPortletUrl);
				gisViewerPortletUrl+="?rid="+new Random().nextLong()
								+"&wmsrequest="+wmsRequest
								+"&uuid="+URLEncoder.encode(gisUUID, "UTF-8");

				/*resp.setContentType(TEXT_PLAIN);
				resp.setCharacterEncoding(UTF_8);
				PrintWriter out = resp.getWriter();
				out.println(gisPortletUrl);
				logger.info("returning link: " + gisPortletUrl);
				out.close();*/
				urlRedirect(req, resp, gisViewerPortletUrl);
			}

			if(isGeoExplorerLink){
				ScopeProvider.instance.set(scope);
				String geoExplorerPortletUrl  = getGeoExplorerApplicationURL(scope);
				logger.info("GeoExplorer Application url is: " + geoExplorerPortletUrl);
				geoExplorerPortletUrl+="?rid="+new Random().nextLong()
								+"&luuid="+URLEncoder.encode(geoExplorerUUID, "UTF-8");
				urlRedirect(req, resp, geoExplorerPortletUrl);
			}

		} catch (IllegalArgumentException e){
			logger.error("IllegalArgumentException:", e);
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Illegal argument to carry out the request!");
			return;

		} catch (Exception e) {
			logger.error("Exception:", e);
			String error = "Sorry, an error occurred on resolving request with UUID "+gisUUID+" and scope "+scope+". Please, contact support!";
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error);
			return;
		}finally{
			if(originalScope!=null){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider set to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}

	}

	/**
	 * Encode url with param delimiter.
	 *
	 * @param wmsRequest the wms request
	 * @return the string
	 */
	private String encodeURLWithParamDelimiter(String wmsRequest){
		return wmsRequest.replaceAll("&", PARAM_SEPARATOR_REPLACEMENT_VALUE);
	}

	/**
	 * Decode url with param delimiter.
	 *
	 * @param wmsRequest the wms request
	 * @return the string
	 */
	private String decodeURLWithParamDelimiter(String wmsRequest){
		return wmsRequest.replaceAll(PARAM_SEPARATOR_REPLACEMENT_VALUE, "&");
	}

	/**
	 * Append param replacement.
	 *
	 * @param wmsRequest the wms request
	 * @return the string
	 */
	private String appendParamReplacement(String wmsRequest){
		return wmsRequest+"&"+PARAM_SEPARATOR_REPLACEMENT_KEY+"="+PARAM_SEPARATOR_REPLACEMENT_VALUE;
	}


	/**
	 * Gets the layer wms request.
	 *
	 * @param scope the scope
	 * @param gisUUID the gis uuid
	 * @return the layer wms request
	 * @throws Exception the exception
	 */
	protected String getLayerWmsRequest(String scope, String gisUUID) throws Exception{

		try {
			GeonetworkInstance gi = getCachedGeonetworkInstance(scope);
			return MetadataConverter.getWMSOnLineResource(gi, gisUUID);
		}catch (GeonetworkInstanceException e){
			logger.error("An error occurred when instancing geonetowrk gis layer with UUID "+gisUUID, e);
			throw new IllegalArgumentException("Sorry, An error occurred when instancing geonetwork with UUID: "+gisUUID);
		} catch (Exception e) {
			logger.error("An error occurred when retrieving gis layer with UUID "+gisUUID, e);
			throw new IllegalArgumentException("Sorry, An error occurred when retrieving gis layer with UUID "+gisUUID);
		}
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		this.doGet(req, resp);
	}


	/**
	 * Send error.
	 *
	 * @param response the response
	 * @param status the status
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendError(HttpServletResponse response, int status, String message) throws IOException{
		response.setStatus(status);
		logger.info("error message: "+message);
		logger.info("writing response...");
		StringReader sr = new StringReader(message);
		IOUtils.copy(sr, response.getOutputStream());
		logger.info("response writed");
		response.flushBuffer();
	}

	/**
	 * Url redirect.
	 *
	 * @param req the req
	 * @param response the response
	 * @param redirectTo the redirect to
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void urlRedirect(HttpServletRequest req, HttpServletResponse response, String redirectTo) throws IOException  {
	    response.sendRedirect(response.encodeRedirectURL(redirectTo));
	}


	/**
	 * Gets the request url.
	 *
	 * @param req the req
	 * @return the request url
	 */
	public static String getRequestURL(HttpServletRequest req) {

	    String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80
	    String contextPath = req.getContextPath();   // /mywebapp
	    StringBuffer url =  new StringBuffer();
	    url.append(scheme).append("://").append(serverName);

	    if (serverPort != 80 && serverPort != 443) {
	        url.append(":").append(serverPort);
	    }

	    logger.trace("server: "+url);
	    logger.trace("omitted contextPath: "+contextPath);
	    return url.toString();
	}

//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args) {
//		GisResolver gisResolver = new GisResolver();
//		String scope = "/gcube/devsec/devVRE";
//		String UUID = "177e1c3c-4a22-4ad9-b015-bfc443d16cb8";
//		try {
////			ScopeProvider.instance.set(scope);
////			ServerParameters geonetworkParams = gisResolver.getCachedServerParameters(scope);
////			String wmsRequest = gisResolver.getLayerWmsRequest(scope, UUID, geonetworkParams);
////			logger.info("Final url is: " + wmsRequest);
////			wmsRequest = URLEncoder.encode(wmsRequest, UTF_8);
////			logger.info("Encoded WMS request is: " + wmsRequest);
////			String gisPortletUrl = gisResolver.getGisViewerApplicationURL(scope);
////			logger.info("Gis Viewer Application url is: " + gisPortletUrl);
//////			logger.info("WmsRequest is: " + wmsRequest);
//////			wmsRequest = encodeURLWithParamDelimiter(wmsRequest);
//////			logger.info("Encoded url is: " + wmsRequest);
//////			wmsRequest = appendParamReplacement(wmsRequest);
////			gisPortletUrl+="?wmsrequest="+wmsRequest;
////
////			System.out.println(gisPortletUrl);
////			urlRedirect(req, resp, gisPortletUrl);
//
//			ScopeProvider.instance.set(scope);
//			String geoExplorerURL = gisResolver.getGeoExplorerApplicationURL(scope);
//			logger.info("GeoExplorer url is: " + geoExplorerURL);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
