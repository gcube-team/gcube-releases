/**
 * 
 */
package org.gcube.portlets.user.tdwx.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactory;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactoryRegistry;
import org.gcube.portlets.user.tdwx.server.session.TDSession;
import org.gcube.portlets.user.tdwx.server.session.TDSessionList;
import org.gcube.portlets.user.tdwx.server.util.ServiceCredentials;
import org.gcube.portlets.user.tdwx.shared.Constants;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SessionUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(SessionUtil.class);

	public static final String TDWX_SESSIONS_ATTRIBUTE_NAME = "TDWX.SESSIONS";

	/**
	 * 
	 * @param httpServletRequest
	 * @return
	 * @throws TDGWTServiceException
	 */
	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest) throws Exception {
		return getServiceCredentials(httpServletRequest, null);
	}

	/**
	 * 
	 * @param httpServletRequest
	 * @param scopeGroupId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest, String scopeGroupId)
			throws Exception {

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

			logger.info("Set SecurityToken: " + token);
			SecurityTokenProvider.instance.set(token);
			logger.info("Set ScopeProvider: " + scope);
			ScopeProvider.instance.set(scope);

			sCredentials = new ServiceCredentials(userName, scope, token);

		} else {
			logger.info("Retrieving credential in session!");
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
				throw new Exception(error);
			}

			GCubeUser gCubeUser = pContext.getCurrentUser(httpServletRequest);

			if (gCubeUser == null) {
				String error = "Error retrieving gCubeUser in scope " + scope
						+ ": " + gCubeUser;
				logger.error(error);
				throw new Exception(error);
			}

			userName = gCubeUser.getUsername();

			if (userName == null || userName.isEmpty()) {
				String error = "Error retrieving username in scope " + scope
						+ ": " + userName;
				logger.error(error);
				throw new Exception(error);
			}

			token = pContext.getCurrentUserToken(scope, userName);

			if (token == null || token.isEmpty()) {
				String error = "Error retrieving token for " + userName
						+ " in " + scope + ": " + token;
				logger.error(error);
				throw new Exception(error);
			}

			String name = gCubeUser.getFirstName();
			String lastName = gCubeUser.getLastName();
			String fullName = gCubeUser.getFullname();

			String userAvatarURL = gCubeUser.getUserAvatarURL();

			String email = gCubeUser.getEmail();

			if (hasScopeGroupId) {
				logger.info("Set SecurityToken: " + token);
				SecurityTokenProvider.instance.set(token);
				logger.info("Set ScopeProvider: " + scope);
				ScopeProvider.instance.set(scope);

				groupId = scopeGroupId;

				long gId;

				try {
					gId = Long.parseLong(scopeGroupId);
				} catch (Throwable e) {
					String error = "Error retrieving groupId: " + scopeGroupId;
					logger.error(error, e);
					throw new Exception(error);
				}

				GCubeGroup group;
				try {
					group = new LiferayGroupManager().getGroup(gId);
				} catch (Throwable e) {
					String error = "Error retrieving group: " + groupName;
					logger.error(error);
					throw new Exception(error);
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

	public static DataSourceX getDataSource(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, int tdSessionId) {
		TDSession tdSession = getSession(httpRequest, serviceCredentials,
				tdSessionId);
		return tdSession.getDataSource();
	}

	public static void setDataSource(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, int tdSessionId,
			DataSourceX datasource) {
		TDSession tdSession = getSession(httpRequest, serviceCredentials,
				tdSessionId);
		tdSession.setDataSource(datasource);
		logger.trace("datasource " + datasource + " set in session "
				+ tdSessionId);
	}

	protected static TDSession getSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, int tdSessionId) {
		// workaround to TDSession object loaded from different class loader
		HttpSession httpSession = httpRequest.getSession();
		Object tsSession = httpSession
				.getAttribute(TDWX_SESSIONS_ATTRIBUTE_NAME);
		TDSessionList tdSessions = (tsSession instanceof TDSessionList) ? ((TDSessionList) tsSession)
				: null;
		if (tdSessions == null) {
			tdSessions = new TDSessionList();
			httpSession.setAttribute(TDWX_SESSIONS_ATTRIBUTE_NAME, tdSessions);
		}
		if (tdSessions.get(tdSessionId) == null) {
			tdSessions.set(tdSessionId, new TDSession(tdSessionId));
			logger.trace("created new td sessions " + tdSessionId);
		}
		return tdSessions.get(tdSessionId);
	}

	public static DataSourceX openDataSource(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, TableId tableId)
			throws DataSourceXException {
		DataSourceXFactoryRegistry dataSourceFactoryRegistry = DataSourceXFactoryRegistry
				.getInstance();
		DataSourceXFactory factory = dataSourceFactoryRegistry.get(tableId
				.getDataSourceFactoryId());
		if (factory == null)
			throw new DataSourceXException("DataSourceFactory with id "
					+ tableId.getDataSourceFactoryId() + " don't exists");	
		return factory.openDataSource(serviceCredentials, tableId);
	}

	public static void closeDataSource(HttpServletRequest httpRequest,ServiceCredentials serviceCredentials, int tdSessionId)
			throws DataSourceXException {
		DataSourceX currentDataSource = getDataSource(httpRequest, serviceCredentials, tdSessionId);
		if (currentDataSource != null) {
			DataSourceXFactoryRegistry dataSourceFactoryRegistry = DataSourceXFactoryRegistry
					.getInstance();
			DataSourceXFactory factory = dataSourceFactoryRegistry
					.get(currentDataSource.getDataSourceFactoryId());
			factory.closeDataSource(serviceCredentials, currentDataSource);
		}
	}

}
