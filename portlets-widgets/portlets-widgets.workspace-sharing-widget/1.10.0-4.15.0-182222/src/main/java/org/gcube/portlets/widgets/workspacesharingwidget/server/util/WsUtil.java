/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.portlets.widgets.workspacesharingwidget.server.GWTWorkspaceSharingBuilder;
import org.gcube.portlets.widgets.workspacesharingwidget.server.notifications.NotificationsProducer;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VO;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VRE;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa 
 * Nov 25, 2016
 */
public class WsUtil {

	public static final String WORKSPACEBUILDER_ATTRIBUTE = "WORKSPACE_SHARING_BUILDER_ATTRIBUTE";
	public static final String NOTIFICATION_MANAGER = "NOTIFICATION_SHARING_MANAGER";
	public static final String NOTIFICATION_PRODUCER = "NOTIFICATION_SHARING_PRODUCER";
	public static final String WORKSPACE_SCOPE_UTIL = "WORKSPACE_SHARING_SCOPE_UTIL";
	public static final String NOTIFICATION_PORTLET_CLASS_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl"; //USE THE SAME OF WORKSPACE
	protected static Logger logger = LoggerFactory.getLogger(WsUtil.class);

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.info("Development Mode ON");
			return false;
		}
	}


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest){
		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(httpServletRequest);
		String username = user.getUsername();
		String fullName = user.getFullname();
		String email = user.getEmail();
		String avatarID = user.getUserAvatarId();
		String avatarURL = user.getUserAvatarURL();
		//SESSION
		String currentScope = pContext.getCurrentScope(httpServletRequest);
		String userToken = pContext.getCurrentUserToken(httpServletRequest);
		long currGroupId = pContext.getCurrentGroupId(httpServletRequest);

		return new PortalContextInfo(username, fullName, email, avatarID, avatarURL, currentScope, userToken, currGroupId);
	}

	/**
	 * Checks if is session expired.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return true, if is session expired
	 * @throws Exception the exception
	 */
	public static boolean isSessionExpired(HttpServletRequest httpServletRequest) throws Exception {
		logger.trace("workspace session validating...");
		PortalContextInfo info = getPortalContext(httpServletRequest);
		return info.getUsername()==null;
	}


	/**
	 * Gets the vres from infrastructure.
	 *
	 * @param infrastructure the infrastructure name
	 * @param startScopes the starting scopes
	 * @return an arraylist of VO containing their child VREs
	 */
	public static List<VO> getVresFromInfrastructure(String infrastructure, String startScopes) {
		List<VO> toReturn = new ArrayList<VO>();

		String[] organizations;
		if (startScopes.contains(",")) {
			organizations = startScopes.split(",");
			for (int i = 0; i < organizations.length; i++) {
				VO toAdd = new VO();
				toAdd.setName(organizations[i]);
				toAdd.setVres((ArrayList<VRE>) LoginServiceUtil.getVREsFromInfrastructure(infrastructure+"/"+organizations[i]));
				toReturn.add(toAdd);
			}
		}
		else {
			organizations = new String[1];
			organizations[0] = startScopes;
			VO toAdd = new VO();
			toAdd.setName(organizations[0]);
			toAdd.setVres((ArrayList<VRE>) LoginServiceUtil.getVREsFromInfrastructure(infrastructure+"/"+organizations[0]));
			toReturn.add(toAdd);
		}
		return toReturn;
	}

	

	/**
	 * Gets the notification manager.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the notification manager
	 */
	public static NotificationsManager getNotificationManager(HttpServletRequest httpServletRequest)
	{
		PortalContextInfo info = getPortalContext(httpServletRequest);
		HttpSession session = httpServletRequest.getSession();
		NotificationsManager notifMng = (NotificationsManager) session.getAttribute(NOTIFICATION_MANAGER);

		if (notifMng == null) {
			try{
				logger.info("Create new NotificationsManager for user: "+info.getUsername());
				logger.info("New ApplicationNotificationsManager with portlet class name: "+NOTIFICATION_PORTLET_CLASS_ID);
				SocialNetworkingSite site = new SocialNetworkingSite(httpServletRequest);
				SocialNetworkingUser curser = new SocialNetworkingUser(info.getUsername(), info.getUserEmail(), info.getUserFullName(), info.getUserAvatarID());
				notifMng = new ApplicationNotificationsManager(site, info.getCurrentScope(), curser, NOTIFICATION_PORTLET_CLASS_ID);
				session.setAttribute(NOTIFICATION_MANAGER, notifMng);
			}catch (Exception e) {
				logger.error("An error occurred instancing ApplicationNotificationsManager for user: "+info.getUsername(),e);
			}
		}

		return notifMng;
	}


	/**
	 * Gets the notification producer.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the notification producer
	 */
	public static NotificationsProducer getNotificationProducer(HttpServletRequest httpServletRequest)
	{
		PortalContextInfo info = getPortalContext(httpServletRequest);
		HttpSession session = httpServletRequest.getSession();
		NotificationsProducer notifProducer = (NotificationsProducer) session.getAttribute(NOTIFICATION_PRODUCER);

		if (notifProducer == null) {
			logger.info("Create new Notification Producer for user: "+info.getUsername());
			notifProducer = new NotificationsProducer(httpServletRequest);
			session.setAttribute(NOTIFICATION_PRODUCER, notifProducer);
		}

		return notifProducer;
	}

	/**
	 * Gets the user id.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the user id
	 */
	public static String getUserId(HttpServletRequest httpServletRequest) {

		PortalContextInfo info = getPortalContext(httpServletRequest);
		return info.getUsername();
	}

	/**
	 * Checks if is vre.
	 *
	 * @param scope the scope
	 * @return true, if is vre
	 */
	public static boolean isVRE(String scope){

		int slashCounter = 0;
		char slash="/".charAt(0);
		for (int i = 0; i < scope.length(); i++) {
			if (scope.charAt(i)==slash) {
				slashCounter++;
			}
		}
		
		if(slashCounter < 3){
			logger.trace("currentScope is not VRE");
			return false;
		}

		logger.trace("currentScope is VRE");
		return true;
	}


	/**
	 * Gets the scope util filter.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the scope util filter
	 */
	public static ScopeUtility getScopeUtilFilter(HttpServletRequest httpServletRequest){

		PortalContextInfo info = getPortalContext(httpServletRequest);
		ScopeUtility scopeUtil = null;
		try{
			scopeUtil = (ScopeUtility) httpServletRequest.getSession().getAttribute(WsUtil.WORKSPACE_SCOPE_UTIL);

			if(scopeUtil==null){
				scopeUtil = new ScopeUtility(info.getCurrentScope());

			}
		}catch (Exception e) {
			logger.error("an error occurred in getscope filter ",e);
		}

		return scopeUtil;
	}

	/**
	 * Gets the GWT workspace sharing builder.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the GWT workspace sharing builder
	 */
	public static GWTWorkspaceSharingBuilder getGWTWorkspaceSharingBuilder(HttpServletRequest httpServletRequest) {
		PortalContextInfo info = getPortalContext(httpServletRequest);
		GWTWorkspaceSharingBuilder builder = null;

		try{
			builder = (GWTWorkspaceSharingBuilder) httpServletRequest.getSession().getAttribute(WsUtil.WORKSPACEBUILDER_ATTRIBUTE);

			if(builder==null)
				return new GWTWorkspaceSharingBuilder();
		}catch (Exception e) {
			logger.error("an error occurred in get builder ",e);
		}

		return new GWTWorkspaceSharingBuilder();
	}
}
