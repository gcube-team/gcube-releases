package gr.cite.linkedin;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.gcube.portal.landingpage.LandingPageManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import gr.cite.additionalemailaddresses.CheckAdditionalEmailAddresses;
import gr.cite.linkedin.model.UserInfo;
import gr.cite.linkedin.util.LinkedInConstantVariables;
import gr.cite.linkedin.util.LoginHookEssentialMethods;



public class LinkedInOAuth extends BaseStrutsAction {

	private static final Log log = LogFactoryUtil.getLog(LinkedInOAuth.class);
	private final static String OAUTH2_SERVICE = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code"; 


	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("in LinkedInOauth execute");

		HttpSession session = request.getSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		String returnFromLinkedInUrl = PortalUtil.getPortalURL(request) + PropsUtil.get(LinkedInConstantVariables.RETURN_FROM_LINKEDIN_URL);
		String linkedInClientId = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), LinkedInConstantVariables.LINKEDIN_CLIENT_ID, PropsUtil.get(LinkedInConstantVariables.LINKEDIN_CLIENT_ID));
		String linkedInClientSecret = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), LinkedInConstantVariables.LINKEDIN_CLIENT_SECRET, PropsUtil.get(LinkedInConstantVariables.LINKEDIN_CLIENT_SECRET));
		Boolean canCreateAccount = PrefsPropsUtil.getBoolean(themeDisplay.getCompanyId(), PropsKeys.COMPANY_SECURITY_STRANGERS);

		User user = null;
		String landingPage = null;

		//initial call to the hook and redirection to linkedin
		if (ParamUtil.getString(request, Constants.CMD).equals("login")){

			System.out.println("Initial call for login...");
			setTokenToSessionAndRedirectToLinkedIn(response, session, linkedInClientId, linkedInClientSecret, returnFromLinkedInUrl);

			//answer from linkedin with the data we need 
		}
		else if (ParamUtil.getString(request, Constants.CMD).equals("token")) {

			System.out.println("Returning from LinkedIn... calling retrieveUsersLinkedInInfo");
			UserInfo linkedInUserInfo = retrieveUsersLinkedInInfo(request, session, linkedInClientId, linkedInClientSecret, returnFromLinkedInUrl);

			if (linkedInUserInfo == null){
				log.debug("Could not extract data, returning...");
				return null;
			}

			log.debug("User's first name: " + linkedInUserInfo.getFirstName());
			log.debug("User's last name: " + linkedInUserInfo.getLastName());
			log.debug("User's email address: " + linkedInUserInfo.getEmailAddress());

			try {
				user = CheckAdditionalEmailAddresses.checkInIfAdditionalEmailAndIfVerified(linkedInUserInfo.getEmailAddress());
			} catch (Exception e) {
				log.error("Error occured while searching in additional emails", e);
				e.printStackTrace();
				throw e;
			}

			if(user != null){

				log.debug("Email " + linkedInUserInfo.getEmailAddress() + " has been found in additional Email Addresses");
				session.setAttribute(LinkedInConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_LINKEDIN, user.getEmailAddress());

			}else if (canCreateAccount){
				long groupId = PortalUtil.getScopeGroupId(request);
				String portalURL = PortalUtil.getPortalURL(request, true);
				LoginHookEssentialMethods.addUser(groupId, portalURL, session, themeDisplay.getCompanyId(), linkedInUserInfo);
				user = UserLocalServiceUtil.getUserById(UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), linkedInUserInfo.getEmailAddress()).getUserId());
			}else{
				try{
					user = UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), linkedInUserInfo.getEmailAddress());
					log.debug("Login user " + user.getFullName() + " email address " + user.getEmailAddress());
					session.setAttribute(LinkedInConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_LINKEDIN, user.getEmailAddress());
				}catch (PortalException e){
					SessionErrors.add(session, NoSuchUserException.class);
				}
			}
			//ensure the user is redirected to the requested page before logging in
			if (session.getAttribute("redirectUrlAfterLogin") != null && session.getAttribute("redirectUrlAfterLogin").toString().compareTo("") != 0) {
				landingPage = session.getAttribute("redirectUrlAfterLogin").toString();
				session.setAttribute("redirectUrlAfterLogin", null);
			}
			else {
				landingPage = LandingPageManager.getLandingPagePath(request, user);
			}
			response.sendRedirect(landingPage);
		}
		return null;
	}
	/**
	 * 
	 * Sets Request token in session and redirects to linkedin
	 * 
	 * @param response
	 * @param session
	 * @param linkedInClientId
	 * @param linkedInClientSecret
	 * @param returnFromLinkedInUrl
	 * @throws IOException
	 */
	private void setTokenToSessionAndRedirectToLinkedIn(
			HttpServletResponse response, HttpSession session,
			String linkedInClientId, String linkedInClientSecret,
			String returnFromLinkedInUrl) throws IOException {

		String state = getRandomString(8);
		session.setAttribute(LinkedInConstantVariables.OAUTH_STATE_PARAMANAME, state);
		session.setAttribute(LinkedInConstantVariables.REDIRECT_URI_PARAM, state);

		String linkedInAuthUrl = OAUTH2_SERVICE + ""
				+ "&client_id="+linkedInClientId
				+ "&state="+state
				+ "&redirect_uri="+returnFromLinkedInUrl
				+ "&scope=r_liteprofile%20r_emailaddress";
		log.debug("setTokenToSessionAndRedirectToLinkedIn -> " + linkedInAuthUrl);
		response.sendRedirect(linkedInAuthUrl);
	}


	/**
	 * 
	 * Returns the user info from linkedIn
	 * 
	 * @param request
	 * @param session
	 * @param linkedInClientId
	 * @param linkedInClientSecret
	 * @return UserInfo
	 */
	private UserInfo retrieveUsersLinkedInInfo(HttpServletRequest request, HttpSession session, 
			String linkedInClientId, 
			String linkedInClientSecret,
			String redirectURI) {
		try {
			String oauthVerifier = ParamUtil.getString(request, "code");
			log.info("retrieveUsersLinkedInInfo code="+oauthVerifier);
			session.removeAttribute(LinkedInConstantVariables.REQUEST_TOKEN_SECRET_LINKEDIN);
			////
			String controlSeq2Compare = (String) session.getAttribute(LinkedInConstantVariables.OAUTH_STATE_PARAMANAME);
			session.removeAttribute(LinkedInConstantVariables.OAUTH_STATE_PARAMANAME);
			String controlSequence = ParamUtil.getString(request, LinkedInConstantVariables.OAUTH_STATE_QUERYSTRING_PARAM);

			
			if (ParamUtil.getString(request, "error") != null && ParamUtil.getString(request, "error").compareTo("") != 0) {
				log.warn("User refused to login with LinkedIn probably did not authorised D4Science: "
						+ "ParamUtil.getString(request, \"error\")="+ParamUtil.getString(request, "error"));
				return null;
			}
			
			if (controlSequence.compareTo(controlSeq2Compare) != 0) {
				log.warn("The control sequence, state parameters don't match, man in the middle attack?");
				return null;
			}


			HttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(LinkedInConstantVariables.ACCESS_TOKEN_ENDPOINT);

			// Request parameters and other properties.
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));
			params.add(new BasicNameValuePair("code", oauthVerifier));
			params.add(new BasicNameValuePair("redirect_uri", redirectURI));
			params.add(new BasicNameValuePair("client_id", linkedInClientId));
			params.add(new BasicNameValuePair("client_secret", linkedInClientSecret));
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			//Execute and get the response.
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream myInputStream = entity.getContent();
				try {
					String jsonText = readJSONAsString(myInputStream, "UTF-8");
					log.debug("LinkedIn response: " + jsonText);
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
	private UserInfo parseProfile(HttpClient httpClient, String token) {

		String emailAddress = null;
		String emailURL="https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";
		URI uriEmail;
		URI uriProfile;
		try {
			uriEmail = new URI(emailURL);
			String jsonResponseEmail = doGET(uriEmail, httpClient, token);
			emailAddress = parseEmailJSON(jsonResponseEmail);

			uriProfile = new URI(LinkedInConstantVariables.USERPROFILE_API_ENDPOINT);
			String jsonResponseProfile = doGET(uriProfile, httpClient, token);
			return parseUserLiteProfileJSON(jsonResponseProfile, emailAddress);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 
	 */
	private String doGET(URI uri, HttpClient httpClient, String token) {

		HttpGet request = new HttpGet(uri);
		// add request header as in the documentation, @see https://developer.linkedin.com/documents/authentication
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("cache-control", "no-cache ");
		request.addHeader("X-Restli-Protocol-Version", "2.0.0");
		try {
			HttpResponse httpResponse = httpClient.execute(request);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream myInputStream = entity.getContent();
				try {
					String jsonResponse = readJSONAsString(myInputStream, "UTF-8");
					log.info("*****LinkedIn jsonResponse:\n" + jsonResponse);
					return jsonResponse;
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
	 * @param jsonResponse example
	 * 
	 * {"elements": [{
    	"handle": "urn:li:emailAddress:296722448",
    	"handle~": {"emailAddress": "theEMAIL@gmail.com"}
		}]}
	 * 
	 * @return
	 * @throws ParseException 
	 */
	private String parseEmailJSON(String jsonResponse) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
		String emailAddress = null;
		JSONArray elements = (JSONArray) jsonObject.get("elements");
		int size = elements.size();				
		if (elements != null && size > 0) {
			JSONObject el0= (JSONObject) elements.get(0);
			JSONObject handle= (JSONObject) el0.get("handle~");
			emailAddress = handle.get("emailAddress").toString();
		}
		return emailAddress;
	}

	/**
	 * 
	 * @param jsonResponse example
	 * 
	 *{
	    "localizedLastName": "Smith",
	    "lastName": {
	        "localized": {"en_US": "Smith"},
	        "preferredLocale": {
	            "country": "US",
	            "language": "en"
	        }
	    },
	    "firstName": {
	        "localized": {"en_US": "John"},
	        "preferredLocale": {
	            "country": "US",
	            "language": "en"
	        }
	    },
	    "profilePicture": {"displayImage": "urn:li:digitalmediaAsset:C4E03AQFqFtoZfNWRcg"},
	    "id": "5AH4qPfIA_",
	    "localizedFirstName": "John"
	 }
	 * 
	 * @return
	 * @throws ParseException 
	 */
	private UserInfo parseUserLiteProfileJSON(String jsonResponse, String emailAddress) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

		String lastName = null;
		String firstName = null;
		String id = null;

		lastName = jsonObject.get("localizedLastName").toString();
		firstName = jsonObject.get("localizedFirstName").toString();
		id = jsonObject.get("id").toString();
		UserInfo toReturn = new UserInfo();
		toReturn.setEmailAddress(emailAddress);
		toReturn.setFirstName(firstName);
		toReturn.setLastName(lastName);
		toReturn.setId(id);
		return toReturn;
	}

	/**
	 * 
	 * @param length length of the random string.
	 * @return the random string alphanumeric of the given length
	 */
	private String getRandomString(int length) {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuffer salt = new StringBuffer();
		Random rnd = new Random();
		while (salt.length() < length) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}
	
	public String readJSONAsString(InputStream inputStream, String encoding)
	        throws IOException {
	    return readFully(inputStream).toString(encoding);
	}

	public byte[] readFullyAsBytes(InputStream inputStream)
	        throws IOException {
	    return readFully(inputStream).toByteArray();
	}

	private ByteArrayOutputStream readFully(InputStream inputStream)
	        throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int length = 0;
	    while ((length = inputStream.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    return baos;
	}
}
