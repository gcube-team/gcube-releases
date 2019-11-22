package org.gcube.portlets.user.socialprofile.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.socialprofile.client.SocialService;
import org.gcube.portlets.user.socialprofile.shared.UserContext;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SocialServiceImpl extends RemoteServiceServlet implements SocialService {

	private static final Logger logger = LoggerFactory.getLogger(SocialServiceImpl.class);

	private static final String LINKEDIN_HOST_SERVICE_NAME = "host";
	private static final String LINKEDIN_CLIEND_ID_PROPNAME = "client_id";
	private static final String LINKEDIN_CLIEND_SECRET_PROPNAME = "client_secret";

	private static final String LINKEDIN_API_REQUEST = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";

	/**
	 * Retrieve the current user by using the portal manager
	 * @return a GcubeUser object
	 */
	private GCubeUser getCurrentUser(HttpServletRequest request){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser user = pContext.getCurrentUser(request);
		logger.debug("Returning user " + user);
		return user;
	}

	/**
	 * Retrieve the current scope by using the portal manager
	 * @return a string representing the context
	 */
	private String getCurrentContext(HttpServletRequest request){
		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String context = pContext.getCurrentScope(request);
		logger.debug("Returning context " + context);
		return context;
	}

	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			logger.trace("Development Mode ON");
			return false;
		}			
	}

	@Override
	public UserContext getUserContext(String userid) {
		if (userid == null || userid.equals("") || userid.equals(getCurrentUser(this.getThreadLocalRequest()).getUsername())) {
			logger.debug("Own Profile");
			return getOwnProfile();
		}
		else {
			logger.debug(userid + " Reading Profile of " + userid);
			return getUserProfile(userid);
		}
	}

	@Override
	public String saveProfessionalBackground(String summary) {

		String username = getCurrentUser(this.getThreadLocalRequest()).getUsername();
		// parse (html sanitize)
		String toReturn = transformSummary(summary);
		if(isWithinPortal()){
			UserManager um = new LiferayUserManager();
			try{
				GCubeUser user = um.getUserByUsername(username);
				um.setUserProfessionalBackground(user.getUserId(), summary); // save as it is
				return toReturn; // sanitized
			}catch(Exception e){
				logger.error("Unable to save the professional background " + summary + " for user " + username);
				return null;
			}
		}else
			return toReturn; // development mode
	}

	private UserContext getUserProfile(String username) {
		String email = username+"@isti.cnr.it";
		String fullName = username+" FULL";
		String thumbnailURL = "images/Avatar_default.png";
		PortalContext pContext = PortalContext.getConfiguration();
		String contextName = pContext.getCurrentGroupName(this.getThreadLocalRequest());
		if (isWithinPortal()) {		
			try {
				UserManager um = new LiferayUserManager();
				GCubeUser user = um.getUserByUsername(username);
				thumbnailURL = user.getUserAvatarURL();
				fullName = user.getFullname();
				email = user.getEmail();
				HashMap<String, String> vreNames = new HashMap<String, String>();
				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmail(), "", true, false, vreNames);

				String headline = user.getJobTitle();
				String company = user.getLocation_industry();
				String summary = transformSummary(um.getUserProfessionalBackground(user.getUserId()));

				final String MessageAppPageURL = 
						new StringBuilder(PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest()))
						.append(GCubePortalConstants.USER_MESSAGES_FRIENDLY_URL)
						.toString();

				return new UserContext(userInfo, headline, company, summary, contextName, isInfrastructureScope(), false, MessageAppPageURL);

			} catch (Exception e) {
				e.printStackTrace();
				return new UserContext();
			} 
		} else {
			logger.info("Returning test USER");
			HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
			fakeVreNames.put("/gcube/devsec/devVRE","devVRE");

			UserInfo user =  new UserInfo(username, username+ "FULL", thumbnailURL, email, "fakeAccountUrl", true, false, fakeVreNames);
			return new UserContext(user, "", "", ""
					+ "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam."
					+ "", contextName, false, isInfrastructureScope(), "URLFINTO");
		}		
	}

	private UserContext getOwnProfile() {
		try {
			PortalContext pContext = PortalContext.getConfiguration();
			String contextName = pContext.getCurrentGroupName(this.getThreadLocalRequest());
			String username = getCurrentUser(this.getThreadLocalRequest()).getUsername();
			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";

			if (isWithinPortal()) {

				UserManager um = new LiferayUserManager();
				GCubeUser user = um.getUserByUsername(username);
				thumbnailURL = user.getUserAvatarURL();
				fullName = user.getFullname();
				email = user.getEmail();
				String accountURL = "";
				HashMap<String, String> vreNames = new HashMap<String, String>();

				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmail(), accountURL, true, false, vreNames);
				String headline = user.getJobTitle();
				String company = user.getLocation_industry();
				String summary = transformSummary(um.getUserProfessionalBackground(user.getUserId()));

				return new UserContext(userInfo, headline, company, summary, contextName, isInfrastructureScope(), true, "");
			}
			else {
				logger.info("Returning test USER");
				HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
				fakeVreNames.put("/gcube/devsec/devVRE","devVRE");
				//fakeVreNames.put("/gcube/devNext/NexNext","NexNext");

				UserInfo user =  new UserInfo(username, fullName, thumbnailURL, email, "fakeAccountUrl", true, false, fakeVreNames);
				return new UserContext(user, "", "", ""
						+ "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam."
						+ "", contextName, isInfrastructureScope(), true, "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UserContext();
	}


	private String transformSummary(String professionalBackground) {
		String toReturn = escapeHtml(professionalBackground);
		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");
		// then replace all the double spaces by the html version &nbsp;
		toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
		return toReturn;
	}

	@Override
	public Boolean saveHeadline(String newHeadline) {
		try {
			UserManager um = new LiferayUserManager();
			return um.updateJobTitle(um.getUserId(getCurrentUser(this.getThreadLocalRequest()).getUsername()), newHeadline);			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 

	}
	@Override
	public Boolean saveIsti(String institution) {
		//try save the location/industry	
		try {
			UserManager um = new LiferayUserManager();
			um.saveCustomAttr(um.getUserId(getCurrentUser(this.getThreadLocalRequest()).getUsername()), CustomAttributeKeys.USER_LOCATION_INDUSTRY.getKeyName(), institution);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}

	/**
	 * This method fetches the user profile by authenticating with LinkedIn through OAuth2 protocol
	 * once authenitcated it call another methos to get the profile and parse it.
	 * @return true if everything goes ok, false otherwise.
	 */
	@Override
	public String fetchUserProfile(String authCode, String redirectURI) {
		try {
			HashMap<String, String> infoMap = getLinkedInUASInfo();
			HttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(infoMap.get(LINKEDIN_HOST_SERVICE_NAME));

			// Request parameters and other properties.
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));
			params.add(new BasicNameValuePair("code", authCode));
			params.add(new BasicNameValuePair("redirect_uri", redirectURI));
			params.add(new BasicNameValuePair("client_id", infoMap.get(LINKEDIN_CLIEND_ID_PROPNAME)));
			params.add(new BasicNameValuePair("client_secret", infoMap.get(LINKEDIN_CLIEND_SECRET_PROPNAME)));
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			//Execute and get the response.
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream myInputStream = entity.getContent();
				try {
					String jsonText = IOUtils.toString(myInputStream, "UTF-8");
					logger.debug("LinkedIn response: " + jsonText);
					if (jsonText == null)
						return null;

					JSONParser parser = new JSONParser();
					@SuppressWarnings("rawtypes")
					ContainerFactory containerFactory = new ContainerFactory(){
						public List creatArrayContainer() {
							return new LinkedList();
						}
						public Map createObjectContainer() {
							return new LinkedHashMap();
						}
					};
					@SuppressWarnings("unchecked")
					Map<String, String> json = (Map<String, String>) parser.parse(jsonText, containerFactory);

					if (json.get("error") != null)
						return null;

					String token = json.get("access_token");
					if (token == null)
						return null;
					//here we got authorized by the user 
					return parseProfile(httpClient, token);
				}				
				finally {
					myInputStream.close();
				}
			} else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	/**
	 * Ask the basic profile to LinkedIn API for the authenticated user, parse rge profile and write info into local DB.
	 * @param httpClient
	 * @param token
	 * @return the LinkedIn public URL of the user, or null in case of errors
	 */
	private String parseProfile(HttpClient httpClient, String token) {
		HttpGet request = new HttpGet(LINKEDIN_API_REQUEST);
		// add request header as in the documentation, @see https://developer.linkedin.com/documents/authentication
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("cache-control", "no-cache ");
		request.addHeader("X-Restli-Protocol-Version", "2.0.0");
		try {
			logger.debug("Asking LinkedIn profile via http GET for " + getCurrentUser(this.getThreadLocalRequest()).getUsername());
			HttpResponse httpResponse = httpClient.execute(request);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream myInputStream = entity.getContent();
				try {
					String jsonResponse = IOUtils.toString(myInputStream, "UTF-8");
					//xmlResponse =  testParsing();
					logger.debug("LinkedIn jsonResponse: " + jsonResponse);
					GCubeUser currUser = getCurrentUser(this.getThreadLocalRequest());

					JSONParser parser = new JSONParser();
					logger.info("Parsing LinkedIn profile jsonResponse for " + currUser.getUsername());
					String pictureURL = null;
					JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
					//lalal
					JSONObject profilePictureObject = (JSONObject) jsonObject.get("profilePicture");
					JSONObject displayImageObj = (JSONObject) profilePictureObject.get("displayImage~");
					JSONArray displayImageElems = (JSONArray) displayImageObj.get("elements");
					int size = displayImageElems.size();
					if (displayImageElems != null && size > 0) {
						JSONObject elements1 = null;
						if (size > 1) {
							elements1 = (JSONObject) displayImageElems.get(1);
						} else {
							elements1 = (JSONObject) displayImageElems.get(0);
						}
						JSONArray identifiers = (JSONArray) elements1.get("identifiers");
						JSONObject identifier0 = (JSONObject) identifiers.get(0);
						pictureURL = identifier0.get("identifier").toString();
					}	

					if (isWithinPortal()) {
						logger.debug("LinkedIn Profile gotten correctly for " + getCurrentUser(this.getThreadLocalRequest()).getUsername() + " attempting to write into DB ...");						//						//set the picture
						if (pictureURL.compareTo("") != 0 && pictureURL.startsWith("http")) {
							byte[] pictureData = getUserPictureFromURL(httpClient, pictureURL);
							if (pictureData != null) {
								logger.debug("Updating Image Profile with this one: " + pictureURL);
								UserLocalServiceUtil.updatePortrait(currUser.getUserId(), pictureData);
							}
						}
						//						
						if (pictureURL != null)
							return pictureURL;
						else 
							return null;
					} else {
						logger.warn("Development Mode ON, not attempting to write into DB");
						return "fakePublicURL";
					}
				}
				finally {
					myInputStream.close();
				}
			}
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	/**
	 * 
	 * @param httpClient
	 * @param pictureURL
	 * @return a byte array for the picture
	 */
	private byte[] getUserPictureFromURL(HttpClient httpClient, String pictureURL) {
		HttpGet request = new HttpGet(pictureURL);
		try {
			return IOUtils.toByteArray(httpClient.execute(request).getEntity().getContent());
		} catch (Exception e) {
			logger.error("Could not get bytes from picture URL " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * this method return the URL and the keys to access the LinkedIn User AuthN Service (UAS)
	 * @return an hashmap containing the 3 info needed
	 */
	private HashMap<String, String> getLinkedInUASInfo(){
		String scope = getCurrentContext(this.getThreadLocalRequest());
		logger.info("Looking for a LinkedIn UAS in " + scope);
		String previousScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/string() eq 'Service'");
		query.addCondition("$resource/Profile/Name/string() eq 'LinkedIn-user-authorization'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> list = client.submit(query);
		ScopeProvider.instance.set(previousScope);

		if (list.size() > 1) {
			logger.warn("Multiple LinkedIn-user-authorization Service Endpoints available in the scope, should be only one.");
			return null;
		}
		else if (list.size() == 1) {
			ServiceEndpoint se = list.get(0);
			AccessPoint ap = se.profile().accessPoints().iterator().next();
			String authServiceURL =  ap.address();
			String clientId = "";
			String clientSecret = "";
			try {
				for (Property property : ap.properties()) {
					if (property.name().compareTo(LINKEDIN_CLIEND_ID_PROPNAME) == 0) 
						clientId = StringEncrypter.getEncrypter().decrypt(property.value());
					if (property.name().compareTo(LINKEDIN_CLIEND_SECRET_PROPNAME) == 0) 
						clientSecret = StringEncrypter.getEncrypter().decrypt(property.value());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(LINKEDIN_HOST_SERVICE_NAME, authServiceURL);
			map.put(LINKEDIN_CLIEND_ID_PROPNAME, clientId);
			map.put(LINKEDIN_CLIEND_SECRET_PROPNAME, clientSecret);
			return map;
		}
		else return null;
	}
	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		ScopeBean scope = new ScopeBean(getCurrentContext(this.getThreadLocalRequest()));
		return 	scope.is(Type.INFRASTRUCTURE);
	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
