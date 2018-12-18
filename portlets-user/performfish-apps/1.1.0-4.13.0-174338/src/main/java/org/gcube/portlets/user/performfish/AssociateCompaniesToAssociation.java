package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AssociateCompaniesToAssociation
 */
public class AssociateCompaniesToAssociation extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(AssociateCompaniesToAssociation.class);
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of AssociateCompaniesToAssociation");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] associationIds = ParamUtil.getLongValues(httpReq, "associationId");
			if (associationIds.length > 0) { //super user mode
				String vreName = GroupLocalServiceUtil.getGroup(groupId).getName();
				Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Company> companies = DBUtil.listCompaniesNotAssociatedToAssociations(conn);
				for (Company company : companies) {
					company.setName(TeamLocalServiceUtil.getTeam(company.getCompanyId()).getName());
					company.setImageUrl(Utils.getCompanyLogoURL(company.getName(), groupId, request));			
				}
				
				Team theAssociation = TeamLocalServiceUtil.getTeam(associationIds[0]);
				request.setAttribute("companies", companies);
				request.setAttribute("selectedAssociation", theAssociation);
				request.setAttribute("vreName", vreName);
				request.setAttribute("operationFinished", false); 
			} else {
				request.setAttribute("operationFinished", true); 
				super.render(request, response);
			}

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
	public void associateCompanies(ActionRequest actionRequest,ActionResponse actionResponse) throws Exception {
		String currentUsername = ParamUtil.getString(actionRequest, "currentUsername", "unknown");
		_log.info("associateCompanies action request from " + currentUsername);
		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long selectedAssociationId = ParamUtil.getLong(actionRequest, "selectedAssociationId");
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		try {
			long[] companyIdsToAssociate = ParamUtil.getLongValues(actionRequest, "associateCompanyIds");			
			if (companyIdsToAssociate.length > 0) {
				_log.info("associateCompanyIds writing to DB now ... \nselectedAssociationId="+selectedAssociationId+"\n companyIdsToAssociate="+companyIdsToAssociate.toString());
				boolean result = DBUtil.associateCompaniesToAssociation(conn, selectedAssociationId, companyIdsToAssociate);
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
