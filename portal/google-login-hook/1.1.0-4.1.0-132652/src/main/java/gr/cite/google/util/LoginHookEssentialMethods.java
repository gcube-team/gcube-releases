package gr.cite.google.util;

import gr.cite.google.model.GoogleJson;
import gr.cite.google.util.GoogleConstantVariables;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletURLFactoryUtil;

public class LoginHookEssentialMethods {
	
	private static final Log log = LogFactoryUtil.getLog(LoginHookEssentialMethods.class);

	/**
	 * 
	 * Try to add new user to portal, then set user's email address in session.
	 * 
	 * 
	 * @param session
	 * @param companyId
	 * @param googleJson
	 * @return User
	 * @throws SystemException, PortalException, EmailAddressException
	 */
	public static User addUser(HttpSession session, long companyId, GoogleJson googleJson) throws PortalException, SystemException {
		
		User user = null;

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;
		String emailAddress = googleJson.getEmail();
		String openId = StringPool.BLANK;
		Locale locale = LocaleUtil.getDefault();
		String firstName = googleJson.getGivenName();
		String middleName = StringPool.BLANK;
		String lastName = googleJson.getFamilyName();
		int prefixId = 0;
		int suffixId = 0;
		boolean male = false;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		ServiceContext serviceContext = new ServiceContext();

		try{
			user = UserLocalServiceUtil.addUser(creatorUserId, companyId,
					autoPassword, password1, password2, autoScreenName, screenName,
					emailAddress, 0, openId, locale, firstName, middleName,
					lastName, prefixId, suffixId, male, birthdayMonth, birthdayDay,
					birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
					userGroupIds, sendEmail, serviceContext);
	
			user = UserLocalServiceUtil.updateLastLogin(user.getUserId(),user.getLoginIP());
			user = UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);
			user = UserLocalServiceUtil.updateEmailAddressVerified(user.getUserId(), true);
			log.info("New user was created. Login new user with current credentials");
		
		}catch (DuplicateUserEmailAddressException e){
			log.info("User with the same email address already exists. Login user with said mail.");
		}

		session.setAttribute(GoogleConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_GOOGLE, emailAddress);

		return user;
	}
	
	/**
	 * 
	 * Redirects User to appropriate portal page.
	 * 
	 * @param response
	 * @param redirectUrlCustom is the portal page user would visit if user was already logged in
	 * @param redirectUrlDefault is the default landing page of the portal, which is a fall-back in case of empty redirectUrlCustom
	 * @throws IOException
	 * @throws SystemException 
	 * @throws PortalException 
	 * @throws WindowStateException 
	 * @throws PortletModeException 
	 */
	public static void reditectUserToHisPage(HttpServletResponse response, HttpServletRequest request, String redirectUrlCustom, String redirectUrlDefault) throws PortletModeException, WindowStateException, IOException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLFactoryUtil.create(request, PortletKeys.LOGIN, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
		
		portletURL.setPortletMode(PortletMode.VIEW);
		portletURL.setWindowState(LiferayWindowState.POP_UP);
		portletURL.setParameter("redirect", redirectUrlCustom);
		portletURL.setParameter("struts_action", "/login/login_redirect");

		response.sendRedirect(portletURL.toString());	
	}
}
