package org.gcube.portlets.user.simulfishgrowth.portlet;

import java.util.Collection;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.simulfishgrowthdata.util.AccessPointer;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class BasePortlet extends MVCPortlet {
	private static Log logger = LogFactoryUtil.getLog(BasePortlet.class);

	private static final String USERNAME = "i2s_username";
	private static final String SEC_TOKEN = "i2s_security_token";
	private static final String SCOPE = "i2s_scope";
	public static final String CTX_PARAM_DATABASE_ENDPOINT = "DatabaseEndpoint";
	public static final String CTX_PARAM_DATA_ACCESS_ENDPOINT = "DataAccessEndPoint";
	public static final String CTX_PARAM_CALC_KPIS_IDENTIFIER_ENDPOINT_NAME = "CalcKPIsIdentifierEndpointName";
	public static final String CTX_PARAM_CALC_KPI_ENDPOINT_NAME = "CalcKPIEndpointName";
	public static final String CTX_PARAM_GOOGLE_MAPS_KEY_ENDPOINT_NAME = "GoogleMapsKeyEndpointName";

	public static final String DEBUG_CALC_KPIS_IDENTIFIER = "debug.CalcKPIsIdentifier";
	public static final String DEBUG_GOOGLE_MAPS_KEY = "debug.GoogleMapsKey";
	public static final String DEBUG_BYPASS_KPI_CALCULATION = "debug.bypassKPICalculation";
	public static final String DEBUG_BYPASS_FILE_UPLOAD = "debug.bypassFileUpload";
	public static final String DEBUG_DB_ACCESSPOINT_ADDRESS = "debug.dbAccessPoint.address";
	public static final String DEBUG_DB_ACCESSPOINT_NAME = "debug.dbAccessPoint.name";
	public static final String DEBUG_DB_ACCESSPOINT_PSW = "debug.dbAccessPoint.psw";
	public static final String DEBUG_DB_ACCESSPOINT_USER = "debug.dbAccessPoint.user";
	public static final String DEBUG_API_ACCESSPOINT = "debug.apiAccessPoint";
	public static final String DEBUG_DATAMINER_TOKEN = "debug.dataMinerToken";
	public static final String DEBUG_DATAMINER_SCOPE = "debug.dataMinerScope";
	public static final String DEBUG_DATAMINER_ADDRESS = "debug.dataMinerAddress";
	public static final String DEBUG_HOME_API_SCOPE = "debug.homeApiScope";
	public static final String DEBUG_HOME_API_USER_NAME = "debug.homeApiUserName";
	public static final String DEBUG_MOCK_DATA = "debug.mockData";

	protected String debugHomeApiScope;
	protected String debugHomeApiUserName;
	protected String debugDataMinerToken;
	protected String debugDataMinerScope;
	static public String debugDataMinerAddress;
	protected String debugDbAccessPointAddress;
	protected String debugApiAccessPoint;
	protected String debugCalcKPIsIdentifier;

	static protected String GoogleKey = null;

	synchronized static String getGoogleKey(String scope) throws Exception {
		if (GoogleKey == null) {

			String endpointName = "Google API Keys";
			ScopeProvider.instance.set(scope);
			AccessPoint apoint = new AccessPointer(endpointName).getIt();
			String encrKey = apoint.propertyMap().get("Key").value();
			GoogleKey = StringEncrypter.getEncrypter().decrypt(encrKey);
		}
		return GoogleKey;

	}

	String getToken(HttpSession httpSession) {
		String token = (String) httpSession.getAttribute(BasePortlet.SEC_TOKEN);
		return token;
	}

	String getScope(HttpSession httpSession) {
		String scope = (String) httpSession.getAttribute(BasePortlet.SCOPE);
		ScopeProvider.instance.set(scope);
		return scope;
	}

	String getUsername(HttpSession httpSession) {
		String username = (String) httpSession.getAttribute(BasePortlet.USERNAME);
		return username;
	}

	@Override
	public void init() throws PortletException {
		super.init();

		debugHomeApiScope = StringUtils.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_HOME_API_SCOPE),
				null);
		debugHomeApiUserName = StringUtils
				.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_HOME_API_USER_NAME), null);
		debugDataMinerToken = StringUtils.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_DATAMINER_TOKEN),
				null);
		debugDataMinerScope = StringUtils.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_DATAMINER_SCOPE),
				null);
		debugDataMinerAddress = StringUtils
				.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_DATAMINER_ADDRESS), null);
		debugDbAccessPointAddress = StringUtils
				.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_DB_ACCESSPOINT_ADDRESS), null);
		debugApiAccessPoint = StringUtils.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_API_ACCESSPOINT),
				null);
		debugCalcKPIsIdentifier = StringUtils
				.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_CALC_KPIS_IDENTIFIER), null);
		ConnectionUtils.MOCK_DATA = Boolean.parseBoolean(getPortletContext().getInitParameter(DEBUG_MOCK_DATA));

		GoogleKey = StringUtils.defaultIfEmpty(getPortletContext().getInitParameter(DEBUG_GOOGLE_MAPS_KEY), null);

	}

	/**
	 * the current ASLSession
	 * 
	 * @return the session
	 */
	protected ASLSession getASLSession(PortletRequest request) {
		String sessionID = PortalUtil.getHttpServletRequest(request).getSession().getId();
		String user = (String) PortalUtil.getHttpServletRequest(request).getSession()
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	protected void prepareSession(HttpSession httpSession, RenderRequest request) {
		ScopeHelper.setContext(request);
		String scope = getASLSession(request).getScope();
		httpSession.setAttribute(BasePortlet.SCOPE, scope);
		String username = getASLSession(request).getUsername();
		httpSession.setAttribute(BasePortlet.USERNAME, username);

		String token = getASLSession(request).getSecurityToken();
		httpSession.setAttribute(BasePortlet.SEC_TOKEN, token);

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("scope [%s] username [%s] token [%s]", scope, username, token));
		}
	}

	protected AccessPoint getDBAccessPoint(String scope) throws UserFriendlyException {
		AccessPoint accessPoint = null;
		try {
			ScopeProvider.instance.set(scope);
			if (debugDbAccessPointAddress != null) {
				accessPoint = new AccessPoint();
				accessPoint.address(getPortletContext().getInitParameter(DEBUG_DB_ACCESSPOINT_ADDRESS));
				accessPoint.name(getPortletContext().getInitParameter(DEBUG_DB_ACCESSPOINT_NAME));
				accessPoint.credentials(
						StringEncrypter.getEncrypter()
								.encrypt(getPortletContext().getInitParameter(DEBUG_DB_ACCESSPOINT_PSW)),
						getPortletContext().getInitParameter(DEBUG_DB_ACCESSPOINT_USER));
				logger.warn(String.format("Debug db endpoint used [%s]", accessPoint));
			} else {
				accessPoint = new AccessPointer(
						getPortletContext().getInitParameter(BasePortlet.CTX_PARAM_DATABASE_ENDPOINT)).getIt();
			}
		} catch (Exception e) {
			logger.error("Could not retrieve database information from the server", e);
			throw new UserFriendlyException("Could not retrieve database information from the server", e);
		} finally {
			// if (prevProviderScope == null)
			// ScopeProvider.instance.reset();
			// else
			// ScopeProvider.instance.set(prevProviderScope);
		}
		return accessPoint;
	}

	protected String getCalcKPIsIdentifier(String scope) throws Exception {
		String toRet = null;
		try {
			if (debugCalcKPIsIdentifier != null) {
				toRet = debugCalcKPIsIdentifier;
				logger.warn(String.format("Debug CalcKPIsIdentifier used [%s]", toRet));
			} else {
				String endpointName = getPortletContext()
						.getInitParameter(BasePortlet.CTX_PARAM_CALC_KPIS_IDENTIFIER_ENDPOINT_NAME);
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Query IS for [%s]", endpointName));
				}
				ScopeProvider.instance.set(scope);
				try {
					SimpleQuery query = ICFactory.queryFor(GenericResource.class);
					query.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", endpointName));
					query.setResult("$resource/Profile/Body/text()");
					DiscoveryClient<String> client = ICFactory.client();
					List<String> results = client.submit(query);
					if (results != null && !results.isEmpty()) {
						toRet = results.get(0).toString();
					}
				} catch (Exception e) {
					throw new Exception(String.format("Error getting resource [%s]", endpointName), e);
				}
				if (logger.isDebugEnabled())
					logger.debug(String.format("For [%s] in scope [%s] got [%s]", endpointName, scope, toRet));
				return toRet;
			}
		} catch (Exception e) {
			logger.error("Could not setup communication info", e);
			throw new Exception("Could not setup communication info", e);
		}
		return toRet;
	}

	protected String getDataAccessEndpoint(String scope) throws Exception {
		String endpoint = null;
		String endpointName = getPortletContext().getInitParameter(BasePortlet.CTX_PARAM_DATA_ACCESS_ENDPOINT);
		try {
			ScopeProvider.instance.set(scope);
			if (debugApiAccessPoint != null) {
				endpoint = debugApiAccessPoint;
				logger.warn(String.format("Debug data api endpoint used [%s]", endpoint));
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Query IS for [%s]", endpointName));
				}
				SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
				query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'", endpointName))
						.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()");
				DiscoveryClient<String> client = ICFactory.client();
				List<String> results = client.submit(query);
				// TODO I need to query on the Endpoint@EntryName attribute
				Collection apis = CollectionUtils.select(results, new Predicate() {

					@Override
					public boolean evaluate(Object endpoint) {
						return ((String) endpoint).endsWith("api");
					}
				});

				if (!apis.iterator().hasNext()) {
					throw new Exception(String.format("IS query for [%s] return no results", endpointName));
				}
				endpoint = (String) apis.iterator().next();
			}
			if (logger.isDebugEnabled())
				logger.debug(String.format("For [%s] in scope [%s] got [%s]", endpointName, scope, endpoint));
		} catch (Exception e) {
			throw new Exception("Could not setup communication info", e);
		}
		return endpoint;
	}

	protected String scopeAsOwnerId(final String scope) {
		return StringUtils.replace(scope, "/", "_");
	}

	class AddGCubeHeadersCreator {
		private AddGCubeHeaders created;
		private HttpSession httpSession;

		public AddGCubeHeadersCreator(HttpSession httpSession) {
			this.httpSession = httpSession;
		}

		public AddGCubeHeadersCreator(PortletRequest request) {
			this.httpSession = PortalUtil.getHttpServletRequest(request).getSession();
		}

		public AddGCubeHeaders create() {
			String scope = getScope(httpSession);
			created = new AddGCubeHeaders(scope, getToken(httpSession));
			return created;
		}
	}

}