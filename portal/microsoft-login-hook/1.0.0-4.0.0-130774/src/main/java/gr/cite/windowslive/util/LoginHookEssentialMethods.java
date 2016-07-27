package gr.cite.windowslive.util;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

import gr.cite.windowslive.model.WindowsLiveUserInfo;

/**
 * @author mnikolopoulos
 *
 */
public class LoginHookEssentialMethods {

	private static final Log log = LogFactoryUtil.getLog(LoginHookEssentialMethods.class);

	/**
	* 
	* Adds the user to the portal if the user's email already exists, just sets
	* to session the email address in order for the user to login.
	* 
	* @param session
	* @param companyId
	* @param userinfo
	* @return User
	* @throws SystemException
	* @throws PortalException
	*/
	public static User addUser(HttpSession session, long companyId, WindowsLiveUserInfo userinfo) throws Exception {

		User user = null;

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;
		String emailAddress = userinfo.getEmails().getAccount();
		String openId = StringPool.BLANK;
		Locale locale = LocaleUtil.getDefault();
		String firstName = userinfo.getFirstName();
		String middleName = StringPool.BLANK;
		String lastName = userinfo.getLastName();
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

		try {
			user = UserLocalServiceUtil.addUser(creatorUserId, companyId, autoPassword, password1, password2,
					autoScreenName, screenName, emailAddress, 0, openId, locale, firstName, middleName, lastName,
					prefixId, suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
					organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);

			user = UserLocalServiceUtil.updateLastLogin(user.getUserId(), user.getLoginIP());
			user = UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);
			user = UserLocalServiceUtil.updateEmailAddressVerified(user.getUserId(), true);
			log.info("New user was created succesfully, login with the newly created user");

		} catch (DuplicateUserEmailAddressException e) {
			log.debug("User Email address already exists, abort creation, login with current email address");
		}

		session.setAttribute(WindowsLiveConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_WINDOWSLIVE, emailAddress);

		return user;
	}

	public static Log getLog() {
		return log;
	}

}
