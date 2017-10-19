package org.gcube.portal.auth;
import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class AuthUtil  {
	private static final Logger _log = LoggerFactory.getLogger(AuthUtil.class);

	public final static String REDIRECT_URL = "RedirectURL";
	public final static String LOGOURL_ATTR = "Logo";
	public final static String TOKEN_ATTR_NAME = "gcube-token";
	public final static String OAUTH_ENDPOINT_CLASS = "Portal";
	public final static String OAUTH_ENDPOINT_NAME = "oauth";
	private static final String OAUTH_ENDPOINT_ENTRYNAME = "jersey-servlet";

	/**
	 * look for the clientId passes as parameter
	 * @param clientId
	 * @return a <code>RequestingApp</code> contanining the application name, the description and the application logo URL if any, or <code>null</code> if non existent
	 */
	public static RequestingApp getAuthorisedApplicationInfoFromIs(String clientId) {
		RequestingApp toReturn = new RequestingApp();
		try {
			String encodedClientId = URLEncoder.encode(clientId, "UTF-8").replaceAll("\\+", "%20");
			String icproxyEndPoint = PortalContext.getICProxyEndPoint();		
			String callToICProxy = new StringBuilder(icproxyEndPoint)
					.append("/")
					.append(encodedClientId)
					.toString();

			URL pageURL = new URL(callToICProxy);
			URLConnection siteConnection = (HttpURLConnection) pageURL.openConnection();
			String portalToken = PortalContext.getPortalApplicationToken();
			siteConnection.addRequestProperty(TOKEN_ATTR_NAME, portalToken);
			InputStream is = null;
			try {
				is =  siteConnection.getInputStream();
			}
			catch (IOException e) {
				_log.warn("The requested clientId does not exist: " + encodedClientId);				
				return null;
			}
			ServiceEndpoint res = Resources.unmarshal(ServiceEndpoint.class, is);
			toReturn.setApplicationId(res.profile().name()); 
			Group<AccessPoint> apGroup = res.profile().accessPoints();
			AccessPoint[] accessPoints = apGroup.toArray(new AccessPoint[apGroup.size()]);
			AccessPoint found = accessPoints[0];
			for (Property prop : found.properties()) {
				if (prop.name().compareTo(LOGOURL_ATTR) == 0) {
					toReturn.setLogoURL(prop.value());
					return toReturn;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return toReturn;
	}

	/**
	 * construct a map for getting attributes from the quiery string 
	 * @param redirectionURL the string in redirect
	 * @return a map containing the attributes, takes into account escaped characters
	 */
	public static Map<String, String> getQueryMap(String redirectionURL) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String decodedURL = "";
			try {
				decodedURL = java.net.URLDecoder.decode(redirectionURL, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				_log.error("UnsupportedEncodingException=" + e.getMessage());
				return new HashMap<String, String>();
			}
			String[] url = decodedURL.split("\\?");
			if (url == null || url.length < 2)
				return new HashMap<String, String>();
			final String query = url[1];
			String[] params = query.split("&");

			for (String param : params) {
				String name = param.split("=")[0];
				String value = param.split("=")[1];
				map.put(name, value);
			}
		}
		catch (Exception e) {
			_log.error("Some exception in getting parameters from query string=" + e.getMessage());
			return map;
		}
		return map;
	}
	/**
	 * <p>
	 * @return a qualifier token for a given user token or <code>null</code> in case of problems
	 * </p>
	 * @param userToken 
	 */
	public static String generateAuthorizationQualifierToken(String appName, String userToken) {
		String qToken;
		String apiQualifier = "AuthorisedApp-"+appName;		
		try {
			String encodedApiQualifier = URLEncoder.encode(apiQualifier, "UTF-8").replaceAll("\\+", "%20");
			String currToken = SecurityTokenProvider.instance.get();
			SecurityTokenProvider.instance.set(userToken);		
			qToken = authorizationService().generateApiKey(encodedApiQualifier);
			SecurityTokenProvider.instance.set(currToken);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return qToken;
	}

	/**
	 * look for the clientId passes as parameter
	 * @param clientId
	 * @return a <code>RequestingApp</code> contanining the application name, the description and the application logo URL if any, or <code>null</code> if non existent
	 */
	public static ServiceEndpoint getAuthorisedApplicationInfoFromIsICClient(String infrastructureName, String clientId) throws Exception  {
		String scope = "/" + infrastructureName;
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		String encodedClientId = URLEncoder.encode(clientId, "UTF-8").replaceAll("\\+", "%20");
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/ID/text() eq '"+ encodedClientId +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		if (toReturn.size() > 0)
			return toReturn.get(0);
		else 
			return null;
	}	
	/**
	 * <p>
	 *  return the authorised redirect for the service endpoint of type OnlineService 
	 * </p>
	 * @param toLookFor an instance of <code>ServiceEndpoint</code>
	 * @return the list of authorised redirectURLs or <code>null
	 */
	public static List<String> getAuthorisedRedirectURLsFromIs(ServiceEndpoint toLookFor) {
		List<String> autRedirectURLs = new ArrayList<>();
		Group<AccessPoint> apGroup =  toLookFor.profile().accessPoints();
		AccessPoint[] accessPoints = (AccessPoint[]) apGroup.toArray(new AccessPoint[apGroup.size()]);
		for (int i = 0; i < accessPoints.length; i++) {
			if (accessPoints[i].name().compareTo(REDIRECT_URL) == 0) {
				AccessPoint found = accessPoints[i];
				autRedirectURLs.add(found.address());
			}
		}
		return autRedirectURLs;
	}
	/**
	 * Instantiates a new gcore endpoint reader.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public static String getOAuthServiceEndPoint(String infrastructureName) throws Exception {
		String scope = "/" + infrastructureName;
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);


		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",OAUTH_ENDPOINT_CLASS));
		query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
		query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",OAUTH_ENDPOINT_NAME));
		query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+OAUTH_ENDPOINT_ENTRYNAME+"\"]/text()");


		DiscoveryClient<String> client = client();
		List<String> toReturn = client.submit(query);
		if (toReturn == null || toReturn.isEmpty()) throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+OAUTH_ENDPOINT_NAME +", serviceClass: " +OAUTH_ENDPOINT_CLASS +", in scope: "+scope);


		ScopeProvider.instance.set(currScope);
		if (toReturn.size() > 0)
			return toReturn.get(0);
		else 
			return null;
	}
	/**
	 * check if the context exists
	 * @param context
	 * @return <code>true</code> if the scope is valid and exists or <code>false</code> otherwise
	 */
	public static boolean isValidContext(String context) {
		GroupManager gm = new LiferayGroupManager();
		try {
			long groupId = gm.getGroupIdFromInfrastructureScope(context);
			return (groupId > 0);
		
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
			_log.error("Something wrong in the Context parameter: " + context + " -> " +e.getMessage());
			return false;
		} 
	}

}
