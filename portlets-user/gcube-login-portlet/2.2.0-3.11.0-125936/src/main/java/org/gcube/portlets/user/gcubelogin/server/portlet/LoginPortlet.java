package org.gcube.portlets.user.gcubelogin.server.portlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.application.framework.core.session.SessionManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
/**
 * <a href="JSPPortlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class LoginPortlet extends GenericPortlet {

	public static final String GATEWAY_NAME = "GATEWAY_NAME";
	
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






	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		long userid = Long.parseLong(renderRequest.getRemoteUser());

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		renderRequest.getPortletSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay, PortletSession.APPLICATION_SCOPE);

		
		User user = null;
		try {
			user = UserLocalServiceUtil.getUser(userid);
			
			Group currentGroup = GroupLocalServiceUtil.getGroup(themeDisplay.getLayout().getGroup().getGroupId());
			if (currentGroup.getName().equalsIgnoreCase(getDefaultCommunityName(renderRequest)) ) {
				SessionManager.getInstance().getASLSession(renderRequest.getPortletSession().getId()
						, user.getScreenName()).invalidate();
			}
			
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
			_log.error(path + " is not a valid include");
		}
		else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
	
	/**
	 * The Default Community is a community where all portal user belong to
	 * @return the default community URL
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private String getDefaultCommunityName(RenderRequest renderRequest) throws PortalException, SystemException {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String comName = "";

		try {
			String propertyfile = this.getPortletContext().getRealPath("")+"/config/resources.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			comName = props.getProperty("defaultcommunity");
			
			//set the gateway label in the session
			String gatewayLabel = props.getProperty("gatewaylabel");
			renderRequest.getPortletSession().setAttribute(GATEWAY_NAME, gatewayLabel, PortletSession.APPLICATION_SCOPE);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.info("/config/resources.properties not found, Returning \"Data e-Infrastructure gateway\" as default Community");
			}
		
		String toReturn = comName;	
		return toReturn;
	}


	protected String editJSP;
	protected String helpJSP;
	protected String viewJSP;

	private static Log _log = LogFactoryUtil.getLog(LoginPortlet.class);

}