package gr.cite.windowslive;


import com.github.scribejava.apis.LiveApi;
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

import gr.cite.windowslive.model.WindowsLiveUserInfo;
import gr.cite.windowslive.scribe.OAuthMyService;
import gr.cite.windowslive.scribe.WindowsLiveMyService;
import gr.cite.windowslive.util.LoginHookEssentialMethods;
import gr.cite.windowslive.util.WindowsLiveConstantVariables;
import gr.cite.windowslive.util.WindowsLiveSpecificMethods;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portal.landingpage.LandingPageManager;


/**
 * @author mnikolopoulos
 *
 */
public class WindowsLiveOAuth extends BaseStrutsAction {
	
	private static final Log log = LogFactoryUtil.getLog(WindowsLiveOAuth.class);
	
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String returnFromWindowsLiveUrl = PortalUtil.getPortalURL(request) + PropsUtil.get(WindowsLiveConstantVariables.RETURN_FROM_WINDOWSLIVE_URL);
		String windowsLiveClientId = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), WindowsLiveConstantVariables.WINDOWSLIVE_CLIENT_ID, WindowsLiveConstantVariables.WINDOWSLIVE_CLIENT_ID);
		String windowsLiveClientSecret = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), WindowsLiveConstantVariables.WINDOWSLIVE_CLIENT_SECRET, PropsUtil.get(WindowsLiveConstantVariables.WINDOWSLIVE_CLIENT_SECRET));
		Boolean canCreateAccount = PrefsPropsUtil.getBoolean(themeDisplay.getCompanyId(), PropsKeys.COMPANY_SECURITY_STRANGERS);
		
		String emailAddress = null;
		User user = null;
		String landingPage = null;
		//initial call to the hook and redirection to windows live
		if (ParamUtil.getString(request, Constants.CMD).equals("login")){
			
			log.info("Initial call for login...");
			setTokenToSessionAndRedirectToWindowsLive(response, session, windowsLiveClientId, windowsLiveClientSecret, returnFromWindowsLiveUrl);
			
		//answer from windows live with the data we need 
		}else{
			
			log.info("User gave permision to read data...");
			
			WindowsLiveUserInfo windowsLiveUserInfo = retrieveUsersWindowsLiveInfo(request, session, windowsLiveClientId, windowsLiveClientSecret);
			emailAddress = WindowsLiveSpecificMethods.returnEmailAddress(windowsLiveUserInfo);
			
			if (windowsLiveUserInfo == null || emailAddress.isEmpty()){
				log.info("Could not extract data, returning...");
				return null;
			}
			
			log.info("User's first name: " + windowsLiveUserInfo.getFirstName());
			log.info("User's last name: " + windowsLiveUserInfo.getLastName());
			log.info("User's email address: " + windowsLiveUserInfo.getEmails().getAccount());
			
			if (canCreateAccount){
				LoginHookEssentialMethods.addUser(session, themeDisplay.getCompanyId(), windowsLiveUserInfo);
				user = UserLocalServiceUtil.getUserById(UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), windowsLiveUserInfo.getEmails().getAccount()).getUserId());
			}else{
				try{
					user = UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), windowsLiveUserInfo.getEmails().getAccount());
					log.debug("Login user " + user.getFullName() + " email address " + user.getEmailAddress());
					session.setAttribute(WindowsLiveConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_WINDOWSLIVE, user.getEmailAddress());
				}catch (PortalException e){
					SessionErrors.add(session, NoSuchUserException.class);
				}
			}
			landingPage = LandingPageManager.getLandingPagePath(request, user);
			response.sendRedirect(landingPage);	
		}
		
		return null;
	}

	public static Log getLog() {
		return log;
	}

	/**
	 * @param response
	 * @param session
	 * @param windowsLiveClientId
	 * @param windowsLiveClientSecret
	 * @param returnFromWindowsLiveUrl
	 * @throws IOException
	 */
	private void setTokenToSessionAndRedirectToWindowsLive(
			HttpServletResponse response, HttpSession session,
			String windowsLiveClientId, String windowsLiveClientSecret,
			String returnFromWindowsLiveUrl) throws IOException {
		
		OAuth20Service service = new ServiceBuilder().apiKey(windowsLiveClientId).apiSecret(windowsLiveClientSecret).callback(returnFromWindowsLiveUrl).scope("wl.emails").build(LiveApi.instance());
		String windowsLiveAuthUrl = service.getAuthorizationUrl();
		
		response.sendRedirect(windowsLiveAuthUrl);
	}

	private WindowsLiveUserInfo retrieveUsersWindowsLiveInfo(HttpServletRequest request, HttpSession session, String windowsLiveClientId, String windowsLiveClientSecret) {
		String oautCode = ParamUtil.getString(request, "code");
		
		OAuthMyService service = (OAuthMyService) new ServiceBuilder().apiKey(windowsLiveClientId).callback(PortalUtil.getPortalURL(request) + PropsUtil.get(WindowsLiveConstantVariables.RETURN_FROM_WINDOWSLIVE_URL)).apiSecret(windowsLiveClientSecret).build(WindowsLiveMyService.instance());

		if (Validator.isNull(oautCode)) {
			return null;
		}
		
		OAuthRequest authRequest = windowsLiveCommunicationForAuth(session, service, oautCode);

		String bodyResponse = authRequest.send().getBody();
		
		return new Gson().fromJson(bodyResponse, WindowsLiveUserInfo.class);
		
	}

	private OAuthRequest windowsLiveCommunicationForAuth(HttpSession session, OAuthMyService service, String oautCode) {
		session.removeAttribute(WindowsLiveConstantVariables.REQUEST_TOKEN_SECRET_WINDOWSLIVE);
		
		Verifier v = new Verifier(oautCode);
		Token accessToken = service.getAccessTokenWithPost(v);

		OAuthRequest authrequest = new OAuthRequest(Verb.GET, WindowsLiveConstantVariables.API_CALL, service);
		service.signRequest(accessToken, authrequest);
		
		return authrequest;
		
	}
}
