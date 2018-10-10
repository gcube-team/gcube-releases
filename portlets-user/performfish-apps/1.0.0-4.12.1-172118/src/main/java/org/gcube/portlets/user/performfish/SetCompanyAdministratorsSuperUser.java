package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
//TODO check why it gives error when the company administrator is set
/**
 * Portlet implementation class SetCompanyAdministratorsSuperUser
 */
public class SetCompanyAdministratorsSuperUser extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(PromoteCompanyAdministrators.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.debug(" This is render method of SetCompanyAdministratorsSuperUser");

		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] companyIds = ParamUtil.getLongValues(httpReq, "companyId");
			if (companyIds.length > 0) { //super user mode

				Team theTeam = TeamLocalServiceUtil.getTeam(companyIds[0]);
				request.setAttribute("theTeam", theTeam);

				List<GCubeUser> availableUsers = Utils.listVREUsersNotAssociatedToAnyCompany(theTeam.getTeamId(), theTeam.getGroupId());
				request.setAttribute("availableUsers", availableUsers); //pass to the JSP

				String imageUrl = Utils.getCompanyLogoURL(theTeam.getName(), groupId, request);
				request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				request.setAttribute("operationFinished", false); 

				//display the view.jsp
				super.render(request, response);	
			} else {
				request.setAttribute("operationFinished", true); 
				super.render(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void promoteAdminUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		try {
			long[] userIdsToPromoteAdmins = ParamUtil.getLongValues(actionRequest, "addUserIds");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			long companyId = ParamUtil.getLong(actionRequest, "companyId");
			LiferayRoleManager rm =	new LiferayRoleManager();
			GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.COMPANY_ADMIN_SITE_ROLE, currentGroupId);
			for (int i = 0; i < userIdsToPromoteAdmins.length; i++) {
				rm.assignRoleToUser(userIdsToPromoteAdmins[i], currentGroupId, adminRole.getRoleId());
				_log.info("Associating Admin role to user with id " + userIdsToPromoteAdmins[i] + " done");
				rm.assignTeamToUser(userIdsToPromoteAdmins[i], companyId); //add the Admin to the Company
			}			

		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}
	}
//	/**
//	 * 
//	 * @param actionRequest
//	 * @param actionResponse
//	 * @throws Exception
//	 */
//	public void associateCompanyUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
//		try {
//			long[] userIdsToPromoteAdmins = ParamUtil.getLongValues(actionRequest, "addUserIds");
//			System.out.println("userIdsToPromoteAdmins length=" + userIdsToPromoteAdmins.length);
//			String[] usernamesToAssociateToCompany = new String[userIdsToPromoteAdmins.length];
//			_log.debug("converting userIds in usernames");
//			for (int i = 0; i < userIdsToPromoteAdmins.length; i++) {
//				usernamesToAssociateToCompany[i] = UserLocalServiceUtil.getUser(userIdsToPromoteAdmins[i]).getScreenName();
//			}
//			long companyId = ParamUtil.getLong(actionRequest, "companyId");
//			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername");
//			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
//			String context = Utils.getCurrentContext(currentGroupId);
//			_log.info("Super User: " + currentUsername + " has requested the association of some user(s) to the company having id="+companyId + " in context="+context);
//			LiferayRoleManager rm =	new LiferayRoleManager();
//			LiferayUserManager um = new LiferayUserManager();
//			long firstUserId = um.getUserByUsername(usernamesToAssociateToCompany[0]).getUserId();
//			rm.setUserTeams(firstUserId, new long[]{companyId}); //add the admin to the company
//			if (usernamesToAssociateToCompany.length > 1) { //more than one admin was selected
//				String[] remainingUsersToShare = new String[usernamesToAssociateToCompany.length-1];
//				for (int i = 1; i < usernamesToAssociateToCompany.length; i++) {
//					remainingUsersToShare[i-1] = usernamesToAssociateToCompany[i];
//				}
//
//			}
//		}
//		catch (Exception e) {
//			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
//			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
//			e.printStackTrace();
//		}
//
//	}

}
