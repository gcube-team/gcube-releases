
package org.gcube.portal.staging.server;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * PortalStaging Portlet Class
 * @author Massimiliano Assante, ISTI-CNR
 */
public class PortalStaging extends GenericPortlet {
	private static Log _log = LogFactoryUtil.getLog(PortalStaging.class);
	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		PortletURL submitUrl = response.createActionURL();
		
		request.setAttribute( "submitUrl", submitUrl);
		
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/html/gcube-patch/PortalStaging_view.jsp");
	    dispatcher.include(request, response);
		
	}


	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
		 // get the values submitted with the form

	    String value = request.getParameter("install");
	    _log.info("Starting gCube Portal staging procedure [" +  value + "]");
	    try {
			CommunityCreator.getInstance().createDefaultCommunity(request);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	    _log.info("gCube Portal staging procedure step 1st done, redirecting to 2nd step ... ");
		request.setAttribute( "forward", true);
	}

}
