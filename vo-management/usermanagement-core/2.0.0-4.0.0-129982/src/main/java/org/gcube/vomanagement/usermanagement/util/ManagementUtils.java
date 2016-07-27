package org.gcube.vomanagement.usermanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.CompanyLocalServiceUtil;

public class ManagementUtils {
	/**
	 * logger
	 */
	private static final Logger _log = LoggerFactory.getLogger(ManagementUtils.class);
	
	
	/**
	 * 
	 */
	private static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";


	public static Company getCompany() throws PortalException, SystemException {
		return CompanyLocalServiceUtil.getCompanyByWebId(getDefaultCompanyWebId());
	}
	/**
	 * 
	 * @return the default company web-id (e.g. iMarine.eu)
	 */
	public static String getDefaultCompanyWebId() {
		String defaultWebId = "";
		try {
			defaultWebId = GetterUtil.getString(PropsUtil.get("company.default.web.id"));
		}
		catch (NullPointerException e) {
			_log.info("Cound not find property company.default.web.id in portal.ext file returning default web id: " + DEFAULT_COMPANY_WEB_ID);
			return DEFAULT_COMPANY_WEB_ID;
		}
		return defaultWebId;
	}
}
