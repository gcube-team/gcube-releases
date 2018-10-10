package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.user.performfish.bean.Farm;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.ResourcePermission;
import com.liferay.portal.model.Role;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.LayoutServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ManageFarms
 */
public class ManageFarms extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ManageCompanies.class);
	private static RoleManager RM =  new LiferayRoleManager();
	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		try {
			long groupId = PortalUtil.getScopeGroupId(request);

			GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			if (theCompany != null && Utils.isCompanyAdmin(Utils.getCurrentUser(request), groupId, theCompany)) { //check is Admin
				Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Farm> farms = DBUtil.listFarmsByCompanyId(conn, theCompany.getTeamId());
				for (Farm farm : farms) {
					GCubeTeam theFarm = RM.getTeam(farm.getFarmId());
					farm.setName(theFarm.getTeamName());
					farm.setDateCreated(theFarm.getCreatedate());
					farm.setImageUrl(Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request));		
					List<GCubeUser> farmAdmins = Utils.getFarmAdminTeamMembers(theCompany, theFarm);
					farm.setAdministrators(farmAdmins);
				}
				request.setAttribute("farms", farms);
				request.setAttribute("company", theCompany);
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
	 * ADD FARM
	 * @param actionRequest
	 * @param actionResponse
	 * @throws Exception
	 */
	public void addFarm(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		long groupId = ParamUtil.getLong(actionRequest, "currentGroupId");
		long companyId = ParamUtil.getLong(actionRequest, "CompanyId");
		String farmName = ParamUtil.getString(actionRequest, "farmName");
		String farmLocation = ParamUtil.getString(actionRequest, "Location", "");		
		String theCreatorFullName = ParamUtil.getString(actionRequest, "theCreatorFullName", "");		
		try {
			Connection dbConnection = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
			if (groupId > 0 && companyId > 0 && !farmName.equals("")) {
				//create the team first
				String companyName = TeamLocalServiceUtil.getTeam(companyId).getName();
				String theFarmName = farmName.trim();
				GCubeTeam newFarm = new LiferayRoleManager().createTeam(groupId, theFarmName, farmName + " is a farm belonging to " + companyName);

				if (newFarm != null) {
					_log.info("addFarm writing to DB now ... farm team created with id="+newFarm.getTeamId());
					boolean resultInsert = DBUtil.addFarm(dbConnection, newFarm.getTeamId(), companyId, theFarmName, farmLocation, theCreatorFullName);
					boolean resultAssignPermissions = assignToFarmTheCompanyPermissions(groupId, companyId, newFarm.getTeamId());
					if (! (resultInsert && resultAssignPermissions) ) {
						renderErrorPage(actionRequest, actionResponse);
					}
				}				
				else 
					renderErrorPage(actionRequest, actionResponse);
			}
			else 
				renderErrorPage(actionRequest, actionResponse);
		} catch (Exception e) {			
			e.printStackTrace();
			renderErrorPage(actionRequest, actionResponse);
		}
	}
	/**
	 * 
	 * @param groupId
	 * @return the layout objects of the pages for the phases enabled for a given company (e.g. phases: Hatchery, Growout)
	 * @throws Exception 
	 */
	private List<Layout> getCompanyEnabledPhasesLayout(long groupId, long companyTeamId) throws Exception {

		List<Layout> toReturn = new ArrayList<>();
		List<Layout> all3phases = new ArrayList<>();
		List<Layout> pages = LayoutServiceUtil.getLayouts(groupId, true);
		for (Layout layout : pages) {
			if (layout.getFriendlyURL().equalsIgnoreCase(PFISHConstants.GROWOUT_PAGE_LAYOUT_FRIENDLY_URL) ||
					layout.getFriendlyURL().equalsIgnoreCase(PFISHConstants.HATCHERY_PAGE_LAYOUT_FRIENDLY_URL) ||
					layout.getFriendlyURL().equalsIgnoreCase(PFISHConstants.PREGROW_PAGE_LAYOUT_FRIENDLY_URL)) {
				all3phases.add(layout);
			}			
		}
		final long defaultCompanyId = PortalUtil.getDefaultCompanyId();
		Role teamRoleCompany = RoleLocalServiceUtil.getTeamRole(defaultCompanyId, companyTeamId);
		List<ResourcePermission> rPermissions = ResourcePermissionLocalServiceUtil.getRoleResourcePermissions(teamRoleCompany.getRoleId());

		for (Layout layout : all3phases) {
			for (ResourcePermission rp : rPermissions) {
				if (layout.getPrimaryKey() == Long.parseLong(rp.getPrimKey()) && rp.getActionIds() == 1) { //tha actiondId has to be 1
					toReturn.add(layout);
				}
			}
		}	
		return toReturn;
	}

	/**
	 * 
	 * @param groupId
	 * @param companyTeamId
	 * @param farmTeamId
	 * @return
	 */
	private boolean assignToFarmTheCompanyPermissions(long groupId, long companyTeamId, long farmTeamId) {
		try {
			final long defaultCompanyId = PortalUtil.getDefaultCompanyId();
			List<Layout> layoutCompanyPhases = getCompanyEnabledPhasesLayout(groupId, companyTeamId);
			Role teamRoleFarm = RoleLocalServiceUtil.getTeamRole(defaultCompanyId, farmTeamId);

			for (Layout phase : layoutCompanyPhases) {
				_log.info("setting Resource Permissions for farm with id = " + farmTeamId);
				ResourcePermissionLocalServiceUtil.setResourcePermissions(
						defaultCompanyId, Layout.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(phase.getPrimaryKey()), teamRoleFarm.getRoleId(),
						new String[] { ActionKeys.VIEW });
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void renderErrorPage(ActionRequest actionRequest, ActionResponse actionResponse) {
		SessionErrors.add(actionRequest.getPortletSession(),"operation-error");
		actionResponse.setRenderParameter("mvcPath", PFISHConstants.OPERATION_ERROR_PATH);
	}
}