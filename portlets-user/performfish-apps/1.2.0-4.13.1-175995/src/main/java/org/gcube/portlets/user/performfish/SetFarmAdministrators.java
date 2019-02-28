package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
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

/**
 * Portlet implementation class SetFarmAdministrators
 */
public class SetFarmAdministrators extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(SetFarmAdministrators.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.debug(" This is render method of SetFarmAdministrators");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] companyIds = ParamUtil.getLongValues(httpReq, "companyId");
			long[] farmIds = ParamUtil.getLongValues(httpReq, "farmId");
			if (companyIds.length > 0 && farmIds.length > 0) { //super user mode

				Team theCompany = TeamLocalServiceUtil.getTeam(companyIds[0]);
				Team theFarm = TeamLocalServiceUtil.getTeam(farmIds[0]);
				request.setAttribute("theCompany", theCompany);
				request.setAttribute("theFarm", theFarm);
				List<GCubeUser> availableUsers = Utils.getAvailableFarmMembersToAssociate(theFarm.getTeamId(), theCompany.getTeamId(),  theCompany.getGroupId());
				request.setAttribute("availableUsers", availableUsers); //pass to the JSP

				String imageUrl = Utils.getCompanyLogoURL(theCompany.getName(), groupId, request);
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
			long farmId = ParamUtil.getLong(actionRequest, "farmId");
			Team theFarm = TeamLocalServiceUtil.getTeam(farmId);
			LiferayRoleManager rm =	new LiferayRoleManager();
			GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.FARM_ADMIN_SITE_ROLE, currentGroupId);
			for (int i = 0; i < userIdsToPromoteAdmins.length; i++) {
				rm.assignRoleToUser(userIdsToPromoteAdmins[i], currentGroupId, adminRole.getRoleId());
				_log.info("Associating Farm Admin role to user with id " + userIdsToPromoteAdmins[i] + " done");
				rm.assignTeamToUser(userIdsToPromoteAdmins[i], theFarm.getTeamId()); //add the Admin to the farm
				_log.info("Associating user with id " + userIdsToPromoteAdmins[i] + "  to Farm done");
			}			

		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}
	}
}
