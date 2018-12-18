package gr.cite.shibboleth.util;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;

import gr.cite.shibboleth.ShibbolethSamlFilter;
import gr.cite.shibboleth.exceptions.EmailAttributeMissmatchSamlApplicationException;
import gr.cite.shibboleth.model.EduUser;

public class ShibbolethSpecificMethods {
	
	private static final Log _log = LogFactoryUtil.getLog(ShibbolethSamlFilter.class);
	
	public static EduUser createUser(HttpServletRequest request) throws EmailAttributeMissmatchSamlApplicationException, SystemException {
		long companyId = PortalUtil.getDefaultCompanyId();
		_log.debug("*** companyId = " + companyId);
		_log.debug("*** ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL = " + ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL);
		_log.debug("*** PropsUtil.get(ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL) = " + PropsUtil.get(ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL));
		
		String shibbolethEmailAttribute = PrefsPropsUtil.getString(
						companyId, 
						ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL, 
						PropsUtil.get(ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL));
		String shibbolethSurnameAttribute = 
				PrefsPropsUtil.getString(
						companyId, 
						ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_FIRST_NAME, 
						PropsUtil.get(ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_FIRST_NAME));
		String shibbolethForeNameAttribute = 
				PrefsPropsUtil.getString(
						companyId, 
						ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_LAST_NAME, 
						PropsUtil.get(ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_LAST_NAME));

		_log.debug("request.getAttribute(Shib-Identity-Provider) value = " + request.getAttribute("Shib-Identity-Provider").toString());
		
		_log.debug("*** Reading the attributes from the Identity provider...\n");
		_log.debug("Looking for shibbolethEmailAttribute="+shibbolethEmailAttribute);
		_log.debug("shibbolethEmailAttribute value: "+request.getAttribute(shibbolethEmailAttribute));
		_log.debug("Looking for shibbolethForeNameAttribute="+shibbolethForeNameAttribute);
		_log.debug("shibbolethForeNameAttribute value: "+request.getAttribute(shibbolethForeNameAttribute));
		_log.debug("Looking for shibbolethSurnameAttribute="+shibbolethSurnameAttribute);
		_log.debug("shibbolethSurnameAttribute value: "+request.getAttribute(shibbolethSurnameAttribute));
		
		EduUser eduUser = new EduUser();
		eduUser.setEmail((String) request.getAttribute(shibbolethEmailAttribute));

		if (Validator.isNull(eduUser.getEmail())) {
			throw new EmailAttributeMissmatchSamlApplicationException("The data from the  identity provider does not contain the attribute >>" + shibbolethEmailAttribute + "<< change it appropriately");
		} else {
			eduUser.setName((String) request.getAttribute(shibbolethForeNameAttribute));
			eduUser.setSurName((String) request.getAttribute(shibbolethSurnameAttribute));

			return eduUser;
		}
	}
}
