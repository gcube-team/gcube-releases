
package org.gcube.portlets.user.searchportlet.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.portlet.PortletRequestDispatcher;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * SearchPortlet Portlet Class
 * 
 * @author Panagiota Koltsida, NKUA
 */
public class AdvancedSearchPortlet extends GenericPortlet {
	
	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp";

	/**
	 * JSP file name to be rendered on the view mode
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "/SearchPortlet_view.jsp";
	
	@Override
	public void init() throws PortletException {
		super.init();
		
		System.out.println("AdvancedSearchPortlet.Init");
		
		ClassLoader cl =  ClassLoader.getSystemClassLoader();
		 
        URL[] urls = ((URLClassLoader)cl).getURLs();
        
        try {
			Class.forName("org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        try {
			Class.forName("org.gcube.search.SearchClient");
			System.out.println("class org.gcube.search.SearchClient found");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("##########################");
        
	};

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
		String username = (String)request.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
		ASLSession session = SessionManager.getInstance().getASLSession(request.getPortletSession().getId(), username);
		session.setAttribute("searchURL", response.createRenderURL().toString());
		
		try
		{
			response.setContentType("text/html;charset=UTF-8");
			// the regular search form is displayed.
			getPortletContext().getRequestDispatcher(VIEW_JSP).include(request, response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
		


	public void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/SearchPortlet_edit.jsp");
        dispatcher.include(request, response);
		
	}

	public void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/SearchPortlet_help.jsp");
        dispatcher.include(request, response);
		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
