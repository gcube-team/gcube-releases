package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.user.performfish.bean.Association;
import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.bean.Farm;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AddNewCompanyOrAssociation
 */
public class AddNewCompanyOrAssociation extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(AssociateUsersToCompany.class);
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of AddNewCompanyOrAssociation");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			String vreName = GroupLocalServiceUtil.getGroup(groupId).getName();
			request.setAttribute("vreName", vreName);
			List<GCubeTeam> allTeams = getAllTeams(request);
			List<GCubeTeam> availableTeams = new ArrayList<>();
			Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
			List<Company> companies = DBUtil.getCompanies(conn);
			List<Farm> farms = DBUtil.getAllFarms(conn);
			List<Association> associations = DBUtil.getAllAssociations(conn);
			for (GCubeTeam gCubeTeam : allTeams) {
				boolean found = false;
				for (Company c : companies) {
					if (gCubeTeam.getTeamId() == c.getCompanyId()) {
						found = true;
						break;
					}
				}
				for (Farm f : farms) {
					if (gCubeTeam.getTeamId() == f.getFarmId()) {
						found = true;
						break;
					}
				}
				for (Association a : associations) {
					if (gCubeTeam.getTeamId() == a.getAssociationId()) {
						found = true;
						break;
					}
				}
				if (!found)
					availableTeams.add(gCubeTeam);		
			}
			request.setAttribute("availableTeams", availableTeams);
			super.render(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<GCubeTeam> getAllTeams(RenderRequest request) throws Exception {
		long groupId = PortalUtil.getScopeGroupId(request);
		RoleManager rm = new LiferayRoleManager();
		return rm.listTeamsByGroup(groupId);
	}
	/**
	 * 
	 * @param actionRequest
	 * @param actionResponse
	 * @throws Exception
	 */
	public void makeCompanies(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		_log.info("makeCompanies action request ...");
		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		try {
			String[] companyIdsToAdd = ParamUtil.getParameterValues(actionRequest, "selectedTeamIds");
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername", "unknown");
			if (companyIdsToAdd.length > 0) {
				_log.info("associateCompanies writing to DB now ...");
				boolean result = DBUtil.addCompany(conn, companyIdsToAdd, currentUsername);
				if (!result) {
					SessionErrors.add(actionRequest.getPortletSession(), "operation-error");
					actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
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
	/**
	 * 
	 * @param actionRequest
	 * @param actionResponse
	 * @throws Exception
	 */
	public void makeAssociations(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		_log.info("makeAssociations action request ...");
		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		try {
			String[] associationsIdsToAdd = ParamUtil.getParameterValues(actionRequest, "selectedTeamIds");
			String currentUsername = ParamUtil.getString(actionRequest, "currentUsername", "unknown");
			if (associationsIdsToAdd.length > 0) {
				_log.info("associateCompanies writing to DB now ...");
				boolean result = DBUtil.addAssociation(conn, associationsIdsToAdd, currentUsername);
				if (!result) {
					SessionErrors.add(actionRequest.getPortletSession(), "operation-error");
					actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
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