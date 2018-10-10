package org.gcube.portlets.admin;

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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class RemovedUserAccountThread implements Runnable {
	private static Log _log = LogFactoryUtil.getLog(RemovedUserAccountThread.class);

	final String SUBJECT = "User account REMOVAL notification";

	private String userName;
	private String fullName;
	private String emailAddress;
	private long userId;

	public RemovedUserAccountThread(long userId,String userName, String fullName, String emailAddress) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.fullName = fullName;
		this.emailAddress = emailAddress;
	}

	@Override
	public void run() {
		handleUserRemoval(userId, userName, fullName, emailAddress);
	}

	private void handleUserRemoval(long userId, String userName, String fullName, String emailAddress) {
		System.out.println("trying removeUser account for " + userName);
		//first remove the account
		try {
			UserLocalServiceUtil.deleteUser(userId);
		} catch (Exception e) {			
			e.printStackTrace();
		} 
		System.out.println("removeUser account for " + userName + " done with success, now notify the managers ... ");
		//the notify the managers
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		RoleManager rm = new LiferayRoleManager();
		try {
			String rootVoName = PortalContext.getConfiguration().getInfrastructureName();
			long groupId = gm.getGroupIdFromInfrastructureScope("/"+rootVoName);
			long infraManagerRoleId = -1;
			try {
				infraManagerRoleId = rm.getRoleIdByName(GatewayRolesNames.INFRASTRUCTURE_MANAGER.getRoleName());
			}
			catch (RoleRetrievalFault e) {
				_log.warn("There is no (Site) Role " + infraManagerRoleId + " in this portal. Will not notify about removed user accounts.");
				return;
			}
			_log.trace("Root is: " + rootVoName + " Scanning roles ....");	

			List<GCubeUser> managers = um.listUsersByGroupAndRole(groupId, infraManagerRoleId); 
			if (managers == null || managers.isEmpty()) {
				_log.warn("There are no users with (Site) Role " + infraManagerRoleId + " on " + rootVoName + " in this portal. Will not notify about removed user accounts.");
			}
			else {
				for (GCubeUser manager : managers) {
					sendNotification(manager, userName, fullName, emailAddress);
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
		.append("<p>").append(sender).append(" ").append("removed his/her account from the portal with the following email: ") // has done something
		.append(newUserEmailAddress)
		.append("</div><br />")
		.append("<p>You received this email because you are an Infrastructure Manager in this portal</p>")
		.append("</div></p>")
		.append("</body>");

		return body.toString();

	}



}
