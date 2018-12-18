/**
 * 
 */
package org.gcube.portlets.admin.dataminermanagerdeployer.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.dataminermanagerdeployer.server.util.ServiceCredentials;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.Constants;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.exception.ServiceException;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	public static ServiceCredentials getServiceCredentials(HttpServletRequest httpServletRequest, String token)
			throws ServiceException {

		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String groupId = null;
		String groupName = null;

		if (Constants.DEBUG_MODE) {
			logger.debug("No credential found in session, use test user!");
			/*
			 * InfoLocale infoLocale = getInfoLocale(httpServletRequest, null);
			 * Locale locale = new Locale(infoLocale.getLanguage());
			 * 
			 * ResourceBundle messages = ResourceBundle.getBundle(
			 * StatAlgoImporterServiceMessagesConstants.TDGWTServiceMessages,
			 * locale);
			 */
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;

			logger.debug("Set SecurityToken: " + token);
			SecurityTokenProvider.instance.set(token);
			logger.debug("Set ScopeProvider: " + scope);
			ScopeProvider.instance.set(scope);

			sCredentials = new ServiceCredentials(userName, scope, token);

		} else {
			logger.debug("Retrieving credential in session!");
			PortalContext pContext = PortalContext.getConfiguration();

			logger.debug("Set SecurityToken: " + token);
			SecurityTokenProvider.instance.set(token);

			AuthorizationEntry entry;
			try {
				entry = authorizationService().get(token);
			} catch (Exception e) {
				throw new ServiceException("AuthorizationEntry not found: " + e.getLocalizedMessage(), e);
			}

			scope = entry.getContext();
			logger.debug("Set ScopeProvider: " + scope);
			ScopeProvider.instance.set(scope);

			if (scope == null || scope.isEmpty()) {
				String error = "Error retrieving scope: " + scope;
				logger.error(error);
				throw new ServiceException(error);
			}

			GCubeUser gCubeUser = pContext.getCurrentUser(httpServletRequest);

			if (gCubeUser == null) {
				String error = "Error retrieving gCubeUser in scope " + scope + ": " + gCubeUser;
				logger.error(error);
				throw new ServiceException(error);
			}

			userName = gCubeUser.getUsername();

			if (userName == null || userName.isEmpty()) {
				String error = "Error retrieving username in scope " + scope + ": " + userName;
				logger.error(error);
				throw new ServiceException(error);
			}

			token = pContext.getCurrentUserToken(scope, userName);

			if (token == null || token.isEmpty()) {
				String error = "Error retrieving token for " + userName + " in " + scope + ": " + token;
				logger.error(error);
				throw new ServiceException(error);
			}

			String name = gCubeUser.getFirstName();
			String lastName = gCubeUser.getLastName();
			String fullName = gCubeUser.getFullname();

			String userAvatarURL = gCubeUser.getUserAvatarURL();

			String email = gCubeUser.getEmail();

			//groupId = String.valueOf(pContext.getCurrentGroupId(httpServletRequest));

			//groupName = pContext.getCurrentGroupName(httpServletRequest);

			sCredentials = new ServiceCredentials(userName, fullName, name, lastName, email, scope, groupId, groupName,
					userAvatarURL, token);
		}

		logger.debug("ServiceCredentials: " + sCredentials);

		return sCredentials;
	}

}
