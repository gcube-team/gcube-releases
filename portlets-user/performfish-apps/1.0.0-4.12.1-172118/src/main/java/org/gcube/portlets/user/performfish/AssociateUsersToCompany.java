package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.portlets.user.performfish.bean.CompanyMember;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.comparators.CompanyMemberComparator;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AssociateUsersToCompany
 */
public class AssociateUsersToCompany extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(AssociateUsersToCompany.class);
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of AssociateUsersToCompany");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theTeam = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			request.setAttribute("theTeam", TeamLocalServiceUtil.getTeam(theTeam.getTeamId()));
			if (theTeam == null || !Utils.isCompanyAdmin(Utils.getCurrentUser(request), groupId, theTeam)) { //check is Admin
				_log.error("Some error occurred");
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_ADMIN_PAGE_PATH);
				dispatcher.include(request, response);		
			} else {
				List<CompanyMember> companyMembers = Utils.getRegularCompanyMembers(theTeam.getTeamId(), theTeam.getGroupId());
				Collections.sort(companyMembers, new CompanyMemberComparator());
				request.setAttribute("companyMembers", companyMembers); //pass to the JSP
				String imageUrl = Utils.getCompanyLogoURL(theTeam.getTeamName(), groupId, request);
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
	public void associateCompanyUsers(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		try {
			String[] usernamesToAssociateToCompany = ParamUtil.getParameterValues(actionRequest, "addUserIds");
			long teamId = ParamUtil.getLong(actionRequest, "teamId");
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername");
			long currentGroupId = ParamUtil.getLong(actionRequest, "currentGroupId");
			String context = Utils.getCurrentContext(currentGroupId);
			_log.info(currentUsername + " has requested the association of some user(s) to the company having id="+teamId + " in context="+context);
			LiferayRoleManager rm =	new LiferayRoleManager();
			LiferayUserManager um = new LiferayUserManager();
			GCubeTeam team = rm.getTeam(teamId);
			WorkspaceFolder wsFolder = Utils.getWSCompanyFolder(currentUsername, context, team);
			if (wsFolder.isShared()) {
				WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) wsFolder;
				List<String> usersToShareWith = Arrays.asList(usernamesToAssociateToCompany);
				_log.info("Sharing with users =>"+usersToShareWith.toString());
				sharedFolder.share(usersToShareWith);
				sharedFolder.setACL(usersToShareWith, ACLType.WRITE_ALL);
				for (int i = 0; i < usernamesToAssociateToCompany.length; i++) {
					_log.info("sharing OK associateing user to team =>"+usernamesToAssociateToCompany[i]);
					long userId = um.getUserByUsername(usernamesToAssociateToCompany[i]).getUserId();
					List<Team> currentTeams = TeamLocalServiceUtil.getUserTeams(userId, currentGroupId);
					long[] teamIds = null;
					if (currentTeams != null && !currentTeams.isEmpty()) {
						teamIds = new long[currentTeams.size()+1];
						int j = 0;
						for (Team t : currentTeams) {
							teamIds[j] = t.getTeamId();
							j++;
						}
						teamIds[j] = teamId;
						TeamLocalServiceUtil.setUserTeams(userId, teamIds);
					} else {
						teamIds = new long[1];
						teamIds[0] = teamId;
						TeamLocalServiceUtil.setUserTeams(userId, teamIds);
					}
					CacheRegistryUtil.clear();
					MultiVMPoolUtil.clear();
					WebCachePoolUtil.clear();
					_log.info("added user to team Done");
				}
			}
			else {
				SessionErrors.add(actionRequest.getPortletSession(), "operation-error");
				actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			}
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
			e.printStackTrace();
		}

	}
}
