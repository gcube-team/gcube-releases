package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.user.performfish.bean.Association;
import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ManageAssociations
 */
public class ManageAssociations extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ManageCompanies.class);

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of ManageAssociations");
		try {
			long groupId = PortalUtil.getScopeGroupId(request);
			String vreName = GroupLocalServiceUtil.getGroup(groupId).getName();
			Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
			List<Association> associations = DBUtil.getAllAssociations(conn);
			LinkedHashMap<Association, List<Company>> associationCompanies = new LinkedHashMap<>();
			
			for (Association ass : associations) {
				Team theTeam = TeamLocalServiceUtil.getTeam(ass.getAssociationId());
				ass.setShortName(theTeam.getName());
				ass.setFullName(theTeam.getDescription());
				ass.setImageUrl(Utils.getCompanyLogoURL(ass.getShortName(), groupId, request));	
				List<Long> companIds = DBUtil.getCompaniesIdsByAssociation(conn, ass.getAssociationId());
				List<Company> children = new ArrayList<>();
				for (Long companId : companIds) {
					Team theCompany = TeamLocalServiceUtil.getTeam(companId);
					Company toAdd = new Company(companId, ass.getAssociationId(), false);
					toAdd.setName(theCompany.getName());
					toAdd.setImageUrl(Utils.getCompanyLogoURL(theCompany.getName(), groupId, request));	
					children.add(toAdd);
				}
				associationCompanies.put(ass, children);
			}
			request.setAttribute("associations", associations);
			request.setAttribute("associationCompanies", associationCompanies);
			request.setAttribute("vreName", vreName);
			
		
			super.render(request, response);				
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
