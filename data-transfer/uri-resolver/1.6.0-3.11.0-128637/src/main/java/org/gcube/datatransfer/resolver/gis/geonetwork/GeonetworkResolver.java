/**
 *
 */
package org.gcube.datatransfer.resolver.gis.geonetwork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.datatransfer.resolver.gis.GeoRuntimeReader;
import org.gcube.datatransfer.resolver.gis.GeoRuntimeReader.GEO_SERVICE;
import org.gcube.datatransfer.resolver.gis.GeonetowrkAccessParameter;
import org.gcube.datatransfer.resolver.gis.GeonetworkServiceInterface;
import org.gcube.datatransfer.resolver.gis.MetadataConverter;
import org.gcube.datatransfer.resolver.gis.entity.ServerParameters;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.datatransfer.resolver.gis.exception.IllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class GeonetworkResolver.
 *
 * Works as a proxy in order to authenticate HTTP POST calls on gCube Geonetwork servers discovered by SCOPE on gCube Information System
 * Used by gCube Data Catalog for harvesting metadata
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 12, 2016
 */
public class GeonetworkResolver extends HttpServlet{

	/**
	 *
	 */
	public static final String SRV_EN_MEF_EXPORT = "/srv/en/mef.export"; //MEF Geonetwork service
	/**
	 *
	 */
	public static final String UUID = "uuid";
	/**
	 *
	 */
	public static final String APPLICATION_XML = "application/xml";
	/**
	 *
	 */
	private static final long serialVersionUID = -61097584153314181L;
	public static final String SCOPE = "scope";
	public static final String REMAIN_PATH = "remainPath";
	public static final String RESET_CACHE = "resetcache";
	public static final String RESET_CACHED_SCOPE = "resetcachedscope";
	public static final String CSW_SERVER = "srv/en/csw";

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GeonetworkResolver.class);

	protected Map<String, ServerParameters> cacheGNServerParams; //A cache: scope - geonetwork parameters

	private Timer timer;

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
				logger.info("Resetting Geonetwork configuratios cache...");
				resetCacheServerParameters();
			}
		}, CACHE_RESET_DELAY, CACHE_RESET_TIME);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * This call is not authenticated
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("doGET running...");
		String scopeValue = req.getParameter(SCOPE);
		String remainValue = req.getParameter(REMAIN_PATH);
		String resetCache = req.getParameter(RESET_CACHE);
		String resetScope = req.getParameter(RESET_CACHED_SCOPE);

		if (scopeValue == null || scopeValue.equals("")) {
			logger.debug("Scope not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, SCOPE+" not found or empty");
			return;
		}

		if(resetCache!=null && Boolean.parseBoolean(resetCache)){
			resetCacheServerParameters();
		}

		if(resetScope!=null && Boolean.parseBoolean(resetScope)){
			resetCacheServerParameterForScope(scopeValue);
		}

		logger.info("SCOPE: " + scopeValue +", Query String: " + req.getQueryString());
		try {

			ServerParameters geonetworkParams = getGeonetworkCachedServerParameters(scopeValue);
			HTTPCallsUtils httpUtils = new HTTPCallsUtils();
//			boolean authorized = GNAuthentication.login(httpUtils, geonetworkParams.getUrl(), geonetworkParams.getUser(), geonetworkParams.getPassword());
//			logger.trace("Authorized on "+geonetworkParams +" ? "+authorized);
			String newQueryString = purgeScopeFromQueryString(scopeValue, req.getQueryString());
			logger.trace("Purged query string from "+scopeValue+" is: "+newQueryString);
			String baseURL = remainValue==null ||remainValue.isEmpty()?geonetworkParams.getUrl()+"/"+CSW_SERVER:geonetworkParams.getUrl()+"/"+CSW_SERVER+remainValue;
			logger.trace("New base URL "+baseURL);
			newQueryString = purgeRemainFromQueryString(remainValue, newQueryString);
			logger.trace("Purged query string from "+remainValue+" is: "+newQueryString);
			String gnGetlURL = newQueryString==null || newQueryString.isEmpty()? baseURL : baseURL+"?"+newQueryString;
			logger.info("Sending get request to URL: "+gnGetlURL);
			String response = httpUtils.get(gnGetlURL);
			logger.info("Response return Content-Type: "+httpUtils.getLastContentType());
			resp.setContentType(httpUtils.getLastContentType());
			InputStream in = IOUtils.toInputStream(response);
			OutputStream out = resp.getOutputStream();
			try{
				int bytes = IOUtils.copy(in, out);
				if(bytes==0)
					logger.warn("ResponseBody is empty, returning empty resp");
			}catch(Exception e){
				logger.error("Error on copy response:", e);
			}finally{
				IOUtils.closeQuietly(in);
			}

		} catch (IllegalArgumentException e){
			logger.error("IllegalArgumentException:", e);
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Illegal argument to carry out the request!");
			return;

		} catch (Exception e) {
			logger.error("Exception:", e);
			String error = "Sorry, an error occurred on resolving geonetwork request with scope "+scopeValue+". Please, contact support!";
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error);
			return;
		}
	}

	/**
	 * Purge remain from query string.
	 *
	 * @param remain_value the scope
	 * @param queryString the query string
	 * @return the string
	 */
	private static String purgeRemainFromQueryString(String remain_value, String queryString){
//		SCOPE is: /gcube/devsec/devVRE
//		[INFO ] 2016-04-05 15:01:42,808 org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkResolver -
//		Query String is: scope=/gcube/devsec/devVRE&version=2.0.2&request=GetCapabilities&service=CSW
		int start = queryString.indexOf(REMAIN_PATH+"=");

		if(start>=0){
			int end = queryString.indexOf("&", start);

			if(end==-1 && queryString.length()==(REMAIN_PATH+"="+remain_value).length()){ //SCOPE IS THE UNIQUE PARAMETER INTO QUETY STRING
				logger.debug("Scope is the unique parameter, returning empty query string");
				return "";
			}else if(end<queryString.length())
				return queryString.substring(end+1, queryString.length());
		}

		return queryString;
	}


	/**
	 * Purge scope from query string.
	 *
	 * @param scope_value the scope
	 * @param queryString the query string
	 * @return the string
	 */
	private static String purgeScopeFromQueryString(String scope_value, String queryString){
//		SCOPE is: /gcube/devsec/devVRE
//		[INFO ] 2016-04-05 15:01:42,808 org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkResolver -
//		Query String is: scope=/gcube/devsec/devVRE&version=2.0.2&request=GetCapabilities&service=CSW
		int start = queryString.indexOf(SCOPE+"=");

		if(start>=0){
			int end = queryString.indexOf("&", start);

			if(end==-1 && queryString.length()==(SCOPE+"="+scope_value).length()){ //SCOPE IS THE UNIQUE PARAMETER INTO QUETY STRING
				logger.debug("Scope is the unique parameter, returning empty query string");
				return "";
			}else if(end<queryString.length())
				return queryString.substring(end+1, queryString.length());
		}

		return queryString;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * This call is authenticated
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {

		/*MultiReadHttpServletRequest req2;
		if(req instanceof MultiReadHttpServletRequest)
			req2 = (MultiReadHttpServletRequest) req;*/

		logger.info("doPost running...");
		String scope = req.getParameter(SCOPE);
		String remainValue = req.getParameter(REMAIN_PATH);

		if (scope == null || scope.equals("")) {
			logger.debug("Scope not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, SCOPE+" not found or empty");
			return;
		}

		logger.info("SCOPE is: " + scope);

		try {

			ServerParameters geonetworkParams = getGeonetworkCachedServerParameters(scope);
			HTTPCallsUtils httpUtils = new HTTPCallsUtils();
			logger.info("Parameters..");
		   	for (Enumeration<String> e = req.getParameterNames(); e.hasMoreElements();){
				String p = e.nextElement();
			   logger.debug("param "+p + " value "+Arrays.toString(req.getParameterValues(p)));
		   	}

		   	//DEBUG BODY
//			String readBody = IOUtils.toString(req.getReader());
//			logger.debug("doPost read body request: "+readBody);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

			boolean authorized = GNAuthentication.login(httpUtils, geonetworkParams.getUrl(), geonetworkParams.getUser(), geonetworkParams.getPassword());
			logger.trace("Authorized on "+geonetworkParams +" ? "+authorized);

			// SPECIFIC HANDLER FOR GEONETWORK REQUEST: /srv/en/mef.export
			String gnCSWlURL;
			if(remainValue!=null && remainValue.compareTo(SRV_EN_MEF_EXPORT)==0){
				logger.info("In case of mef.export, perfoming a custom handler");
				gnCSWlURL = geonetworkParams.getUrl() + SRV_EN_MEF_EXPORT;
				String[] uuidValues = req.getParameterValues(UUID);
				if(uuidValues!=null){
					String data = null;
					for (String uuid : uuidValues) {
						data = UUID+"="+uuid;
					}
					if(data!=null){
						logger.debug("Writing "+data +" to byte array");
						byteArray.write(data.getBytes());
					}else
						IOUtils.copy(req.getReader(), byteArray);
				}else
					IOUtils.copy(req.getReader(), byteArray);
			}else{
				gnCSWlURL = remainValue==null ||remainValue.isEmpty()?geonetworkParams.getUrl()+"/"+CSW_SERVER:geonetworkParams.getUrl()+"/"+CSW_SERVER+remainValue;
				IOUtils.copy(req.getReader(), byteArray);
			}

			logger.info("Sending CSW POST request to URL: "+gnCSWlURL);
			logger.info("Content-Type: "+req.getContentType());
			//DEBUG
			logger.debug("POST - BODY : "+byteArray.toString());
			InputStream in = httpUtils.post(gnCSWlURL, new ByteArrayInputStream(byteArray.toByteArray()), req.getContentType(), req.getParameterMap());
			//END DEBUG
			logger.info("Response return Content-Type: "+httpUtils.getLastContentType());
			resp.setContentType(httpUtils.getLastContentType());
			OutputStream out = resp.getOutputStream();

			if(in==null){
				logger.warn("Input stream returned is null, sending "+HttpServletResponse.SC_NOT_FOUND);
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			try{
				int bytes = IOUtils.copy(in, out);
				if(bytes==0)
					logger.warn("ResponseBody is empty, returning empty resp");
			}catch(Exception e){
				logger.error("Error on copy response:", e);
			}finally{
				IOUtils.closeQuietly(in);
			}

		} catch (IllegalArgumentException e){
			logger.error("IllegalArgumentException:", e);
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Illegal argument to carry out the request!");
			return;

		} catch (Exception e) {
			logger.error("Exception:", e);
			String error = "Sorry, an error occurred on resolving geonetwork request with scope "+scope+". Please, contact support!";
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error);
			return;
		}
	}

	/**
	 * Gets the geonetwork cached server parameters.
	 *
	 * @param scope the scope
	 * @return the geonetwork cached server parameters
	 * @throws Exception the exception
	 */
	protected ServerParameters getGeonetworkCachedServerParameters(String scope) throws Exception{

		if(cacheGNServerParams==null)
			resetCacheServerParameters();

		logger.info("Tentative for recovering geonetwork server parameters from cache with scope: "+scope);
		ServerParameters serverParam = cacheGNServerParams.get(scope);

		if(serverParam==null){
			logger.info("Cache having null Geonetwork server parameters, reading from IS..");
			GeoRuntimeReader reader = new GeoRuntimeReader();
			try {
				serverParam = reader.retrieveGisParameters(scope, GEO_SERVICE.GEONETWORK);
				cacheGNServerParams.put(scope, serverParam);
				logger.info("Updated Cache for Geonetwork server parameters! Scope "+scope+" linking "+serverParam);
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
	 * Reset cache server parameter for scope.
	 *
	 * @param scope the scope
	 */
	private void resetCacheServerParameterForScope(String scope){
		if(cacheGNServerParams!=null && cacheGNServerParams.get(scope)!=null){
			cacheGNServerParams.remove(scope);
			logger.info("Reset of "+scope+" in Cache Geonetwork server params perfomed!");
		}else
			logger.info("Reset of "+scope+" in Cache Geonetwork skipped, scope not exists!");
	}


	/**
	 * Reset cache server parameters.
	 */
	private void resetCacheServerParameters(){
		cacheGNServerParams = new HashMap<String, ServerParameters>();
		logger.info("Reset of Cache Geonetwork server params perfomed!");
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
			GeonetworkServiceInterface gntwAccess = new GeonetowrkAccessParameter(scope, geonetworkParams);
			return MetadataConverter.getWMSOnLineResource(gntwAccess.getGeonetworkInstance(true), gisUUID);
		}catch (GeonetworkInstanceException e){
			logger.error("An error occurred when instancing geonetowrk gis layer with UUID "+gisUUID, e);
			throw new IllegalArgumentException("Sorry, An error occurred when instancing geonetwork with UUID: "+gisUUID);
		} catch (Exception e) {
			logger.error("An error occurred when retrieving gis layer with UUID "+gisUUID, e);
			throw new IllegalArgumentException("Sorry, An error occurred when retrieving gis layer with UUID "+gisUUID);
		}
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
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		/*String scopeValue ="/gcube/devsec/devVRE";
		String remainValue = "/srv/en/mef.export";
		String queryString = "scope=/gcube/devsec/devVRE&remainPath=/srv/en/mef.export&version=2.0.2&request=GetCapabilities&service=CSW";
		ServerParameters geonetworkParams = new ServerParameters("http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork", "", "");

		String newQueryString = purgeScopeFromQueryString(scopeValue, queryString);
		logger.info("Purged query string from "+scopeValue+" is: "+newQueryString);

		String baseURL = remainValue==null ||remainValue.isEmpty()?geonetworkParams.getUrl()+"/"+CSW_SERVER:geonetworkParams.getUrl()+"/"+CSW_SERVER+remainValue;
		logger.info("New base URL "+baseURL);
		newQueryString = purgeRemainFromQueryString(remainValue, newQueryString);
		logger.info("Purged query string from "+remainValue+" is: "+newQueryString);

		String gnGetlURL = newQueryString==null || newQueryString.isEmpty()? baseURL : baseURL+"?"+newQueryString;
		logger.info("Sending get request to URL: "+gnGetlURL);*/

		HTTPCallsUtils httpUtils = new HTTPCallsUtils();
		String data ="";

		String contentType = "application/x-www-form-urlencoded";
		String uuid = "fao-fsa-map-27.7.j";
		String gnCSWlURL = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/srv/en//srv/en/mef.export";

		try {
			/*File file = File.createTempFile(uuid, ".xml");
			InputStream response = httpUtils.post(gnCSWlURL, file, contentType);*/

			//MAP
			Map<String, String[]> map = new HashMap<String, String[]>();
			String[] value = new String[1];
			value[0]=uuid;
			map.put(UUID, value);
//			data = "<request><uuid>"+uuid+"</uuid></request>";
			data = "uuid="+uuid;
			byte[] byteArray = data.getBytes();
			InputStream response = httpUtils.post(gnCSWlURL, new ByteArrayInputStream(byteArray), contentType, map);

			if(response!=null){
				try {
					final Path destination = Paths.get("test");
					Files.copy(response, destination);
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
