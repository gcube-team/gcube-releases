package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.user.performfish.bean.CompanyMember;
import org.gcube.portlets.user.performfish.bean.Farm;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ManageCompanyAdmins
 */
public class ManageCompanyAdmins extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ManageCompanyAdmins.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theTeam = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			request.setAttribute("theTeam", TeamLocalServiceUtil.getTeam(theTeam.getTeamId()));
			RoleManager rm = new LiferayRoleManager();
			if (theTeam != null && Utils.isCompanyAdmin(Utils.getCurrentUser(request), groupId, theTeam)) { //check is Admin
				//check folder exists
				Utils.getWSCompanyFolder(Utils.getCurrentUser(request).getUsername(), Utils.getCurrentContext(request), theTeam);

				List<CompanyMember> companyAdmins = Utils.getCompanyAdminTeamMembers(theTeam.getTeamId(), theTeam.getGroupId());
				for (CompanyMember member : companyAdmins) { //anonymising email
					member.setEmail(member.getUser().getEmail());
					List<Farm> theFarms = Utils.getFarmsByUserId(member.getUserId(), theTeam.getTeamId(), groupId);
					if (theFarms != null && !theFarms.isEmpty()) {
						GCubeTeam theFarmTeam = rm.getTeam(theFarms.get(0).getFarmId());
						member.setAssociatedFarms(theFarms);
						member.setAdmin(Utils.isFarmAdmin(member.getUser(), groupId, theFarmTeam));
					}
				}
				
				request.setAttribute("companyAdmins", companyAdmins); //pass to the JSP

				String imageUrl = Utils.getCompanyLogoURL(theTeam.getTeamName(), groupId, request);
				request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				//display the view.jsp
				super.render(request, response);				
			}
			else {
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_ADMIN_PAGE_PATH);
				dispatcher.include(request, response);		
			}

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 * @param actionRequest
	 * @param actionResponse
	 * @throws Exception
	 */
	public void removeAdministratorRoleFromUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		try {
			long[] userIdsToRemoveFromCompanyAdmins = ParamUtil.getLongValues(actionRequest, "removeUserIds");
			long teamId = ParamUtil.getLong(actionRequest, "teamId");
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			_log.info(currentUsername + " has requested the deletion of some user(s) from company administrator having id="+teamId );
			LiferayRoleManager rm =	new LiferayRoleManager();
			GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.COMPANY_ADMIN_SITE_ROLE, currentGroupId);
			for (int i = 0; i < userIdsToRemoveFromCompanyAdmins.length; i++) {
				rm.removeRoleFromUser(userIdsToRemoveFromCompanyAdmins[i], currentGroupId, adminRole.getRoleId());
				_log.info("removing role from user with id " + userIdsToRemoveFromCompanyAdmins[i] + " done");
			}			
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param actionRequest
	 * @param actionResponse
	 * @throws Exception
	 */
	public void removeFarmUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		try {
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			long companyUserId = ParamUtil.getLong(actionRequest, "companyUserId");
			long companyId = ParamUtil.getLong(actionRequest, "companyId");
			String context = Utils.getCurrentContext(currentGroupId);
			_log.info(currentUsername + " has requested the deletion of userid " + companyUserId + " from farms in context="+context);
			List<Farm> theUserFarms = Utils.getFarmsByUserId(companyUserId, companyId, currentGroupId);
			for (Farm farm : theUserFarms) {
				removeFromFarm(farm.getFarmId(), currentGroupId, companyUserId);
			}
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}
	}
	
	private boolean removeFromFarm(long farmId, long currentGroupId, long companyUserId) throws Exception {
		Team team = TeamLocalServiceUtil.getTeam(farmId);//check that the farm is real
		TeamLocalServiceUtil.deleteUserTeam(companyUserId, team.getTeamId());
		_log.info("removing user from farm Done");
		LiferayRoleManager rm =	new LiferayRoleManager();
		GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.FARM_ADMIN_SITE_ROLE, currentGroupId);
		_log.info("removing role from user with id " + companyUserId);
		return rm.removeRoleFromUser(companyUserId, currentGroupId, adminRole.getRoleId());
	}


}
