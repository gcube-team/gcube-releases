package org.gcube.vomanagement.usermanagement.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.VirtualGroupNotExistingException;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
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
	/**
	 * read the list of virtual groups the current site (i-marine, services etc. ) should show up
	 * @param actualGroupId
	 * @return he list of virtual groups the current site (i-marine, services etc. ) should show up
	 * @throws GroupRetrievalFault
	 * @throws VirtualGroupNotExistingException
	 */
	public static List<VirtualGroup> getVirtualGroupsBySiteGroupId(long actualGroupId) throws GroupRetrievalFault, VirtualGroupNotExistingException {
		List<VirtualGroup> toReturn = new ArrayList<VirtualGroup>();
		try {
			long userId = LiferayUserManager.getAdmin().getUserId();
			PrincipalThreadLocal.setName(userId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(userId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
			Group site = GroupLocalServiceUtil.getGroup(actualGroupId);
		//	_log.debug("Set Thread Permission done, getVirtual Group of " + site.getName());
			if (site.getExpandoBridge().getAttribute(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName()) == null ||  site.getExpandoBridge().getAttribute(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName()).equals("")) {
				String warningMessage = String.format("Attribute %s not initialized.", CustomAttributeKeys.VIRTUAL_GROUP.getKeyName());
				_log.warn(warningMessage); 
				throw new VirtualGroupNotExistingException(warningMessage);
			} else {
				String[] values = (String[]) site.getExpandoBridge().getAttribute(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName());  
				VirtualGroup toAdd = new VirtualGroup();
				if (values != null && values.length > 0) {
					for (int i = 0; i < values.length; i++) {
						toAdd = new VirtualGroup();
						String[] splits = values[i].split("\\|");
						toAdd.setName(splits[0]);
						toAdd.setDescription(splits[1]);
						toReturn.add(toAdd);
						//_log.debug("VirtualGroup selected found for " + site.getName() + " -> " + toAdd.getName());
					}					
				} else {
					toAdd.setName("NoVirtualGroupAssigned");
					toAdd.setDescription("NoVirtualGroupDescription");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}
}
