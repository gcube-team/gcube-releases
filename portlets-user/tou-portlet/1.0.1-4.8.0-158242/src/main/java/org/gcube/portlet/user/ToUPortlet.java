package org.gcube.portlet.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.mailing.message.Recipient;
import org.gcube.portal.mailing.service.EmailTemplateService;
import org.gcube.portal.mailing.templates.TemplateUserHasLeftVRE;
import org.gcube.portal.tou.TermsOfUse;
import org.gcube.portal.tou.TermsOfUseImpl;
import org.gcube.portal.tou.exceptions.ToUNotFoundException;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ToU
 */
public class ToUPortlet extends MVCPortlet {

	private static final Log logger = LogFactoryUtil.getLog(ToUPortlet.class);
	public static final String ACCEPTED_TOU_KEY = "acceptedLatestToU";
	public static final String REDIRECT_URL_KEY = "redirectUrl";
	private static final TermsOfUse TOU_UTILS_LIB = new TermsOfUseImpl();
	private static final UserManager USER_MANAGER = new LiferayUserManager();

	/**
	 * Invoked when ToU is accepted by the user
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void acceptToU(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		//business logic here
		logger.info("Called acceptToU");
		try {
			GCubeUser user = USER_MANAGER.getUserById(PortalUtil.getUserId(actionRequest));
			long groupId = PortalUtil.getScopeGroupId(actionRequest);
			logger.debug("Group id is " + groupId + "  and username is " + user.getUsername());
			TOU_UTILS_LIB.setAcceptedToU(user.getUsername(), groupId);
		} catch (PortalException | SystemException | ToUNotFoundException
				| UserRetrievalFault | UserManagementSystemException
				| GroupRetrievalFault e) {
			throw new PortletException("Unable to perform accept operation [" + e.getMessage() + "]");
		}
		actionResponse.setRenderParameter("mvcPath","/html/tou/AfterAccepted.jsp");
	}

	/**
	 * Invoked when the user is sure she wants to deny the ToU
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void denyToU(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		//business logic here
		logger.info("Called denyToU");
		String redirectUrl = removeUserFromVRE(actionRequest);
		logger.info("Redirect url after denyToU is " + redirectUrl);
		actionRequest.setAttribute(REDIRECT_URL_KEY, redirectUrl);
		actionResponse.sendRedirect(redirectUrl);

	}

	/**
	 * Check if the user has accepted latest ToU
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void hasAcceptedLatestToU(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		//business logic here
		logger.info("Called hasAcceptedLatestToU");
		try {
			GCubeUser user = USER_MANAGER.getUserById(PortalUtil.getUserId(actionRequest));
			long groupId = PortalUtil.getScopeGroupId(actionRequest);

			org.gcube.portal.tou.model.ToU tou = TOU_UTILS_LIB.getToUGroup(groupId);
			if(tou == null){
				actionRequest.setAttribute(ACCEPTED_TOU_KEY, false);
			}else{

				Long versionAcceptedByUser = TOU_UTILS_LIB.hasAcceptedToUVersion(user.getUsername(), groupId);
				if(versionAcceptedByUser == null){
					actionRequest.setAttribute(ACCEPTED_TOU_KEY, false);
				}else if(versionAcceptedByUser == tou.getVersion()){
					actionRequest.setAttribute(ACCEPTED_TOU_KEY, true);
				}else
					actionRequest.setAttribute(ACCEPTED_TOU_KEY, false);
			}
		} catch (Exception e) {
			throw new PortletException("Unable to perform accept operation [" + e.getMessage() + "]");
		}

	}

	/**
	 * Check if the user has accepted the tou
	 * @param groupName
	 * @param username
	 * @return
	 * @throws IOException
	 * @throws PortletException
	 */
	public static boolean hasAcceptedLatestToU(long groupId, String username) throws IOException, PortletException {

		//business logic here
		logger.info("Called hasAcceptedLatestToU");

		org.gcube.portal.tou.model.ToU tou = null;
		try {
			tou = TOU_UTILS_LIB.getToUGroup(groupId);
			Long versionAcceptedByUser = TOU_UTILS_LIB.hasAcceptedToUVersion(username, groupId);
			if(versionAcceptedByUser == null){
				return false;
			}else if(versionAcceptedByUser == tou.getId()){
				logger.info("Latest ToU for this group already accepted");
				return true;
			}else
				return false;

		} catch (Exception e) {
			if(e instanceof ToUNotFoundException)
				return true;
			else
				throw new PortletException("There was an error while checking if user accepted the ToU");
		}

	}

	/**
	 * On remove user from VRE
	 * @param actionRequest
	 * @return
	 */
	private static String removeUserFromVRE(ActionRequest actionRequest) {

		UserManager userM = new LiferayUserManager();
		try {
			GCubeUser user = USER_MANAGER.getUserById(PortalUtil.getUserId(actionRequest));
			long groupId = PortalUtil.getScopeGroupId(actionRequest);
			String scope = new LiferayGroupManager().getInfrastructureScope(groupId);
			HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
			String username = user.getUsername();
			logger.info("Going to remove user from the current Group: " + groupId + ". Username is: " + username);
			userM.dismissUserFromGroup(groupId, userM.getUserId(username));
			sendUserUnregisteredNotification(username, scope, httpRequest,
					PortalContext.getConfiguration().getGatewayURL(httpRequest),
					PortalContext.getConfiguration().getGatewayName(httpRequest));
			return PortalContext.getConfiguration().getSiteLandingPagePath(httpRequest);
		} catch (Exception e) {
			logger.error("Error while removing user", e);
			return null;
		} 
	}

	/**
	 * 
	 * @param username
	 * @param scope
	 * @param request 
	 * @param portalbasicurl
	 * @param gatewayName
	 */
	public static void sendUserUnregisteredNotification(String username, String scope, HttpServletRequest request, String portalbasicurl, String gatewayName) {
		ArrayList<String> adminEmails = getAdministratorsEmails(scope);
		ArrayList<Recipient> recipients = new ArrayList<>();
		for (String email : adminEmails) {
			recipients.add(new Recipient(email));
		}	
		UserManager um = new LiferayUserManager();
		GCubeUser currUser = null;
		try {
			currUser = um.getUserByUsername(username);
		} catch (Exception e) {
			logger.trace("Error getUserByUsername", e);
		}

		String selectedVRE = scope.substring(scope.lastIndexOf("/")+1, scope.length());		

		EmailTemplateService.send("Unregistration from VRE", 
				new TemplateUserHasLeftVRE(currUser, selectedVRE, gatewayName, portalbasicurl), 
				request, 
				recipients.toArray(new Recipient[recipients.size()]));

	}

	protected static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		long groupId = -1;
		try {
			List<GCubeGroup> allGroups = groupManager.listGroups();
			logger.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				long grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getInfrastructureScope(grId);
				logger.debug("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Map<GCubeUser, List<GCubeRole>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<GCubeUser> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (GCubeUser usr:users) {
			List<GCubeRole> roles = usersAndRoles.get(usr);
			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getRoleName().equals("VO-Admin") || roles.get(i).getRoleName().equals("VRE-Manager")) {
					adminEmailsList.add(usr.getEmail());
					logger.debug("Admin: " + usr.getFullname());
					break;
				}
			}
		}
		return adminEmailsList;
	}
}