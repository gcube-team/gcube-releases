package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.ApplicationProfileScopePerUrlReader;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Util class with static methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GenericUtils {

	// Logger
	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utils.class);
	private static final Log logger = LogFactoryUtil.getLog(GenericUtils.class);
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";

	/**
	 * Given a ckan organization name retrieve the infrastructure scope
	 * @param organizationName (prevre, devvre, ...)
	 * @return the scope of the infrastructure
	 */
	public static String retrieveScopeFromOrganizationName(String organizationName) throws Exception {

		logger.debug("Organization name is " + organizationName);

		GroupManager gm = new LiferayGroupManager();
		List<GCubeGroup> groups = gm.listGroups();
		for (GCubeGroup gCubeGroup : groups) {
			if(gCubeGroup.getGroupName().equalsIgnoreCase(organizationName))
				return gm.getInfrastructureScope(gCubeGroup.getGroupId());
		}

		return null;
	}

	/**
	 * First check to retrieve the token, else create it
	 * @param username
	 * @param context
	 * @return the user token for the context
	 */
	public static String tryGetElseCreateToken(String username, String context) {
		String token = null;
		try{
			try{
				logger.debug("Checking if token for user " + username + " in context " + context + " already exists...");
				token = authorizationService().resolveTokenByUserAndContext(username, context);
				logger.debug("It exists!");
			}catch(ObjectNotFound e){
				logger.info("Creating token for user " + username + " and context " + context);
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
				logger.debug("received token: "+ token.substring(0, 5) + "***********************");
			}
		}catch(Exception e){
			logger.error("Failed both token retrieval and creation", e);
		}
		return token;
	}

	/**
	 * Get the scope in which ckan information needs to be discovered from the url
	 * @param httpServletRequest
	 * @return
	 */
	public static String getScopeFromClientUrl(HttpServletRequest httpServletRequest){

		if(httpServletRequest == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		String scopeToReturn = null;
		try{
			String clientUrl = getCurrentClientUrl(httpServletRequest).split("\\?")[0];
			logger.debug("Client url is " + clientUrl);

			// check if this information is in session, otherwise set it and return
			HttpSession session = httpServletRequest.getSession();

			if((scopeToReturn = (String) session.getAttribute(clientUrl)) != null){
				logger.debug("Scope to return is " + scopeToReturn);
			}else{
				// ask to the ckan library and set it
				scopeToReturn = ApplicationProfileScopePerUrlReader.getScopePerUrl(clientUrl);
				logger.debug("Scope to return is " + scopeToReturn);
				session.setAttribute(clientUrl, scopeToReturn);
			}
		}catch(Exception e){
			scopeToReturn = getCurrentContext(httpServletRequest, false);
			logger.warn("Failed to determine the scope from the client url, returning the current one: " + scopeToReturn);
		}
		return scopeToReturn;
	}

	/**
	 * Needed to get the url of the client
	 * @param httpServletRequest the httpServletRequest object
	 * @return the instance of the user
	 * @see the url at client side
	 */
	public static String getCurrentClientUrl(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getHeader(GCUBE_REQUEST_URL);
	}

	/**
	 * Retrieve the current user by using the portal manager
	 * @return a GcubeUser object
	 */
	public static GCubeUser getCurrentUser(HttpServletRequest request){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser user = pContext.getCurrentUser(request);
		logger.debug("Returning user " + user);
		return user;
	}

	/**
	 * Retrieve the current scope by using the portal manager
	 * @param b
	 * @return a GcubeUser object
	 */
	public static String getCurrentContext(HttpServletRequest request, boolean setInThread){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String context = pContext.getCurrentScope(request);
		logger.debug("Returning context " + context);

		if(context != null && setInThread)
			ScopeProvider.instance.set(context);

		return context;
	}

	/**
	 * Retrieve the current token by using the portal manager
	 * @param b
	 * @return a GcubeUser object
	 */
	public static String getCurrentToken(HttpServletRequest request, boolean setInThread){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String token = pContext.getCurrentUserToken(getCurrentContext(request, false), getCurrentUser(request).getUsername());
		logger.debug("Returning token " + token.substring(0, token.length()-7)+"XXXXX");

		if(token != null && setInThread)
			SecurityTokenProvider.instance.set(token);

		return token;
	}

	/**
	 * Retrieve the group given the scope
	 * @param scope
	 * @return
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	public static GCubeGroup getGroupFromScope(String scope) throws UserManagementSystemException, GroupRetrievalFault{

		if(scope == null || scope.isEmpty())
			throw new IllegalArgumentException("Scope is missing here!!");

		GroupManager gm = new LiferayGroupManager();
		long groupId = gm.getGroupIdFromInfrastructureScope(scope);
		return gm.getGroup(groupId);

	}

}