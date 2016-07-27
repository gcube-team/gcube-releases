package org.gcube.portal.notifications.thread;

import java.util.List;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class NewUserAccountNotificationThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(NewUserAccountNotificationThread.class);

	final String SUBJECT = "New user account notification";

	private String newUserUserName;
	private String newUserFullName;
	private String newUserEmailAddress;


	public NewUserAccountNotificationThread(String newUserUserName,	String newUserFullName, String newUserEmailAddress) {
		super();
		this.newUserUserName = newUserUserName;
		this.newUserFullName = newUserFullName;
		this.newUserEmailAddress = newUserEmailAddress;
	}

	@Override
	public void run() {
		handleUserRegistration(newUserUserName, newUserFullName, newUserEmailAddress);
	}

	private void handleUserRegistration(String newUserUserName,	String newUserFullName, String newUserEmailAddress) {
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		RoleManager rm = new LiferayRoleManager();
		try {
			System.out.println("addUser hook ON");
			String rootVoName = PortalContext.getConfiguration().getInfrastructureName();
			long groupId = gm.getGroupIdFromInfrastructureScope("/"+rootVoName);
			long infraManagerRoleId = -1;
			try {
				infraManagerRoleId = rm.getRoleIdByName(GatewayRolesNames.INFRASTRUCTURE_MANAGER.getRoleName());
			}
			catch (RoleRetrievalFault e) {
				_log.warn("There is no (Site) Role " + infraManagerRoleId + " in this portal. Will not notify about newly user accounts.");
				return;
			}
			_log.trace("Root is: " + rootVoName + " Scanning roles ....");	

			List<GCubeUser> managers = um.listUsersByGroupAndRole(groupId, infraManagerRoleId); 
			if (managers == null || managers.isEmpty()) {
				_log.warn("There are no users with (Site) Role " + infraManagerRoleId + " on " + rootVoName + " in this portal. Will not notify about newly user accounts.");
			}
			else {
				for (GCubeUser manager : managers) {
					sendNotification(manager, newUserUserName, newUserFullName, newUserEmailAddress);
					_log.info("sent email to manager: " + manager.getEmail());	
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void sendNotification(GCubeUser manager, String newUserUserName, String newUserFullName, String newUserEmailAddress) {
		EmailNotification toSend = new EmailNotification(manager.getEmail(), SUBJECT, 
				getHTMLEmail(manager.getFirstName(), newUserUserName, newUserFullName, newUserEmailAddress), null);
		toSend.sendEmail();
	}
	
	private static String getHTMLEmail(String userFirstName, String newUserUserName, String newUserFullName, String newUserEmailAddress) {
		String sender = newUserFullName + " ("+newUserUserName+") ";

		StringBuilder body = new StringBuilder();

		body.append("<body><br />")
		.append("<div style=\"color:#000; font-size:13px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif;\">")
		.append("Dear ").append(userFirstName).append(",")  //dear <user>
		.append("<p>").append(sender).append(" ").append("registered to the portal with the following email: ") // has done something
		.append(newUserEmailAddress)
		.append("</div><br />")
		.append("<p>You received this email because you are an Infrastructure Manager in this portal</p>")
		.append("</div></p>")
		.append("</body>");

		return body.toString();

	}

	
	
}
