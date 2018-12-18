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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters;
import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters.MODE;
import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters.VISIBILITY;
import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter;
import org.gcube.datatransfer.resolver.gis.GeonetworkInstance;
import org.gcube.datatransfer.resolver.gis.GeonetworkServiceInterface;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.datatransfer.resolver.gis.geonetwork.HTTPCallsUtils.HttpResponse;
import org.gcube.datatransfer.resolver.gis.util.GetResponseRecordFilter;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;



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
	public static final String REMAIN_PATH_PARAM = "remainPath";
	public static final String RESET_CACHE_PARAM = "resetcache";
	public static final String RESET_CACHED_SCOPE_PARAM = "resetcachedscope";
	public static final String CSW_SERVER = "srv/en/csw";

//	public static final String PARAMETER_FILTER_PUBLIC_IDS = UriResolverRewriteFilter.PARAMETER_FILTER_PUBLIC_IDS;
//	public static final String PARAMETER_NO_AUTHENTICATION = UriResolverRewriteFilter.PARAMETER_NO_AUTHENTICATION;

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GeonetworkResolver.class);

	protected Map<String, GeonetworkInstance> cacheGNInstances; //A cache: scope - GeonetworkInstance

	private Timer timer;

	//THIRTY MINUTES
	public static final long CACHE_RESET_TIME = 30*60*1000;

	//TEN SECONDS
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
				logger.info("Resetting Geonetwork configuratiors cache...");
				purgeCacheGeonetworkInstances();
			}
		}, CACHE_RESET_DELAY, CACHE_RESET_TIME);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * This call is not authenticated
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("doGET running... query string: "+req.getQueryString());
		String scopeValue = req.getParameter(SCOPE);
		String remainValue = req.getParameter(REMAIN_PATH_PARAM);
		String mode = req.getParameter(GeonetworkRequestFilterParameters.MODE.class.getSimpleName());
		String visibility = req.getParameter(GeonetworkRequestFilterParameters.VISIBILITY.class.getSimpleName());
		String owner = req.getParameter(GeonetworkRequestFilterParameters.OWNER_PARAM);
		String resetCache = req.getParameter(RESET_CACHE_PARAM);
		String resetScope = req.getParameter(RESET_CACHED_SCOPE_PARAM);

		String originalScope = ScopeProvider.instance.get();

		if (scopeValue == null || scopeValue.equals("")) {
			logger.debug("Scope not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, SCOPE+" not found or empty");
			return;
		}

		MODE theMode;
		VISIBILITY theVisibility;

		theMode = GeonetworkRequestFilterParameters.MODE.valueOf(mode);
		theVisibility = GeonetworkRequestFilterParameters.VISIBILITY.valueOf(visibility);

		if(resetCache!=null && Boolean.parseBoolean(resetCache)){
			purgeCacheGeonetworkInstances();
		}

		if(resetScope!=null && Boolean.parseBoolean(resetScope)){
			resetGeonetoworkInstanceCacheForScope(scopeValue);
		}

		logger.info("SCOPE: " + scopeValue);
		logger.info("MODE is: "+theMode);
		logger.info("VISIBILITY is: "+theVisibility);
		logger.info(GeonetworkRequestFilterParameters.OWNER_PARAM +" is: "+owner);

		try {

			GeonetworkInstance gnInstance = getGeonetworkInstanceForScope(scopeValue);

//			if(gnInstance==null){
//				logger.info("GeonetworkInstance not istanciable via geonetwork library.. using ");
//				ServerParameters serverParams = getGeonetworkCachedServerParameters(scopeValue);
//				gnInstance = gntwAccess.getGeonetworkInstance();
//			}

			ScopeProvider.instance.set(scopeValue);

			HTTPCallsUtils httpUtils = new HTTPCallsUtils();
			Configuration config = gnInstance.getGeonetworkPublisher().getConfiguration();
			String geonetworkUrl = config.getGeoNetworkEndpoint();
//			boolean authorized = GNAuthentication.login(httpUtils, geonetworkParams.getUrl(), geonetworkParams.getUser(), geonetworkParams.getPassword());
//			logger.trace("Authorized on "+geonetworkParams +" ? "+authorized);
			String newQueryString = purgeScopeFromQueryString(scopeValue, req.getQueryString());
			logger.trace("Purged query string from "+scopeValue+" is: "+newQueryString);
			String baseURL = remainValue==null ||remainValue.isEmpty()?geonetworkUrl+"/"+CSW_SERVER:geonetworkUrl+"/"+CSW_SERVER+remainValue;
			logger.trace("New base URL "+baseURL);
			newQueryString = purgeRemainFromQueryString(remainValue, newQueryString);
			logger.trace("Purged query string from "+remainValue+" is: "+newQueryString);
			String gnGetlURL = newQueryString==null || newQueryString.isEmpty()? baseURL : baseURL+"?"+newQueryString;
			logger.info("Sending get request to URL: "+gnGetlURL);
			HttpResponse response = httpUtils.get(gnGetlURL);

			switch(response.getStatus()){
			case HttpServletResponse.SC_OK:

				logger.info("Response return Content-Type: "+httpUtils.getLastContentType());
				resp.setContentType(httpUtils.getLastContentType());
				InputStream in = IOUtils.toInputStream(response.getResponse());
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

				break;

			case HttpServletResponse.SC_FORBIDDEN:

				sendError(resp, response.getStatus(), "Sorry, you are not authorized to perform this request");
				break;

			default:
				sendError(resp, response.getStatus(), "Sorry, an error occurred on resolving geonetwork request with scope "+scopeValue);
			}

		} catch (Exception e) {
			logger.error("Exception:", e);
			String error = "Sorry, an error occurred on resolving geonetwork request with scope "+scopeValue+". Please, contact support!";
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
		int start = queryString.indexOf(REMAIN_PATH_PARAM+"=");

		if(start>=0){
			int end = queryString.indexOf("&", start);

			if(end==-1 && queryString.length()==(REMAIN_PATH_PARAM+"="+remain_value).length()){ //SCOPE IS THE UNIQUE PARAMETER INTO QUETY STRING
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
	@SuppressWarnings("resource")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {

		/*MultiReadHttpServletRequest req2;
		if(req instanceof MultiReadHttpServletRequest)
			req2 = (MultiReadHttpServletRequest) req;*/

		String originalScope = ScopeProvider.instance.get();
		logger.info("doPost running...");
		String scope = req.getParameter(SCOPE);
		String remainValue = req.getParameter(REMAIN_PATH_PARAM);
		String mode = req.getParameter(GeonetworkRequestFilterParameters.MODE.class.getSimpleName());
		String visibility = req.getParameter(GeonetworkRequestFilterParameters.VISIBILITY.class.getSimpleName());
		String theOwner = req.getParameter(GeonetworkRequestFilterParameters.OWNER_PARAM);
		//boolean filterPublicMetadata = false;
		boolean noAuthenticationB = false;

		MODE theMode;
		VISIBILITY theVisibility;

		if (scope == null || scope.equals("")) {
			logger.debug("Scope not found");
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, SCOPE+" not found or empty");
			return;
		}

		theMode = GeonetworkRequestFilterParameters.MODE.valueOf(mode);
		theVisibility = GeonetworkRequestFilterParameters.VISIBILITY.valueOf(visibility);

		logger.info(SCOPE +" is: " + scope);
		logger.info(GeonetworkRequestFilterParameters.MODE.class.getSimpleName() +" is: "+theMode);
		logger.info(GeonetworkRequestFilterParameters.VISIBILITY.class.getSimpleName() +" is: "+theVisibility);
		logger.info(GeonetworkRequestFilterParameters.OWNER_PARAM +" is: "+theOwner);

		try {

			GeonetworkServiceInterface gntwAccess = new GeonetworkAccessParameter(scope);
			GeonetworkInstance gnInstance = gntwAccess.getGeonetworkInstance();

			ScopeProvider.instance.set(scope);
			logger.info("set scope provider "+scope);
			Configuration config = gnInstance.getGeonetworkPublisher().getConfiguration();
			Account account = config.getScopeConfiguration().getAccounts().get(Type.CKAN);
			logger.info("CKAN user owner is: "+account.getUser());

			logger.info("Parameters..");
		   	for (Enumeration<String> e = req.getParameterNames(); e.hasMoreElements();){
				String p = e.nextElement();
			   logger.debug("param "+p + " value "+Arrays.toString(req.getParameterValues(p)));
		   	}

		   	//DEBUG BODY
//			String readBody = IOUtils.toString(req.getReader());
//			logger.debug("doPost read body request: "+readBody);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

			String geonetworkUrl = config.getGeoNetworkEndpoint();

			// SPECIFIC HANDLER FOR GEONETWORK REQUEST: /srv/en/mef.export
			String gnCSWlURL;
			if(remainValue!=null && remainValue.compareTo(SRV_EN_MEF_EXPORT)==0){
				logger.info("In case of mef.export, perfoming a custom handler");
				gnCSWlURL = geonetworkUrl + SRV_EN_MEF_EXPORT;
				String[] uuidValues = req.getParameterValues(UUID);
				if(uuidValues!=null){
					String data = null;
					for (String uuid : uuidValues) {
						data = UUID+"="+uuid;
					}
					if(data!=null){
						logger.debug("Writing "+data +" into byte array");
						byteArray.write(data.getBytes());
					}else
						IOUtils.copy(req.getReader(), byteArray);
				}else
					IOUtils.copy(req.getReader(), byteArray);
			}else{
				gnCSWlURL = remainValue==null ||remainValue.isEmpty()?geonetworkUrl+"/"+CSW_SERVER:geonetworkUrl+"/"+CSW_SERVER+remainValue;
				IOUtils.copy(req.getReader(), byteArray);
			}

			//filterPublicMetadata = theVisibility.equals(VISIBILITY.PRV)?true:false;

			HTTPCallsUtils httpUtils = new HTTPCallsUtils();

			//PRIVATE LAYERS
			if(theVisibility.equals(VISIBILITY.PRV)){
				logger.debug("Visibility: "+VISIBILITY.PRV+" getting private layers..");
				//VRE LAYERS
				if(theMode.equals(MODE.VRE)){
					logger.debug("Getting "+MODE.VRE+" layers..");

				//HARVESTED LAYERS
				}else{
					logger.debug("Getting "+MODE.HARVEST+" layers, I'm using the owner: '"+theOwner +"' passed as parameter to filter layer/s returned..");
					if(theOwner==null || theOwner.isEmpty()){
						String error = "Harvest owner is missing. It is not possible to filter layers for the request "+MODE.HARVEST + " in the scope: "+scope+", without a valid owner as input";
						logger.error(error);
						sendError(resp, HttpServletResponse.SC_BAD_REQUEST, error);
						return;
					}
				}

				if(account.getUser()!=null){
					boolean authorized = GNAuthentication.login(httpUtils, geonetworkUrl, account.getUser(), account. getPassword());
					logger.trace("Authorized on "+geonetworkUrl +" ? "+authorized);
				}else
					logger.info("Skipping authentication, ckan user (the owner) is null");

			//PUBLIC LAYERS
			}else{
				logger.debug("Visibility: "+VISIBILITY.PUB+" getting public layers..");
				//VRE LAYERS
				if(theMode.equals(MODE.VRE)){
					logger.debug("Getting "+MODE.VRE+" layers, the VRE account: "+account.getUser() +" will be used as owner user for filtering... Is it right?");
					theOwner = account.getUser();

				//HARVESTED LAYERS
				}else{
					logger.debug("Getting "+MODE.HARVEST+" layers, I'm using the owner: '"+theOwner +"' passed as parameter to filter layer/s returned..");
					if(theOwner==null || theOwner.isEmpty()){
						String error = "Harvest owner is missing. It is not possible to filter layers for the request "+MODE.HARVEST + " in the scope: "+scope+", without a valid owner as input";
						logger.error(error);
						sendError(resp, HttpServletResponse.SC_BAD_REQUEST, error);
						return;
					}
				}
			}

			logger.info("Sending CSW POST request to URL: "+gnCSWlURL);
			logger.debug("Content-Type: "+req.getContentType());
			//DEBUG
			//logger.debug("POST - BODY : "+byteArray.toString());
			InputStream in = httpUtils.post(gnCSWlURL, new ByteArrayInputStream(byteArray.toByteArray()), req.getContentType(), req.getParameterMap());
			//END DEBUG
			logger.debug("Response return Content-Type: "+httpUtils.getLastContentType());
			resp.setContentType(httpUtils.getLastContentType());
			OutputStream out = resp.getOutputStream();

			if(in==null){
				logger.warn("Input stream returned is null, sending "+HttpServletResponse.SC_NOT_FOUND);
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			try{

				ReusableInputStream reus = new ReusableInputStream(in);

				if(theVisibility.equals(VISIBILITY.PRV)){
					logger.info("Private VISIBILITY performing so getting public file identifiers to apply filtering..");
					FilterGetRecords filterGetRecords = new FilterGetRecords(byteArray.toString());
					if(filterGetRecords.getFoundPublicIds()!=null && filterGetRecords.getFoundPublicIds().size()>0){
						logger.info("I'm removing list of public IDs with "+filterGetRecords.getFoundPublicIds().size() +" item/s. Is it right?");
						in = GetResponseRecordFilter.overrideResponseIdsByListIds(reus, filterGetRecords.getFoundPublicIds(), "Replaced a public UUID, please ignore");
					}
				}else {

					logger.info("Public VISIBILITY perfoming check on ownership...");
					Document doc = GetResponseRecordFilter.inputStreamToW3CDocument(reus);
					List<String> fileIdentifiers = GetResponseRecordFilter.getTextContentStringsForTagName(doc, "gmd:fileIdentifier");
					List<String> noMatchingOwner = new ArrayList<String>();
					for (String fileId : fileIdentifiers) {
						String own = GetResponseRecordFilter.getMetaCategoryByFileIdentifier(fileId, config.getGeoNetworkEndpoint(),config.getAdminAccount().getUser(), config.getAdminAccount().getPassword());
						//String own = GetResponseRecordFilter.getMetaOwnerNameByFileIdentifier(fileId, config.getGeoNetworkEndpoint(),config.getAdminAccount().getUser(), config.getAdminAccount().getPassword());
						if(own.compareTo(theOwner)!=0){
							logger.debug("Owner of file Identifier "+fileId+" not matching the owner passed: "+theOwner+", removing it..");
							noMatchingOwner.add(fileId);
						}
					}
					if(noMatchingOwner.size()>0){
						logger.info("Removing "+noMatchingOwner.size()+" layer/s not macthing the owner: "+theOwner);
						in = GetResponseRecordFilter.overrideResponseIdsByListIds(reus, noMatchingOwner, "Replaced UUID owned by another user, please ignore");
					}else{
						logger.info("No replace on UUIDs was applied for the owner: "+theOwner);
						in = reus;
					}
				}

				ReusableInputStream reusIs = new ReusableInputStream(in);
				int bytes = IOUtils.copy(reusIs, out);
				//logger.trace("POST - RETURN : "+IOUtils.toString(reusIs));

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
	 * Gets the geonetwork instance for scope.
	 *
	 * @param scope the scope
	 * @return the geonetwork instance for scope
	 * @throws Exception the exception
	 */
	protected GeonetworkInstance getGeonetworkInstanceForScope(String scope) throws Exception{

		if(cacheGNInstances==null)
			purgeCacheGeonetworkInstances();

		logger.info("Attempt to get geonetwork instance from GeonetworkInstance cache for scope: "+scope);
		GeonetworkInstance geoInstance = cacheGNInstances.get(scope);

		if(geoInstance==null){
			logger.info("Cache having null GeonetworkInstance for scope "+scope+", reading by Geonetwork library...");
			try {
				geoInstance = discoveryGeonetworkInstance(scope);
				cacheGNInstances.put(scope, geoInstance);
				logger.info("Updated GeonetworkInstance Cache adding couple: Scope "+scope+" - GeonetworkInstance "+geoInstance);
			} catch (Exception e) {
				logger.error("An error occurred on reading GeonetworkInstance for scope "+scope, e);
				throw new Exception("Sorry, An error occurred on reading GeonetworkInstance for scope "+scope);
			}
		}else
			logger.info("GeonetworkInstance cache for scope: "+scope+" is not null using it: "+geoInstance);

		return geoInstance;
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
		return gntwAccess.getGeonetworkInstance(true, null);
	}



	/**
	 * Reset geonetowork instance cache for scope.
	 *
	 * @param scope the scope
	 */
	private void resetGeonetoworkInstanceCacheForScope(String scope){
		if(cacheGNInstances!=null && cacheGNInstances.get(scope)!=null){
			cacheGNInstances.remove(scope);
			logger.info("Reset of "+scope+" in Cache Geonetwork server params perfomed!");
		}else
			logger.info("Reset of "+scope+" in Cache Geonetwork skipped, scope not exists!");
	}



	/**
	 * Purge cache geonetwork instances.
	 */
	private void purgeCacheGeonetworkInstances(){
		cacheGNInstances = new HashMap<String, GeonetworkInstance>();
		logger.info("Reset of GeonetworkInstance cache perfomed!");
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
}
