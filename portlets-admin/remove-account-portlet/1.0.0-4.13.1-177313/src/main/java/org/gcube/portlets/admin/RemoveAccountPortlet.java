package org.gcube.portlets.admin;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class RemoveAccountPortlet
 */
public class RemoveAccountPortlet extends MVCPortlet {

	@ProcessAction(name = "deleteAccount")
	public void deleteAccount(ActionRequest actionRequest,
			ActionResponse response) throws IOException, PortletException {
		User user = null;
		try {
			user = PortalUtil.getUser(actionRequest);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	 	Thread emailManagersThread = new Thread(new RemovedUserAccountThread(
	 			user.getUserId(),
	 			user.getScreenName(), 
	 			user.getFullName(), 
	 			user.getEmailAddress()));
       	emailManagersThread.start();
		
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		response.sendRedirect(themeDisplay.getURLSignOut());

	}
}
