package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class PromoteFarmAdministrators
 */
public class PromoteFarmAdministrators extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(PromoteFarmAdministrators.class);
	private static RoleManager RM = new LiferayRoleManager();
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.debug(" This is render method of PromoteFarmAdministrators");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			//long[] farmIds = ParamUtil.getLongValues(httpReq, "farmId");
			String[] farmIds = ParamUtil.getParameterValues(httpReq, Utils.ENCODED_FARM_PARAM);

			GCubeTeam theFarm = null;
			if (farmIds != null && farmIds.length > 0) {
				long selectedFarmId = Utils.unmaskId(farmIds[0]);
				theFarm = RM.getTeam(selectedFarmId);
				request.getPortletSession().setAttribute("theFarm", theFarm);
			} else {
				theFarm = (GCubeTeam) request.getPortletSession().getAttribute("theFarm");
			}
			if (theFarm != null && Utils.isFarmAdmin(Utils.getCurrentUser(request), groupId, theFarm)) { //check is a Farm Admin
				//check folder exists
				Utils.getWSCompanyFolder(Utils.getCurrentUser(request).getUsername(), Utils.getCurrentContext(request), theFarm);
				
				List<GCubeUser> companyAdmins = Utils.getFarmAdminTeamMembers(theCompany, theFarm);
				request.setAttribute("companyAdmins", companyAdmins); //pass to the JSP
				request.setAttribute("theFarm", theFarm); //pass to the JSP
				String imageUrl = Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request);
				request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				//display the view.jsp
				super.render(request, response);				
			}
			else {
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_FARM_ADMIN_PAGE_PATH);
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
			GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.FARM_ADMIN_SITE_ROLE, currentGroupId);
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
