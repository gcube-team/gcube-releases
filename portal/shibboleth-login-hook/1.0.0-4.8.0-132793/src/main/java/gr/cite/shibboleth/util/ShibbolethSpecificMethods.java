package gr.cite.shibboleth.util;

import gr.cite.shibboleth.ShibbolethSamlFilter;
import gr.cite.shibboleth.exceptions.EmailAttributeMissmatchSamlApplicationException;
import gr.cite.shibboleth.model.EduUser;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public class ShibbolethSpecificMethods {
	
	private static final Log log = LogFactoryUtil.getLog(ShibbolethSamlFilter.class);
	
	public static EduUser createUser(HttpServletRequest request) throws EmailAttributeMissmatchSamlApplicationException, SystemException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		
		String shibbolethEmailAttribute = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL, PropsUtil.get(ShibbolethConstantVariables.SHIBBOELTH_ATTRIBUTE_EMAIL));
		String shibbolethSurnameAttribute = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_FIRST_NAME, PropsUtil.get(ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_FIRST_NAME));
		String shibbolethForeNameAttribute = PrefsPropsUtil.getString(themeDisplay.getCompanyId(), ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_LAST_NAME, PropsUtil.get(ShibbolethConstantVariables.SHIBBOLETH_ATTRIBUTE_LAST_NAME));

		log.debug("Read the attributes from the Identity provider...");
		
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
