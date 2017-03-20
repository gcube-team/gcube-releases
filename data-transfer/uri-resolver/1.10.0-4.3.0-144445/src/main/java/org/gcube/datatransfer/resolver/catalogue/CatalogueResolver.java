/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.ResourceCatalogueCodes;
import org.gcube.datatransfer.resolver.UriResolverRewriteFilter;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileNotFoundException;
import org.gcube.datatransfer.resolver.catalogue.resource.ApplicationProfileReaderForCatalogueResolver;
import org.gcube.datatransfer.resolver.catalogue.resource.CkanPorltetApplicationProfile;
import org.gcube.datatransfer.resolver.catalogue.resource.UpdateApplicationProfileCatalogueResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GisResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 7, 2016
 */
public class CatalogueResolver extends HttpServlet{

	private static final long serialVersionUID = -8273405286016095823L;

	private static final String TEXT_PALIN_CHARSET_UTF_8 = "text/plain;charset=UTF-8";
	public static final String UTF_8 = "UTF-8";

	public static final String ENC_CATALOGUE_LINK_PARAM = UriResolverRewriteFilter.PARAMETER_ENC_CATALOGUE_LINK;

	public static final String DIRECT_CATALOGUE_LINK_PARAM = UriResolverRewriteFilter.PARAMETER_DIRECT_CATALOGUE_LINK;

	private static final String PATH_SEPARATOR = "/";

	public static final String PARAMETER_PATH = "path";

	public static final String ENV_SCOPE = "SCOPE"; //Environment Variable

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(CatalogueResolver.class);

	private ApplicationProfileReaderForCatalogueResolver appPrCatResolver;


	/** The scope to enc decr. */
	private String scopeToEncDecr = null;


	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		initScopeFromEnv();
	}


	/**
	 * Inits the scope from env.
	 *
	 * @return the string
	 * @throws ServletException the servlet exception
	 */
	private String initScopeFromEnv() throws ServletException{

		if(scopeToEncDecr!=null)
			return scopeToEncDecr;

		scopeToEncDecr = System.getenv(ENV_SCOPE);
		logger.info("Reading Environment Variable "+ENV_SCOPE+" to get the scope for encrypt/descrypt; I read scope: "+scopeToEncDecr);
		if(scopeToEncDecr == null || scopeToEncDecr.isEmpty())
			throw new ServletException(CatalogueResolver.class.getName() +" cannot work without set the Environment Variable: "+ENV_SCOPE);

		appPrCatResolver = new ApplicationProfileReaderForCatalogueResolver(scopeToEncDecr, true);
		logger.info("Resource read for CatalogueResolver: "+appPrCatResolver);
		return scopeToEncDecr;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		//ScopeProvider.instance.set(scope);

		String clearCatalogueQueryLink = req.getParameter(DIRECT_CATALOGUE_LINK_PARAM);
		String encryptedCatalogueQueryLink = req.getParameter(ENC_CATALOGUE_LINK_PARAM);

		boolean foundClearLink = false;
		if(clearCatalogueQueryLink==null || clearCatalogueQueryLink.isEmpty()){
			logger.info("Link is not a direct link to catalogue");
		}else
			foundClearLink = true;

		String queryStringToCat = "";
		if(foundClearLink){
			logger.info("Trying to resolve clear link to catalogue using query link: "+clearCatalogueQueryLink);
			queryStringToCat = clearCatalogueQueryLink;
		}else{
			logger.info("Trying to resolve encripted link to catalogue using query link: "+encryptedCatalogueQueryLink);
			if(encryptedCatalogueQueryLink==null || encryptedCatalogueQueryLink.isEmpty()){
				logger.error("Data Catalogue Link is malformed, set "+ENC_CATALOGUE_LINK_PARAM+" parameter");
				sendError(resp, HttpStatus.SC_BAD_REQUEST, "Data Catalogue Link is malformed, either you must set "+ENC_CATALOGUE_LINK_PARAM+" parameter or "+DIRECT_CATALOGUE_LINK_PARAM+ " parameter");
				return;
			}
			String base64DecodedId  = "";
			try {

				base64DecodedId = base64DecodeString(encryptedCatalogueQueryLink);
				logger.info("Base 64 decoded Data Catalogue Link: "+base64DecodedId +", now decrypting...");

				if(scopeToEncDecr==null)
					initScopeFromEnv();

				ScopeProvider.instance.set(scopeToEncDecr);
				queryStringToCat = StringEncrypter.getEncrypter().decrypt(base64DecodedId);
				logger.info("Decrypted Data Catalogue Link: "+queryStringToCat);

			}catch (Exception e) {
				logger.error("An error occurred during decrypting data catalogue link: "+base64DecodedId+", using the scope: "+scopeToEncDecr, e);
				sendError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "The system cannot decrypt the Catalogue Link");
				return;
			}
		}

		CatalogueEntityRequest cer = new CatalogueEntityRequest();
		for (CatalogueRequestParameter parameter : CatalogueRequestParameter.values()) {
			String value = getValueOfParameter(parameter.getKey(), queryStringToCat);
			cer.addParameterToRequest(parameter.getKey(), value);
		}

		if(cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_CONTEXT.getKey()).compareToIgnoreCase("product")==0){
			logger.debug("Read "+CatalogueRequestParameter.ENTITY_CONTEXT.getKey() + " value: 'product' replacing with 'dataset'");
			cer.addParameterToRequest(CatalogueRequestParameter.ENTITY_CONTEXT.getKey(), "dataset");
		}

		logger.debug("Read parameters: "+cer.toString());

		String scope = cer.getValueOfParameter(CatalogueRequestParameter.GCUBE_SCOPE.getKey());
		if(scope==null || scope.isEmpty()){
			logger.error("An error occurred during resolving data catalogue link: the scope to search CKan Portlet is null or empty");
			sendError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "The system cannot resolve the Catalogue Link, the scope is null or empty");
			return;
		}

		/**
		 * I'm replacing VRE_NAME passed into parameter GCUBE_SCOPE with the scope read from ApplicationProfileReaderForCatalogueResolver#RESOURCE_NAME
		 * that is the full scope
		 */
		if(foundClearLink){
			if(appPrCatResolver==null)
				appPrCatResolver = new ApplicationProfileReaderForCatalogueResolver(scopeToEncDecr, true);

			String fullScope = appPrCatResolver.getHashVreNameScope().get(scope);

			if(fullScope==null){
				logger.debug("FullScope is null for VRE_NAME: "+scope+" into Application Profile: "+ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME+", reading profile again");
				appPrCatResolver = new ApplicationProfileReaderForCatalogueResolver(scopeToEncDecr, true);
				fullScope = appPrCatResolver.getHashVreNameScope().get(scope);
			}

			logger.debug("Read fullScope: "+fullScope + " for VRE_NAME: "+scope +" into Application Profile "+ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME);
			cer.addParameterToRequest(CatalogueRequestParameter.GCUBE_SCOPE.getKey(), fullScope);
			scope = fullScope;
		}

		String ckanPorltetUrl = "";
		try{
			logger.info("Using scope "+scope+ " to search Ckan Portlet URL from IS");
			ScopeProvider.instance.set(scope);
			ckanPorltetUrl = CkanPorltetApplicationProfile.getPortletUrlFromInfrastrucure();
			if(ckanPorltetUrl == null || ckanPorltetUrl.isEmpty()){
				sendError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred during discovery Data Catalogue URL, try again later");
				return;
			}
		}catch(Exception e){
			logger.error("An error occurred during discovery Data Catalogue URL: ",e);
			sendError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred during discovery Data Catalogue URL, try again later");
			return;
		}

		//UrlEncoderUtil.encodeQuery(cer.getParameters());
		String buildPath = PARAMETER_PATH +"=";
		buildPath+= PATH_SEPARATOR+cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_CONTEXT.getKey()) + PATH_SEPARATOR;
		buildPath+=cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_NAME.getKey());

		String finalUrl = ckanPorltetUrl+"?"+buildPath;
		logger.info("Builded final URL: "+finalUrl);
	    resp.sendRedirect(resp.encodeRedirectURL(finalUrl));
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final CatalogueEntityRequest cer = new CatalogueEntityRequest();
		String originalScope = null;
		try{
			String jsonRequest = IOUtils.toString(req.getInputStream());
			logger.debug("Read json request: "+jsonRequest);
			JSONObject inputJson = new JSONObject(jsonRequest);

			for (CatalogueRequestParameter parameter : CatalogueRequestParameter.values()) {

			try{

				switch (parameter) {
				case QUERY_STRING:
					//TODO must be implemented
					JSONArray queryString = inputJson.getJSONArray(parameter.getKey());
					break;
				default:
					String value = inputJson.getString(parameter.getKey());
					logger.debug("Read value: "+value+", for parameter: "+parameter.getKey());
					cer.addParameterToRequest(parameter.getKey(), value);
					break;
				}

			}catch(Exception e){
				String error = "";
				try {

					if(parameter.isMandatory()){
						error = parameter.getKey() +" not found";
						sendError(resp, HttpStatus.SC_BAD_REQUEST, error);
						return;
					}
					else
						logger.debug("Not Mandatory parameter: "+parameter.getKey()+", not found, continuing...");

				}catch (IOException e1) {
					//silent
				}
			}

			}
		}catch(JSONException e){
			try {
				logger.error("Json passed is malformed: ", e);
				sendError(resp, HttpStatus.SC_BAD_REQUEST, "Json passed is malformed");
			}
			catch (IOException e1) {
				//silent
			}
			return;
		}

		try{
			//CHECK IF INPUT SCOPE IS VALID
			String scope = cer.getValueOfParameter(CatalogueRequestParameter.GCUBE_SCOPE.getKey());
			if(!scope.startsWith("/")){
				logger.info("Scope not start with char '/' adding it");
				scope+="/"+scope;
				cer.addParameterToRequest(CatalogueRequestParameter.GCUBE_SCOPE.getKey(), scope);
			}

			String buildLink = getServerURL(req);
			//buildLink += req.getRequestURI(); //AVOIDNG TO ADD THE SERVLET NAME: 'catalogue', THERE IS THE URL REWRITE USED FOR 'uri-resolver'

			String clearURL = cer.getValueOfParameter(CatalogueRequestParameter.CLEAR_URL.getKey());

			boolean bClearURL = false;
			try{
				bClearURL = Boolean.parseBoolean(clearURL);
			}catch(Exception e){
				//silent
			}

			resp.setContentType(TEXT_PALIN_CHARSET_UTF_8);
			resp.setCharacterEncoding(UTF_8);

			logger.info("Clear URL is: "+bClearURL);
			if(bClearURL){

				final String vreName = scope.substring(scope.lastIndexOf("/")+1, scope.length());
				//buildLink+=PATH_SEPARATOR+vreName+PATH_SEPARATOR+cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_CONTEXT.getKey())+PATH_SEPARATOR+cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_NAME.getKey());
				String econtext = cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_CONTEXT.getKey());
				ResourceCatalogueCodes rc = ResourceCatalogueCodes.valueOfCodeValue(econtext);
				if(rc==null){
					logger.error("Entity context is null/malformed");
					sendError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error during generating Data Catalogue Link, the entity context passed is not recognized. Is it malformed?");
					return;
				}
				buildLink += PATH_SEPARATOR+rc.getId()+PATH_SEPARATOR+vreName+PATH_SEPARATOR+cer.getValueOfParameter(CatalogueRequestParameter.ENTITY_NAME.getKey());
				logger.info("Writing Decoded Catalogue Link: "+buildLink);

				//IT'S GOING TO UPDATE THE GENERIC RESOURCE IF IS NEEDED
				new Thread(){
					public void run() {
						try {
							String fullScope = cer.getValueOfParameter(CatalogueRequestParameter.GCUBE_SCOPE.getKey());
							boolean endPointUpdated = UpdateApplicationProfileCatalogueResolver.validateEndPoint(scopeToEncDecr, vreName, fullScope);

							if(endPointUpdated)
								appPrCatResolver = new ApplicationProfileReaderForCatalogueResolver(scopeToEncDecr, true);

						}
						catch (ApplicationProfileNotFoundException e) {
							logger.error("Error during validating Application Profile", e);
						}
					};
				}.start();

			}else{
				//ADDING THE SERVLET NAME
				buildLink += req.getRequestURI();

				cer.removeParameterToRequest(CatalogueRequestParameter.CLEAR_URL.getKey());
				originalScope = ScopeProvider.instance.get();
				logger.info("Using scope "+scopeToEncDecr+ " from env to get encrypt key");
				ScopeProvider.instance.set(scopeToEncDecr);
				//String query = UrlEncoderUtil.encodeQuery(cer.getParameters());
				String query = "";
				for (String key : cer.getParameters().keySet()) {
					query+=key+"="+ cer.getParameters().get(key) +"&";
				}
				query = UrlEncoderUtil.removeLastChar(query);
				logger.info("Builded query string: "+query);
				String encriptedQuery = StringEncrypter.getEncrypter().encrypt(query);
				logger.info("Encrypted query: "+encriptedQuery);
				String encodedQuery = base64EncodeStringURLSafe(encriptedQuery);
				logger.info("Catalogue Query Link: "+encodedQuery);
				buildLink+=PATH_SEPARATOR+encodedQuery;
				logger.info("Writing Encoded Catalogue Link: "+buildLink);
			}

			resp.getWriter().write(buildLink);

		}catch(Exception e){
			try {
				logger.error("An internal error is occurred: ", e);
				sendError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred during generating Data Catalogue Link, try again later");
				return;
			}
			catch (IOException e1) {
				//silent
			}
		}finally{
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider setted to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
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
	protected static void sendError(HttpServletResponse response, int status, String message) throws IOException{

		logger.error("error message: "+message);
		logger.info("writing response...");

		if(response==null)
			return;

		response.setStatus(status);
		StringReader sr = new StringReader(message);
		IOUtils.copy(sr, response.getOutputStream());
		logger.info("response writed");
		response.flushBuffer();
	}


	/**
	 * Gets the server url.
	 *
	 * @param req the req
	 * @return the server url
	 */
	public String getServerURL(HttpServletRequest req) {

	    String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80
	    //String contextPath = req.getContextPath();   // /mywebapp

	    // Reconstruct original requesting URL
	    StringBuffer url =  new StringBuffer();
	    url.append(scheme).append("://").append(serverName);

	    if (serverPort != 80 && serverPort != 443)
	        url.append(":").append(serverPort);

//	    if(contextPath!=null)
//	        url.append(":").append(contextPath);

	    String uToS = url.toString();
	    logger.debug("returning servlet context URL: "+uToS);
	    return uToS;
	}


	/**
	 * Gets the value of parameter in the passed query string, null otherwise
	 *
	 * @param parameter the parameter
	 * @param httpQueryString the http query string
	 * @return the value of parameter if exists, null otherwise
	 */
	public static String getValueOfParameter(String parameter, String httpQueryString) {
//		logger.trace("finding: "+wmsParam +" into "+url);
		int index = httpQueryString.toLowerCase().indexOf(parameter.toLowerCase()+"="); //ADDING CHAR "=" IN TAIL TO BE SECURE  IT IS A PARAMETER
//		logger.trace("start index of "+wmsParam+ " is: "+index);
		String value = "";
		if(index > -1){

			int start = index + parameter.length()+1; //add +1 for char '='
			String sub = httpQueryString.substring(start, httpQueryString.length());
			int indexOfSeparator = sub.indexOf("&");
			int end = indexOfSeparator!=-1?indexOfSeparator:sub.length();
			value = sub.substring(0, end);
		}else
			return null;

//		logger.trace("return value: "+value);
		return value;
	}

	/**
	 * Base64 encode string url safe.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String base64EncodeStringURLSafe(String s) {

		try {
			return Base64.encodeBase64URLSafeString(s.getBytes(UTF_8));
		}
		catch (UnsupportedEncodingException e) {
			logger.error("Failed to decode the String", e);
			logger.error("Returning input string: " + s);
			return s;
		}
	}

	/**
	 * Base64 decode string.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String base64DecodeString(String s) {

		try {
			return new String(Base64.decodeBase64(s.getBytes(UTF_8)));
		}
		catch (UnsupportedEncodingException e) {
			logger.error("Failed to decode the String", e);
			logger.error("Returning input string: " + s);
			return s;
		}
	}

	public static void main(String[] args) {

		try{
			String scope = "d4science.research";
			ApplicationProfileReaderForCatalogueResolver appPrCatResolver = new ApplicationProfileReaderForCatalogueResolver(scope, true);
			logger.info("Reosurce for Catalogue Resolver: "+appPrCatResolver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
