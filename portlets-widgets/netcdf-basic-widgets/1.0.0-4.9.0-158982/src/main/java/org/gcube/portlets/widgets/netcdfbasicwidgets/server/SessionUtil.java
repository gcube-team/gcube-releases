/**
 * 
 */
package org.gcube.portlets.widgets.netcdfbasicwidgets.server;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.util.ServiceCredentials;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.Constants;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.exception.ServiceException;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFId;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	/**
	 * 
	 * @param httpServletRequest
	 *            http servlet request
	 * @return service credentials
	 * @throws ServiceException
	 *             service exception
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
	 *            scope group id
	 * @return service credentials
	 * @throws ServiceException
	 *             service exception
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

	public static void setNetCDFData(NetCDFData netCDFData, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws ServiceException {
		
		if (serviceCredentials == null) {
			logger.error("ServiceCredentials is null!");
			throw new ServiceException("Service Credentials is null!");
		}
		HttpSession session = httpRequest.getSession();


		Object obj = session.getAttribute(Constants.NETCDF_DATA_MAP);
		if (obj == null) {
			logger.debug("Create new NETCDF_DATA_MAP");
			HashMap<String, NetCDFData> netCDFDataMap = new HashMap<>();
			logger.debug("Add NetCDFData");
			netCDFDataMap.put(netCDFData.getNetCDFId().getId(), netCDFData);
			session.setAttribute(Constants.NETCDF_DATA_MAP, netCDFDataMap);
		} else {
			if (obj instanceof HashMap<?, ?>) {
				@SuppressWarnings("unchecked")
				HashMap<String, NetCDFData> netCDDataMap = (HashMap<String, NetCDFData>) obj;
				if (netCDDataMap.containsKey(netCDFData.getNetCDFId().getId())) {
					logger.debug("Error invalid NetCDFData in session");
					throw new ServiceException("Invalid NetCDFData in session!");
				} else {
					logger.debug("Add NetCDFData");
					netCDDataMap.put(netCDFData.getNetCDFId().getId(), netCDFData);
					session.setAttribute(Constants.NETCDF_DATA_MAP, netCDDataMap);
				}
			} else {
				logger.error("Attention no  NetCDFDataMap in Session!");
				throw new ServiceException("Sign Out, portlet is changed, a new session is required!");
			}
		}

		return;

	}
	
	public static NetCDFData getNetCDFData(NetCDFId netCDFId, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws ServiceException {
		
		if (serviceCredentials == null) {
			logger.error("ServiceCredentials is null!");
			throw new ServiceException("Service Credentials is null!");
		}
		HttpSession session = httpRequest.getSession();


		Object obj = session.getAttribute(Constants.NETCDF_DATA_MAP);
		if (obj == null) {
			logger.error("Attention no  NetCDFDataMap in Session!");
			throw new ServiceException("Sign Out, portlet is changed, a new session is required!");
		} else {
			if (obj instanceof HashMap<?, ?>) {
				@SuppressWarnings("unchecked")
				HashMap<String, NetCDFData> netCDFDataMap = (HashMap<String, NetCDFData>) obj;
				if (netCDFDataMap.containsKey(netCDFId.getId())) {
					logger.debug("Get NetCDFData in session");
					return netCDFDataMap.get(netCDFId.getId());
				} else {
					logger.error("Attention no  NetCDFData in Session!");
					throw new ServiceException("Attention no  NetCDFData in Session!");
				}
			} else {
				logger.error("Attention no  NetCDFDataMap in Session!");
				throw new ServiceException("Sign Out, portlet is changed, a new session is required!");
			}
		}
	}
	
	
	public static void removeNetCDFData(NetCDFId netCDFId, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws ServiceException {
		
		if (serviceCredentials == null) {
			logger.error("ServiceCredentials is null!");
			throw new ServiceException("Service Credentials is null!");
		}
		HttpSession session = httpRequest.getSession();


		Object obj = session.getAttribute(Constants.NETCDF_DATA_MAP);
		if (obj == null) {
			logger.error("Attention no  NetCDFDataMap in Session!");
			throw new ServiceException("Sign Out, portlet is changed, a new session is required!");
		} else {
			if (obj instanceof HashMap<?, ?>) {
				@SuppressWarnings("unchecked")
				HashMap<String, NetCDFData> netCDFDataMap = (HashMap<String, NetCDFData>) obj;
				if (netCDFDataMap.containsKey(netCDFId.getId())) {
					logger.debug("Remove NetCDFData in session");
					netCDFDataMap.remove(netCDFId.getId());
				} else {
					logger.error("Attention no  NetCDFData in Session!");
					throw new ServiceException("Attention no  NetCDFResource in Session!");
				}
			} else {
				logger.error("Attention no  NetCDFDataMap in Session!");
				throw new ServiceException("Sign Out, portlet is changed, a new session is required!");
			}
		}
	}
	

}
