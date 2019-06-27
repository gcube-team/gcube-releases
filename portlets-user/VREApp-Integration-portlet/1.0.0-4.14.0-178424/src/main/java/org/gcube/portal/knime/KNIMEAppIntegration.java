package org.gcube.portal.knime;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class KNIMEAppIntegration
 */
public class KNIMEAppIntegration extends MVCPortlet {

    private static Log _log = LogFactoryUtil.getLog(KNIMEAppIntegration.class);
    
	public void doView(RenderRequest request, RenderResponse response)throws PortletException, IOException {	
		response.setContentType("text/html");
		
		String username = getCurrentUsername(request);
		String context = getCurrentContext(request);
		String token = getCurrentUserToken(context, username);
		
		_log.debug("KNIMEAppIntegration doView " + username + " - " + context);
		
		if (token != null){
			request.setAttribute("securityToken", token);
		}
		
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/html/knimeappintegration/view.jsp");
	    dispatcher.include(request, response);		
	}
	
	
	private static String getCurrentUsername(RenderRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return UserLocalServiceUtil.getUser(userId).getScreenName();
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}
	private static String getCurrentContext(RenderRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static String getCurrentContext(long groupId) {
		try {
			PortalContext pContext = PortalContext.getConfiguration(); 
			return pContext.getCurrentScope(""+groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * <p>
	 * Returns the gCube authorisation token for the given user 
	 * </p>	
	 * @param scope infrastrucure context (scope)
	 * @param username the GCubeUser username @see {@link GCubeUser}
	 * @return the Token for the user in the context, or <code>null</code> if a token for this user could not be found
	 */
	private static String getCurrentUserToken(String context, String username) {
		String userToken = null;

		try {
			ScopeProvider.instance.set(context);
			userToken = authorizationService().resolveTokenByUserAndContext(username, context);
			SecurityTokenProvider.instance.set(userToken);
		} 
		catch (Exception e) {
			_log.error("Error while trying to generate token for user " + username + "in scope " + context);
			e.printStackTrace();
			return null;
		}

		return userToken;
	}


}
