package org.gcube.application.aquamaps.aquamapsspeciesview;



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
 * <a href="JSPPortlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class AquaMapsSpeciesView extends GenericPortlet {

	public void init() throws PortletException {
		editJSP = getInitParameter("edit-jsp");
		helpJSP = getInitParameter("help-jsp");
		viewJSP = getInitParameter("view-jsp");
	}

	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
	throws IOException, PortletException {

		String jspPage = renderRequest.getParameter("jspPage");

		if (jspPage != null) {
			include(jspPage, renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	public void doEdit(
			RenderRequest renderRequest, RenderResponse renderResponse)
	throws IOException, PortletException {

		if (renderRequest.getPreferences() == null) {
			super.doEdit(renderRequest, renderResponse);
		}
		else {
			include(editJSP, renderRequest, renderResponse);
		}
	}

	public void doHelp(
			RenderRequest renderRequest, RenderResponse renderResponse)
	throws IOException, PortletException {

		include(helpJSP, renderRequest, renderResponse);
	}


	private static final String LIFERAY_USER_ID_KEY = "liferay.user.id";






	
	public void doView(RenderRequest request, RenderResponse response)    throws PortletException, IOException {
	    ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(viewJSP);
	    dispatcher.include(request, response);
	}
	
	
	public static final int LIFERAY_COMMUNITY_ID = 2;

	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
	throws IOException, PortletException {
	}

	protected void include(
			String path, RenderRequest renderRequest,
			RenderResponse renderResponse)
	throws IOException, PortletException {

		PortletRequestDispatcher portletRequestDispatcher =
			getPortletContext().getRequestDispatcher(path);

		if (portletRequestDispatcher == null) {
//			_log.error(path + " is not a valid include");
		}
		else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}

	
	protected String editJSP;
	protected String helpJSP;
	protected String viewJSP;

//	private static Log _log = LogFactoryUtil.getLog(AquaMapsSpeciesView.class);

}