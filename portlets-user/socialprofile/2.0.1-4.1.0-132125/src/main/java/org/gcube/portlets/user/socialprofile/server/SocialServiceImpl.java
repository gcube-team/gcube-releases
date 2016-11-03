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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.socialprofile.client.SocialService;
import org.gcube.portlets.user.socialprofile.shared.UserContext;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.model.Contact;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SocialServiceImpl extends RemoteServiceServlet implements SocialService {

	private static final Logger _log = LoggerFactory.getLogger(SocialServiceImpl.class);

	private static final String LINKEDIN_HOST_SERVICE_NAME = "host";
	private static final String LINKEDIN_CLIEND_ID_PROPNAME = "client_id";
	private static final String LINKEDIN_CLIEND_SECRET_PROPNAME = "client_secret";

	private static final String LINKEDIN_API_REQUEST = "https://api.linkedin.com/v1/people/~:(id,headline,summary,location:(name),industry,positions,picture-urls::(original),public-profile-url)";

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube");
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
		//		user = "costantino.perciante";
		return user;
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
			_log.trace("Development Mode ON");
			return false;
		}			
	}
	@Override
	public UserContext getUserContext(String userid) {
		if (userid == null || userid.equals("") || userid.equals(getASLSession().getUsername())) {
			System.out.println("Own Profile");
			_log.info("Own Profile");
			return getOwnProfile();
		}
		else {
			System.out.println("Reading Profile");
			_log.info(userid + " Reading Profile");
			return getUserProfile(userid);
		}
	}

	@Override
	public String saveProfessionalBackground(String summary) {
		// parse (html sanitize)
		String toReturn = transformSummary(summary);
		if(isWithinPortal()){
			UserManager um = new LiferayUserManager();
			ASLSession session = getASLSession();
			try{
				GCubeUser user = um.getUserByUsername(session.getUsername());
				um.setUserProfessionalBackground(user.getUserId(), summary); // save as it is
				return toReturn; // sanitized
			}catch(Exception e){
				_log.error("Unable to save the professional background " + summary + " for user " + session.getUsername());
				return null;
			}
		}else
			return toReturn; // development mode
	}

	private UserContext getUserProfile(String username) {
		ASLSession session = getASLSession();
		String email = username+"@isti.cnr.it";
		String fullName = username+" FULL";
		String thumbnailURL = "images/Avatar_default.png";
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

				return new UserContext(userInfo, headline, company, summary, session.getScopeName(), false, isInfrastructureScope());

			} catch (Exception e) {
				e.printStackTrace();
				return new UserContext();
			} 
		} else {
			_log.info("Returning test USER");
			HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
			fakeVreNames.put("/gcube/devsec/devVRE","devVRE");
			//fakeVreNames.put("/gcube/devNext/NexNext","NexNext");

			UserInfo user =  new UserInfo(username, username+ "FULL", thumbnailURL, email, "fakeAccountUrl", true, false, fakeVreNames);
			return new UserContext(user, "", "", ""
					+ "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam."
					+ "", session.getScopeName(), false, isInfrastructureScope());
		}		
	}

	private UserContext getOwnProfile() {
		try {
			ASLSession session = getASLSession();
			String username = session.getUsername();
			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";

			if (isWithinPortal()) {

				UserManager um = new LiferayUserManager();
				GCubeUser user = um.getUserByUsername(username);
				thumbnailURL = user.getUserAvatarURL();
				fullName = user.getFullname();
				email = user.getEmail();
				//				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
				String accountURL = "TODO"; //TODO: //themeDisplay.getURLMyAccount().toString();
				HashMap<String, String> vreNames = new HashMap<String, String>();

				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmail(), accountURL, true, false, vreNames);
				String headline = user.getJobTitle();
				String company = user.getLocation_industry();
				String summary = transformSummary(um.getUserProfessionalBackground(user.getUserId()));

				return new UserContext(userInfo, headline, company, summary, session.getScopeName(), true, isInfrastructureScope() );
			}
			else {
				_log.info("Returning test USER");
				HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
				fakeVreNames.put("/gcube/devsec/devVRE","devVRE");
				//fakeVreNames.put("/gcube/devNext/NexNext","NexNext");

				UserInfo user =  new UserInfo(getASLSession().getUsername(), fullName, thumbnailURL, email, "fakeAccountUrl", true, false, fakeVreNames);
				return new UserContext(user, "", "", ""
						+ "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam."
						+ "", session.getScopeName(), true, isInfrastructureScope() );
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
			return um.updateJobTitle(um.getUserId(getASLSession().getUsername()), newHeadline);			
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
			um.saveCustomAttr(um.getUserId(getASLSession().getUsername()), CustomAttributeKeys.USER_LOCATION_INDUSTRY.getKeyName(), institution);
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
					_log.debug("LinkedIn response: " + jsonText);
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
		try {
			_log.debug("Asking LinkedIn profile via http GET for " + getASLSession().getUsername());
			HttpResponse httpResponse = httpClient.execute(request);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream myInputStream = entity.getContent();
				try {
					String xmlResponse = IOUtils.toString(myInputStream, "UTF-8");
					//xmlResponse =  testParsing();
					_log.debug("LinkedIn xmlResponse: " + xmlResponse);


					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(IOUtils.toInputStream(xmlResponse));

					_log.debug("Parsing LinkedIn profile xmlResponse for " + getASLSession().getUsername());
					String headline = "";
					String summary = "";
					String location = "";
					String industry = "";

					List<String> currValue = null;
					XPathHelper helper = new XPathHelper(doc.getDocumentElement());
					currValue = helper.evaluate("/person/headline/text()");
					if (currValue != null && currValue.size() > 0) {
						headline = currValue.get(0);
					} 
					currValue = helper.evaluate("/person/summary/text()");
					if (currValue != null && currValue.size() > 0) {
						summary = currValue.get(0);
					} 
					currValue = helper.evaluate("/person/location/name/text()");
					if (currValue != null && currValue.size() > 0) {
						location = currValue.get(0);
					} 
					currValue = helper.evaluate("/person/industry/text()");
					if (currValue != null && currValue.size() > 0) {
						industry = currValue.get(0);
					} 
					//positions
					String positions = "";
					currValue = helper.evaluate("/person/positions/position");
					int positionsNo = currValue.size();
					_log.debug("Number of positions: " + positions);
					if (positionsNo > 0) {
						positions = (positionsNo > 1) ? "\n\nCurrent Positions:" : "\n\nCurrent Position:";
						for (int i = 0; i < positionsNo; i++) {		
							List<String> positionTitle = null;
							List<String> companyName = null;
							List<String> companType = null;
							List<String> companSize = null;
							List<String> companyIndustry = null;
							List<String> positionSummary = null;

							positionTitle = helper.evaluate("/person/positions/position["+(i+1)+"]/title/text()");
							if (positionTitle != null && positionTitle.size() > 0) {
								positions += "\n\n"+positionTitle.get(0);
							}
							companyName = helper.evaluate("/person/positions/position["+(i+1)+"]/company/name/text()");
							if (companyName != null && companyName.size() > 0) {
								positions += "\n"+companyName.get(0);
							}
							companType = helper.evaluate("/person/positions/position["+(i+1)+"]/company/type/text()");
							if (companType != null && companType.size() > 0) {
								positions += "\n" + companType.get(0)+";";
							}
							companSize = helper.evaluate("/person/positions/position["+(i+1)+"]/company/size/text()");
							if (companSize != null && companSize.size() > 0) {
								positions += " " + companSize.get(0)+";";
							}
							companyIndustry = helper.evaluate("/person/positions/position["+(i+1)+"]/company/industry/text()");
							if (companyIndustry != null && companyIndustry.size() > 0) {
								positions += "\n" +companyIndustry.get(0);
							}
							positionSummary = helper.evaluate("/person/positions/position["+(i+1)+"]/summary/text()");
							if (positionSummary != null && positionSummary.size() > 0 ) {
								positions +=  "\n\n" + positionSummary.get(0);
							}

						}
					}
					String pictureURL = "";
					currValue = helper.evaluate("/person/picture-urls/picture-url/text()");
					if (currValue != null && currValue.size() > 0) {
						pictureURL = currValue.get(0);
					} 

					String publicProfileURL = "";
					currValue = helper.evaluate("/person/public-profile-url/text()");
					if (currValue != null && currValue.size() > 0) {
						publicProfileURL = currValue.get(0);
					} 

					//add the positions to the summary
					summary += positions;				

					if (isWithinPortal()) {
						_log.debug("LinkedIn Profile gotten correctly for " + getASLSession().getUsername() + " attempting to write into DB ...");
						com.liferay.portal.model.User user;
						user = UserLocalServiceUtil.getUserByScreenName(SiteManagerUtil.getCompany().getCompanyId(), getASLSession().getUsername());
						//headline
						if (headline.compareTo("") != 0) {
							String checkedHeadline = headline;
							if (headline.length() >= 75)
								checkedHeadline = headline.substring(0, 70) + " ...";
							user.setJobTitle(escapeHtml(checkedHeadline));
						}
						//location and industry
						String locationOrIndustry = "";
						if (location.compareTo("") != 0 && industry.compareTo("") != 0) {
							locationOrIndustry = escapeHtml(location + " | " + industry);
						}
						else if (location.compareTo("") != 0 || industry.compareTo("") != 0)
							locationOrIndustry = escapeHtml(location + industry);
						//summary
						if (summary.compareTo("") != 0)
							user.setComments(escapeHtml(summary));

						//public profile URL
						if (publicProfileURL.compareTo("") != 0) {
							Contact contact = user.getContact();
							contact.setMySpaceSn(publicProfileURL);
							ContactLocalServiceUtil.updateContact(contact);
						}

						boolean toReturn = (UserLocalServiceUtil.updateUser(user) != null);
						//set the picture
						if (pictureURL.compareTo("") != 0 && pictureURL.startsWith("http")) {
							byte[] pictureData = getUserPictureFromURL(httpClient, pictureURL);
							if (pictureData != null) {
								_log.debug("Updating Image Profile with this one: " + pictureURL);
								UserLocalServiceUtil.updatePortrait(user.getUserId(), pictureData);
							}
						}

						//update the location/industry
						if(locationOrIndustry != null)
							new LiferayUserManager().saveCustomAttr(user.getUserId(), CustomAttributeKeys.USER_LOCATION_INDUSTRY.getKeyName(), locationOrIndustry);
						if (toReturn)
							return publicProfileURL;
						else return null;
					} else {
						_log.warn("Development Mode ON, not attempting to write into DB");
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
			_log.error("Could not get bytes from picture URL " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * this method return the URL and the keys to access the LinkedIn User AuthN Service (UAS)
	 * @return an hashmap containing the 3 info needed
	 */
	private HashMap<String, String> getLinkedInUASInfo(){
		String scope = getASLSession().getScope();
		_log.info("Looking for a LinkedIn UAS in " + scope);
		String previousScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/string() eq 'Service'");
		query.addCondition("$resource/Profile/Name/string() eq 'LinkedIn-user-authorization'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> list = client.submit(query);
		ScopeProvider.instance.set(previousScope);

		if (list.size() > 1) {
			_log.warn("Multiple LinkedIn-user-authorization Service Endpoints available in the scope, should be only one.");
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
		ScopeBean scope = new ScopeBean(getASLSession().getScope());
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
