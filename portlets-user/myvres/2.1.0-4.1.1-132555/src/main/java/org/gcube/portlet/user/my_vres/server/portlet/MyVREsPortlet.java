package org.gcube.portlet.user.my_vres.server.portlet;




import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

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
		long userid = Long.parseLong(renderRequest.getRemoteUser());

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		renderRequest.getPortletSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay, PortletSession.APPLICATION_SCOPE);


		User user = null;
		try {
			user = UserLocalServiceUtil.getUser(userid);

		} catch (SystemException e) {			
			e.printStackTrace();
		} catch (PortalException e) {
			e.printStackTrace();
		}


		//get the username
		String username = user.getScreenName();

		//Set the username to the portlet session, so that it can be accessed through the servlet
		renderRequest.getPortletSession().setAttribute("username", username, PortletSession.APPLICATION_SCOPE);
		renderRequest.getPortletSession().setAttribute("user", username, PortletSession.APPLICATION_SCOPE);

		include(viewJSP, renderRequest, renderResponse);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {
		PortletRequestDispatcher portletRequestDispatcher =	getPortletContext().getRequestDispatcher(path);

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
