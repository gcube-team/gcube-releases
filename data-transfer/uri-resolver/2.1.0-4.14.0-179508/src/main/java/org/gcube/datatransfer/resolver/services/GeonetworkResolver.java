package org.gcube.datatransfer.resolver.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.ConstantsResolver;
import org.gcube.datatransfer.resolver.caches.LoadingGeonetworkInstanceCache;
import org.gcube.datatransfer.resolver.gis.GeonetworkInstance;
import org.gcube.datatransfer.resolver.gis.geonetwork.FilterGetRecords;
import org.gcube.datatransfer.resolver.gis.geonetwork.GNAuthentication;
import org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkRequestFilterParameters.MODE;
import org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkRequestFilterParameters.VISIBILITY;
import org.gcube.datatransfer.resolver.gis.geonetwork.ReusableInputStream;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.gcube.datatransfer.resolver.util.GetResponseRecordFilter;
import org.gcube.datatransfer.resolver.util.HTTPCallsUtils;
import org.gcube.datatransfer.resolver.util.HTTPCallsUtils.HttpResponse;
import org.gcube.datatransfer.resolver.util.ScopeUtil;
import org.gcube.datatransfer.resolver.util.SingleFileStreamingOutput;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess.Version;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * The Class GeonetworkResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 23, 2018
 */
@Path("geonetwork")
public class GeonetworkResolver {

	/**
	 *
	 */
	public static final String REPLACED_A_PUBLIC_UUID_PLEASE_IGNORE = "Replaced a public UUID, please ignore";
	public static final String REPLACED_UUID_BY_FILTER_PLEASE_IGNORE = "Replaced UUID by "+GeonetworkResolver.class.getSimpleName()+" filters, please ignore";

	private static Logger logger = LoggerFactory.getLogger(GeonetworkResolver.class);

	public static final String PATH_PARAM_MODE = "mode";
	public static final String PATH_PARAM_REQUEST_DELIMITER = "requestDelimiter";
//	public static final String PATH_PARAM_OWNER = "owner";
	private static final String PATH_PARAM_VISIBILITY = "visibility";

	private static final String PATH_PARAM_SCOPE = "scope";
	public static final String CSW_SERVER = "srv/en/csw";

	public static final String VALUE_OF_REQUEST_DELIMITIER = "$$";
	public static final String PATH_PARAM_REMAINPATH = "remainPath";

	public static final String QUERY_PARAM_RESET_CACHE_PARAM = "resetcache";
	public static final String QUERY_PARAM_RESET_CACHED_SCOPE_PARAM = "resetcachedscope";

	public static final String SRV_EN_MEF_EXPORT = "/srv/en/mef.export"; //MEF Geonetwork service

	public static final String UUID = "uuid";

	public static final String SCOPE_SEPARATOR = "|";

	//protected Map<String, GeonetworkInstance> cacheGNInstances;

	private String helpURI = "https://wiki.gcube-system.org/gcube/GCube_Resource_Catalogue#Geonetwork_Resolver";

	/**
	 * Gets the geonetwork request criteria.
	 * Creates a request criteria from input parameter pathWithoutGN
	 * The parameter pathWithoutGN should be an ordered string (like REST request):
	 * SCOPE/MODE/VISIBILITY/OWNER/$$
	 * SCOPE must be: ROOT|VO|VRE
	 * MODE must be: {@link MODE}
	 * VISIBILITY must be: {@link VISIBILITY}
	 * OWNER (is optional): filter by owner
	 *
	 * @param req the req
	 * @param scope the scope
	 * @param mode the mode
	 * @param visibility the visibility
	 * @param filterKey the filter key
	 * @param filterValue the filter value
	 * @param remainPath the remain path
	 * @param resetCache the reset cache
	 * @param resetScope the reset scope
	 * @return the geonetwork request criteria
	 * @throws WebApplicationException the web application exception
	 */

	@GET
	@Path("/{"+PATH_PARAM_SCOPE+"}/{"+PATH_PARAM_MODE+"}/{"+PATH_PARAM_VISIBILITY+"}/{filterKey}/{filterValue}/$${"+PATH_PARAM_REMAINPATH+":(/[^?$]+)?}")
	public Response submitGet(@Context HttpServletRequest req,
		@PathParam(PATH_PARAM_SCOPE) @Nullable String scope,
		@PathParam(PATH_PARAM_MODE) @Nullable String mode,
		@PathParam(PATH_PARAM_VISIBILITY) @Nullable String visibility,
		@PathParam("filterKey") @Nullable String filterKey,
		@PathParam("filterValue") @Nullable String filterValue,
		@PathParam(PATH_PARAM_REMAINPATH) @Nullable String remainPath,
		@QueryParam(QUERY_PARAM_RESET_CACHE_PARAM) @Nullable String resetCache,
		@QueryParam(QUERY_PARAM_RESET_CACHED_SCOPE_PARAM) @Nullable String resetScope) throws WebApplicationException{

		logger.info(this.getClass().getSimpleName()+" GET starts...");
		String gnGetlURL = null;

		try {
			logger.info("Params are [mode: "+mode+", scope: "+scope+", visibility: "+visibility+", filterKey: "+filterKey+", filterValue: "+filterValue+", remainPath: "+remainPath+"]");

			if(scope==null || scope.isEmpty()){
				logger.error("Path Parameter 'scope' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter 'scope'", this.getClass(), helpURI);
			}

			if(mode==null || mode.isEmpty()){
				logger.error("Path Parameter 'scope' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter 'mode'", this.getClass(), helpURI);
			}

			scope = ScopeUtil.normalizeScope(scope, "|");
			mode = mode.toUpperCase();
			try{
				MODE.valueOf(mode);
			}catch(Exception e){
				List<MODE> toPrint = Arrays.asList(MODE.values());
				logger.error("The 'mode' parameter is wrong, Have you pass a valid parameter MODE like "+toPrint+"?");
				throw ExceptionManager.wrongParameterException(req, "The 'mode' parameter must be value of "+toPrint, this.getClass(), helpURI);
			}

			if(visibility==null){
				logger.error("Path Parameter 'visibility' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter 'visibility'", this.getClass(), helpURI);
			}

			visibility = visibility.toUpperCase();
			try{
				VISIBILITY.valueOf(visibility);
			}catch (Exception e) {
				List<VISIBILITY> toPrint = Arrays.asList(VISIBILITY.values());
				logger.error("The 'visibility' parameter is wrong, Have you pass a valid parameter VISIBILITY like "+toPrint+"?");
				throw ExceptionManager.wrongParameterException(req, "The 'visibility' parameter must be value of "+toPrint, this.getClass(), helpURI);
			}

			logger.info("Remaining path is: "+remainPath);
			
			
			try {
				
				//I'M LOADING GN CONFIGURATIONS (ENDPOINT, USER, PWD AND SO ON..) FOR PASSED SCOPE FROM GN LIBRARY BY A GENERAL METHOD WITHOUT TO PERFORM AUTHENTICATION ON GN,
				//AFTER THAT I'M USING THEM TO PERFORM AUTHENTICATION ON GN VIA HTTP_CLIENTS IF NEEDED
				GeonetworkInstance gnInstance = getGeonetworkInstanceForScope(scope);
				logger.info("set scope provider "+scope);
				ScopeProvider.instance.set(scope);
				Account account = gnInstance.getAccount();
				Version version = gnInstance.getVersion();
				String geonetworkUrl = gnInstance.getEndPoint();
				Configuration config = gnInstance.getConfig()!=null?gnInstance.getConfig():null;
				
				if(account==null || account.getUser()==null || account.getPassword()==null || config==null) {
					logger.info("Loading GN instance and configurations via Geonetwork Library...");
					config = gnInstance.getGeonetworkPublisher().getConfiguration();
					account = config.getScopeConfiguration().getAccounts().get(Type.CKAN);
					version = config.getGeoNetworkVersion();
					geonetworkUrl = config.getGeoNetworkEndpoint();
				}
				
				logger.info("SCOPE: {}, CKAN user used is: {}, GN EndPoint: {}",scope, account.getUser(), geonetworkUrl);
				
				HTTPCallsUtils httpUtils = new HTTPCallsUtils();
				
				if(visibility.equals(VISIBILITY.PRV.name())){
					if(account.getUser()!=null){
						switch (version) {
						case DUE:
							boolean authorized = GNAuthentication.login(httpUtils, geonetworkUrl, account.getUser(), account.getPassword());
							logger.info("Authorized on GN2 "+geonetworkUrl +" ? "+authorized);
							break;
	
						default:
							httpUtils = new HTTPCallsUtils(account.getUser(), account.getPassword());
							logger.info("Authorized on GN3 via HTTCallsUtils...");
							break;
						}
						
					}else {
						logger.warn("I'm not able to perform authentication, the user read from config with "+Type.CKAN+" is null");
					}
				}

				String baseURL = remainPath==null ||remainPath.isEmpty()?geonetworkUrl+"/"+CSW_SERVER:geonetworkUrl+"/"+CSW_SERVER+remainPath;
				logger.info("The base URL is: "+baseURL);
				String queryString = req.getQueryString()==null || req.getQueryString().isEmpty()?"":"?"+req.getQueryString();
				gnGetlURL = baseURL+queryString;
				logger.info("Sending get request to URL: "+gnGetlURL);
				HttpResponse proxedGNResponse = httpUtils.get(gnGetlURL);

				switch(proxedGNResponse.getStatus()){
				case HttpServletResponse.SC_OK:

					//Building the response
					InputStream streamToWrite = IOUtils.toInputStream(proxedGNResponse.getResponse());
					StreamingOutput so = new SingleFileStreamingOutput(streamToWrite);

					logger.info("Response return Content-Type: "+httpUtils.getLastContentType());

					ResponseBuilder responseBuilder = Response
					.ok(so)
					.header(ConstantsResolver.CONTENT_TYPE, httpUtils.getLastContentType());
					return responseBuilder.build();

				case HttpServletResponse.SC_FORBIDDEN:
					throw ExceptionManager.forbiddenException(req, "You are not authorized to perform the request "+gnGetlURL, this.getClass(), helpURI);

				default:
					throw ExceptionManager.internalErrorException(req, "Sorry, an error occurred performing the geonetwork request "+gnGetlURL+" with scope "+scope, this.getClass(), helpURI);
				}

			} catch (Exception e) {
				logger.error("Exception:", e);
				String error = "Sorry, an error occurred on resolving geonetwork request with scope "+scope+". Please, contact support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}


		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error during perform GET operation to: "+gnGetlURL+". Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}

	}

	/**
	 * Submit post.
	 *
	 * @param req the req
	 * @param scope the scope
	 * @param mode the mode
	 * @param visibility the visibility
	 * @param filterKey the filter key
	 * @param filterValue the filter value
	 * @param remainPath the remain path
	 * @param resetCache the reset cache
	 * @param resetScope the reset scope
	 * @return the response
	 * @throws WebApplicationException the web application exception
	 */
	@POST
	@Path("/{"+PATH_PARAM_SCOPE+"}/{"+PATH_PARAM_MODE+"}/{"+PATH_PARAM_VISIBILITY+"}/{filterKey}/{filterValue}/$${"+PATH_PARAM_REMAINPATH+":(/[^?$]+)?}")
	public Response submitPost(@Context HttpServletRequest req,
		@PathParam(PATH_PARAM_SCOPE) @Nullable String scope,
		@PathParam(PATH_PARAM_MODE) @Nullable String mode,
		@PathParam(PATH_PARAM_VISIBILITY) @Nullable String visibility,
		@PathParam("filterKey") @Nullable String filterKey,
		@PathParam("filterValue") @Nullable String filterValue,
		@PathParam(PATH_PARAM_REMAINPATH) @Nullable String remainPath,
		@QueryParam(QUERY_PARAM_RESET_CACHE_PARAM) @Nullable String resetCache,
		@QueryParam(QUERY_PARAM_RESET_CACHED_SCOPE_PARAM) @Nullable String resetScope) throws WebApplicationException{

		logger.info(this.getClass().getSimpleName()+" POST starts...");
		String gnCSWlURL = null;

		try{
			logger.info("Params are [mode: "+mode+", scope: "+scope+", visibility: "+visibility+", filterKey: "+filterKey+", filterValue: "+filterValue+", remainPath: "+remainPath+"]");

			if(scope==null || scope.isEmpty()){
				logger.error("Path Parameter 'scope' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter 'scope'", this.getClass(), helpURI);
			}

			if(mode==null || mode.isEmpty()){
				logger.error("Path Parameter 'scope' not found");
				ExceptionManager.badRequestException(req, "Missing mandatory path parameter 'mode'", this.getClass(), helpURI);
			}

			scope = ScopeUtil.normalizeScope(scope, "|");
			mode = mode.toUpperCase();
			try{
				MODE.valueOf(mode);
			}catch(Exception e){
				List<MODE> toPrint = Arrays.asList(MODE.values());
				logger.error("The 'mode' parameter is wrong, Have you pass a valid parameter MODE like "+toPrint+"?");
				throw ExceptionManager.wrongParameterException(req, "The 'mode' parameter must be value of "+toPrint, this.getClass(), helpURI);
			}

			if(visibility==null){
				logger.error("Path Parameter 'visibility' not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory path parameter 'visibility'", this.getClass(), helpURI);
			}

			visibility = visibility.toUpperCase();
			try{
				VISIBILITY.valueOf(visibility);
			}catch (Exception e) {
				List<VISIBILITY> toPrint = Arrays.asList(VISIBILITY.values());
				logger.error("The 'visibility' parameter is wrong, Have you pass a valid parameter VISIBILITY like "+toPrint+"?");
				throw ExceptionManager.wrongParameterException(req, "The 'visibility' parameter must be value of "+toPrint, this.getClass(), helpURI);
			}

			//HOW TO PASS ANY FILTER?
			//TODO INVESTIGATE ON HOW TO PASS MORE THAN ONE FILTER...
			Map<String,String> filters = new HashMap<String, String>();
			if(filterKey!=null && filterValue!=null){
				//IGNORING THE EMPTY FILTER 'null|null'
				if(filterKey.compareToIgnoreCase("null")!=0){
					filters.put(filterKey, filterValue);
					logger.debug("Added filter parmas to map filters: "+filters);
				}
			}


			try {

				//I'M LOADING GN CONFIGURATIONS (ENDPOINT, USER, PWD AND SO ON..) FOR PASSED SCOPE FROM GN LIBRARY BY A GENERAL METHOD WITHOUT TO PERFORM AUTHENTICATION ON GN,
				//AFTER THAT I'M USING THEM TO PERFORM AUTHENTICATION ON GN VIA HTTP_CLIENTS IF NEEDED
				GeonetworkInstance gnInstance = getGeonetworkInstanceForScope(scope);
				logger.info("set scope provider "+scope);
				ScopeProvider.instance.set(scope);
				Account account = gnInstance.getAccount();
				Version version = gnInstance.getVersion();
				String geonetworkUrl = gnInstance.getEndPoint();
				Configuration config = gnInstance.getConfig()!=null?gnInstance.getConfig():null;
				
				if(account==null || account.getUser()==null || account.getPassword()==null || config==null) {
					logger.info("Loading GN instance and configurations via Geonetwork Library...");
					config = gnInstance.getGeonetworkPublisher().getConfiguration();
					account = config.getScopeConfiguration().getAccounts().get(Type.CKAN);
					version = config.getGeoNetworkVersion();
					geonetworkUrl = config.getGeoNetworkEndpoint();
				}
				logger.info("SCOPE: {}, CKAN user used is: {}, GN EndPoint: {}",scope, account.getUser(), geonetworkUrl);

	//			logger.info("Parameters..");
	//		   	for (Enumeration<String> e = req.getParameterNames(); e.hasMoreElements();){
	//				String p = e.nextElement();
	//			   logger.debug("param "+p + " value "+Arrays.toString(req.getParameterValues(p)));
	//		   	}

			   	//DEBUG BODY
	//			String readBody = IOUtils.toString(req.getReader());
	//			logger.debug("doPost read body request: "+readBody);
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();


				// SPECIFIC HANDLER FOR GEONETWORK REQUEST: /srv/en/mef.export
				if(remainPath!=null && remainPath.compareTo(SRV_EN_MEF_EXPORT)==0){
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
							IOUtils.copy(req.getInputStream(), byteArray);
					}else
						IOUtils.copy(req.getInputStream(), byteArray);
				}else{
					logger.debug("IS NOT A REQUEST TO "+SRV_EN_MEF_EXPORT);
					gnCSWlURL = remainPath==null || remainPath.isEmpty()?geonetworkUrl+"/"+CSW_SERVER:geonetworkUrl+"/"+CSW_SERVER+remainPath;
					IOUtils.copy(req.getInputStream(), byteArray);
				}

				HTTPCallsUtils httpUtils = new HTTPCallsUtils();

				//PRIVATE LAYERS
				if(visibility.equals(VISIBILITY.PRV.name())){
					logger.info("Visibility: "+VISIBILITY.PRV+" getting private layers..");
					//VRE LAYERS
					if(mode.equals(MODE.VRE.name())){
						logger.info("Getting "+MODE.VRE+" layers..");
					//HARVESTED LAYERS
					}else{
						filters.put("isHarvested", "y");
						logger.info("Getting "+MODE.HARVEST+" layers, I added 'isHarvested = y' to the filters ["+filters+"]");
					}

					if(account.getUser()!=null){
						switch (version) {
						case DUE:
							boolean authorized = GNAuthentication.login(httpUtils, geonetworkUrl, account.getUser(), account.getPassword());
							logger.info("Authorized on GN2 "+geonetworkUrl +" ? "+authorized);
							break;

						default:
							httpUtils = new HTTPCallsUtils(account.getUser(), account.getPassword());
							logger.info("Authorized on GN3 via HTTCallsUtils...");
							break;
						}
						
					}else {
						logger.warn("I'm not able to perform authentication, the user read from config with "+Type.CKAN+" is null");
					}

				//PUBLIC LAYERS
				}else{
					logger.info("Visibility: "+VISIBILITY.PUB+" getting public layers..");
					//VRE LAYERS
					if(mode.equals(MODE.VRE.name())){
						logger.info("Getting "+MODE.VRE+" layers, the VRE account: "+account.getUser() +" will be used as owner user for filtering... Is it right?");
						filters.put("ownername", account.getUser());
					//HARVESTED LAYERS
					}else{
						//filters.put("isHarvested", "y");
						logger.info("Getting "+MODE.HARVEST+" layers, I'm applying the filters ["+filters+"]");
					}
				}

				logger.info("Sending CSW POST request to URL: "+gnCSWlURL);
				logger.debug("Content-Type: "+req.getContentType());

				//DEBUG
				//logger.debug("POST - BODY : "+byteArray.toString());
				InputStream in = httpUtils.post(gnCSWlURL, new ByteArrayInputStream(byteArray.toByteArray()), req.getContentType(), req.getParameterMap());

				if(in==null){
					logger.warn("Input stream returned is null, sending "+HttpServletResponse.SC_NOT_FOUND);
					throw ExceptionManager.notFoundException(req, "Input stream is null to the request "+gnCSWlURL+ " with body: "+byteArray.toString(), this.getClass(), helpURI);
				}

				try{

					ReusableInputStream reus = new ReusableInputStream(in);

					if(visibility.equals(VISIBILITY.PRV.name())){
						logger.info("Private VISIBILITY requested, retrieving public file identifiers to apply filtering..");
						FilterGetRecords filterGetRecords = new FilterGetRecords(byteArray.toString(), geonetworkUrl);
						if(filterGetRecords.getFoundPublicIds()!=null && filterGetRecords.getFoundPublicIds().size()>0){
							logger.info("I'm removing list of public IDs with "+filterGetRecords.getFoundPublicIds().size() +" item/s. Is it right?");
							in = GetResponseRecordFilter.overrideResponseIdsByListIds(reus, filterGetRecords.getFoundPublicIds(), REPLACED_A_PUBLIC_UUID_PLEASE_IGNORE);
						}
					}

					if(filters.size()>0){
						logger.info("Applying filtering on geonet:info... filter/s used: "+filters);
						Document doc = GetResponseRecordFilter.inputStreamToW3CDocument(reus);
						List<String> fileIdentifiers = GetResponseRecordFilter.getTextContentStringsForTagName(doc, "gmd:fileIdentifier");
						List<String> noMatchingFilter = new ArrayList<String>();
						for (String fileId : fileIdentifiers) {

							//CKECKING THE FILTERS
							for (String fkey : filters.keySet()) {
									String value = GetResponseRecordFilter.getMetadataValueByFileIdentifier(fileId, config.getGeoNetworkEndpoint(),config.getAdminAccount().getUser(), config.getAdminAccount().getPassword(), fkey);
									//String own = GetResponseRecordFilter.getMetaOwnerNameByFileIdentifier(fileId, config.getGeoNetworkEndpoint(),config.getAdminAccount().getUser(), config.getAdminAccount().getPassword());
									String fValue = filters.get(fkey);
									//Either the filter KEY doesn't matching any geonet:info (value==null)
									//or the value of filterKey in geonet:info doesn't matching the passed filterValue (value.compareTo(fValue)!=0)
									if(value==null || value.compareTo(fValue)!=0){
										logger.trace(fkey +" of File Identifier "+fileId+" not matching the filter: "+fkey+" with value: "+fValue+", adding it to list to remove file identifier and exit from loop..");
										noMatchingFilter.add(fileId);
										//WHEN I ADD THE FILE IDENTIFIER TO FILTERED ID, I CAN EXIT FROM CKECKING FILTERS LOOP
										break;
									}
							}
						}

						if(noMatchingFilter.size()>0){
							logger.info("Removing "+noMatchingFilter.size()+" layer/s that not macthing the filters: "+filters);
							//Document doc2 = GetResponseRecordFilter.inputStreamToW3CDocument(reus);
							in = GetResponseRecordFilter.overrideResponseIdsByListIds(reus, noMatchingFilter, REPLACED_UUID_BY_FILTER_PLEASE_IGNORE);
							//in = GetResponseRecordFilter.w3CDocumentToInputStream(doc);

						}else{
							logger.info("No replace on UUIDs was applied from filters: "+filters);
							in = reus;
						}

					}

					ReusableInputStream reusIs = new ReusableInputStream(in);

					//END DEBUG
					logger.info("Response return Content-Type: "+httpUtils.getLastContentType());
					return Response
					.ok(reusIs)
					//.header(ConstantsResolver.CONTENT_DISPOSITION,"attachment; filename = \""+fileName+"\"")
					.header(ConstantsResolver.CONTENT_TYPE, httpUtils.getLastContentType()).build();

				}catch(Exception e){
					logger.error("Error on copy the response to send to client: ", e);
					throw ExceptionManager.internalErrorException(req, "Error on copy the response!",  this.getClass(), helpURI);
				}finally{
					IOUtils.closeQuietly(in);
				}

			} catch (IllegalArgumentException e){
				logger.error("IllegalArgumentException:", e);
				throw ExceptionManager.badRequestException(req, "Illegal argument to carry out the request!",  this.getClass(), helpURI);

			} catch (Exception e) {
				logger.error("Exception:", e);
				String error = "Sorry, an error occurred on resolving geonetwork request with scope "+scope+". Please, contact support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error during perform POST operation to: "+gnCSWlURL+". Please, contact the support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
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

		logger.info("Trying to read the {} from cache for scope: {}",GeonetworkInstance.class.getSimpleName(), scope);
		GeonetworkInstance geoInstance;
		try {
			geoInstance = LoadingGeonetworkInstanceCache.get(scope);
		}catch (Exception e) {
			logger.error("An error occurred on reading GeonetworkInstance for scope "+scope, e);
			throw new Exception("Sorry, An error occurred on reading GeonetworkInstance for scope "+scope);
		}
		return geoInstance;

	}

}
