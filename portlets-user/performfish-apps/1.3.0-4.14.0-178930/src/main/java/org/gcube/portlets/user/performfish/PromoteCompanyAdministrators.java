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
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class PromoteCompanyAdministrators
 */
public class PromoteCompanyAdministrators extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(PromoteCompanyAdministrators.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.debug(" This is render method of PromoteCompanyAdministrators");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theTeam = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			request.setAttribute("theTeam", TeamLocalServiceUtil.getTeam(theTeam.getTeamId()));
			if (theTeam != null && Utils.isCompanyAdmin(Utils.getCurrentUser(request), groupId, theTeam)) { //check is Admin
				//check folder exists
				Utils.getWSCompanyFolder(Utils.getCurrentUser(request).getUsername(), Utils.getCurrentContext(request), theTeam);

				List<CompanyMember> companyAdmins = Utils.getCompanyAdminTeamMembers(theTeam.getTeamId(), theTeam.getGroupId());
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

	public void promoteAdminUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		try {
			long[] userIdsToPromoteAdmins = ParamUtil.getLongValues(actionRequest, "addUserIds");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			LiferayRoleManager rm =	new LiferayRoleManager();
			GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.COMPANY_ADMIN_SITE_ROLE, currentGroupId);
			for (int i = 0; i < userIdsToPromoteAdmins.length; i++) {
				rm.assignRoleToUser(userIdsToPromoteAdmins[i], currentGroupId, adminRole.getRoleId());
				_log.info("Associating Admin role to user with id " + userIdsToPromoteAdmins[i] + " done");
			}			
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}
	}
}
