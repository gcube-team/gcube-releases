package org.gcube.applicationsupportlayer.social.mailing;

import java.util.ArrayList;
import java.util.Set;

import org.gcube.portal.databook.shared.Notification;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante
 * @author Costantino Perciante
 *
 */
public class EmailPlugin {

	private static final Logger _log = LoggerFactory.getLogger(EmailPlugin.class);
	protected static final int SECONDS2WAIT = 60;
	private static EmailPlugin singleton;
	private UserManager userManager;
	/**
	 * 
	 * @param context the infrastucture context (scope)
	 * @return
	 */
	public static EmailPlugin getInstance(UserManager userManager, String context) {
		if (singleton == null) {
			singleton = new EmailPlugin(userManager, context);
		}
		return singleton;
	}

	private EmailPlugin(UserManager userManager, String context) {	
		this.userManager = userManager;
		new EmailNotificationsConsumer(context).start();
	}

	protected static ArrayList<NotificationMail> BUFFER_EMAILS = new ArrayList<NotificationMail>();
	/**
	 * enqueue the message to send
	 * @param notification2Save
	 * @param vreName
	 * @param portalName
	 * @param senderEmail
	 */
	public void sendNotification(String portalURL, String siteLandingPagePath, Notification notification2Save, String vreName, String portalName, String senderEmail, Set<String> mentionedGroups, String ... hashtags)  {
		EmailNotificationProducer thread = new EmailNotificationProducer(new NotificationMail(userManager, portalURL, siteLandingPagePath, notification2Save, vreName, portalName, senderEmail, mentionedGroups, hashtags));
		thread.start();
		_log.trace("Thread notification Mail started OK");
	}
}



