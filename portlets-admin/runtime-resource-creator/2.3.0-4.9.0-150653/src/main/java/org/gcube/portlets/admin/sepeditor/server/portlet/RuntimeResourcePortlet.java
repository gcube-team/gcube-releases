
package org.gcube.portlets.admin.sepeditor.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
/**
 * 
 * @author Massimiliano Assante - ISTI-CNR
 * @version 1.0 Feb 9th 2012
 */
public class RuntimeResourcePortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		try {
			ScopeHelper.setContext(request);
		}
		catch (Exception e) {
			System.out.println("Exception while setting portlet context, are you not logged?");
		}
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/RuntimePortlet_view.jsp");
	    dispatcher.include(request, response);		
	}

	/**
	 * 
	 */
	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}

}
