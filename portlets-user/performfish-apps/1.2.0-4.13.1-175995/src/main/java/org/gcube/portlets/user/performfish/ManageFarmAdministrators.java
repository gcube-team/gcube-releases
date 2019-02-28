package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
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
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ManageFarmAdministrators
 */
public class ManageFarmAdministrators extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ManageFarmAdministrators.class);
	private static RoleManager RM = new LiferayRoleManager();
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of ManageFarmAdministrators");
		try {
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] farmIds = ParamUtil.getLongValues(httpReq, "farmId");
			GCubeTeam theFarm = null;
			if (farmIds == null || farmIds.length == 0 ) {
				if (Utils.getUserFarmsNumber(request, response, this) < 2) {
					theFarm = Utils.checkBelongingToOneFarmOnly(request, response, this);
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}					
			}
			else { //the farmId is passed via param on the query string
				theFarm = RM.getTeam(farmIds[0]);
				if (Utils.checkBelongsToTeam(PortalUtil.getUserId(request), theFarm.getTeamId(), PortalUtil.getScopeGroupId(request)) ) {//check that the user belong ot the farm
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}
				else {
					PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_BELONGING_ANY_FARM_PAGE_PATH);
					dispatcher.include(request, response);	
				}					
			}
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			if (theCompany != null && theFarm !=null) {
				request.setAttribute("theCompany", theCompany);
				request.setAttribute("theFarm", TeamLocalServiceUtil.getTeam(theFarm.getTeamId()));
				if (theCompany != null && Utils.isFarmAdmin(Utils.getCurrentUser(request), groupId, theCompany)) { //check is Admin
					Boolean isCompanyAdministrator = Utils.isCompanyAdmin(Utils.getCurrentUser(request), groupId, theCompany);
					request.setAttribute("isCompanyAdministrator", isCompanyAdministrator);
					//check folder exists
					Utils.getWSCompanyFolder(Utils.getCurrentUser(request).getUsername(), Utils.getCurrentContext(request), theCompany);

					List<GCubeUser> farmAdmins = Utils.getFarmAdminTeamMembers(theCompany, theFarm);
					request.setAttribute("farmAdmins", farmAdmins); //pass to the JSP

					String imageUrl = Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request);
					request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
					//display the view.jsp
					super.render(request, response);				
				}
				else {
					PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_FARM_ADMIN_PAGE_PATH);
					dispatcher.include(request, response);		
				}
			}
			else {
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.TOO_MANY_FARM_PAGE_PATH);
				dispatcher.include(request, response);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_FARM_ADMIN_PAGE_PATH);
			dispatcher.include(request, response);	
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
			GCubeRole adminRole = new LiferayRoleManager().getRole(PFISHConstants.FARM_ADMIN_SITE_ROLE, currentGroupId);
			for (int i = 0; i < userIdsToRemoveFromCompanyAdmins.length; i++) {
				rm.removeRoleFromUser(userIdsToRemoveFromCompanyAdmins[i], currentGroupId, adminRole.getRoleId());
				_log.info("removing role from user with id " + userIdsToRemoveFromCompanyAdmins[i] + " done");
			}
			actionResponse.setPortletMode(PortletMode.VIEW);
			actionResponse.setWindowState(WindowState.NORMAL);
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}
	}
}