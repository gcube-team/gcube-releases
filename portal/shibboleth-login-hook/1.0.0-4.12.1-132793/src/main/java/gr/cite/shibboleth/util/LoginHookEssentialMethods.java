package gr.cite.shibboleth.util;

import gr.cite.shibboleth.model.EduUser;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

public class LoginHookEssentialMethods {
	
	private static final Log log = LogFactoryUtil.getLog(LoginHookEssentialMethods.class); 
	
	public static User addUser(HttpSession session, long companyId, EduUser eduUser) throws Exception {
		
		User user = null;

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;
		String emailAddress = eduUser.getEmail();
		String openId = StringPool.BLANK;
		Locale locale = LocaleUtil.getDefault();
		String firstName = eduUser.getName();
		String middleName = StringPool.BLANK;
		String lastName = eduUser.getSurName();
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
			user = UserLocalServiceUtil.addUser(creatorUserId, companyId,
					autoPassword, password1, password2, autoScreenName,
					screenName, emailAddress, 0, openId, locale, firstName,
					middleName, lastName, prefixId, suffixId, male,
					birthdayMonth, birthdayDay, birthdayYear, jobTitle,
					groupIds, organizationIds, roleIds, userGroupIds,
					sendEmail, serviceContext);

			user = UserLocalServiceUtil.updateLastLogin(user.getUserId(), user.getLoginIP());
			user = UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);
			user = UserLocalServiceUtil.updateEmailAddressVerified(user.getUserId(), false);
			log.debug("New user was created succesfully, login with the newly created user");

		} catch (DuplicateUserEmailAddressException e) {
			log.debug("User Email address already exists, abort creation, login with current email address");
		}
		
		session.setAttribute(ShibbolethConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_SHIBBOLETH, emailAddress);
		
		return user;
	}
}
