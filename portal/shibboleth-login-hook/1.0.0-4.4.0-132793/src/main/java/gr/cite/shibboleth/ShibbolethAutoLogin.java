package gr.cite.shibboleth;

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

import gr.cite.shibboleth.util.ShibbolethConstantVariables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ShibbolethAutoLogin extends BaseAutoLogin {

	private static final Log log = LogFactoryUtil.getLog(ShibbolethAutoLogin.class);

	@Override
	protected String[] doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

		long companyId = PortalUtil.getCompanyId(request);

		boolean shibbolethEnabled = PrefsPropsUtil.getBoolean(companyId, ShibbolethConstantVariables.SHIBBOLETH_AUTH_ENABLED, true);

		log.debug("Is Shibboleth enabled: " + shibbolethEnabled);

		if (!shibbolethEnabled) {
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

	private User getUser(HttpServletRequest request, long companyId) throws PortalException, SystemException {

		HttpSession session = request.getSession();
		String emailAddress = GetterUtil.getString(session.getAttribute(ShibbolethConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_SHIBBOLETH));
		session.removeAttribute(ShibbolethConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_SHIBBOLETH);

		if (Validator.isNull(emailAddress)) {
			return null;
		}

		User user = UserLocalServiceUtil.getUserByEmailAddress(companyId, emailAddress);

		return user;
	}

}