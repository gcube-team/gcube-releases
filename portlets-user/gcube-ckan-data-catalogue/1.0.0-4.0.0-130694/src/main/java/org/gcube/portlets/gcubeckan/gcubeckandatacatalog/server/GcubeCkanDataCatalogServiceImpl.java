package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.CKanUtils;
import org.gcube.datacatalogue.ckanutillibrary.CKanUtilsImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.traceprov.internal.org.apache.commons.io.IOUtils;
/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
@SuppressWarnings("serial")
public class GcubeCkanDataCatalogServiceImpl extends RemoteServiceServlet implements GcubeCkanDataCatalogService {

	public static final String UTF_8 = "UTF-8";
	private static final String PORT_HTTP = ":80";
	private static final String PORT_HTTPS = ":443";
	private static final String HTTPS = "https";
	private static final String HTTP = "http";
	public static String CKANCONNECTORCONTEXT = "CkanConnectorContext";
	public static String CKANCONNECTORLOGOUT = "CkanConnectorLogout";
	public static final String USERNAME_ATTRIBUTE = ScopeHelper.USERNAME_ATTRIBUTE;
	private static Logger logger = LoggerFactory.getLogger(GcubeCkanDataCatalogServiceImpl.class);
	private final static String DEFAULT_ROLE = "OrganizationMember";

	public final static String TEST_USER = "test.user";
	public final static String TEST_SCOPE = "/gcube/devsec/devVRE";
	public final static String TEST_MAIL = "test.user@test-com";
	public final static String TEST_SEC_TOKEN = "4620e6d0-2313-4f48-9d54-eb3efd01a810";

	// CKAN KEYS (PLEASE NOTE THAT THESE INFO ARE SAVED INTO SESSION PER SCOPE)
	private static final String CKAN_ORGS_USER_KEY = "ckanOrgs"; // organizations to whom he belongs (retrieved by the instance in the current scope)
	private static final String CKAN_HIGHEST_ROLE = "ckanHighestRole"; // editor, member, admin (this information is retrieved according the scope)
	private static final String CKAN_ORGANIZATIONS_PUBLISH_KEY = "ckanOrganizationsPublish"; // here he can publish (admin role)

	/**
	 * Instanciate the ckan util library.
	 * Since it needs the scope, we need to check if it is null or not
	 * @param discoverScope if you want to the discover the utils library in this specified scope
	 * @return
	 */
	public CKanUtils getCkanUtilsObj(String discoverScope){

		HttpSession httpSession = getThreadLocalRequest().getSession();
		ASLSession aslSession = getASLSession(httpSession);
		String currentScope =  aslSession.getScope();
		String user = aslSession.getUsername();

		CKanUtils instance = null;
		try{
			if(user.equals(TEST_USER)){ 
				// session expired or, maybe, outside the portal
				logger.warn("User is "+TEST_USER +" are we out from portal?");
				logger.warn("I'm using root scope "+ CKanUtilsImpl.PRODUCTION_SCOPE_ROOT);
				instance = new CKanUtilsImpl(CKanUtilsImpl.PRODUCTION_SCOPE_ROOT);
			}else{
				String scopeInWhichDiscover = (discoverScope != null && !discoverScope.isEmpty()) ? discoverScope : currentScope;
				logger.debug("Discovering ckan utils library into scope " + scopeInWhichDiscover);
				instance = new CKanUtilsImpl(scopeInWhichDiscover);
			}
		}catch(Exception e){
			logger.error("Unable to retrieve ckan utils", e);
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService#getCKanConnector(java.lang.String, java.lang.String)
	 */
	@Override
	public CkanConnectorAccessPoint getCKanConnector(String pathInfoParameter, String queryStringParameters) throws Exception {
		logger.info("getCKanConnector [pathInfo: "+pathInfoParameter + ", query: "+queryStringParameters+"]");
		try{

			if(queryStringParameters!=null && Base64.isBase64(queryStringParameters.getBytes())){
				byte[] valueDecoded=Base64.decodeBase64(queryStringParameters.getBytes());
				queryStringParameters = new String(valueDecoded);
				logger.info("queryStringParameters detected like Base64 and decoded like: "+queryStringParameters);
			}

			CkanConnectorAccessPoint ckAP = getCkanConnectorAccessPoint(pathInfoParameter, queryStringParameters);
			ASLSession aslSession = getASLSession(this.getThreadLocalRequest().getSession());
			SessionUtil.saveCkanAccessPoint(this.getThreadLocalRequest().getSession(), aslSession.getScope(), ckAP);
			logger.info("Builded URI to CKAN Connector: "+ckAP.buildURI());
			logger.debug("returning ckanConnectorUri: "+ckAP);
			return ckAP;
			//		return "http://ckan-d-d4s.d4science.org";
		}catch(Exception e ){
			String message = "Sorry an error occurred during contacting gCube Ckan Data Catalogue";
			logger.error(message, e);
			throw new Exception(message);
		}
	}


	/**
	 * Gets the ckan connector access point.
	 *
	 * @param pathInfoParameter the path info parameter
	 * @param queryStringParameters the query string parameters
	 * @return the ckan connector access point
	 * @throws Exception
	 */
	private CkanConnectorAccessPoint getCkanConnectorAccessPoint(String pathInfoParameter, String queryStringParameters) throws Exception {

		if(outsidePortal()){
			CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(getCkanUtilsObj(null).getCatalogueUrl(),"");
			return ckan;
		}

		//CKAN BASE URL
		ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
		GcoreEndpointReader ckanEndPoint = null;
		try{
			ckanEndPoint = SessionUtil.getCkanEndPoint(this.getThreadLocalRequest().getSession(), session.getScope());
		}catch(Exception e){
			logger.error("CkanConnectorAccessPoint error: "+e.getMessage());
			throw new Exception("Sorry, an error occurred during contacting d4Science Data Catalogue, try again later");
		}
		String ckanConnectorBaseUrl = ckanEndPoint.getCkanResourceEntyName();
		ckanConnectorBaseUrl = ckanConnectorBaseUrl.startsWith(HTTP) && !ckanConnectorBaseUrl.startsWith(HTTPS)?ckanConnectorBaseUrl.replaceFirst(HTTP, HTTPS):ckanConnectorBaseUrl;
		ckanConnectorBaseUrl = ckanConnectorBaseUrl.contains(PORT_HTTP)?ckanConnectorBaseUrl.replace(PORT_HTTP, PORT_HTTPS):ckanConnectorBaseUrl;
		logger.debug("Base URL is: "+ckanConnectorBaseUrl);


		//GET CONTEXT
		String ckanContext = getServletContext().getInitParameter(CKANCONNECTORCONTEXT);
		logger.debug(CKANCONNECTORCONTEXT + " is: "+ckanContext);
		ckanContext= ckanContext!=null?ckanContext:"";

		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(ckanConnectorBaseUrl, ckanContext);
		pathInfoParameter = CkanConnectorAccessPoint.checkURLPathSeparator(pathInfoParameter, true, false);
		logger.debug("External Path Info parameter: "+pathInfoParameter);

		//ADD PATH INFO
		ckan.addPathInfo(pathInfoParameter);
		logger.debug("CKanConnector pathInfo: "+ckan.getPathInfoParameter());
		ckan.addQueryString(queryStringParameters);

		//GET TOKEN
		String gcubeTokenValue = null;
		if(SessionUtil.isIntoPortal()){
			gcubeTokenValue = getGcubeSecurityToken();
		}else{
			logger.warn("******** Using TEST_USER security token!!!");
			gcubeTokenValue = TEST_SEC_TOKEN;
		}

		ckan.addGubeToken(gcubeTokenValue);

		//ADDING LIST OF VRE TO WHICH USER BELONGS
		if(!SessionUtil.isIntoPortal()){
			return  ckan;
		}

		List<String> listVres = UserUtil.getListVreForUser(session.getUserEmailAddress());
		ckan.addListOfVREs(listVres);

		return ckan;
	}

	/**
	 * Gets the gcube security token.
	 *
	 * @return the gcube security token
	 */
	protected String getGcubeSecurityToken() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		logger.debug("Get security token return: "+session.getSecurityToken());

		if(session.getSecurityToken()==null || session.getSecurityToken().isEmpty()){
			logger.warn("Security token retured from ASL is null or empty, I'm setting security token...");
			setAuthorizationToken(session);
		}

		return session.getSecurityToken();
	}


	/**
	 * Temporary method to set the authorization token.
	 *
	 * @param session the new authorization token
	 */
	private static void setAuthorizationToken(ASLSession session) {
		String username = session.getUsername();
		String scope = session.getScope();
		ScopeProvider.instance.set(scope);
		logger.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<String>();
		userRoles.add(DEFAULT_ROLE);
		session.setSecurityToken(null);
		String token = authorizationService().build().generate(session.getUsername(), userRoles);
		logger.debug("received token: "+token);
		session.setSecurityToken(token);
		logger.info("Security token set in session for: "+username + " on " + scope);
	}

	/**
	 * Gets the ASL session.
	 *
	 * @param httpSession the http session
	 * @return the ASL session
	 */
	protected ASLSession getASLSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.warn("****** STARTING IN TEST MODE - NO USER FOUND *******");
			//for test only
			user = TEST_USER;
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(TEST_SCOPE);
			//session.setUserEmailAddress(TEST_MAIL);

			return session;
		} else logger.trace("user found in session "+user);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService#getMyRole()
	 */
	@Override
	public CkanRole getMyRole(){

		// base role as default value
		CkanRole toReturn = CkanRole.MEMBER;

		if(!SessionUtil.isIntoPortal()){
			logger.warn("OUT FROM PORTAL DETECTED RETURNING ROLE: "+CkanRole.ADMIN);
			toReturn = CkanRole.ADMIN;
		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);
			String currentScope = session.getScope();
			String username = session.getUsername();
			String groupName = session.getGroupName(); // e.g. devVRE

			// get key per scope
			String keyPerScope = UserUtil.concatenateSessionKeyScope(CKAN_HIGHEST_ROLE, session.getScope());

			//  check if session expired
			if(username.equals(TEST_USER)){

				logger.warn("Session expired, returning " + toReturn);

			}else{
				// check into session
				if(httpSession.getAttribute(keyPerScope) != null){

					toReturn = (CkanRole)httpSession.getAttribute(keyPerScope);
					logger.info("Found user role into session " + toReturn + " and it is going to be returned for user " + username);

				}else{

					// we build up also a list that keeps track of the scopes (orgs) in which the user has role ADMIN
					List<OrganizationBean> orgsInWhichAdminRole = new ArrayList<OrganizationBean>();
					toReturn = UserUtil.getHighestRole(currentScope, username, groupName, this, orgsInWhichAdminRole);
					httpSession.setAttribute(keyPerScope, toReturn);

					logger.info("Set role " + toReturn + " into session for user " + username);

					// if he is an admin preload:
					// 1) organizations in which he can publish (the widget will find these info in session)
					if(toReturn.equals(CkanRole.ADMIN)){
						httpSession.setAttribute(UserUtil.concatenateSessionKeyScope(CKAN_ORGANIZATIONS_PUBLISH_KEY, currentScope), orgsInWhichAdminRole);
						logger.info("Set organizations in which he can publish to " + orgsInWhichAdminRole + " into session for user " + username);
					}
				}
			}
		}

		// return the role
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService#getUser()
	 */
	@Override
	public String getUser() {

		HttpSession httpSession = this.getThreadLocalRequest().getSession();

		logger.debug("User in session is " + getASLSession(httpSession).getUsername());
		return getASLSession(httpSession).getUsername();

	}

	@Override
	public String logoutURIFromCkan() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		String username = session.getUsername();
		CkanConnectorAccessPoint ckanAP = SessionUtil.getCkanAccessPoint(this.getThreadLocalRequest().getSession(), session.getScope());
		//		String token = getGcubeSecurityToken();
		logger.info("Logout from CKAN for: "+username +" by token: "+ckanAP.getGcubeTokenValue());

		String ckanConnectorLogut = getServletContext().getInitParameter(CKANCONNECTORLOGOUT);
		logger.debug(CKANCONNECTORLOGOUT + " is: "+ckanConnectorLogut);

		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(ckanAP.getBaseUrl(), ckanConnectorLogut);
		ckan.addGubeToken(ckanAP.getGcubeTokenValue());
		return ckan.buildURI();
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService#logoutFromCkan()
	 */
	@Override
	public void logoutFromCkan() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		String username = session.getUsername();
		CkanConnectorAccessPoint ckanAP = SessionUtil.getCkanAccessPoint(this.getThreadLocalRequest().getSession(), session.getScope());
		//		String token = getGcubeSecurityToken();
		logger.info("Logout from CKAN for: "+username +" by token: "+ckanAP.getGcubeTokenValue());

		String ckanConnectorLogut = getServletContext().getInitParameter(CKANCONNECTORLOGOUT);
		logger.debug(CKANCONNECTORLOGOUT + " is: "+ckanConnectorLogut);

		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(ckanAP.getBaseUrl(), ckanConnectorLogut);
		ckan.addPathInfo(ckanConnectorLogut);
		ckan.addGubeToken(ckanAP.getGcubeTokenValue());

		URL url;
		try {
			String deleteURI = ckan.buildURI();
			logger.debug("Perfoming HTTP delete to URI: "+deleteURI);
			url = new URL(deleteURI);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
			httpCon.setRequestMethod("DELETE");
			httpCon.connect();

			HttpServletResponse response = this.getThreadLocalResponse();

			if(httpCon.getResponseCode()==HttpStatus.SC_OK){
				response.setContentLength(httpCon.getContentLength());
				Map<String, List<String>> map = httpCon.getHeaderFields();
				for (String  key : map.keySet()) {
					String hf = httpCon.getHeaderField(key);
					logger.trace("key: "+key +", value: "+hf);
					if(key==null){
						logger.trace("skyp key: "+key +", value: "+hf);
					}else
						response.setHeader(key,hf);
				}

				response.setContentLength(httpCon.getContentLength());
				String encoding = httpCon.getContentEncoding();
				encoding = encoding == null ? UTF_8 : encoding;
				response.setCharacterEncoding(encoding);
				response.setStatus(HttpStatus.SC_OK);

				InputStream in = httpCon.getInputStream();
				ServletOutputStream out = response.getOutputStream();
				IOUtils.copy(in, out);
				logger.info("Logout Completed, response code: "+HttpStatus.SC_OK);

			}else{
				logger.warn("An error occurred during perfoming CKAN logout, Response status is: "+httpCon.getResponseCode());
			}
		}
		catch (IOException e) {
			logger.error("An error occured during performing Logout from CKAN for: "+username +" by token: "+ckanAP.getGcubeTokenValue(), e);
		}
	}

	@Override
	public Map<String, String> getCkanOrganizationsNamesAndUrlsForUser() {

		// TODO generate this list dynamically taking into account the current scope

		Map<String, String> toReturn = new HashMap<>();

		if(!SessionUtil.isIntoPortal()){

			logger.warn("You are not into the portal");
			toReturn.put("testVRE", "/organization/devvre");

		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);
			String username = session.getUsername();

			String keyPerScope = UserUtil.concatenateSessionKeyScope(CKAN_ORGS_USER_KEY, session.getScope());

			if(!username.equals(TEST_USER)){

				// check if the aslsession already has such information
				if(httpSession.getAttribute(keyPerScope) != null){
					toReturn = (Map<String, String>) httpSession.getAttribute(keyPerScope);
					logger.debug("List of organizations was into the session " + toReturn);
				}else{
					logger.debug("Organizations list wasn't into session, retrieving them");
					List<CkanOrganization> organizations = getCkanUtilsObj(null).getOrganizationsByUser(username);
					for (CkanOrganization ckanOrganization : organizations) {
						toReturn.put(ckanOrganization.getTitle(), "/organization/" + ckanOrganization.getName());
					}
					logger.debug("List of organizations to return for user " + username + " is " + toReturn);
					httpSession.setAttribute(keyPerScope, toReturn);
				}
			}
		}

		return toReturn;
	}

	@Override
	public boolean outsidePortal() {

		if(!SessionUtil.isIntoPortal()){

			logger.warn("You are not into the portal");
			return false;

		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);
			String username = session.getUsername();

			if(username.equals(TEST_USER))
				return true;

			return false;
		}
	}
}
