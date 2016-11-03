package org.gcube.portlets.admin.manageusers.server.portlet;




import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * ManageVREUsersPortlet Portlet Class
 * @author Massimiliano Assante - ISTI CNR
 * @version 1.0 Feb 2014
 */
public class ManageVREUsersPortlet extends GenericPortlet {
	
	private static Log _log = LogFactoryUtil.getLog(ManageVREUsersPortlet.class);

	
	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		ScopeHelper.setContext(request);
		
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/manageusers_view.jsp");
	    dispatcher.include(request, response);
		
	}
	
		/**
	 * 
	 */
	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}

}
