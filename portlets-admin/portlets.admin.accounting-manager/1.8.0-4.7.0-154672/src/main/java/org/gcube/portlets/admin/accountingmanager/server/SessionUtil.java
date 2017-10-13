/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingState;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.server.util.ServiceCredentials;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskWrapper;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest) throws ServiceException {
		return getServiceCredentials(httpServletRequest, null);
	}

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest, String scopeGroupId)
			throws ServiceException {

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
			// boolean hasCurrUserId = false;

			if (scopeGroupId != null && !scopeGroupId.isEmpty()) {
				hasScopeGroupId = true;

			} else {
				hasScopeGroupId = false;
			}

			/*
			 * if (currUserId != null && !currUserId.isEmpty()) { hasCurrUserId
			 * = true;
			 * 
			 * } else { hasCurrUserId = false; }
			 */

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

			GCubeUser gCubeUser = pContext.getCurrentUser(httpServletRequest);

			/*
			 * if (hasCurrUserId) { try { gCubeUser = new
			 * LiferayUserManager().getUserById(Long .valueOf(currUserId)); }
			 * catch (Exception e) { String error =
			 * "Error retrieving gCubeUser for: [userId= " + currUserId +
			 * ", scope: " + scope + "]"; logger.error(error, e); throw new
			 * ServiceException(error); } } else { gCubeUser =
			 * pContext.getCurrentUser(httpServletRequest); }
			 */

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

			token = pContext.getCurrentUserToken(scope, userName);

			/*
			 * if (hasCurrUserId) { try { token =
			 * pContext.getCurrentUserToken(scope, Long.valueOf(currUserId)); }
			 * catch (Exception e) { String error =
			 * "Error retrieving token for: [userId= " + currUserId +
			 * ", scope: " + scope + "]"; logger.error(error, e); throw new
			 * ServiceException(error); }
			 * 
			 * } else { token =
			 * pContext.getCurrentUserToken(httpServletRequest); }
			 */

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

		logger.debug("ServiceCredentials: " + sCredentials);

		return sCredentials;
	}

	public static void setAccountingStateData(HttpSession httpSession,
			ServiceCredentials serviceCredentials,
			AccountingType accountingType,
			AccountingStateData accountingStateData) {
		@SuppressWarnings("unchecked")
		HashMap<String, AccountingState> accountingStateMap = (HashMap<String, AccountingState>) httpSession
				.getAttribute(Constants.SESSION_ACCOUNTING_STATE_MAP);

		if (accountingStateMap == null) {
			AccountingState accountingState = new AccountingState();
			accountingState.setState(accountingType, accountingStateData);
			accountingStateMap = new HashMap<>();
			accountingStateMap.put(serviceCredentials.getScope(),
					accountingState);
			httpSession.setAttribute(Constants.SESSION_ACCOUNTING_STATE_MAP,
					accountingStateMap);
		} else {
			AccountingState accountingState = accountingStateMap
					.get(serviceCredentials.getScope());

			if (accountingState == null) {
				accountingState = new AccountingState();
				accountingState.setState(accountingType, accountingStateData);
				accountingStateMap.put(serviceCredentials.getScope(),
						accountingState);
			} else {
				accountingState.setState(accountingType, accountingStateData);
			}
		}
		
		
		
		return;
	}

	public static AccountingStateData getAccountingStateData(
			HttpSession httpSession, ServiceCredentials serviceCredentials,
			AccountingType accountingType) {
		
		@SuppressWarnings("unchecked")
		HashMap<String, AccountingState> accountingStateMap = (HashMap<String, AccountingState>) httpSession
				.getAttribute(Constants.SESSION_ACCOUNTING_STATE_MAP);

		if (accountingStateMap == null) {
			return null;
		} else {
			AccountingState accountingState = accountingStateMap
					.get(serviceCredentials.getScope());

			if (accountingState == null) {
				return null;
			} else {
				return accountingState.getState(accountingType);
			}
		}
		
		
		
	}


	/**
	 * 
	 * @param httpSession http session
	 * @param serviceCredentials service credentials
	 * @return hash map of tasks
	 */
	public static HashMap<String, TaskWrapper> getTaskWrapperMap(
			HttpSession httpSession,
			ServiceCredentials serviceCredentials) {
		@SuppressWarnings("unchecked")
		HashMap<String, TaskWrapper> taskWrapperMap = (HashMap<String, TaskWrapper>) httpSession
				.getAttribute(SessionConstants.TASK_WRAPPER_MAP);
		return taskWrapperMap;
	}

	/**
	 * 
	 * @param httpSession session
	 * @param serviceCredentials service credentials
	 * @param taskWrapperMap task wrapper map
	 */
	public static void setTaskWrapperMap(HttpSession httpSession,
			ServiceCredentials serviceCredentials,
			HashMap<String, TaskWrapper> taskWrapperMap) {
		httpSession.setAttribute(SessionConstants.TASK_WRAPPER_MAP, taskWrapperMap);
		return;
	}

	public static Context getContext(ServiceCredentials serviceCredentials)
			throws ServiceException {

		try {
			logger.info("Current context is " + serviceCredentials.getScope());
			ArrayList<String> contexts = new ArrayList<>();

			if (Constants.DEBUG_MODE) {
				contexts.add(serviceCredentials.getScope());
				for (int i = 0; i < 50; i++) {
					contexts.add("/d4science.research-infrastructures.eu/gCubeApps/PerformanceEvaluationInAquaculture"
							+ i);
				}
			} else {
				contexts.add(serviceCredentials.getScope());
				GroupManager gm = new LiferayGroupManager();
				long currentGroupId = gm
						.getGroupIdFromInfrastructureScope(serviceCredentials
								.getScope());
				GCubeGroup currentGroup = gm.getGroup(currentGroupId);

				// three cases
				if (gm.isVRE(currentGroupId)) {

					// do nothing

				} else if (gm.isVO(currentGroupId)) {

					// iterate over its vres
					List<GCubeGroup> children = currentGroup.getChildren();
					for (GCubeGroup gCubeGroup : children) {
						contexts.add(gm.getInfrastructureScope(gCubeGroup
								.getGroupId()));
					}

				} else {

					// is root
					List<GCubeGroup> children = currentGroup.getChildren();
					for (GCubeGroup gCubeGroup : children) {
						contexts.add(gm.getInfrastructureScope(gCubeGroup
								.getGroupId()));

						// get the vo children
						List<GCubeGroup> childrenVO = gCubeGroup.getChildren();
						for (GCubeGroup voChildren : childrenVO) {
							contexts.add(gm.getInfrastructureScope(voChildren
									.getGroupId()));
						}
					}

				}
			}
			// add the current scope too
			Context context = new Context(contexts);

			return context;

		} catch (Exception e) {
			logger.error("Error retrieving context!", e);
			throw new ServiceException("Error retrieving context!", e);
		}

	}

}
