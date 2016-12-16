/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.ApplicationProfileScopePerUrlReader;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class SessionUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public class SessionUtil {

	public static final String CKAN_END_POINT = "CKAN_END_POINT";
	public static final String CKAN_ACCESS_POINT = "CKAN_ACCESS_POINT";
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";
	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	/**
	 * Gets the ckan end point.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the ckan end point
	 * @throws Exception the exception
	 */
	public static GcoreEndpointReader getCkanEndPoint(HttpSession session, String scope) throws Exception{

		String key = getKeyForSession(CKAN_END_POINT, scope);
		logger.debug("Getting GcoreEndpointReader for key: "+key +", from HttpSession");
		GcoreEndpointReader ckanEndPoint = (GcoreEndpointReader) session.getAttribute(key);
		logger.debug("GcoreEndpointReader for key: "+key +", found in session? "+(ckanEndPoint!=null));
		if(ckanEndPoint==null){
			logger.debug("GcoreEndpointReader is null, instancing new..");
			ckanEndPoint = new GcoreEndpointReader(scope);
			session.setAttribute(key, ckanEndPoint);
		}
		logger.debug("returning: "+ckanEndPoint);
		return ckanEndPoint;
	}

	/**
	 * Checks if is into portal.
	 *
	 * @return true, if is into portal
	 */
	public static boolean isIntoPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}catch (Exception ex) {
			logger.debug("Development Mode ON");
			return false;
		}
	}

	/**
	 * Save ckan access point.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @param ckAP the ck ap
	 */
	public static void saveCkanAccessPoint(HttpSession session, String scope, CkanConnectorAccessPoint ckAP) {
		String key = getKeyForSession(CKAN_ACCESS_POINT, scope);
		session.setAttribute(key, ckAP);
	}

	/**
	 * Gets the key for session.
	 *
	 * @param key the key
	 * @param scope the scope
	 * @return the key for session
	 */
	private static String getKeyForSession(String key, String scope){
		return key+scope;
	}

	/**
	 * Gets the ckan access point.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the ckan access point
	 */
	public static CkanConnectorAccessPoint getCkanAccessPoint(HttpSession session, String scope) {
		String key = getKeyForSession(CKAN_ACCESS_POINT, scope);
		return (CkanConnectorAccessPoint) session.getAttribute(key);
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
		if(httpServletRequest == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");
		
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
