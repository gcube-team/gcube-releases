package org.gcube.applicationsupportlayer.social.mailing;

import org.gcube.portal.databook.shared.Notification;
/**
 * The email cached object
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class NotificationMail {
	
	private Notification notification2Send;
	private String vreName;
	private String portalName; 
	private String senderEmail;
	private String[] hashtags;
	
	public NotificationMail(Notification notification2Send, String vreName,	String portalName, String senderEmail, String ... hashtags) {
		super();
		this.notification2Send = notification2Send;
		this.vreName = vreName;
		this.portalName = portalName;
		this.senderEmail = senderEmail;
		this.hashtags = hashtags;
	}

	protected Notification getNotification2Send() {
		return notification2Send;
	}

	protected String getVreName() {
		return vreName;
	}

	protected String getPortalName() {
		return portalName;
	}

	protected String getSenderEmail() {
		return senderEmail;
	}	
	
	protected String[] getHashtags() {
		return hashtags;
	}
}
