package org.gcube.portlet.user.my_vres.server.portlet;




import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.portal.PortalContext;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * MyVREsPortlet Portlet Class
 * @author Massimiliano Assante - ISTI CNR
 * @version 1.0 Jun 2012
 */
public class MyVREsPortlet extends GenericPortlet {
	
	private static Log _log = LogFactoryUtil.getLog(MyVREsPortlet.class);

	protected String viewJSP;
	public void init() throws PortletException {
		viewJSP = "/WEB-INF/jsp/MyVREs_view.jsp";
	}

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		include(viewJSP, renderRequest, renderResponse);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {
		PortletRequestDispatcher portletRequestDispatcher =	getPortletContext().getRequestDispatcher(path);
		PortalContext.setUserInSession(renderRequest);
		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		}
		else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
	/**
	 * 
	 */
	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}

}
