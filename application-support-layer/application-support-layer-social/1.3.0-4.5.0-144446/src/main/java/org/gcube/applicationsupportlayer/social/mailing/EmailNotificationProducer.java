package org.gcube.applicationsupportlayer.social.mailing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotificationProducer extends Thread {
	private static Logger _log = LoggerFactory.getLogger(EmailNotificationsConsumer.class);

	NotificationMail toBuffer;
	
	public EmailNotificationProducer(NotificationMail toBuffer) {
		this.toBuffer = toBuffer;
	}
	
	@Override
	public void run() {
		if (EmailPlugin.BUFFER_EMAILS != null) {
			//sync method to ensure the producer do not put new emails in the meantime
			synchronized(EmailPlugin.BUFFER_EMAILS) {
				EmailPlugin.BUFFER_EMAILS.add(toBuffer);
				_log.debug("Notification email for " + toBuffer.getNotification2Send().getUserid() + " added to the buffer");
			}
		}
	}
}
