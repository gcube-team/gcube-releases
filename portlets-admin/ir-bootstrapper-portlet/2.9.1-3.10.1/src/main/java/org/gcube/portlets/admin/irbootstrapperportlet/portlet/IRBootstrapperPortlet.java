
package org.gcube.portlets.admin.irbootstrapperportlet.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
//import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;

import javax.portlet.PortletRequestDispatcher;

//import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;

//import com.liferay.portal.kernel.exception.PortalException;
//import com.liferay.portal.kernel.exception.SystemException;
//import com.liferay.portal.kernel.util.WebKeys;
//import com.liferay.portal.model.Group;
//import com.liferay.portal.model.Organization;
//import com.liferay.portal.model.User;
//import com.liferay.portal.service.UserLocalServiceUtil;
//import com.liferay.portal.theme.ThemeDisplay;


/**
 * IRBootstrapperPortlet Portlet Class
 * @author Panagiota Koltsida, NKUA
 */
public class IRBootstrapperPortlet extends GenericPortlet {
	
	/*
	 * (non-Javadoc)
	 * @see javax.portlet.GenericPortlet#init(javax.portlet.PortletConfig)
	 */
	public void init(PortletConfig conf) throws PortletException
	{
		super.init(conf);		
		String adminEmail = conf.getInitParameter("adminEmail");
		IRBootstrapperData.getInstance().setAdminEmail(adminEmail);
	}

	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		ScopeHelper.setContext(request);
		
		//get the username
//		long userid = Long.parseLong(request.getRemoteUser());
//		User user = null;
//	
//		try {
//			user = UserLocalServiceUtil.getUser(userid);
//		} catch (PortalException | SystemException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		String username = user.getScreenName();
//		String sessionID = request.getPortletSession().getId();
//		SessionManager.getInstance().getASLSession(sessionID, username).setScope("/gcube/devNext");	
		
        PortletContext context = getPortletContext();
        PortletRequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/jsp/IRBootstrapperPortlet_view.jsp");
        dispatcher.include(request, response);      
	    
	}


	public void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/IRBootstrapperPortlet_edit.jsp");
        dispatcher.include(request, response);
		
	}

	public void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/IRBootstrapperPortlet_help.jsp");
        dispatcher.include(request, response);
		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
