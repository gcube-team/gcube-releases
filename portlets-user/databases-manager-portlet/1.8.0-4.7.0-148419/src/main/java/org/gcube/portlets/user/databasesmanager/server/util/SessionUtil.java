package org.gcube.portlets.user.databasesmanager.server.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.databasesmanager.shared.Constants;
import org.gcube.portlets.user.databasesmanager.shared.exception.ServiceException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	/**
	 * 
	 * @param httpServletRequest
	 *            http servlet request
	 * @return ServiceCredentials
	 * @throws ServiceException ServiceException
	 */
	public static ServiceCredentials getServiceCredentials(HttpServletRequest httpServletRequest)
			throws ServiceException {
		return getServiceCredentials(httpServletRequest, null);
	}

	/**
	 * 
	 * @param httpServletRequest
	 *            http servlet request
	 * @param scopeGroupId
	 *            scope
	 * @return ServiceCredentials
	 * @throws ServiceException ServiceException
	 */
	public static ServiceCredentials getServiceCredentials(HttpServletRequest httpServletRequest, String scopeGroupId)
			throws ServiceException {

		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String token = null;
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
			boolean hasScopeGroupId = false;

			if (scopeGroupId != null && !scopeGroupId.isEmpty()) {
				hasScopeGroupId = true;

			} else {
				hasScopeGroupId = false;
			}

			if (hasScopeGroupId) {
				scope = pContext.getCurrentScope(scopeGroupId);
			} else {
				scope = pContext.getCurrentScope(httpServletRequest);
			}

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

			if (hasScopeGroupId) {
				logger.debug("Set SecurityToken: " + token);
				SecurityTokenProvider.instance.set(token);
				logger.debug("Set ScopeProvider: " + scope);
				ScopeProvider.instance.set(scope);

				groupId = scopeGroupId;

				long gId;

				try {
					gId = Long.parseLong(scopeGroupId);
				} catch (Throwable e) {
					String error = "Error retrieving groupId: " + scopeGroupId;
					logger.error(error, e);
					throw new ServiceException(error);
				}

				GCubeGroup group;
				try {
					group = new LiferayGroupManager().getGroup(gId);
				} catch (Throwable e) {
					String error = "Error retrieving group: " + groupName;
					logger.error(error);
					throw new ServiceException(error);
				}

				groupName = group.getGroupName();

			} else {

				groupId = String.valueOf(pContext.getCurrentGroupId(httpServletRequest));

				groupName = pContext.getCurrentGroupName(httpServletRequest);

			}

			sCredentials = new ServiceCredentials(userName, fullName, name, lastName, email, scope, groupId, groupName,
					userAvatarURL, token);
		}

		logger.debug("ServiceCredentials: " + sCredentials);

		return sCredentials;
	}

}
