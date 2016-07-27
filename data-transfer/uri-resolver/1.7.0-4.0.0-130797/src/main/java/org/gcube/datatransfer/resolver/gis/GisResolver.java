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
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileReader;
import org.gcube.datatransfer.resolver.applicationprofile.ScopeUtil;
import org.gcube.datatransfer.resolver.gis.GeoRuntimeReader.GEO_SERVICE;
import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.GeonetworkLoginLevel;
import org.gcube.datatransfer.resolver.gis.entity.ServerParameters;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.datatransfer.resolver.gis.exception.IllegalArgumentException;
import org.gcube.datatransfer.resolver.gis.property.GisViewerAppGenericResourcePropertyReader;
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

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GisResolver.class);

	protected Map<String, ServerParameters> cachedServerParams; //A cache: scope - geonetwork parameters
	protected Map<String, String> cachedGisViewerApplHostname; //A cache: scope -  GisViewerApp hostname

	private Timer timer;
	private GisViewerAppGenericResourcePropertyReader gisViewerAppPropertyReader;

	//THIRTY MINUTES
	public static final long CACHE_RESET_TIME = 30*60*1000;

	//TEN MINUTES
	public static final long CACHE_RESET_DELAY = 10*1000;

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
			}
		}, CACHE_RESET_DELAY, CACHE_RESET_TIME);
	}

	/**
	 * Gets the cached server parameters.
	 *
	 * @param scope the scope
	 * @return the cached server parameters
	 * @throws Exception the exception
	 */
	protected ServerParameters getCachedServerParameters(String scope) throws Exception{

		if(cachedServerParams==null)
			reseCacheServerParameters();

		logger.info("Tentative to recovering gis server param from cache to scope "+scope);
		ServerParameters serverParam = cachedServerParams.get(scope);

		if(serverParam==null){
			logger.info("Gis server param is null, reading from application profile..");
			GeoRuntimeReader reader = new GeoRuntimeReader();
			try {
				serverParam = reader.retrieveGisParameters(scope, GEO_SERVICE.GEONETWORK);
				cachedServerParams.put(scope, serverParam);
				logger.info("Updated Gis server cache! Scope "+scope+" linking "+serverParam);
			} catch (Exception e) {
				logger.error("An error occurred on reading application profile to "+GEO_SERVICE.GEONETWORK, e);
				throw new Exception("Sorry, An error occurred on reading configuration to  "+GEO_SERVICE.GEONETWORK);
			}
		}else
			logger.info("Cache gis server param is not null using it");

		logger.info("returning geonetworkParams "+serverParam);

		return serverParam;
	}

	/**
	 * Rese cache server parameters.
	 */
	private void reseCacheServerParameters(){
		cachedServerParams = new HashMap<String, ServerParameters>();
		logger.info("Cache server params reset!");
	}

	/**
	 * Rese cache gis viewer application hostname.
	 */
	private void reseCacheGisViewerApplicationHostname(){
		cachedGisViewerApplHostname = new HashMap<String, String>();
		logger.info("Cache Gis Viewer Hostname reset!");
	}

	/**
	 * Reset gis viewer app end point.
	 */
	private void resetGisViewerAppEndPoint(){
		try {
			gisViewerAppPropertyReader = new GisViewerAppGenericResourcePropertyReader();
			logger.info("GisViewerApp end point updated!");
		} catch (PropertyFileNotFoundException e) {
			logger.error("Error on reset GisViewerAppEndPoint ",e);
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

		String infra = ScopeUtil.getInfrastructureNameFromScope(scope);
		logger.info("Tentative of recovering gis viewer application hostname from cache for scope: "+scope);
		String gisViewerAppHostname = cachedGisViewerApplHostname.get(infra);
		if(gisViewerAppHostname==null){
			logger.info("Gis viewer application hostname is null, reading from application profile..");
			if(gisViewerAppPropertyReader==null)
				resetGisViewerAppEndPoint();

			ApplicationProfileReader reader = new ApplicationProfileReader(infra, gisViewerAppPropertyReader.getGenericResource(), gisViewerAppPropertyReader.getAppId());
			String url = reader.getApplicationProfile().getUrl();
			cachedGisViewerApplHostname.put(infra, url);
			logger.info("Updated GisViewerApplication cache! Scope "+scope+" linking "+url);
			return url;
		}else
			logger.info("Cache Gis viewer application is not null using it");

		return gisViewerAppHostname;


	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("The http session id is: " + req.getSession().getId());

		String gisUUID = req.getParameter(GIS_UUID);

		if (gisUUID == null || gisUUID.equals("")) {
			logger.debug("GIS UUID not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, GIS_UUID+" not found or empty");
			return;
		}

		logger.info("GIS UUID is: " + gisUUID);

		String scope = req.getParameter(SCOPE);

		if (scope == null || scope.equals("")) {
			logger.debug("Scope not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, SCOPE+" not found or empty");
			return;
		}

		logger.info("SCOPE is: " + scope);

		try {

			ServerParameters geonetworkParams = getCachedServerParameters(scope);
			String wmsRequest = getLayerWmsRequest(scope, gisUUID, geonetworkParams);
			logger.info("wms url is: " + wmsRequest);
			wmsRequest = URLEncoder.encode(wmsRequest, UTF_8);
			logger.info("encoded WMS url is: " + wmsRequest);

			String gisPortletUrl = getGisViewerApplicationURL(scope);
			logger.info("Gis Viewer Application url is: " + gisPortletUrl);
			gisPortletUrl+="?rid="+new Random().nextLong()
							+"&wmsrequest="+wmsRequest
							+"&uuid="+URLEncoder.encode(gisUUID, "UTF-8");

			/*resp.setContentType(TEXT_PLAIN);
			resp.setCharacterEncoding(UTF_8);
			PrintWriter out = resp.getWriter();
			out.println(gisPortletUrl);
			logger.info("returning link: " + gisPortletUrl);
			out.close();*/
			urlRedirect(req, resp, gisPortletUrl);

		} catch (IllegalArgumentException e){
			logger.error("IllegalArgumentException:", e);
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Illegal argument to carry out the request!");
			return;

		} catch (Exception e) {
			logger.error("Exception:", e);
			String error = "Sorry, an error occurred on resolving request with UUID "+gisUUID+" and scope "+scope+". Please, contact support!";
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error);
			return;
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
	 * @param geonetworkParams the geonetwork params
	 * @return the layer wms request
	 * @throws Exception the exception
	 */
	protected String getLayerWmsRequest(String scope, String gisUUID, ServerParameters geonetworkParams) throws Exception{

		try {
			GeonetworkServiceInterface gntwAccess = new GeonetworkAccessParameter(scope, geonetworkParams);
			return MetadataConverter.getWMSOnLineResource(gntwAccess.getGeonetworkInstance(true, GeonetworkLoginLevel.ADMIN), gisUUID);
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
	protected void sendError(HttpServletResponse response, int status, String message) throws IOException
	{
//		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setStatus(status);
		logger.info("error message: "+message);
		logger.info("writing response...");
		StringReader sr = new StringReader(message);
		IOUtils.copy(sr, response.getOutputStream());

//		response.getWriter().write(resultMessage.toString());
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
	    return;
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
	//   String servletPath = req.getServletPath();   // /servlet/MyServlet
	//  String pathInfo = req.getPathInfo();         // /a/b;c=123
	//  String queryString = req.getQueryString();          // d=789

	    // Reconstruct original requesting URL
	    StringBuffer url =  new StringBuffer();
	    url.append(scheme).append("://").append(serverName);

	    if (serverPort != 80 && serverPort != 443) {
	        url.append(":").append(serverPort);
	    }

	    logger.trace("server: "+url);
	    logger.trace("omitted contextPath: "+contextPath);
	    return url.toString();
	}
	/*
	public static void main(String[] args) {
		GisResolver gisResolver = new GisResolver();
		String scope = "/gcube/devsec/devVRE";
		String UUID = "177e1c3c-4a22-4ad9-b015-bfc443d16cb8";
		try {
			ServerParameters geonetworkParams = gisResolver.getCachedServerParameters(scope);
			String wmsRequest = gisResolver.getLayerWmsRequest(scope, UUID, geonetworkParams);
			logger.info("Final url is: " + wmsRequest);
			wmsRequest = URLEncoder.encode(wmsRequest, UTF_8);
			logger.info("Encoded WMS request is: " + wmsRequest);
			String gisPortletUrl = gisResolver.getGisViewerApplicationURL(scope);
			logger.info("Gis Viewer Application url is: " + gisPortletUrl);
//			logger.info("WmsRequest is: " + wmsRequest);
//			wmsRequest = encodeURLWithParamDelimiter(wmsRequest);
//			logger.info("Encoded url is: " + wmsRequest);
//			wmsRequest = appendParamReplacement(wmsRequest);
			gisPortletUrl+="?wmsrequest="+wmsRequest;

			System.out.println(gisPortletUrl);
//			urlRedirect(req, resp, gisPortletUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
