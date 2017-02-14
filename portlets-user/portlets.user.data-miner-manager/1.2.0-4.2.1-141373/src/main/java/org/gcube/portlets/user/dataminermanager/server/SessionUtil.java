/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.server;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.dataminermanager.server.dmservice.SClient;
import org.gcube.portlets.user.dataminermanager.server.dmservice.SClient4WPSBuilder;
import org.gcube.portlets.user.dataminermanager.server.dmservice.SClientBuilder;
import org.gcube.portlets.user.dataminermanager.server.dmservice.SClientDirector;
import org.gcube.portlets.user.dataminermanager.server.util.ServiceCredentials;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest) throws ServiceException {
		return getServiceCredentials(httpServletRequest, null, null);
	}

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest, String scopeGroupId,
			String currUserId) throws ServiceException {

		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String token = null;
		String groupId = null;
		String groupName = null;

		if (Constants.DEBUG_MODE) {
			logger.info("No credential found in session, use test user!");
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;

			sCredentials = new ServiceCredentials(userName, scope, token);

		} else {
			logger.info("Retrieving credential in session!");
			PortalContext pContext = PortalContext.getConfiguration();
			boolean hasScopeGroupId = false;
			boolean hasCurrUserId = false;

			if (scopeGroupId != null && !scopeGroupId.isEmpty()) {
				hasScopeGroupId = true;

			} else {
				hasScopeGroupId = false;
			}

			if (currUserId != null && !currUserId.isEmpty()) {
				hasCurrUserId = true;

			} else {
				hasCurrUserId = false;
			}

			if (hasScopeGroupId) {
				scope = pContext.getCurrentScope(scopeGroupId);
			} else {
				scope = pContext.getCurrentScope(httpServletRequest);

			}

			logger.debug("Scope: " + scope);
			if (scope == null || scope.isEmpty()) {
				String error = "Error retrieving scope: " + scope;
				logger.error(error);
				throw new ServiceException(error);
			}

			GCubeUser gCubeUser = null;

			if (hasCurrUserId) {
				try {
					gCubeUser = new LiferayUserManager().getUserById(Long
							.valueOf(currUserId));
				} catch (Exception e) {
					String error = "Error retrieving gCubeUser for: [userId= "
							+ currUserId + ", scope: " + scope + "]";
					logger.error(error, e);
					throw new ServiceException(error);
				}
			} else {
				gCubeUser = pContext.getCurrentUser(httpServletRequest);
			}

			if (gCubeUser == null) {
				String error = "Error retrieving gCubeUser in scope " + scope
						+ ": " + gCubeUser;
				logger.error(error);
				throw new ServiceException(error);
			}

			userName = gCubeUser.getUsername();
			logger.debug("UserName: " + userName);

			if (userName == null || userName.isEmpty()) {
				String error = "Error retrieving username in scope " + scope
						+ ": " + userName;
				logger.error(error);
				throw new ServiceException(error);
			}

			if (hasCurrUserId) {
				try {
					token = pContext.getCurrentUserToken(scope,
							Long.valueOf(currUserId));
				} catch (Exception e) {
					String error = "Error retrieving token for: [userId= "
							+ currUserId + ", scope: " + scope + "]";
					logger.error(error, e);
					throw new ServiceException(error);
				}

			} else {
				token = pContext.getCurrentUserToken(scope, httpServletRequest);
			}

			logger.debug("Token: " + token);
			if (token == null || token.isEmpty()) {
				String error = "Error retrieving token for " + userName
						+ " in " + scope + ": " + token;
				logger.error(error);
				throw new ServiceException(error);
			}

			String name = gCubeUser.getFirstName();
			String lastName = gCubeUser.getLastName();
			String fullName = gCubeUser.getFullname();

			String userAvatarURL = gCubeUser.getUserAvatarURL();

			String email = gCubeUser.getEmail();

			if (hasScopeGroupId) {

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

				groupId = String.valueOf(pContext
						.getCurrentGroupId(httpServletRequest));

				groupName = pContext.getCurrentGroupName(httpServletRequest);

			}

			sCredentials = new ServiceCredentials(userName, fullName, name,
					lastName, email, scope, groupId, groupName, userAvatarURL,
					token);
		}

		logger.info("ServiceCredentials: " + sCredentials);

		return sCredentials;
	}

	public static SClient getSClient(ServiceCredentials serviceCredentials,
			HttpSession session) throws Exception {

		if (serviceCredentials == null) {
			logger.error("ServiceCredentials is null!");
			throw new ServiceException("Service Credentials is null!");
		}
		SClient sClient;

		Object obj = session.getAttribute(Constants.SClientMap);
		if (obj == null) {
			logger.info("Create new SClientMap");
			HashMap<String, SClient> sClientMap = new HashMap<>();
			logger.info("Create new SClient");
			SClientBuilder sBuilder = new SClient4WPSBuilder(serviceCredentials);
			SClientDirector director = new SClientDirector();
			director.setSClientBuilder(sBuilder);
			director.constructSClient();
			sClient = director.getSClient();

			sClientMap.put(serviceCredentials.getScope(), sClient);
			session.setAttribute(Constants.SClientMap, sClientMap);
		} else {
			if (obj instanceof HashMap<?, ?>) {
				@SuppressWarnings("unchecked")
				HashMap<String, SClient> sClientMap = (HashMap<String, SClient>) obj;
				if (sClientMap.containsKey(serviceCredentials.getScope())) {
					logger.info("Use SClient in session");
					sClient = sClientMap.get(serviceCredentials.getScope());
				} else {
					logger.info("Create new SClient");
					SClientBuilder sBuilder = new SClient4WPSBuilder(
							serviceCredentials);

					SClientDirector director = new SClientDirector();
					director.setSClientBuilder(sBuilder);
					director.constructSClient();
					sClient = director.getSClient();
					sClientMap.put(serviceCredentials.getScope(), sClient);
					session.setAttribute(Constants.SClientMap, sClientMap);
				}

			} else {
				logger.error("Attention no SClientMap in Session!");
				throw new ServiceException(
						"Sign Out, portlet is changed, a new session is required!");
			}
		}

		return sClient;

	}

}
