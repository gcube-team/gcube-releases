package gr.cite.shibboleth;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portal.landingpage.LandingPageManager;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import gr.cite.additionalemailaddresses.CheckAdditionalEmailAddresses;
import gr.cite.shibboleth.model.EduUser;
import gr.cite.shibboleth.util.LoginHookEssentialMethods;
import gr.cite.shibboleth.util.ShibbolethConstantVariables;
import gr.cite.shibboleth.util.ShibbolethSpecificMethods;

public class ShibbolethSamlFilter extends BaseFilter {

	private static final Log log = LogFactoryUtil.getLog(ShibbolethSamlFilter.class);

	@Override
	protected void processFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws Exception {

		HttpSession session = request.getSession();
		long companyId = PortalUtil.getCompanyId(request);

		Boolean canCreateAccount = PrefsPropsUtil.getBoolean(companyId, PropsKeys.COMPANY_SECURITY_STRANGERS);
		User user = null;
		String landingPage = null;
		EduUser eduUser = ShibbolethSpecificMethods.createUser(request);

		if (eduUser == null) {
			log.error("Could not create user object");
			return;
		} else {

			log.debug("Email: " + eduUser.getEmail());
			log.debug("FirstName: " + eduUser.getName());
			log.debug("LastName: " + eduUser.getSurName());

			try {
				user = CheckAdditionalEmailAddresses.checkInIfAdditionalEmailAndIfVerified(eduUser.getEmail());
			} catch (Exception e) {
				log.error("Error occured while searching in additional emails", e);
				e.printStackTrace();
				throw e;
			}

			if(user != null){

				log.debug("Email " + eduUser.getEmail() + " has been found in additional Email Addresses");
				session.setAttribute(ShibbolethConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_SHIBBOLETH, user.getEmailAddress());

			}else if (canCreateAccount){

				LoginHookEssentialMethods.addUser(session, companyId, eduUser);
				user = UserLocalServiceUtil.getUserById(UserLocalServiceUtil.getUserByEmailAddress(companyId, eduUser.getEmail()).getUserId());

			}else{
				try{

					user = UserLocalServiceUtil.getUserByEmailAddress(companyId, eduUser.getEmail());
					log.debug("Login user " + user.getFullName() + " email address " + user.getEmailAddress());
					session.setAttribute(ShibbolethConstantVariables.USER_EMAIL_ADDRESS_FOR_SESSION_SHIBBOLETH, user.getEmailAddress());

				}catch (PortalException e){
					SessionErrors.add(session, NoSuchUserException.class);
				}
			}

			String customRedirect = null;
			if (session.getAttribute("PORTLET_RENDER_PARAMETERS_") != null) {
				@SuppressWarnings("unchecked")
				ConcurrentHashMap<Long, ConcurrentHashMap> map = (ConcurrentHashMap<Long, ConcurrentHashMap>) session.getAttribute("PORTLET_RENDER_PARAMETERS_");
				for (Long el :map.keySet()) {
					@SuppressWarnings("unchecked")
					ConcurrentHashMap<String, Object> theMapValue = (ConcurrentHashMap<String, Object>) map.get(el);
					for (String key : theMapValue.keySet()) {
						if (key.compareTo("58") == 0) {
							@SuppressWarnings("unchecked")
							HashMap<String, String[]> map2 = (HashMap<String, String[]>) theMapValue.get(key);
							String[] vulo = map2.get("_58_redirect");
							if (vulo != null && vulo.length > 0) {
								customRedirect = vulo[0];
							}
						}	
					}
				}
			}
			
			if (customRedirect == null) {
				System.out.println("customRedirect not found, call getLandingPagePath");
				landingPage = LandingPageManager.getLandingPagePath(request, user);
				response.sendRedirect(landingPage);
			} else {
				System.out.println("customRedirect found");
				response.sendRedirect(customRedirect);
			}
			
		}
		return;
	}

	@Override
	protected Log getLog() {
		return log;
	}
}