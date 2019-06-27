package org.gcube.applicationsupportlayer.social.mailing;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The email cached object
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class NotificationMail {
	private static final Logger _log = LoggerFactory.getLogger(NotificationMail.class);

	private Notification notification2Send;
	private UserManager userManager;
	private String vreName;
	private String portalName; 
	private String senderEmail;
	private String portalURL;
	private String siteLandingPagePath;
	private String[] hashtags;
	private Set<String> mentionedVReGroups;
	private static DatabookStore store = new DBCassandraAstyanaxImpl();

	public NotificationMail(UserManager userManager, String portalURL, String siteLandingPagePath, Notification notification2Send, String vreName, String portalName, String senderEmail, Set<String> mentionedVReGroups, String ... hashtags) {
		super();
		this.userManager = userManager;
		this.portalURL = portalURL;
		this.siteLandingPagePath = siteLandingPagePath;
		this.notification2Send = notification2Send;
		this.vreName = vreName;
		this.portalName = portalName;
		this.senderEmail = senderEmail;
		this.hashtags = hashtags;
		this.mentionedVReGroups = mentionedVReGroups;
	}

	protected Message getMessageNotification(Session session) throws Exception {

		GCubeUser user = null;
		try {
			user = userManager.getUserByUsername(notification2Send.getUserid());
		} catch (Exception e1) {
			e1.printStackTrace();
			_log.warn("While trying to get email for user/group: " + notification2Send.getUserid());
			return null;
		} 
		String email = user.getEmail();

		Message msg2Return = new MimeMessage(session);
	
		if (notification2Send.getType() == NotificationType.POST_ALERT || 
				notification2Send.getType() == NotificationType.COMMENT || 
				notification2Send.getType() == NotificationType.MENTION ||  
				notification2Send.getType() == NotificationType.OWN_COMMENT) {
			String[] splits = senderEmail.split("@");
			StringBuilder sb = new StringBuilder(splits[0]);
			sb.append("+")
			.append(notification2Send.getSubjectid())
			.append("_")
			.append(AppType.POST)
			.append("@")
			.append(splits[1]);
			senderEmail = sb.toString();
		}
		if (notification2Send.getType() == NotificationType.MESSAGE) {
			String[] splits = senderEmail.split("@");
			senderEmail = splits[0] + "+" + notification2Send.getSubjectid()  + "$" + AppType.MSG + "@" + splits[1];
			StringBuilder sb = new StringBuilder(splits[0]);
			sb.append("+")
			.append(notification2Send.getSubjectid())
			.append("_")
			.append(AppType.MSG)
			.append("@")
			.append(splits[1]);
			senderEmail = sb.toString();
		}
		// EMAIL SENDER
		msg2Return.setHeader("Content-Type", "text/html; charset=UTF-8");
		String senderEmailText = new StringBuilder(notification2Send.getSenderFullName()).append(" on ").append(portalName).toString();
		msg2Return.setFrom(new InternetAddress(senderEmail, senderEmailText));
		msg2Return.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

		// retrieve post/comments from its id (if possible)
		Feed feed = null;
		List<Comment> comments = null;
		String commentKey = null;
		String vreNameFromFeed = null;

		if (notification2Send.getType() == NotificationType.COMMENT ||
				notification2Send.getType() == NotificationType.OWN_COMMENT ||  
				notification2Send.getType() == NotificationType.LIKE ||
				notification2Send.getType() == NotificationType.MENTION) {
			try{
				String feedId = notification2Send.getSubjectid();
				feed = store.readFeed(feedId);
				comments = store.getAllCommentByFeed(feedId);
				Collections.sort(comments); // sort them
				commentKey = notification2Send.getCommentKey();

				// try to set vreName when notification is created in infrastructure scope
				String[] splittedVREName = feed.getVreid().split("/");
				if(vreName == null)
					vreName = splittedVREName[splittedVREName.length - 1];

				// if the notification is a comment, extract the vre name from the feed and not from the scope
				if(notification2Send.getType().equals(NotificationType.COMMENT) || 
						notification2Send.getType().equals(NotificationType.OWN_COMMENT) || 
						notification2Send.getType().equals(NotificationType.LIKE) ||
						notification2Send.getType().equals(NotificationType.MENTION)
						)
					vreNameFromFeed = splittedVREName[splittedVREName.length - 1];

			} catch(Exception e){
				_log.error("Unable to retrieve posts/comments", e);
			}
		} 

		String vreNameToUse = (vreNameFromFeed == null) ? vreName : vreNameFromFeed;

		_log.debug("VRE Name for the email's subject is going to be " 
				+ vreNameToUse + "[vreNameFromFeed is " + vreNameFromFeed + ", vreName is " + vreName + "]");

		// set subject
		msg2Return.setSubject(SocialMailingUtil.getSubjectByNotificationType(notification2Send, vreNameToUse, user.getFirstName(), mentionedVReGroups, hashtags));

		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(SocialMailingUtil.getTextEmail(notification2Send, user.getFirstName(), portalURL, siteLandingPagePath, email, feed, comments, commentKey, hashtags), "text/plain; charset=UTF-8"); 

		final MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(SocialMailingUtil.getHTMLEmail(vreNameToUse, notification2Send, user.getFirstName(), portalURL, siteLandingPagePath, email, feed, comments, commentKey, hashtags), "text/html; charset=UTF-8");

		final Multipart mp = new MimeMultipart("alternative");
		mp.addBodyPart(textPart);
		mp.addBodyPart(htmlPart);
		// Set Multipart as the message's content
		msg2Return.setContent(mp);

		msg2Return.setSentDate(new Date());

		return msg2Return;
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

	public String getPortalURL() {
		return portalURL;
	}

	public String getSiteLandingPagePath() {
		return siteLandingPagePath;
	}

	public Set<String> getMentionedVReGroups() {
		return mentionedVReGroups;
	}

}
