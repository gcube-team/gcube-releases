package org.gcube.applicationsupportlayer.social.mailing;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.Notification;
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
	

	public static EmailPlugin getInstance() {
		if (singleton == null)
			singleton = new EmailPlugin();
		return singleton;
	}

	private EmailPlugin() {		
		new EmailNotificationsConsumer().start();
	}

	protected static ArrayList<NotificationMail> BUFFER_EMAILS = new ArrayList<NotificationMail>();
	/**
	 * enqueue the message to send
	 * @param notification2Save
	 * @param vreName
	 * @param portalName
	 * @param senderEmail
	 */
	public void sendNotification(String portalURL, String siteLandingPagePath, Notification notification2Save, String vreName, String portalName, String senderEmail, String ... hashtags)  {
		EmailNotificationProducer thread = new EmailNotificationProducer(new NotificationMail(portalURL, siteLandingPagePath, notification2Save, vreName, portalName, senderEmail, hashtags));
		thread.start();
		_log.trace("Thread notification Mail started OK");
	}
}



