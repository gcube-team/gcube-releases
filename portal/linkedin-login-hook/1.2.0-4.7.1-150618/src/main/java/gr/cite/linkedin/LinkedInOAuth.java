package gr.cite.linkedin;


import com.google.gson.Gson;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
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

import gr.cite.additionalemailaddresses.CheckAdditionalEmailAddresses;
import gr.cite.linkedin.model.UserInfo;
import gr.cite.linkedin.util.LinkedInConstantVariables;
import gr.cite.linkedin.util.LoginHookEssentialMethods;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portal.landingpage.LandingPageManager;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


public class LinkedInOAuth extends BaseStrutsAction {
	
	private static final Log log = LogFactoryUtil.getLog(LinkedInOAuth.class);
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
			
			log.debug("Initial call for login...");
			setTokenToSessionAndRedirectToLinkedIn(response, session, linkedInClientId, linkedInClientSecret, returnFromLinkedInUrl);
			
		//answer from linkedin with the data we need 
		}else if (ParamUtil.getString(request, Constants.CMD).equals("token")){
			
			log.debug("User gave permision to read data...");
			UserInfo linkedInUserInfo = retrieveUsersLinkedInInfo(request, session, linkedInClientId, linkedInClientSecret);
			
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
				
				log.info("Email " + linkedInUserInfo.getEmailAddress() + " has been found in additional Email Addresses");
				session.setAttribute(LinkedInConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_LINKEDIN, user.getEmailAddress());
				
			}else if (canCreateAccount){
				LoginHookEssentialMethods.addUser(session, themeDisplay.getCompanyId(), linkedInUserInfo);
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
			
			//landingPage = LandingPageManager.getLandingPagePath(request, user);
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
		
		OAuthService service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(linkedInClientId).apiSecret(linkedInClientSecret).callback(returnFromLinkedInUrl).build();
		Token requestToken = service.getRequestToken();
		String linkedInAuthUrl = service.getAuthorizationUrl(requestToken);
		
		session.setAttribute(LinkedInConstantVariables.REQUEST_TOKEN_SECRET_LINKEDIN, requestToken.getSecret());
		
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
	private UserInfo retrieveUsersLinkedInInfo(HttpServletRequest request, HttpSession session, String linkedInClientId, String linkedInClientSecret) {
		
		String oauthVerifier = ParamUtil.getString(request, LinkedInConstantVariables.OAUTH_VERIFIER);
		String oauthToken = ParamUtil.getString(request, LinkedInConstantVariables.OAUTH_TOKEN);
		String requestTokenSecret = GetterUtil.getString(session.getAttribute(LinkedInConstantVariables.REQUEST_TOKEN_SECRET_LINKEDIN));
		
		session.removeAttribute(LinkedInConstantVariables.REQUEST_TOKEN_SECRET_LINKEDIN);
		
		OAuthService service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(linkedInClientId).apiSecret(linkedInClientSecret).build();

		if (Validator.isNull(oauthVerifier) || Validator.isNull(oauthToken)) {
			return null;
		}
		
		Verifier v = new Verifier(oauthVerifier);
		Token requestToken = new Token(oauthToken, requestTokenSecret);
		Token accessToken = service.getAccessToken((Token) requestToken, v);

		OAuthRequest authRequest = new OAuthRequest(Verb.GET, LinkedInConstantVariables.API_CALL);
		service.signRequest(accessToken, authRequest);

		String bodyResponse = authRequest.send().getBody();
		UserInfo userInfo = new Gson().fromJson(bodyResponse, UserInfo.class);
		
		return userInfo;
		
	}
}
