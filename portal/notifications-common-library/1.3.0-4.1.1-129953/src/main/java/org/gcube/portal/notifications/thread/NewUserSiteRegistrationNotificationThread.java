package org.gcube.portal.notifications.thread;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.sites.util.SitesUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class NewUserSiteRegistrationNotificationThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(NewUserSiteRegistrationNotificationThread.class);

	final String SUBJECT = "New User registration to site notification";

	private User user;
	private Group site;
	private String siteURL;

	public NewUserSiteRegistrationNotificationThread(User user, Group site, String siteURL) {
		super();
		this.user = user;
		this.site = site;
		this.siteURL = siteURL;
	}

	@Override
	public void run() {
		handleUserToSiteRegistration(user, site, siteURL);
	}

	private void handleUserToSiteRegistration(final User user, final Group site, String siteURL) {
		UserManager um = new LiferayUserManager();
		RoleManager rm = new LiferayRoleManager();
		try {
			long groupId = site.getGroupId();
			long infraManagerRoleId = -1;
			try {
				infraManagerRoleId = rm.getRoleIdByName(GatewayRolesNames.INFRASTRUCTURE_MANAGER.getRoleName());
			}
			catch (RoleRetrievalFault e) {
				_log.warn("There is no (Site) Role " + GatewayRolesNames.INFRASTRUCTURE_MANAGER.getRoleName() + " in this Portal. Will not notify about newly user accounts.");
				return;
			}
			List<GCubeUser> managers = um.listUsersByGroupAndRole(groupId, infraManagerRoleId); 
			if (managers == null || managers.isEmpty()) {
				_log.warn("There are no users with (Site) Role " + GatewayRolesNames.INFRASTRUCTURE_MANAGER.getRoleName() + " on Site " + site.getName() + ". Will not notify about newly user accounts.");
			}
			else {
				for (GCubeUser manager : managers) {
					sendNotification(manager, user, site, siteURL);
					_log.info("sent email to manager: " + manager.getEmail());	
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void sendNotification(GCubeUser manager, User user, Group site, String siteURL) {
		EmailNotification toSend = new EmailNotification(manager.getEmail(), SUBJECT, 
				getHTMLEmail(manager.getFirstName(), user.getScreenName(), user.getFullName(), user.getEmailAddress(), site, siteURL), null);
		toSend.sendEmail();
	}
	
	private static String getHTMLEmail(String userFirstName, String newUserUserName, String newUserFullName, String newUserEmailAddress, Group site, String siteURL) {
		String sender = newUserFullName + " ("+newUserUserName+") ";

		StringBuilder body = new StringBuilder();

		body.append("<body><br />")
		.append("<div style=\"color:#000; font-size:13px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif;\">")
		.append("Dear ").append(userFirstName).append(",")  //dear <user>
		.append("<p>").append(sender).append(" ").append("registered to the site " + site.getName() + " with the following email: ") // has done something
		.append(newUserEmailAddress)
		.append("<p>You received this email because you are a Manager in this Site: ").append(siteURL).append("</p>")
		.append("</div>")
		.append("</body>");

		return body.toString();

	}

	
	
}
