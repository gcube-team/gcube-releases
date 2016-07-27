/**
 * 
 */
package org.cotrix.gcube.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class CotrixPortlet extends GenericPortlet {

	private static final String VIEW_JSP = "/view.jsp";

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
	
		ScopeHelper.setContext(request);

		getPortletContext().getRequestDispatcher(VIEW_JSP).include(request,response);
	}
}