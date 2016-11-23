package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.datacatalogue.ckanutillibrary.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.traceprov.internal.org.apache.commons.io.IOUtils;
/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
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

	public final static String TEST_USER = "test.user";
	public final static String TEST_SCOPE = "/gcube/devsec/devVRE";
	public final static String TEST_MAIL = "test.user@test-com";
	public final static String TEST_SEC_TOKEN = "4620e6d0-2313-4f48-9d54-eb3efd01a810";

	/**
	 * Instanciate the ckan util library.
	 * Since it needs the scope, we need to check if it is null or not
	 * @param discoverScope if you want to the discover the utils library in this specified scope
	 * @return
	 */
	public DataCatalogue getCatalogue(String discoverScope){

		HttpSession httpSession = getThreadLocalRequest().getSession();
		ASLSession aslSession = getASLSession(httpSession);
		String currentScope =  aslSession.getScope();

		DataCatalogue instance = null;
		try{

			String scopeInWhichDiscover = (discoverScope != null && !discoverScope.isEmpty()) ? discoverScope : currentScope;
			logger.debug("Discovering ckan utils library into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);

		}catch(Exception e){
			logger.error("Unable to retrieve ckan utils", e);
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService#getCKanConnector(java.lang.String, java.lang.String)
	 */
	@Override
	public CkanConnectorAccessPoint getCKanConnector(String pathInfoParameter, String queryStringParameters, String currentPortletUrl) throws Exception {
		logger.info("getCKanConnector [pathInfo: "+pathInfoParameter + ", query: "+queryStringParameters+"], current url is " + currentPortletUrl);
		try{

			// call asl session otherwise ScopeProvider.instance.get(); returns null
			ASLSession aslSession = getASLSession(this.getThreadLocalRequest().getSession());

			// retrieve scope per current portlet url
			String scopePerCurrentUrl = ApplicationProfileScopePerUrlReader.getScopePerUrl(currentPortletUrl);

			// save it
			this.getThreadLocalRequest().getSession().setAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL, scopePerCurrentUrl);

			if(queryStringParameters!=null && Base64.isBase64(queryStringParameters.getBytes())){
				byte[] valueDecoded=Base64.decodeBase64(queryStringParameters.getBytes());
				queryStringParameters = new String(valueDecoded);
				logger.info("queryStringParameters detected like Base64 and decoded like: "+queryStringParameters);
			}

			CkanConnectorAccessPoint ckAP = getCkanConnectorAccessPoint(pathInfoParameter, queryStringParameters, scopePerCurrentUrl);
			SessionUtil.saveCkanAccessPoint(this.getThreadLocalRequest().getSession(), scopePerCurrentUrl, ckAP);
			logger.info("Builded URI to CKAN Connector: "+ckAP.buildURI());
			logger.debug("returning ckanConnectorUri: "+ckAP);
			return ckAP;
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
	private CkanConnectorAccessPoint getCkanConnectorAccessPoint(String pathInfoParameter, String queryStringParameters, String scopePerCurrentUrl) throws Exception {

		if(outsidePortal()){
			CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(getCatalogue(scopePerCurrentUrl).getCatalogueUrl(),"");
			return ckan;
		}

		//CKAN BASE URL
		ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
		GcoreEndpointReader ckanEndPoint = null;
		try{
			ckanEndPoint = SessionUtil.getCkanEndPoint(this.getThreadLocalRequest().getSession(), scopePerCurrentUrl);
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
			gcubeTokenValue = getGcubeSecurityToken(scopePerCurrentUrl);
		}else{
			logger.warn("******** Using TEST_USER security token!!!");
			gcubeTokenValue = TEST_SEC_TOKEN;
		}

		// set the token into the CkanConnectorAccessPoint
		ckan.addGubeToken(gcubeTokenValue);

		//ADDING LIST OF VRE TO WHICH USER BELONGS
		if(!SessionUtil.isIntoPortal()){
			return  ckan;
		}

		// retrieve the list of VREs to whom the user belongs
		List<String> listVres = UserUtil.getListVreForUser(session.getUserEmailAddress());
		ckan.addListOfVREs(listVres);

		return ckan;
	}

	/**
	 * Gets the gcube security token for the user in current session and for a given scope
	 *
	 * @return the gcube security token
	 * @throws Exception 
	 */
	protected String getGcubeSecurityToken(String scope) throws Exception {

		// ask it directly to the auth service
		ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
		String username = session.getUsername();
		String token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), scope);
		return token;

	}

	//	/**
	//	 * Temporary method to set the authorization token.
	//	 *
	//	 * @param session the new authorization token
	//	 * @throws Exception 
	//	 */
	//	private static void setAuthorizationToken(ASLSession session) throws Exception {
	//		String username = session.getUsername();
	//		String scope = session.getScope();
	//		ScopeProvider.instance.set(scope);
	//		logger.debug("calling service token on scope " + scope);
	//		List<String> userRoles = new ArrayList<String>();
	//		userRoles.add(DEFAULT_ROLE);
	//		session.setSecurityToken(null);
	//		String token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);
	//		logger.debug("received token: "+token);
	//		session.setSecurityToken(token);
	//		logger.info("Security token set in session for: "+username + " on " + scope);
	//	}

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
			return session;
		} 
		else 
			logger.trace("user found in session "+user);
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
			String username = session.getUsername();

			//  check if session expired
			if(username.equals(TEST_USER)){

				logger.warn("Session expired, returning " + toReturn);

			}else{

				// get the scope
				String scopePerCurrentUrl = (String)this.getThreadLocalRequest().getSession().getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);

				// get key per scope
				String keyPerScopeRole = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_HIGHEST_ROLE, scopePerCurrentUrl);
				String keyPerScopeOrganizations = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGANIZATIONS_PUBLISH_KEY, scopePerCurrentUrl);
				
				// check into session
				if(httpSession.getAttribute(keyPerScopeRole) != null){

					toReturn = (CkanRole)httpSession.getAttribute(keyPerScopeRole);
					logger.info("Found user role into session " + toReturn + " and it is going to be returned for user " + username);

				}else{

					try{
						GroupManager gm = new LiferayGroupManager();
						String groupName = gm.getGroup(gm.getGroupIdFromInfrastructureScope(scopePerCurrentUrl)).getGroupName();

						// we build up also a list that keeps track of the scopes (orgs) in which the user has at least role EDITOR
						List<OrganizationBean> orgsInWhichAtLeastEditorRole = new ArrayList<OrganizationBean>();
						toReturn = UserUtil.getHighestRole(scopePerCurrentUrl, username, groupName, this, orgsInWhichAtLeastEditorRole);
						
						// put role in session
						httpSession.setAttribute(keyPerScopeRole, toReturn);
						logger.info("Set role " + toReturn + " into session for user " + username);

						// if he is an admin/editor preload:
						// 1) organizations in which he can publish (the widget will find these info in session)
						if(toReturn.equals(CkanRole.ADMIN) || toReturn.equals(CkanRole.EDITOR)){
							httpSession.setAttribute(keyPerScopeOrganizations, orgsInWhichAtLeastEditorRole);
							logger.info("Set organizations in which he can publish to " + orgsInWhichAtLeastEditorRole + " into session for user " + username);
						}
					}catch(Exception e){
						logger.error("Error while retreving roles... returning " + toReturn, e);
					}
				}
			}
		}

		// return the role
		return toReturn;
	}

	@Override
	public String logoutURIFromCkan() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		String username = session.getUsername();
		
		// get the scope from session
		String scopePerCurrentUrl = (String)this.getThreadLocalRequest().getSession().getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);
		
		CkanConnectorAccessPoint ckanAP = SessionUtil.getCkanAccessPoint(this.getThreadLocalRequest().getSession(), scopePerCurrentUrl);
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
		// get the scope from session
		String scopePerCurrentUrl = (String)this.getThreadLocalRequest().getSession().getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);
		CkanConnectorAccessPoint ckanAP = SessionUtil.getCkanAccessPoint(this.getThreadLocalRequest().getSession(), scopePerCurrentUrl);
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
	public List<BeanUserInOrgRole> getCkanOrganizationsNamesAndUrlsForUser() {

		List<BeanUserInOrgRole> toReturn = new ArrayList<BeanUserInOrgRole>();

		if(!SessionUtil.isIntoPortal()){

			logger.warn("You are not into the portal");
			BeanUserInOrgRole org = new BeanUserInOrgRole("testVRE", "/organization/devvre", CkanRole.ADMIN);
			toReturn.add(org);

		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);
			String username = session.getUsername();

			// retrieve scope per current portlet url
			String scopePerCurrentUrl = (String)this.getThreadLocalRequest().getSession().getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);
			String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGS_USER_KEY, scopePerCurrentUrl);

			if(!username.equals(TEST_USER)){

				// check if the aslsession already has such information
				if(httpSession.getAttribute(keyPerScope) != null){
					toReturn = (List<BeanUserInOrgRole>) httpSession.getAttribute(keyPerScope);
					logger.debug("List of organizations was into the session " + toReturn);
				}else{
					logger.debug("Organizations list wasn't into session, retrieving them");
					DataCatalogue utils = getCatalogue(scopePerCurrentUrl);
					List<CkanOrganization> organizations = utils.getOrganizationsByUser(username);
					for (CkanOrganization ckanOrganization : organizations) {
						String role = utils.getRoleOfUserInOrganization(username, ckanOrganization.getName(), utils.getApiKeyFromUsername(username));
						BeanUserInOrgRole org = new BeanUserInOrgRole(ckanOrganization.getTitle(), "/organization/" + ckanOrganization.getName(), CkanRole.valueOf(role.toUpperCase()));
						toReturn.add(org);
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

			logger.warn("You are in DEV mode");
			return false;

		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);
			String username = session.getUsername();
			return username.equals(TEST_USER);

		}
	}
}
