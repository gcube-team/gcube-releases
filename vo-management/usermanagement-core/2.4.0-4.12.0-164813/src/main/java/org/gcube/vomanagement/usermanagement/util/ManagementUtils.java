package org.gcube.vomanagement.usermanagement.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;

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
	/**
	 * 
	 * @param serverName the host name of the server that is sending the request (e.g. i-marine.d4science.org)
	 * @return the current Group instance based on the request
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static long getSiteGroupIdFromServletRequest(final String serverName) throws SystemException, PortalException  {
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				return site.getGroupId();
			}
		}
		_log.warn("serverName is " +  serverName + " but i could not find any virtualHost associated to it");
		return -1;
	}

}
