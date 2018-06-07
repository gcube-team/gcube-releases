package org.gcube.portlets.user.trainingcourse.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;



/**
 * The Class TrainingCoursePortlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 8, 2018
 */
public class TrainingCoursePortlet extends GenericPortlet{
	
	public static final String WEB_INF_JSP_TRAINING_COURSE_PORTLET_VIEW_JSP = "/WEB-INF/jsp/TrainingCoursePortlet_view.jsp";

	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		response.setContentType("text/html");
		ScopeHelper.setContext(request);
    	PortalContext.setUserInSession(request);
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(WEB_INF_JSP_TRAINING_COURSE_PORTLET_VIEW_JSP);
	    dispatcher.include(request, response);		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}

}
