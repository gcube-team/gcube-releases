package org.gcube.common.portal;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.StringUtils;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.Email;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;




/**
 * <p>
 * Clients can obtain the single instance of the {@link PortalContext} by invoking its static method {@link #getConfiguration()}. 
 * The first invocation of the method triggers the initialisation of the instance.
 * 
 * For documentation see the related <a href="https://wiki.gcube-system.org/gcube/Portal_Context">WIKI</a>
 * </p>
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class PortalContext {
	private static final Logger _log = LoggerFactory.getLogger(PortalContext.class);
	public static final String VRE_ID_ATTR_NAME = "gcube-vreid";
	public static final String USER_ID_ATTR_NAME = "gcube-userId";
	/**
	 * Scope separators used in linear syntax.
	 */
	protected static final String SCOPE_SEPARATOR = "/";
	private static final String REGEX_ISNUMBER = "\\d+";

	private final static String DEFAULT_ROLE = "OrganizationMember";
	public static final String CONFIGURATION_FOLDER = "conf";
	public static final String INFRA_PROPERTY_FILENAME = "infrastructure.properties";
	private static final String GCUBE_DEV__CONTEXT_PROPERTY_FILENAME = "gcube-dev-context.properties";

	private static final String DEV_USERNAME_ATTR = "user.username";
	private static final String DEV_USER_NAME_ATTR = "user.name";
	private static final String DEV_USER_LASTNAME_ATTR = "user.lastname";
	private static final String DEV_USER_EMAIL_ATTR = "user.email";

	private static final String DEV_SCOPE_ATTR = "development.context";
	private static final String DEV_GROUP_NAME_ATTR = "development.groupname";
	private static final String DEV_GROUP_ID_ATTR = "development.groupid";
	private static final String DEV_TOKEN_ATTR = "user.token";



	private static final String DEFAULT_INFRA_NAME = "gcube";
	private static final String DEFAULT_VO_NAME = "devsec";
	private static final String DEFAULT_GATEWAY_NAME = "D4science Gateway";
	private static final String DEFAULT_GATEWAY_EMAIL = "do-not-reply@d4science.org";

	private static PortalContext singleton = new PortalContext();

	private UserManager userManager;

	private String infra;
	private String vos;

	private PortalContext() {
		initialize();
	}
	/**
	 * 
	 * @return the instance
	 */
	public synchronized static PortalContext getConfiguration() {
		return singleton == null ? new PortalContext() : singleton;
	}

	private void initialize() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getCatalinaHome());
			sb.append(File.separator)
			.append(CONFIGURATION_FOLDER)
			.append(File.separator)
			.append(INFRA_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			infra  = props.getProperty(GCubePortalConstants.INFRASTRUCTURE_NAME);
			vos = props.getProperty(GCubePortalConstants.SCOPES);
			userManager = new LiferayUserManager();
		}
		catch(IOException e) {
			infra = DEFAULT_INFRA_NAME;
			vos = DEFAULT_VO_NAME;
			userManager = new LiferayUserManager();
			_log.error("infrastructure.properties file not found under $CATALINA_HOME/conf/ dir, setting default infrastructure Name " + infra + " and VO Name " + vos);
		}		

		_log.info("PortalContext configurator correctly initialized on " + infra);
	}

	/**
	 * 
	 * @return the infrastructure name in which your client runs
	 */
	public String getInfrastructureName() {
		return this.infra;
	}
	/**
	 * 
	 * @return the value of the scopes as it is in the property file (a string with comma separated vales)
	 */
	public String getVOsAsString() {
		return this.vos;
	}
	/**
	 * <p>
	 * Please note that this method works with AJAX calls only (i.e. XMLHttpRequest to exchange data with a server behind the scenes). 
	 * If you use standard http servlet GET or POST to exchange data with a server, you must call the method PortalContext#setUserInSession in your portlet doView method.
	 * 
	 * </p>
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the current user, or <code>null</code> if a current user could not be found or session expired 
	 * @see {@link GCubeUser}
	 */
	public GCubeUser getCurrentUser(HttpServletRequest httpServletRequest) {
		String userIdNo = httpServletRequest.getHeader(USER_ID_ATTR_NAME);
		long userId = -1;
		if (userIdNo != null && userIdNo.matches(REGEX_ISNUMBER)) {
			try {
				_log.debug("The userIdNo is " + userIdNo);
				userId = Long.parseLong(userIdNo);
				return userManager.getUserById(userId);
			} catch (NumberFormatException e) {
				_log.error("The userId is not a number -> " + userId);
			} catch (Exception e) {
				_log.error("Could not read the current userid from header param, either session expired or user not logged in, exception: " + e.getMessage());
			}
		} else {
			if (!isWithinPortal()) {
				GCubeUser toReturn = readUserFromPropertyFile();
				_log.debug("getCurrentUser devMode into IDE detected, returning testing user: " + toReturn.toString());
				return toReturn;
			} else { //must be a custom servlet
				try {
					HttpSession session = httpServletRequest.getSession(false);
					userId = (long) session.getAttribute(USER_ID_ATTR_NAME);
					_log.debug("read the current userid from the http session userId: " + userId + " sessionid="+session.getId());
					return userManager.getUserById(userId);
				} catch (Exception e) {
					_log.error("Could not read the current userid from the http session, either session expired or user not logged in, exception: " + e.getMessage());
					return null;
				}
			}
		}
		return null;
	}
	/**
	 * <p>
	 * This method sets the current userid in the session so that it is possible to get the current user in custom servlets of the same WAR. 
	 * Must be added in Portlet's doView() method before dispatching the request.
	 * </p>
	 * 
	 * @param request the portlet {@link RenderRequest} object
	 */
	public static void setUserInSession(RenderRequest request) {
		long userId = Long.parseLong(request.getRemoteUser());
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		HttpSession session = httpServletRequest.getSession();
		_log.debug("HttpSession#setUserInSession, set userId: " + userId + " sessionid="+session.getId());
		session.setAttribute(USER_ID_ATTR_NAME, userId);
	}
	/**
	 * @param scopeGroupId the liferay groupid number (as String) of the VRE/VO
	 * @return the scope (context)
	 */
	public String getCurrentScope(String scopeGroupId) {
		if (scopeGroupId != null && scopeGroupId.matches(REGEX_ISNUMBER)) {
			long groupId = -1;
			try {
				groupId = Long.parseLong(scopeGroupId);
				LiferayGroupManager gm = new LiferayGroupManager();
				if (gm.isRootVO(groupId)) {
					return SCOPE_SEPARATOR + getInfrastructureName();
				} else 
					return new LiferayGroupManager().getInfrastructureScope(groupId);
			} catch (NumberFormatException e) {
				_log.error("The groupId is not a number -> " + groupId);
			} catch (Exception e) {
				_log.error("This groupId does not belong to any group in this portal -> " + groupId);
			}
		} else {
			if (!isWithinPortal()) {
				String toReturn = readContextPropertyFile();
				_log.debug("getCurrentScope devMode into IDE detected, returning scope: " + toReturn.toString());
				_log.debug("The PortalBeanLocatorUtil stacktrace (java.lang.Exception) is acceptable in dev");
				return toReturn;
			}
		}
		return null;
	}
	/**
	 * <p>
	 * Please note that this method works with AJAX calls only (i.e. XMLHttpRequest to exchange data with a server behind the scenes). 
	 * If you use standard http servlet GET or POST to exchange data with a server, you must you must handle the infrastructure context information differently. 
	 * Please see the following page for further information  @see <a href="https://wiki.gcube-system.org/gcube/ClientContextLibrary">ClientContextLibrary WIKI</a>
	 * </p>	
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the infrastructure context (scope)
	 */
	public String getCurrentScope(HttpServletRequest httpServletRequest) {
		String scopeGroupId = httpServletRequest.getHeader(VRE_ID_ATTR_NAME);
		return getCurrentScope(scopeGroupId);
	} 
	/**
	 * <p>
	 * Please note that this method works with AJAX calls only (i.e. XMLHttpRequest to exchange data with a server behind the scenes). 
	 * If you use standard http servlet GET or POST to exchange data with a server, you must you must handle the infrastructure context information differently. 
	 * Please see the following page for further information  @see <a href="https://wiki.gcube-system.org/gcube/ClientContextLibrary">ClientContextLibrary WIKI</a>
	 * </p>	
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the current group name (e.g. devVRE, BioDiversityLab, RStudioLab etc. )
	 */
	public String getCurrentGroupName(HttpServletRequest httpServletRequest) {
		String groupIdNo = httpServletRequest.getHeader(VRE_ID_ATTR_NAME);
		if (groupIdNo != null && groupIdNo.matches(REGEX_ISNUMBER)) {
			long groupId = -1;
			try {
				groupId = Long.parseLong(groupIdNo);
				LiferayGroupManager gm = new LiferayGroupManager();
				return gm.getGroup(groupId).getGroupName();
			} catch (NumberFormatException e) {
				_log.error("The groupId is not a number -> " + groupId);
			} catch (Exception e) {
				_log.error("This groupId does not belong to any group in this portal -> " + groupId);
			}
		} else {
			if (!isWithinPortal()) {
				String toReturn = readGroupNamePropertyFile();
				_log.debug("getCurrentGroupName devMode into IDE detected, returning group name: " + toReturn.toString());
				_log.debug("The PortalBeanLocatorUtil stacktrace (java.lang.Exception) is acceptable in dev");
				return toReturn;
			}
		}
		return null;
	}
	/**
	 * <p>
	 * Please note that this method works with AJAX calls only (i.e. XMLHttpRequest to exchange data with a server behind the scenes). 
	 * If you use standard http servlet GET or POST to exchange data with a server, you must you must handle the infrastructure context information differently. 
	 * Please see the following page for further information  @see <a href="https://wiki.gcube-system.org/gcube/ClientContextLibrary">ClientContextLibrary WIKI</a>
	 * </p>	
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the current group identifier as long
	 */
	public long getCurrentGroupId(HttpServletRequest httpServletRequest) {
		String groupIdNo = httpServletRequest.getHeader(VRE_ID_ATTR_NAME);
		if (groupIdNo != null && groupIdNo.matches(REGEX_ISNUMBER)) {
			long groupId = -1;
			try {
				groupId = Long.parseLong(groupIdNo);
				return groupId;
			} catch (NumberFormatException e) {
				_log.error("The groupId is not a number -> " + groupId);
			} catch (Exception e) {
				_log.error("This groupId does not belong to any group in this portal -> " + groupId);
			}
		} else {
			if (!isWithinPortal()) {
				long toReturn = readGroupIdPropertyFile();
				_log.debug("getCurrentGroup devMode into IDE detected, returning groupid = " + toReturn);
				_log.debug("The PortalBeanLocatorUtil stacktrace (java.lang.Exception) is acceptable in dev");
				return toReturn;
			}
		}
		return -1;
	}
	/**
	 * <p>
	 * Returns the gCube authorisation token for the given user 
	 * </p>	
	 * @param scope infrastrucure context (scope)
	 * @param userId the GCubeUser user identifier (userId) @see {@link GCubeUser}
	 * @return the Token for the user in the context, or <code>null</code> if a token for this user could not be found
	 */
	public String getCurrentUserToken(String scope, long userId) {
		if (isWithinPortal()) {
			try {
				String username = userManager.getUserById(userId).getUsername();
				return  getCurrentUserToken(scope, username);
			} catch (UserManagementSystemException | UserRetrievalFault e) {
				e.printStackTrace();
			}
		}
		else {
			String toReturn = readTokenPropertyFile();
			_log.debug("getCurrentToken devMode into IDE detected, returning token: " + toReturn.toString());
			_log.debug("The PortalBeanLocatorUtil stacktrace (java.lang.Exception) is acceptable in dev");
			return toReturn;
		}
		return null;
	}
	/**
	 * <p>
	 * Returns the gCube authorisation token for the given user 
	 * </p>	
	 * @param scope infrastrucure context (scope)
	 * @param username the GCubeUser username @see {@link GCubeUser}
	 * @return the Token for the user in the context, or <code>null</code> if a token for this user could not be found
	 */
	public String getCurrentUserToken(String scope, String username) {
		String userToken = null;
		if (isWithinPortal()) {
			try {
				ScopeProvider.instance.set(scope);
				userToken = authorizationService().resolveTokenByUserAndContext(username, scope);
				SecurityTokenProvider.instance.set(userToken);
			} 
			catch (ObjectNotFound ex) {
				userToken = generateAuthorizationToken(username, scope);
				SecurityTokenProvider.instance.set(userToken);
				_log.debug("generateAuthorizationToken OK for " + username + " in scope " + scope);
			}			 
			catch (Exception e) {
				_log.error("Error while trying to generate token for user " + username + "in scope " + scope);
				e.printStackTrace();
				return null;
			}
		} else {
			String toReturn = readTokenPropertyFile();
			_log.debug("getCurrentToken devMode into IDE detected, returning token: " + toReturn.toString());
			_log.debug("The PortalBeanLocatorUtil stacktrace (java.lang.Exception) is acceptable in dev");
			return toReturn;
		}
		return userToken;
	}
	/**
	 * @deprecated please use getCurrentUserToken(String scope, String username) or getCurrentUserToken(String scope, long userId)
	 * <p>
	 * Please note that this method works with AJAX calls only (i.e. XMLHttpRequest to exchange data with a server behind the scenes). 
	 * </p>	
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the Token for the user in the context, or <code>null</code> if a token for this user could not be found
	 */
	public String getCurrentUserToken(HttpServletRequest httpServletRequest) {
		String groupIdNo = httpServletRequest.getHeader(VRE_ID_ATTR_NAME);
		String userToken = null;
		if (groupIdNo != null && groupIdNo.matches(REGEX_ISNUMBER)) {
			String scope = getCurrentScope(httpServletRequest);
			String username = getCurrentUser(httpServletRequest).getUsername();
			try {
				ScopeProvider.instance.set(scope);
				userToken = authorizationService().resolveTokenByUserAndContext(username, scope);
				SecurityTokenProvider.instance.set(userToken);
			} 
			catch (ObjectNotFound ex) {
				userToken = generateAuthorizationToken(username, scope);
				SecurityTokenProvider.instance.set(userToken);
				_log.debug("generateAuthorizationToken OK for " + username + " in scope " + scope);
			}			 
			catch (Exception e) {
				_log.error("Error while trying to generate token for user " + username + "in scope " + scope);
				e.printStackTrace();
				return null;
			}
		} else {
			if (isWithinPortal()) {
				_log.warn("It seems your app is running in Liferay but not context was set on this (HttpServletRequest) request");
			} else {
				String toReturn = readTokenPropertyFile();
				_log.debug("getCurrentToken devMode into IDE detected, returning scope: " + toReturn.toString());
				_log.debug("The PortalBeanLocatorUtil stacktrace (java.lang.Exception) is acceptable in dev");
				return toReturn;
			}
		}
		return userToken;
	}
	/**
	 * 
	 * @param username
	 * @param scope
	 * @throws Exception
	 */
	private static String generateAuthorizationToken(String username, String scope) {
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		String token;
		try {
			token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return token;
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (Exception ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}

	/**
	 * 
	 * @return the value of the scopes
	 */
	public List<String> getVOs() {
		List<String> toReturn = new ArrayList<String>();
		if (vos == null || vos.equals(""))
			return toReturn;
		String[] split = vos.split(",");
		for (int i = 0; i < split.length; i++) {
			toReturn.add(split[i].trim());
		}
		return toReturn;
	}
	/**
	 * 
	 * @deprecated use getConfiguration().getGatewayName() method
	 * read the portal instance name from a property file and returns it
	 */
	@Deprecated 
	public static String getPortalInstanceName() {
		return getConfiguration().getGatewayName();
	}
	/**
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the gateway URL until the first slash, e.g. http(s)://mynode.d4science.org:8080, if the URL uses standard http(s) port like 80 or 443 the port is not returned. 
	 */
	public String getGatewayURL(HttpServletRequest httpServletRequest) {
		String serverName =  httpServletRequest.getServerName();
		String toReturn = (httpServletRequest.isSecure()) ? "https://" : "http://" ;
		//server name
		toReturn += serverName;
		//port
		if (httpServletRequest.isSecure()) 
			toReturn +=  (httpServletRequest.getServerPort() == 443) ? "" : ":"+httpServletRequest.getServerPort() ;
		else
			toReturn +=  (httpServletRequest.getServerPort() == 80) ? "" : ":"+httpServletRequest.getServerPort() ;

		return toReturn;
	}
	/**
	 * 
	 * @param serverName e.g. myportal.mydomain.org
	 * @param serverPort 
	 * @param secure 
	 * @return the gateway URL until the first slash, e.g. http(s)://mynode.d4science.org:8080, if the URL uses standard http(s) port like 80 or 443 the port is not returned. 
	 */
	public String getGatewayURL(String serverName, int serverPort, boolean secure) {
		return PortalUtil.getPortalURL(serverName, serverPort, secure);
	}
	/**
	 * @deprecated use getGatewayURL(HttpServletRequest httpServletRequest)
	 * @return the basic gateway url
	 */
	@Deprecated
	public String getGatewayURL() {
		Long defaultCompanyId = PortalUtil.getDefaultCompanyId();
		try {
			CompanyLocalServiceUtil.getCompany(defaultCompanyId);

			return PortalUtil.getPortalURL(CompanyLocalServiceUtil.getCompany(defaultCompanyId).getVirtualHostname(), 443, true);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "";
	}
	/**
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the landing page path of the current Site e.g. "/group/i-marine"
	 */
	public String getSiteLandingPagePath(final HttpServletRequest request) {
		String sitePath = "";
		Group site;
		try {
			site = getSiteFromServletRequest(request);
			if (site.getPrivateLayoutsPageCount() > 0) {
				sitePath = getGroupFriendlyURL(site);
			} else	{
				_log.debug(site.getName() + " site doesn't have any private page. Default landing page will be used");
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return sitePath;
	}
	/**
	 * 
	 * @param serverName e.g. myportal.mydomain.org
	 * @return the landing page path of the current Site e.g. "/group/i-marine"
	 */
	public String getSiteLandingPagePath(final String serverName) {
		String sitePath = "";
		Group site;
		try {
			site = getSiteFromServerName(serverName);
			if (site.getPrivateLayoutsPageCount() > 0) {
				sitePath = getGroupFriendlyURL(site);
			} else	{
				_log.debug(site.getName() + " site doesn't have any private page. Default landing page will be used");
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return sitePath;
	}
	/**
	 * 
	 * @param request the {@link HttpServletRequest} object
	 * @return the current Group instance based on the request
	 */
	private Group getSiteFromServletRequest(final HttpServletRequest request) throws Exception {
		String serverName = request.getServerName();
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				return site;
			}
		}
		_log.warn("serverName is " +  serverName + " but i could not find any virtualHost associated to it");
		return null;
	}
	/**
	 * 
	 * @param serverName e.g. myportal.mydomain.org
	 * @return the Liferay Group instance responding to the servename passed as parameter
	 */
	private Group getSiteFromServerName(final String serverName) throws Exception {
		_log.debug("serverName passed is " +  serverName);
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			_log.debug("Found  " +  virtualHost.getHostname());
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				_log.debug("Found match! Your site is " +  site.getName());
				return site;
			}
		}
		return null;
	}
	/**
	 * @param currentGroup
	 * @return Returns the friendly u r l of this group.
	 */
	private static String getGroupFriendlyURL(final Group currentGroup) throws Exception {
		String friendlyURL = GCubePortalConstants.PREFIX_GROUP_URL;
		StringBuffer sb = new StringBuffer();
		sb.append(friendlyURL).append(currentGroup.getFriendlyURL());
		return sb.toString();
	}
	/**
	 * 
	 * @param serverName e.g. myportal.mydomain.org
	 * @return the current Site Name based on the servername (e.g. i-marine.d4science.org)
	 */
	public String getGatewayName(final String serverName) {
		String toReturn = DEFAULT_GATEWAY_NAME;
		try {
			Group currSite = getSiteFromServerName(serverName);
			toReturn = currSite.getName();
		} catch (Exception e) {
			toReturn = DEFAULT_GATEWAY_NAME;
			_log.error("Could not read Site Custom Attr: " + CustomAttributeKeys.GATEWAY_SITE_NAME.getKeyName() + ", returning default Gateway Name " + toReturn);
		} 
		return toReturn;
	}
	/**
	 * 
	 * @param httpServletRequest the {@link HttpServletRequest} object
	 * @return the current Site Name based on the request
	 */
	public String getGatewayName(HttpServletRequest request) {
		String toReturn = DEFAULT_GATEWAY_NAME;
		try {
			Group currSite = getSiteFromServletRequest(request);
			toReturn = currSite.getName();
		} catch (Exception e) {
			toReturn = DEFAULT_GATEWAY_NAME;
			_log.error("Could not read Site Custom Attr: " + CustomAttributeKeys.GATEWAY_SITE_NAME.getKeyName() + ", returning default Gateway Name " + toReturn);
		} 
		return toReturn;
	}

	/**
	 * read the infrastructure gateway name from a property file and returns it
	 * @deprecated use getGatewayName(HttpServletRequest request)
	 */
	@Deprecated
	public String getGatewayName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile =  getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.GATEWAY_NAME);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = DEFAULT_GATEWAY_NAME;
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Portal Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Gateway Name: " + toReturn );
		return toReturn;
	}
	/**
	 * 
	 * @param request the {@link HttpServletRequest} object
	 * @return the sender (from) email address  for the current Site based on the request
	 */
	public String getSenderEmail(HttpServletRequest request) {
		String toReturn = DEFAULT_GATEWAY_EMAIL;
		try {
			Group currSite = getSiteFromServletRequest(request);
			toReturn = (String) new LiferayGroupManager().readCustomAttr(currSite.getGroupId(), CustomAttributeKeys.GATEWAY_SITE_EMAIL_SENDER.getKeyName());			
		} catch (Exception e) {
			toReturn = DEFAULT_GATEWAY_EMAIL;
			_log.error("Could not read Site Custom Attr: " + CustomAttributeKeys.GATEWAY_SITE_EMAIL_SENDER.getKeyName() + ", returning default Gateway Email Sender " + toReturn);
		} 
		return toReturn;
	}
	/**
	 * 
	 * @param serverName e.g. myportal.mydomain.org
	 * @return the sender (from) email address  for the current Site based on the request
	 */
	public String getSenderEmail(final String serverName) {
		String toReturn = DEFAULT_GATEWAY_EMAIL;
		try {
			Group currSite = getSiteFromServerName(serverName);
			toReturn = (String) new LiferayGroupManager().readCustomAttr(currSite.getGroupId(), CustomAttributeKeys.GATEWAY_SITE_EMAIL_SENDER.getKeyName());			
		} catch (Exception e) {
			toReturn = DEFAULT_GATEWAY_EMAIL;
			_log.error("Could not read Site Custom Attr: " + CustomAttributeKeys.GATEWAY_SITE_EMAIL_SENDER.getKeyName() + ", returning default Gateway Email Sender " + toReturn);
		} 
		return toReturn;
	}

	/**
	 * <p>
	 *  read the portal application token valid in the root Virtual Organisation into a property file
	 * </p>
	 * @return the portal application token valid in the root Virtual Organisation or <code>null</code> if the token could not be found
	 */
	public static String getPortalApplicationToken() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.PORTAL_APPLICATION_TOKEN);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = null;
			_log.error("gcube-data.properties file or portal app token not found under $CATALINA_HOME/conf dir, please add the property and or a valid token: " + GCubePortalConstants.PORTAL_APPLICATION_TOKEN);
			return toReturn;
		}
		return toReturn;
	}
	/**
	 * <p>
	 *  read the IC Proxy endpoint from a property file, IC Proxy is needed to query the Information System via REST
	 *  e.g. http://dev.d4science.org:8080/icproxy/gcube/service
	 * </p>
	 * @return the IC Proxy endpoint or <code>null</code> if the IC Proxy could not be found
	 */
	public static String getICProxyEndPoint() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.ICPROXY_ENDPOINT_URL);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = null;
			_log.error("gcube-data.properties file or portal app token not found under $CATALINA_HOME/conf dir, please add the property and or a valid token: " + GCubePortalConstants.PORTAL_APPLICATION_TOKEN);
			return toReturn;
		}
		return toReturn;
	}

	/**
	 * read the sender (from) email address for notifications name from a property file and returns it
	 * @deprecated use getSenderEmail(HttpServletRequest request)
	 */
	@Deprecated
	public String getSenderEmail() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.SENDER_EMAIL);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = DEFAULT_GATEWAY_EMAIL;
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Email" + toReturn);
			return toReturn;
		}
		_log.debug("Returning SENDER_EMAIL: " + toReturn );
		return toReturn;
	}
	/**
	 * for development purposes only
	 */
	protected static GCubeUser readUserFromPropertyFile() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getGCubeDevHome());
			sb.append(File.separator)
			.append(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			long userId  = -1;
			String username  = props.getProperty(DEV_USERNAME_ATTR);
			String email  = props.getProperty(DEV_USER_EMAIL_ATTR);
			String firstName  = props.getProperty(DEV_USER_NAME_ATTR);
			String middleName  = "";
			String lastName  = props.getProperty(DEV_USER_LASTNAME_ATTR);
			String fullname  = firstName + lastName;
			long registrationDate  = -1;
			String userAvatarId  = "-1";
			boolean male  = true;
			String jobTitle  = "TestingAccount";
			List<Email> emailAddresses = new ArrayList<>();

			return new GCubeUser(userId, username, email, firstName, middleName, lastName, fullname, registrationDate, userAvatarId, male, jobTitle, emailAddresses);
		}
		catch(IOException e) {
			_log.error(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME + " file not found under $GCUBE_DEV_HOME dir");
			return null;
		}		
	}
	/**
	 * for development purposes only
	 */
	private static String readTokenPropertyFile() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getGCubeDevHome());
			sb.append(File.separator)
			.append(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			String token  = props.getProperty(DEV_TOKEN_ATTR);
			if (token != null && token.compareTo("") != 0)
				return token;
			else {
				_log.error("Token property "+ DEV_TOKEN_ATTR + " is missing or empty in the property file " + propertyfile);
				return null;
			}			
		}
		catch(IOException e) {
			_log.error(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME + " file not found under $GCUBE_DEV_HOME dir");
			return null;
		}		
	}

	/**
	 * for development purposes only
	 */
	private static String readContextPropertyFile() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getGCubeDevHome());
			sb.append(File.separator)
			.append(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			String scope  = props.getProperty(DEV_SCOPE_ATTR);
			if (scope.startsWith(SCOPE_SEPARATOR))
				return scope;
			else {
				_log.error("Scope is not valid, does not start with " + SCOPE_SEPARATOR);
				return null;
			}			
		}
		catch(IOException e) {
			_log.error(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME + " file not found under $GCUBE_DEV_HOME dir");
			return null;
		}		
	}
	/**
	 * for development purposes only
	 */
	private static String readGroupNamePropertyFile() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getGCubeDevHome());
			sb.append(File.separator)
			.append(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			String groupName  = props.getProperty(DEV_GROUP_NAME_ATTR);
			if (groupName != null && !groupName.isEmpty())
				return groupName;
			else {
				_log.error("groupName is not valid, check property " + DEV_GROUP_NAME_ATTR);
				return null;
			}			
		}
		catch(IOException e) {
			_log.error(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME + " file not found under $GCUBE_DEV_HOME dir");
			return null;
		}		
	}

	/**
	 * for development purposes only
	 */
	private static long readGroupIdPropertyFile() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getGCubeDevHome());
			sb.append(File.separator)
			.append(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			String groupIdNo  = props.getProperty(DEV_GROUP_ID_ATTR);
			long groupId = -1;
			if (groupIdNo != null) {
				try {
					groupId = Long.parseLong(groupIdNo);
					return groupId;
				} catch (NumberFormatException e) {
					_log.error("The groupId is not a number -> " + groupIdNo);
				}
			}
			else {
				_log.error("groupId is not valid, check property " + DEV_GROUP_ID_ATTR);
				return -1L;
			}			
		}
		catch(IOException e) {
			_log.error(GCUBE_DEV__CONTEXT_PROPERTY_FILENAME + " file not found under $GCUBE_DEV_HOME dir");
			return -1L;
		}		
		return -1L;
	}
	
	


	/**
	 * 
	 * @return $CATALINA_HOME
	 */
	private static String getCatalinaHome() {
		return (System.getenv("CATALINA_HOME").endsWith("/") ? System.getenv("CATALINA_HOME") : System.getenv("CATALINA_HOME")+"/");
	}

	/**
	 * 
	 * @return $GCUBE-DEV-HOME
	 */
	private static String getGCubeDevHome() {
		return (System.getenv("GCUBE_DEV_HOME").endsWith("/") ? System.getenv("GCUBE_DEV_HOME") : System.getenv("GCUBE_DEV_HOME")+"/");
	}




}
