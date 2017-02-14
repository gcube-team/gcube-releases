package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgGroupRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.GroupBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
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
	private static final String GRSF_ADMIN_ROLE = "GRSF-Admin";
	//private static Logger logger = LoggerFactory.getLogger(GcubeCkanDataCatalogServiceImpl.class);
	private static final Log logger = LogFactoryUtil.getLog(GcubeCkanDataCatalogServiceImpl.class);

	/**
	 * Instanciate the ckan util library.
	 * Since it needs the scope, we need to check if it is null or not
	 * @param discoverScope if you want to the discover the utils library in this specified scope
	 * @return
	 */
	public DataCatalogue getCatalogue(String discoverScope){
		String currentScope = SessionUtil.getCurrentContext(getThreadLocalRequest(), false);
		DataCatalogue instance = null;
		try{
			String scopeInWhichDiscover = discoverScope != null && !discoverScope.isEmpty() ? discoverScope : currentScope;
			logger.debug("Discovering ckan utils library into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);
		}catch(Exception e){
			logger.error("Unable to retrieve ckan utils. Error was " + e.toString());
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

			// just get the current scope and set it into ScopeProvider...
			SessionUtil.getCurrentContext(getThreadLocalRequest(), true);

			// retrieve scope per current portlet url
			String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());

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
			ckan.setOutsideFromPortal(true);
			return ckan;
		}

		//CKAN BASE URL
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
		gcubeTokenValue = getGcubeSecurityToken(scopePerCurrentUrl);

		// set the token into the CkanConnectorAccessPoint
		ckan.addGubeToken(gcubeTokenValue);

		//ADDING LIST OF VRE TO WHICH USER BELONGS
		if(!SessionUtil.isIntoPortal()){
			return  ckan;
		}

		// retrieve the list of VREs to whom the user belongs (actually one vre at most is sent)
		Map<String, String> roleForVre = UserUtil.getVreRoleForUser(
				SessionUtil.getCurrentUser(getThreadLocalRequest()).getEmail(), 
				scopePerCurrentUrl, 
				getCatalogue(scopePerCurrentUrl));
		ckan.addListOfVREs(roleForVre);

		return ckan;
	}

	/**
	 * Gets the gcube security token for the user in current session and for a given scope
	 *
	 * @return the gcube security token
	 * @throws Exception
	 */
	protected String getGcubeSecurityToken(String context) throws Exception {

		// ask it directly to the auth service
		String username = SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();
		String token = null;
		try{
			logger.debug("Checking if token for user " + username + " in context " + context + " already exists...");
			token = authorizationService().resolveTokenByUserAndContext(username, context);
			logger.debug("It exists!");
		}catch(ObjectNotFound e){
			logger.info("Creating token for user " + username + " and context " + context);
			token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
			logger.debug("received token: "+ token.substring(0, 5) + "***********************");
		}
		return token;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GcubeCkanDataCatalogService#getMyRole()
	 */
	@Override
	public RolesCkanGroupOrOrg getMyRole(){

		// base role as default value
		RolesCkanGroupOrOrg toReturn = RolesCkanGroupOrOrg.MEMBER;

		if(!SessionUtil.isIntoPortal()){
			logger.warn("OUT FROM PORTAL DETECTED RETURNING ROLE: "+RolesCkanGroupOrOrg.ADMIN);
			toReturn = RolesCkanGroupOrOrg.ADMIN;
		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			String username =  SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();

			// get the scope
			String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());

			// get key per scope
			String keyPerScopeRole = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_HIGHEST_ROLE, scopePerCurrentUrl);
			String keyPerScopeOrganizations = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGANIZATIONS_PUBLISH_KEY, scopePerCurrentUrl);
			String keyPerScopeGroups = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_GROUPS_MEMBER, scopePerCurrentUrl);

			// check into session
			if(httpSession.getAttribute(keyPerScopeRole) != null){

				toReturn = (RolesCkanGroupOrOrg)httpSession.getAttribute(keyPerScopeRole);
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
					// 2) the list of groups
					if(!toReturn.equals(RolesCkanGroupOrOrg.MEMBER)){
						httpSession.setAttribute(keyPerScopeOrganizations, orgsInWhichAtLeastEditorRole);
						httpSession.setAttribute(keyPerScopeGroups, fetchUserGroups(scopePerCurrentUrl, username));
					}
				}catch(Exception e){
					logger.error("Error while retreving roles... returning " + toReturn, e);
				}
			}
		}

		logger.debug("Returning highest role " + toReturn);

		// return the role
		return toReturn;
	}

	/**
	 * Fetch the list of ckan groups for which the user is member
	 * @param context 
	 * @return
	 */
	private List<GroupBean> fetchUserGroups(String context, String username) {

		List<GroupBean> toReturn = null;
		logger.info("Preloading user's groups");

		try{
			DataCatalogue catalogue = getCatalogue(context);
			String apiKey = catalogue.getApiKeyFromUsername(username);
			toReturn = new ArrayList<GroupBean>();
			Map<String, Map<CkanGroup, RolesCkanGroupOrOrg>> mapRoleGroup = catalogue.getUserRoleByGroup(username, apiKey);
			Set<Entry<String, Map<CkanGroup, RolesCkanGroupOrOrg>>> set = mapRoleGroup.entrySet();
			for (Entry<String, Map<CkanGroup, RolesCkanGroupOrOrg>> entry : set) {
				Set<Entry<CkanGroup, RolesCkanGroupOrOrg>> subSet = entry.getValue().entrySet();
				for (Entry<CkanGroup, RolesCkanGroupOrOrg> subEntry : subSet) {
					toReturn.add(new GroupBean(subEntry.getKey().getTitle(), subEntry.getKey().getName()));	
				}
			}

			logger.debug("List of groups to return is " + toReturn);

		}catch(Exception e){
			logger.error("Failed to preload user's groups");
		}

		return toReturn;
	}

	@Override
	public String logoutURIFromCkan() {
		String username = SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();

		// get the scope from session
		String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());

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
	public String logoutFromCkanURL() {

		// check if the portlet is accessed from outside the portal
		if(outsidePortal())
			return null;

		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();

		if(username == null){
			logger.warn("User is null");
			return null;
		}


		logger.info("Getting CKAN Logout URL...");
		String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());
		CkanConnectorAccessPoint ckanAP = SessionUtil.getCkanAccessPoint(httpSession, scopePerCurrentUrl);
		//		String token = getGcubeSecurityToken();
		logger.info("Logout from CKAN for: "+username );
		logger.info(" by token: "+ckanAP.getGcubeTokenValue() +", the scope is: "+scopePerCurrentUrl);

		String ckanConnectorLogout = getServletContext().getInitParameter(CKANCONNECTORLOGOUT);
		logger.debug(GcubeCkanDataCatalogServiceImpl.CKANCONNECTORLOGOUT + " is: "+ckanConnectorLogout);

		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(ckanAP.getBaseUrl(), ckanConnectorLogout);
		ckan.addGubeToken(ckanAP.getGcubeTokenValue());

		String deleteURI = ckan.buildURI();
		logger.debug(GcubeCkanDataCatalogServiceImpl.CKANCONNECTORLOGOUT + " returning: "+deleteURI);
		return deleteURI;


		/*HttpSession httpSession = this.getThreadLocalRequest().getSession();
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
		}*/
	}

	@Override
	public List<BeanUserInOrgGroupRole> getCkanOrganizationsNamesAndUrlsForUser() {

		List<BeanUserInOrgGroupRole> toReturn = new ArrayList<BeanUserInOrgGroupRole>();

		if(!SessionUtil.isIntoPortal()){

			logger.warn("You are not into the portal");
			BeanUserInOrgGroupRole org = new BeanUserInOrgGroupRole("testVRE", "/organization/devvre", RolesCkanGroupOrOrg.ADMIN);
			toReturn.add(org);

		}else{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			String username = SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();

			// retrieve scope per current portlet url
			String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());
			String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGS_USER_KEY, scopePerCurrentUrl);

			// check if the aslsession already has such information
			if(httpSession.getAttribute(keyPerScope) != null){
				toReturn = (List<BeanUserInOrgGroupRole>) httpSession.getAttribute(keyPerScope);
				logger.debug("List of organizations was into the session " + toReturn);
			}else{
				logger.debug("Organizations list wasn't into session, retrieving them");
				DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
				String apiKey = catalogue.getApiKeyFromUsername(username);
				Map<String, Map<CkanOrganization, RolesCkanGroupOrOrg>> mapRoleGroup = catalogue.getUserRoleByOrganization(username, apiKey);
				Set<Entry<String, Map<CkanOrganization, RolesCkanGroupOrOrg>>> set = mapRoleGroup.entrySet();
				for (Entry<String, Map<CkanOrganization, RolesCkanGroupOrOrg>> entry : set) {
					Set<Entry<CkanOrganization, RolesCkanGroupOrOrg>> subSet = entry.getValue().entrySet();
					for (Entry<CkanOrganization, RolesCkanGroupOrOrg> subEntry : subSet) {
						BeanUserInOrgGroupRole org = new BeanUserInOrgGroupRole(subEntry.getKey().getTitle(), "/organization/" + subEntry.getKey().getName(), subEntry.getValue());
						toReturn.add(org);
					}
				}
				logger.debug("List of organizations to return for user " + username + " is " + toReturn);
				httpSession.setAttribute(keyPerScope, toReturn);
			}
		}
		return toReturn;
	}


	@Override
	public List<BeanUserInOrgGroupRole> getCkanGroupsNamesAndUrlsForUser() {
		List<BeanUserInOrgGroupRole> toReturn = new ArrayList<BeanUserInOrgGroupRole>();

		if(!SessionUtil.isIntoPortal()){

			logger.warn("You are not into the portal");
			BeanUserInOrgGroupRole org = new BeanUserInOrgGroupRole("testGroup", "/group/testgroup", RolesCkanGroupOrOrg.MEMBER);
			toReturn.add(org);

		}else{

			HttpSession httpSession = getThreadLocalRequest().getSession();
			String username = SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();

			// retrieve scope per current portlet url
			String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());
			String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_GROUPS_USER_KEY, scopePerCurrentUrl);

			// check if the aslsession already has such information
			if(httpSession.getAttribute(keyPerScope) != null){
				toReturn = (List<BeanUserInOrgGroupRole>) httpSession.getAttribute(keyPerScope);
				logger.debug("List of groups was into the session " + toReturn);
			}else{
				logger.debug("Groups list wasn't into session, retrieving them");
				DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
				String apiKey = catalogue.getApiKeyFromUsername(username);

				Map<String, Map<CkanGroup, RolesCkanGroupOrOrg>> mapRoleGroup = catalogue.getUserRoleByGroup(username, apiKey);
				Set<Entry<String, Map<CkanGroup, RolesCkanGroupOrOrg>>> set = mapRoleGroup.entrySet();
				for (Entry<String, Map<CkanGroup, RolesCkanGroupOrOrg>> entry : set) {
					Set<Entry<CkanGroup, RolesCkanGroupOrOrg>> subSet = entry.getValue().entrySet();
					for (Entry<CkanGroup, RolesCkanGroupOrOrg> subEntry : subSet) {
						BeanUserInOrgGroupRole org = new BeanUserInOrgGroupRole(subEntry.getKey().getTitle(), "/group/" + subEntry.getKey().getName(), subEntry.getValue());
						toReturn.add(org);
					}
				}
				logger.debug("List of groups to return for user " + username + " is " + toReturn);
				httpSession.setAttribute(keyPerScope, toReturn);
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

			String username = null;
			try{
				username = SessionUtil.getCurrentUser(getThreadLocalRequest()).getUsername();
			}catch(Exception e){
				logger.warn("Maybe there is no user ");
			}
			return username == null || username.isEmpty();
		}
	}

	@Override
	public boolean isManageProductEnabled() {

		logger.info("Checking if the manage product button needs to be shown or not for the current context/user");
		String scopePerCurrentUrl = SessionUtil.getScopeFromClientUrl(getThreadLocalRequest());
		DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
		if(catalogue == null){
			logger.warn("There is no catalogue instance here..., returning false");
			return false;
		}
		else{
			try{
				PortalContext pContext = PortalContext.getConfiguration();
				List<GCubeRole> userRoles = new LiferayRoleManager().listRolesByUserAndGroup(
						pContext.getCurrentUser(getThreadLocalRequest()).getUserId(), 
						pContext.getCurrentGroupId(getThreadLocalRequest()));
				boolean isGRSFAdminRoleSet = false;
				for (GCubeRole gCubeRole : userRoles) {
					if(gCubeRole.getRoleName().equals(GRSF_ADMIN_ROLE)){
						isGRSFAdminRoleSet = true;
						break;
					}
				}

				boolean toReturn = catalogue.isManageProductEnabled() & isGRSFAdminRoleSet;
				logger.debug("IsmanageProductEnabled equals to " + catalogue.isManageProductEnabled());
				logger.debug("isGRSFAdminRoleSet equals to " + isGRSFAdminRoleSet);
				logger.info("Will manage product be enabled for this user? " + Boolean.toString(toReturn));
				return toReturn;
			}catch(Exception e){
				logger.error("Unable to determine if the manage product needs to be shown or not", e);
				return false;
			}
		}

	}
}
