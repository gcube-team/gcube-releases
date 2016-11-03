package gr.cite.google;


import gr.cite.additionalemailaddresses.CheckAdditionalEmailAddresses;
import gr.cite.google.model.GoogleJson;
import gr.cite.google.util.GoogleConstantVariables;
import gr.cite.google.util.LoginHookEssentialMethods;

import java.io.IOException;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portal.landingpage.LandingPageManager;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;


public class GoogleOAuth extends BaseStrutsAction {
	
	private static final Log log = LogFactoryUtil.getLog(GoogleOAuth.class);
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		String returnFromGoogleUrl = PortalUtil.getPortalURL(request) + PropsUtil.get(GoogleConstantVariables.RETURN_FROM_GOOGLE_URL);
		String googleClientId = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), GoogleConstantVariables.GOOGLE_CLIENT_ID, PropsUtil.get(GoogleConstantVariables.GOOGLE_CLIENT_ID));
		String googleClientSecret = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), GoogleConstantVariables.GOOGLE_CLIENT_SECRET, PropsUtil.get(GoogleConstantVariables.GOOGLE_CLIENT_SECRET));
		Boolean canCreateAccount = PrefsPropsUtil.getBoolean(themeDisplay.getCompanyId(), PropsKeys.COMPANY_SECURITY_STRANGERS);
		
		User user = null;
		String landingPage = "";
		
		//Initial call to the hook and redirection to google
		if (ParamUtil.getString(request, Constants.CMD).equals("login")){
			
			log.debug("Initial call for login...");
			
			establishConnectionWithGoogle(session, response, returnFromGoogleUrl, googleClientId, googleClientSecret);
			
		//Answer from google with the data we need 
		} else if (ParamUtil.getString(request, Constants.CMD).equals("token")){
			
			String oauthCode = ParamUtil.getString(request, GoogleConstantVariables.OAUTH_CODE);
			
			log.debug("User gave permision to read data...");
			
			if (Validator.isNull(oauthCode)) {
				return null;
			}
			
			GoogleJson googleUserInfo = retrieveUsersGoogleInfo(oauthCode, returnFromGoogleUrl, googleClientId, googleClientSecret);
			
			log.debug("Google's object: " + googleUserInfo);
			
			if (googleUserInfo ==  null){
				throw new LoginException();
			}

			try {
				user = CheckAdditionalEmailAddresses.checkInIfAdditionalEmailAndIfVerified(googleUserInfo.getEmail());
			} catch (Exception e) {
				log.error("Error occured while searching in additional emails", e);
				e.printStackTrace();
				throw e;
			}
			
			if(user != null){
				
				log.info("Email " + googleUserInfo.getEmail() + " has been found in additional Email Addresses");
				session.setAttribute(GoogleConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_GOOGLE, user.getEmailAddress());
				
			}else if (canCreateAccount){
				LoginHookEssentialMethods.addUser(session, themeDisplay.getCompanyId(), googleUserInfo);
				user = UserLocalServiceUtil.getUserById(UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), googleUserInfo.getEmail()).getUserId());
			}else{
				try{
					user = UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), googleUserInfo.getEmail());
					log.debug("Login user " + user.getFullName() + " email address " + user.getEmailAddress());
					session.setAttribute(GoogleConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_GOOGLE, user.getEmailAddress());
				}catch (PortalException e){
					SessionErrors.add(session, NoSuchUserException.class);
				}
			}
			landingPage = LandingPageManager.getLandingPagePath(request, user);
			response.sendRedirect(landingPage);
		}
		return null;
	}


	/**
	 * 
	 * Initial call to google, in order to retrieve authorization code.
	 * 
	 * @param response
	 * @param returnFromLinkedInUrl
	 * @throws IOException
	 */
	private void establishConnectionWithGoogle(HttpSession session, HttpServletResponse response, String returnFromGoogleUrl, String googleClientId, String googleClientSecret) throws IOException {
		
		OAuth20Service service = new ServiceBuilder().apiKey(googleClientId).apiSecret(googleClientSecret).callback(returnFromGoogleUrl).scope(GoogleConstantVariables.API_CALL_SCOPE).build(GoogleApi20.instance());
		String googleAuthUrl  = service.getAuthorizationUrl();
		
		response.sendRedirect(googleAuthUrl);
	}

	/**
	 * 
	 * Using authorization code, we get access token and make the API call we need to retrieve data
	 * from google.
	 * 
	 * @param oauthCode
	 * @param returnFromGoogleUrl
	 * @return GoogleJson
	 */
	private GoogleJson retrieveUsersGoogleInfo(String oauthCode, String returnFromGoogleUrl, String googleClientId, String googleClientSecret) {
		
		OAuth20Service service = new ServiceBuilder().apiKey(googleClientId).apiSecret(googleClientSecret).callback(returnFromGoogleUrl).scope(GoogleConstantVariables.API_CALL_SCOPE).build(GoogleApi20.instance());
		
		Verifier verifier = new Verifier(oauthCode);
		Token accessToken = service.getAccessToken(verifier);
		
		OAuthRequest authRequest = new OAuthRequest(Verb.GET, GoogleConstantVariables.API_CALL, service);
		service.signRequest(accessToken, authRequest);
		
		String bodyResponse = authRequest.send().getBody();
		GoogleJson googleJson = new Gson().fromJson(bodyResponse, GoogleJson.class);
		
		return googleJson;
	}
}
