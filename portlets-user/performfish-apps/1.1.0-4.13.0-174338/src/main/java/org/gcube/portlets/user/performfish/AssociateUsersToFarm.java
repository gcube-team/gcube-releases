package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.comparators.UserComparator;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AssociateUsersToFarm
 */
public class AssociateUsersToFarm extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(AssociateUsersToFarm.class);
	private static RoleManager RM = new LiferayRoleManager();
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of AssociateUsersToFarm");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] farmIds = ParamUtil.getLongValues(httpReq, "farmId");

			GCubeTeam theFarm = null;
			if (farmIds != null && farmIds.length > 0) {
				theFarm = RM.getTeam(farmIds[0]);
				request.getPortletSession().setAttribute("theFarm", theFarm);
			} else {
				theFarm = (GCubeTeam) request.getPortletSession().getAttribute("theFarm");
			}
			request.setAttribute("theTeam", TeamLocalServiceUtil.getTeam(theFarm.getTeamId()));
			if (theFarm == null || !Utils.isFarmAdmin(Utils.getCurrentUser(request), groupId, theFarm)) { //check is Admin
				_log.error("Some error occurred");
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_FARM_ADMIN_PAGE_PATH);
				dispatcher.include(request, response);		
			} else {
				List<GCubeUser> nonFarmMembers = Utils.getAvailableFarmMembersToAssociate(theFarm.getTeamId(), theCompany.getTeamId(), theCompany.getGroupId());
				Collections.sort(nonFarmMembers, new UserComparator());
				request.setAttribute("nonFarmMembers", nonFarmMembers); //pass to the JSP
				String imageUrl = Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request);
				request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				super.render(request, response);
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
	//TODO: yiou have to manage the workspace folder still ...
	public void associateFarmUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		try {
			String[] usernamesToAssociateToFarm = ParamUtil.getParameterValues(actionRequest, "addUserIds");
			long teamId = ParamUtil.getLong(actionRequest, "teamId");
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			String context = Utils.getCurrentContext(currentGroupId);
			_log.info(currentUsername + " has requested the association of some user(s) to the farm having id="+teamId + " in context="+context);
			LiferayRoleManager rm =	new LiferayRoleManager();
			LiferayUserManager um = new LiferayUserManager();

			for (int i = 0; i < usernamesToAssociateToFarm.length; i++) {
				_log.info("sharing OK associating user to team =>"+usernamesToAssociateToFarm[i]);
				long userId = um.getUserByUsername(usernamesToAssociateToFarm[i]).getUserId();
				rm.assignTeamToUser(userId, teamId);
				_log.info("added user to farm Done");
				CacheRegistryUtil.clear();
				MultiVMPoolUtil.clear();
				WebCachePoolUtil.clear();				
			}
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}

	}

}
