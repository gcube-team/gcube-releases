
package org.gcube.portlets.user.gcubeloggedin.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;

/**
 * LoggedinPortlet Portlet Class
 * @author massi
 */
public class LoggedinPortlet extends GenericPortlet {
	
	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		
		response.setContentType("text/html");

		ScopeHelper.setContext(request);

		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/LoggedinPortlet_view.jsp");
		dispatcher.include(request, response);

	}

	/**
	 * 
	 * @param currentGroup
	 * @return true id the organization is a VO
	 * @throws SystemException .
	 * @throws PortalException .
	 */
	private boolean isVO(Organization currentOrg) throws PortalException, SystemException {		
		return (currentOrg.getParentOrganization().getParentOrganization() == null); 
	}

	
	public void processAction(ActionRequest request, ActionResponse response)
	throws PortletException, IOException {

	}

}
