package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.Collections;
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

import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.comparators.UserComparator;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ManageFarmUsers
 */
public class ManageFarmUsers extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ManageFarmUsers.class);
	private static RoleManager RM = new LiferayRoleManager();
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of ManageFarmUsers");
		try {
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			String[] farmIds = ParamUtil.getParameterValues(httpReq, Utils.ENCODED_FARM_PARAM);
			long[] farmIds2 = ParamUtil.getLongValues(httpReq, "farmId");
			GCubeTeam theFarm = null;
			if ((farmIds == null || farmIds.length == 0) && (farmIds2 == null || farmIds2.length == 0)) {
				if (Utils.getUserFarmsNumber(request, response, this) < 2) {
					theFarm = Utils.checkBelongingToOneFarmOnly(request, response, this);
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}					
			}
			else { //the farmId (encrypted) or farmId2 (not encrypted) is passed via param on the query string
				long selectedFarmId = 0;
				if (farmIds != null && farmIds.length > 0) {
					selectedFarmId = Utils.unmaskId(farmIds[0]);
				} else 
					selectedFarmId = farmIds2[0];
				theFarm = RM.getTeam(selectedFarmId);
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
				if (theFarm != null && Utils.isFarmAdmin(Utils.getCurrentUser(request), groupId, theFarm)) { //check is Admin
					request.setAttribute("theFarm", TeamLocalServiceUtil.getTeam(theFarm.getTeamId()));
					//check folder exists
					Utils.getWSCompanyFolder(Utils.getCurrentUser(request).getUsername(), Utils.getCurrentContext(request), theCompany);
					List<GCubeUser> farmMembers = Utils.getRegularFarmMembers(theFarm.getTeamId(), theFarm.getGroupId());
					for (GCubeUser member : farmMembers) { //anonymising email
						int n = member.getEmail().indexOf("@");
						member.setEmail("********"+member.getEmail().substring(n));
					}
					Collections.sort(farmMembers, new UserComparator());
					request.setAttribute("farmMembers", farmMembers); //pass to the JSP
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
			String[] usernamesToRemoveFromCompany = ParamUtil.getParameterValues(actionRequest, "removeUserIds");
			long farmId = ParamUtil.getLong(actionRequest, "farmId");
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			String context = Utils.getCurrentContext(currentGroupId);
			_log.info(currentUsername + " has requested the deletion of some user(s) from farm having id="+farmId + " in context="+context);
			LiferayRoleManager rm =	new LiferayRoleManager();
			LiferayUserManager um = new LiferayUserManager();
			GCubeTeam team = rm.getTeam(farmId);
			for (int i = 0; i < usernamesToRemoveFromCompany.length; i++) {
				_log.info("removing user from farm =>"+usernamesToRemoveFromCompany[i]);
				long userId = um.getUserByUsername(usernamesToRemoveFromCompany[i]).getUserId();
				TeamLocalServiceUtil.deleteUserTeam(userId, team.getTeamId());
				_log.info("removing user from farm Done");
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
