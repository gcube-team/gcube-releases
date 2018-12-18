package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ManageCompanies
 */
public class ManageCompanies extends MVCPortlet {

	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ManageCompanies.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of ManageCompanies");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			String vreName = GroupLocalServiceUtil.getGroup(groupId).getName();
			Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
			List<Company> companies = DBUtil.getCompanies(conn);
			for (Company company : companies) {
				company.setName(TeamLocalServiceUtil.getTeam(company.getCompanyId()).getName());
				company.setImageUrl(Utils.getCompanyLogoURL(company.getName(), groupId, request));			
			}
			request.setAttribute("companies", companies);
			request.setAttribute("vreName", vreName);
			
		
			super.render(request, response);				
			
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
	public void removeCompanies(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		String currentUsername = ParamUtil.getString(actionRequest, "currentUsername", "unknown");
		_log.info("removeCompanies action request from " + currentUsername);
		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		try {
			String[] companyIdsToRemove = ParamUtil.getParameterValues(actionRequest, "removeCompanyIds");			
			if (companyIdsToRemove.length > 0) {
				_log.info("removeCompanyIds writing to DB now ...");
				boolean result = DBUtil.removeCompany(conn, companyIdsToRemove);
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
	public void manageCompanyFarms(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		String currentUsername = ParamUtil.getString(actionRequest, "currentUsername", "unknown");
		_log.info("manageCompanyFarms action request from " + currentUsername);
		String[] companyIdsToManage = ParamUtil.getParameterValues(actionRequest, "companyIds");	
		if (companyIdsToManage.length == 1) {
			System.out.println("tutto ok");
		}
		else {
			SessionErrors.add(actionRequest.getPortletSession(), "operation-error");
			actionResponse.setRenderParameter("mvcPath", "/html/error_pages/operation-error.jsp");
		}
	}
	
	
}
