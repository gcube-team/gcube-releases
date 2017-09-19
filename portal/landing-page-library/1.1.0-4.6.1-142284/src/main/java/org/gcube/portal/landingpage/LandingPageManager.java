package org.gcube.portal.landingpage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.notifications.thread.NewUserSiteRegistrationNotificationThread;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class LandingPageManager {
	private static final Logger _log = LoggerFactory.getLogger(LandingPageManager.class);
	public static final String GUEST_GROUP_FRIENDLY_URL = "/guest";
	public static final String PRIVATE_GROUP_SERVLET_MAPPING = PropsUtil.get(PropsKeys.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING);
	public static final String PORTAL_CONTEXT = PortalUtil.getPathContext();

	public static String getLandingPagePath(final HttpServletRequest request) throws PortalException, SystemException {
		User currentUser = PortalUtil.getUser(request);
		return getLandingPagePath(request, currentUser);
	}
	/**
	 * 
	 * @param request
	 * @param currentUser
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static String getLandingPagePath(final HttpServletRequest request, User currentUser) throws PortalException, SystemException {
		String sitePath = StringPool.BLANK;	

		String currentVirtualHost = request.getServerName();
		_log.debug("currentHost is " +  currentVirtualHost);
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			_log.debug("Found  " +  virtualHost.getHostname());
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(currentVirtualHost) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				_log.debug("Found match! Your site is " +  site.getName());
				List<Group> userSites = getSites(currentUser.getUserId());
				boolean isRegistered = false;
				for (Group group : userSites) {
					if (group.getGroupId() == site.getGroupId()) {
						isRegistered = true;
						_log.debug("user " +  currentUser.getFullName() + " is registered to " + site.getName() + ". redirecting ...");
						break;
					}					
				}
				if (! isRegistered) {
					_log.debug("But user " +  currentUser.getFullName() + " is not registered to " + site.getName() + ". going to register ...");
					String portalURL = PortalContext.getConfiguration().getGatewayURL(request);
					registerUserToSite(currentUser, site, portalURL);
				}
				break;
			}
		}
		if (site.getPrivateLayoutsPageCount() > 0) {
			sitePath = getGroupFriendlyURL(request, site);
		} else	{
			_log.debug(site.getName() + " site doesn't have any private page. Default landing page will be used");
		}
		return sitePath;
	}
	
	/**
	 * this method is used to register the user to the group if does not belong to it yet
	 * IMPORTANT: it does not add the user to the Site's private pages if the Site Membership type is different from Private
	 * @param user
	 * @param site
	 * @throws SystemException
	 */
	private static void registerUserToSite(User user, Group site, String siteURL) throws SystemException {
		UserLocalServiceUtil.addGroupUser(site.getGroupId(), user.getUserId());
		_log.debug("User " +  user.getScreenName() +" registered to " + site.getName());
		Thread emailSiteManagersThread = new Thread(new NewUserSiteRegistrationNotificationThread(new LiferayUserManager(), new LiferayRoleManager() ,user, site, siteURL));
		emailSiteManagersThread.start();
	}

	public static List<Group> getSites(final long userId) throws PortalException, SystemException {
		List<Group> sites = new ArrayList<Group>();
		for (Group group : GroupLocalServiceUtil.getUserGroups(userId))	{
			if (group.isRegularSite()
					&& !GUEST_GROUP_FRIENDLY_URL.equalsIgnoreCase(group.getFriendlyURL()))	{
				sites.add(group);
			}
		}
		return sites;
	}
	/**
	 * @param request
	 * @param currentGroup
	 * @param isPrivate
	 * @param isUser
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static String getGroupFriendlyURL(final HttpServletRequest request, final Group currentGroup) throws PortalException, SystemException {
		String friendlyURL = PRIVATE_GROUP_SERVLET_MAPPING;
		StringBundler sb = new StringBundler();
		sb.append(PORTAL_CONTEXT);
		sb.append(friendlyURL);
		sb.append(currentGroup.getFriendlyURL());
		return sb.toString();
	}
}
