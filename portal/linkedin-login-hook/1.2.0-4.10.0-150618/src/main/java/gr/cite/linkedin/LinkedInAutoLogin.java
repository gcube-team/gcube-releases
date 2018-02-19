package gr.cite.linkedin;

import gr.cite.linkedin.util.LinkedInConstantVariables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.BaseAutoLogin;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

public class LinkedInAutoLogin extends BaseAutoLogin {

	private static final Log log = LogFactoryUtil.getLog(LinkedInAutoLogin.class);

	@Override
	protected String[] doLogin(HttpServletRequest request,HttpServletResponse response) throws Exception {

		long companyId = PortalUtil.getCompanyId(request);

		boolean linkedInAuthEnabled = PrefsPropsUtil.getBoolean(companyId, "linkedIn.auth.enabled", true);
		
		log.debug("Is LinkedIn enabled: " + linkedInAuthEnabled);

		if (!linkedInAuthEnabled) {
			return null;
		}

		User user = getUser(request, companyId);

		if (user == null) {
			return null;
		}

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	protected User getUser(HttpServletRequest request, long companyId)
			throws PortalException, SystemException {

		HttpSession session = request.getSession();
		String emailAddress = GetterUtil
						.getString(session
						.getAttribute(LinkedInConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_LINKEDIN));
		
		session.removeAttribute(LinkedInConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_LINKEDIN);
		
		log.debug("User's mail form session: " + emailAddress);

		if (Validator.isNull(emailAddress)) {
			return null;
		}

		User user = UserLocalServiceUtil.getUserByEmailAddress(companyId,
				emailAddress);

		return user;
	}
}