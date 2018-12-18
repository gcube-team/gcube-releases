package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.bean.Farm;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AssociationDashboard
 */
public class AssociationDashboard extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(AssociationDashboard.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theAsso = Utils.checkBelongingToOneAssociationOnly(request, response, this);
			request.setAttribute("theAssociation", theAsso);

			if (theAsso != null) { 
				Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				_log.debug("Association name =" + theAsso.getTeamName());
				String imageUrl = Utils.getCompanyLogoURL(theAsso.getTeamName(), groupId, request);
				request.setAttribute("associationLogoURL", imageUrl); //pass to logo URL to the JSP
				
				_log.debug("Featching companies and farms =" + theAsso.getTeamName());
				LinkedHashMap<Company, List<Farm>> associationCompanies = new LinkedHashMap<>();
				List<Long> companIds = DBUtil.getCompaniesIdsByAssociation(conn, theAsso.getTeamId());
				for (Long companId : companIds) {
					Team theCompany = TeamLocalServiceUtil.getTeam(companId);
					Company toPut = new Company(companId, theAsso.getTeamId(), false);
					toPut.setName(theCompany.getName());
					toPut.setImageUrl(Utils.getCompanyLogoURL(theCompany.getName(), groupId, request));	
					List<Farm> companyFarms = DBUtil.listFarmsByCompanyId(conn, companId);
					for (Farm farm : companyFarms) {
						String farmName = TeamLocalServiceUtil.getTeam(farm.getFarmId()).getName();
						farm.setName(farmName);
					}
					associationCompanies.put(toPut, companyFarms);
					request.setAttribute("associationCompanies", associationCompanies);
				}
				//display the view.jsp
				super.render(request, response);				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


}
