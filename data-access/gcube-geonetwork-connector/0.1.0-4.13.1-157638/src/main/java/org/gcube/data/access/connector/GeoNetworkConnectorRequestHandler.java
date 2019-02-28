package org.gcube.data.access.connector;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Base64;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.access.connector.rest.GCubeRestClient;
import org.gcube.data.access.connector.rest.entity.AccessibleCredentialsEntity;
import org.gcube.data.access.connector.utils.AuthenticationUtils;
import org.gcube.data.access.connector.utils.GCubeCache;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.request.RequestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import static org.gcube.common.authorization.client.Constants.authorizationService;

@XmlRootElement(name = GeoNetworkConnectorRequestHandler.REQUEST_HANDLER_NAME)
public class GeoNetworkConnectorRequestHandler extends RequestHandler {

	protected static final String REQUEST_HANDLER_NAME = "authentication-filter";
	private static final String GEONETWORK_CREDENTIALS = "/GeoNetwork/credentials/";
	private static final String SDI = "/SDI";
	private static final boolean GENERAL = false; //retrieve the general credentials from SDI (without web service)

	private Logger logger;
	private GCubeCache<String, String> gCubeCache;
	private GCubeRestClient restClient = new GCubeRestClient();

	public GeoNetworkConnectorRequestHandler() {
		logger = LoggerFactory.getLogger(this.getClass());
		gCubeCache = new GCubeCache<>(AuthenticationUtils.TIME_TO_LIVE, AuthenticationUtils.TIMER_INTERVAL,
				AuthenticationUtils.MAX_ITEMS_CACHE);
	}

	@Override
	public String getName() {
		return REQUEST_HANDLER_NAME;
	}

	@Override
	public void handleRequest(RequestEvent e) {
		logger.warn("Handling request");
		HttpServletRequest httpServletRequest = e.request();

		// get host from ApplicationContext
		String host = e.context().container().configuration().hostname();

		// get token from request
		String token = getToken(httpServletRequest);
		logger.warn("Retrieve token from request = " + token);

		if (StringUtils.hasText(token)) {
			logger.warn("Token found: " + token);

			if (validateToken(token)) {
				// retrieve endpoint to get credentials in Geonetwork
				String endpoint = getEndpoint(token);
				logger.warn("Endpoint found: " + endpoint);
			
				// TODO - Can be the endpoint stored in the cache object?
				if (StringUtils.hasText(endpoint)) {

					String usernameCache = gCubeCache.get(AuthenticationUtils.USERNAME);
					String passwordCache = gCubeCache.get(AuthenticationUtils.PASSWORD);
					String tokenCache = gCubeCache.get(AuthenticationUtils.TOKEN_CACHE);

					// check current token with tokenCache
					if (token.equals(tokenCache)) {
						logger.warn("Set credentials attribute retrieved from cache " + usernameCache + " " + passwordCache);
						httpServletRequest.setAttribute(AuthenticationUtils.USERNAME, usernameCache);
						httpServletRequest.setAttribute(AuthenticationUtils.PASSWORD, passwordCache);
					} else {
						
						//get credentials 
						logger.warn("RETRIEVE CREDENTIALS IN GENERAL MODE = " + GENERAL);						
						AccessibleCredentialsEntity accessibleCredentials = getAccessibleCredentials(endpoint, host, token, GENERAL);
						logger.warn("Credentials: " + accessibleCredentials.getUsername() + "/" + accessibleCredentials.getPassword());

						httpServletRequest.setAttribute(AuthenticationUtils.USERNAME, accessibleCredentials.getUsername());
						httpServletRequest.setAttribute(AuthenticationUtils.PASSWORD, accessibleCredentials.getPassword());
						
						// set/update data in the cache
						logger.warn("Put token in the cache: " + token);
						gCubeCache.put(AuthenticationUtils.TOKEN_CACHE, token);

						logger.warn("Put also username and password in the cache");
						gCubeCache.put(AuthenticationUtils.USERNAME, accessibleCredentials.getUsername());
						gCubeCache.put(AuthenticationUtils.PASSWORD, accessibleCredentials.getPassword());
					}
				}
			} else {
				logger.error("Invalid token in the request");
				RequestError.request_not_authorized_error.fire("Invalid token in the request");
			}
		} else {
			logger.warn("Token not present in the request: NO/OP");
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	// retrieve the Token from request
	private String getToken(HttpServletRequest httpServletRequest) {

		// case 1 - get token from gcube-token query-string
		String gCubeToken = httpServletRequest.getParameter(AuthenticationUtils.GCUBE_QUERY_STRING);
		if (StringUtils.hasText(gCubeToken)) {
			logger.warn("Get token from query-string");
			return gCubeToken;
		}

		// case 2 - get token from gcube-token header
		gCubeToken = httpServletRequest.getHeader(AuthenticationUtils.GCUBE_QUERY_STRING);
		if (StringUtils.hasText(gCubeToken)) {
			logger.warn("Get token from gcube-token header");
			return gCubeToken;
		}

		// case 3 - get token from basic authorization header
		String authorization = httpServletRequest.getHeader(AuthenticationUtils.AUTHORIZATION);
		if (StringUtils.hasText(authorization)
				&& StringUtils.startsWithIgnoreCase(authorization, AuthenticationUtils.BASIC)) {
			logger.warn("Get token from basic authorization header");
			// header = Authorization: Basic base64credentials
			String base64Credentials = StringUtils.delete(authorization, AuthenticationUtils.BASIC);
			String credentials = new String(Base64.getDecoder().decode(StringUtils.trimWhitespace(base64Credentials)));

			// credentials = username:token
			final String[] values = credentials.split(":", 2);
			return values[1];
		}

		logger.warn("gcube-token not found in query-string, in header and in basic authorization header");

		// case 4 - get token from HTML form in the password field
		gCubeToken = httpServletRequest.getParameter(AuthenticationUtils.PASSWORD);
		if (StringUtils.hasText(gCubeToken)) {
			logger.warn("Get token from HTML form (in the password field)");
			String user = httpServletRequest.getParameter(AuthenticationUtils.USERNAME);
			logger.warn("Get username from HTML form: " + user);
			
			if (StringUtils.hasText(user) && user.equals(getUser(gCubeToken))) //check username
				return gCubeToken;
			
			logger.warn("Username doesn't match with ClientInfo of gcube");
		
		} else
			logger.warn("gcube-token also not found in the HTML form in the password field");

		return null;
	}

	private String getEndpoint(String token) {

		try {
			AuthorizationEntry authorizationEntry = authorizationService().get(token);
			String scope = authorizationEntry.getContext();
			logger.warn("Set scope in to " + scope);
			ScopeProvider.instance.set(scope);

			SecurityTokenProvider.instance.set(token);

			String serviceClass = String.format("$resource/Profile/ServiceClass/text() eq '%s'",
					AuthenticationUtils.SDI);
			String serviceName = String.format("$resource/Profile/ServiceName/text() eq '%s'",
					AuthenticationUtils.SDI_SERVICE);			
			String status = String.format("$resource/Profile/DeploymentData/Status/text() eq '%s'",
					AuthenticationUtils.READY);

			SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class).addCondition(serviceClass)
					.addCondition(serviceName).addCondition(status);
			DiscoveryClient<GCoreEndpoint> client = ICFactory.clientFor(GCoreEndpoint.class);

			List<GCoreEndpoint> gCoreEndpoints = client.submit(query);
			int size = gCoreEndpoints.size();
			logger.warn("gCoreEndpoints size = " + size);

			if (size > 0) {//I get only the first. Usually it must be only one 				
				GCoreEndpoint gCoreEndpoint = gCoreEndpoints.get(0);
				return gCoreEndpoint.profile().endpointMap().get("org.gcube.spatial.data.sdi.SDIService").uri().toString();
			}

		} catch (Exception ex) {
			logger.error("Error in getEndpoint() method: " + ex.getMessage());
		}

		return null;
	}

	private String getUser(String token) {
		try {
			AuthorizationEntry authorizationEntry = authorizationService().get(token);
			return authorizationEntry.getClientInfo().getId();
		} catch (Exception ex) {
			logger.error("Error in getUser() method: " + ex.getMessage());
		}
		return null;
	}

	private boolean validateToken(String token) {
		// TODO How to implement the validation of the token
		logger.warn("Validate token in progress...");
		return true;
	}
	
	private AccessibleCredentialsEntity getAccessibleCredentials(String endpoint, String host, String token, boolean general){
		
		if (general){
			String url = endpoint + SDI + "?" + AuthenticationUtils.GCUBE_QUERY_STRING + "=" + token;
			//http://sdi-d-d4s.d4science.org/sdi-service/gcube/service/SDI?gcube-token=feda0617-cd9d-4841-b6f0-e047da5d32ed-98187548";
			return restClient.getGeneralAccessibleCredentials(url, host);
		
		}else{
			String url = endpoint + GEONETWORK_CREDENTIALS + host + "?" + AuthenticationUtils.GCUBE_QUERY_STRING + "=" + token;
			//http://sdi-d-d4s.d4science.org:80/sdi-service/gcube/service/GeoNetwork/credentials/geonetwork-sdi.dev.d4science.org?gcube-token=feda0617-cd9d-4841-b6f0-e047da5d32ed-98187548
			return restClient.getAccessibleCredentials(url);
		} 
	}
}
