package org.gcube.applicationsupportlayer.social.mailing;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.UserModel;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author Massimiliano Assante
 *
 */
public class EmailPlugin {

	private static final Logger _log = LoggerFactory.getLogger(EmailPlugin.class);
	protected static final int SECONDS2WAIT = 60;
	public static final String WRITE_ABOVE_MESSAGE_REPLY = "- Write ABOVE THIS LINE to reply via email";
	public static final String WRITE_ABOVE_TO_REPLY = WRITE_ABOVE_MESSAGE_REPLY + ", reply with empty msg to subscribe -";
	private static EmailPlugin singleton;
	private static DatabookStore store = new DBCassandraAstyanaxImpl();

	public static EmailPlugin getInstance() {
		if (singleton == null)
			singleton = new EmailPlugin();
		return singleton;
	}

	private EmailPlugin() {		
		new EmailNotificationsConsumer().start();
	}

	protected static ArrayList<NotificationMail> BUFFER_EMAILS = new ArrayList<NotificationMail>();

	private static String getHTMLEmail(Notification notification2Save, 
			String userFirstName, 
			String portalURL, 
			String email, 
			String vreName, 
			Feed feed,
			List<Comment> comments,
			String commentKey,
			String ... hashtags) {
		String removedMarkup = notification2Save.getDescription().replaceAll("&amp;", "&");
		if (hashtags != null && hashtags.length > 0) {
			_log.debug("editing hyperlinks for mail client");
			//notification2Save uri contains the absoulte path to the feed in the correct channel, e.g. /group/ustore_vre?oid=f1637958-34d0-48fc-b5ad-13b1116e389d
			String pathToVRE = "/group/data-e-infrastructure-gateway?";
			if (notification2Save.getUri().split("\\?").length > 0) {
				pathToVRE = notification2Save.getUri().split("\\?")[0];
			}					
			removedMarkup = removedMarkup.replace("href=\"?", "href=\""+portalURL + pathToVRE + "?"); //because there is no indication of the portal		
		}

		String sender = notification2Save.getSenderFullName();
		if (notification2Save.getType() == NotificationType.DOCUMENT_WORKFLOW_STEP_REQUEST_TASK)
			sender = "";

		String portalHost = portalURL.replaceAll("https://", "");
		portalHost = portalHost.replaceAll("http://", "");

		StringBuilder body = new StringBuilder();

		body.append("<body>");

		if (notification2Save.getType() == NotificationType.POST_ALERT || 
				notification2Save.getType() == NotificationType.COMMENT || 
				notification2Save.getType() == NotificationType.MENTION || 
				notification2Save.getType() == NotificationType.OWN_COMMENT) {
			body.append("<div>").append(WRITE_ABOVE_TO_REPLY).append("</div><br />");


		}
		if (notification2Save.getType() == NotificationType.MESSAGE) {
			body.append("<div>").append(WRITE_ABOVE_MESSAGE_REPLY).append("</div><br />");
		}

		body.append("<div style=\"color:#000; font-size:13px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif;\">")
		.append("Dear ").append(userFirstName).append(",")  //dear <user>
		.append("<p>").append(sender).append(" ").append(removedMarkup) // has done something
		.append(getActionLink(notification2Save, portalURL)) //Goto
		.append(buildHtmlDiscussion(notification2Save, feed, comments, commentKey)) // the original discussion
		.append("</p>")  
		.append("</div><br/>")
		.append("<p><div style=\"color:#999999; font-size:11px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif;\">")
		.append("This message was sent to <a href=\"mailto:")
		.append(email)
		.append("\" style=\"color:#3B5998;text-decoration:none\" target=\"_blank\">").append(email).append("</a> by ")
		.append("<a href=\"").append(portalURL).append("\" style=\"color:#3B5998;text-decoration:none\" target=\"_blank\">").append(portalHost)
		.append("</a>. ")
		.append(" If you don't want to receive these emails in the future, please <a href=\"")
		.append(portalURL).append("/group/data-e-infrastructure-gateway/notifications\" style=\"color:#3b5998;text-decoration:none\" target=\"_blank\">unsubscribe</a>.")
		.append("</div></p>")
		.append("<p><div style=\"color:#999999; font-size:10px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif; padding-top:15px;\">")
		.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain ")
		.append("information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.")
		.append("If you have received this communication in error, please notify the <sender> and destroy and delete any copies you may have received.")
		.append("</div></p>")
		.append("</body>");

		return body.toString();

	}

	private static String getTextEmail(
			Notification notification2Save, 
			String userFirstName, 
			String portalURL, 
			String email, 
			Feed feed,
			List<Comment> comments,
			String commentKey,
			String[] hashtags) {

		String removedMarkup = convertHTML2Text(notification2Save.getDescription());		

		if (hashtags != null && hashtags.length > 0) {
			for (int i = 0; i < hashtags.length; i++) {		
				_log.debug("replacing " + hashtags[i]);
				removedMarkup = removedMarkup.replace(hashtags[i], " " + hashtags[i] + " "); //because removing html cause trimming we put spaces back
			}
		}
		_log.debug("*****removedMarkup=\n" + removedMarkup);

		String sender = notification2Save.getSenderFullName();
		if (notification2Save.getType() == NotificationType.DOCUMENT_WORKFLOW_STEP_REQUEST_TASK)
			sender = "";

		String portalHost = portalURL.replaceAll("https://", "");
		portalHost = portalHost.replaceAll("http://", "");

		StringBuilder body = new StringBuilder();


		if (notification2Save.getType() == NotificationType.POST_ALERT || 
				notification2Save.getType() == NotificationType.COMMENT || 
				notification2Save.getType() == NotificationType.MENTION || 
				notification2Save.getType() == NotificationType.OWN_COMMENT) {
			body.append(WRITE_ABOVE_TO_REPLY).append("\n\n");

		}
		if (notification2Save.getType() == NotificationType.MESSAGE) {
			body.append(WRITE_ABOVE_MESSAGE_REPLY).append("\n\n");
		}

		body.append("Dear ").append(userFirstName).append(",")  //dear <user>
		.append("\n").append(sender).append(" ").append(removedMarkup) // has done something
		.append("\nsee: ").append(portalURL).append(notification2Save.getUri())
		.append(buildPlainTextDiscussion(notification2Save, feed, comments, commentKey))
		.append("\n----\n")
		.append("This message was sent to ")
		.append(email)
		.append(" by ")
		.append(portalHost)
		.append(" If you don't want to receive these emails in the future, please unsubscribe here: ")
		.append(portalURL).append("/group/data-e-infrastructure-gateway/notifications .");

		return body.toString();

	}

	/**
	 * Build up a discussion given the feed and its comments. 
	 * @param notification2Save
	 * @param feed
	 * @param comments
	 * @param commentKey if not null, when building the discussion stop at this comment.
	 * @return an html string representing the discussion
	 */
	private static String buildHtmlDiscussion(Notification notification2Save, Feed feed, List<Comment> comments, String commentKey){

		try{

			if(notification2Save.getType().equals(NotificationType.POST_ALERT) || feed == null)
				return "";

			String htmlPost = "<br />" + "<br />----<p>Original post:</p>";
			// data formatter
			Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

			// escape html
			String feedTextNoHtml = convertHTML2Text(feed.getDescription());

			// build up html post + comments
			if(notification2Save.getType() == NotificationType.POST_ALERT || (comments.size() == 0 && notification2Save.getType() == NotificationType.MENTION))
				htmlPost += "<div style=\"margin-top: 10px;  margin-bottom: 10px;padding-left: 15px;  "
						+ "font-style: italic; font-weight:bold\">"
						+ feed.getFullName() 
						+ ": " 
						+ (feedTextNoHtml.length() > 30 ? feedTextNoHtml.substring(0, 30) + " ..." : feedTextNoHtml) 
						+ "<p style=\"font-family:Lucida Grande,"
						+ "Verdana,Bitstream Vera Sans,Arial,sans-serif; "
						+ "white-space: nowrap; font-size: smaller; color: #999;\">" + formatter.format(feed.getTime()) + "</p>"
						+"</div>";
			else
				htmlPost += "<div style=\"margin-top: 10px;  margin-bottom: 10px;padding-left: 15px;  "
						+ "font-style: italic\">"
						+ feed.getFullName() 
						+ ": " 
						+ (feedTextNoHtml.length() > 30 ? feedTextNoHtml.substring(0, 30) + " ..." : feedTextNoHtml) 
						+ "<p style=\"font-family:Lucida Grande,"
						+ "Verdana,Bitstream Vera Sans,Arial,sans-serif; "
						+ "white-space: nowrap; font-size: smaller; color: #999;\">" + formatter.format(feed.getTime()) + "</p>"
						+"</div>";

			for (int i = 0; i < comments.size(); i++) {

				String commentTextNoHtml = convertHTML2Text(comments.get(i).getText());

				if((commentKey != null && comments.get(i).getKey().equals(commentKey)) && !(notification2Save.getType() == NotificationType.POST_ALERT)){
					htmlPost += "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  "
							+ "border-left: 3px solid #ccc; font-style: italic; font-weight:bold\">"
							+ comments.get(i).getFullName() 
							+ ": " 
							+ commentTextNoHtml 
							+ "<p style=\"font-family:Lucida Grande,"
							+ "Verdana,Bitstream Vera Sans,Arial,sans-serif; "
							+ "white-space: nowrap; font-size: smaller; color: #999;\">" + formatter.format(comments.get(i).getTime()) + "</p>"
							+"</div>";

					break;
				}
				else
					htmlPost += "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  "
							+ "border-left: 3px solid #ccc; font-style: italic;\">"
							+ comments.get(i).getFullName() 
							+ ": " 
							+ commentTextNoHtml 
							+ "<p style=\"font-family:Lucida Grande,"
							+ "Verdana,Bitstream Vera Sans,Arial,sans-serif; "
							+ "white-space: nowrap; font-size: smaller; color: #999;\">" + formatter.format(comments.get(i).getTime()) + "</p>"
							+"</div>";
			} 

			return htmlPost;
		}catch(Exception e){
			_log.error("Unable to reconstruct html discussion to put into the email body.", e);
		}

		return "";
	}

	/**
	 * Build a plain text discussion given a feed and its comments.
	 * @param feed
	 * @param comments
	 * @param commentKey if not null, when building the discussion stop at this comment.
	 * @return a string representing the discussion
	 */
	private static String buildPlainTextDiscussion(Notification notification2Save, Feed feed, List<Comment> comments, String commentKey){

		try{
			
			if(notification2Save.getType().equals(NotificationType.POST_ALERT) || feed == null)
				return "";

			// build discussion
			String discussion = "\n\n----\n\nOriginal post:";

			// data formatter
			Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

			// escape html
			String feedTextNoHtml = convertHTML2Text(feed.getDescription());

			// build up  post + comments
			discussion += 
					"\n"
							+ "["  + formatter.format(feed.getTime())  + "] " 
							+ feed.getFullName() 
							+ ": " 
							+ (feedTextNoHtml.length() > 30 ? feedTextNoHtml.substring(0, 30) + " ..." : feedTextNoHtml)
							+ "\n";


			for (int i = 0; i < comments.size(); i++) {

				String commentTextNoHtml = convertHTML2Text(comments.get(i).getText());

				discussion += 
						"\t"
								+ "["  + formatter.format(comments.get(i).getTime())  + "] " 
								+ comments.get(i).getFullName() 
								+ ": " 
								+ commentTextNoHtml 
								+ "\n";

				if(commentKey != null && comments.get(i).getKey().equals(commentKey))
					break;
			}

			return discussion;
		}
		catch(Exception e){
			_log.error("Unable to reconstruct plain text discussion to put into the email body.", e);
		}

		return "";
	}


	/**
	 * Convert html into simple text
	 * 
	 */
	protected static String convertHTML2Text(String html) {
		if (html == null) {
			return null;
		}
		String removedMarkup = html.replaceAll("&amp;", "&");
		removedMarkup = removedMarkup.replaceAll("&gt;", ">");
		removedMarkup = removedMarkup.replaceAll("&lt;", "<");
		String text = removedMarkup;
		try {
			Document document = Jsoup.parse(removedMarkup);
			Element body = document.body();
			text = buildStringFromNode(body).toString();
		}
		catch (Exception e) {
			_log.error("While converting HTML into text: " +e.getMessage());
			return removedMarkup;
		}
		return text;
	}

	private static StringBuffer buildStringFromNode(Node node) {
		StringBuffer buffer = new StringBuffer();

		if (node instanceof TextNode) {
			TextNode textNode = (TextNode) node;
			buffer.append(textNode.text().trim());
		}

		for (Node childNode : node.childNodes()) {
			buffer.append(buildStringFromNode(childNode));
		}

		if (node instanceof Element) {
			Element element = (Element) node;
			String tagName = element.tagName();
			if ("p".equals(tagName) || "br".equals(tagName)) {
				buffer.append("\n");
			}
		}

		return buffer;
	}

	private static String getActionLink(Notification notification2Save, String portalURL) {
		StringBuilder actionLink = new StringBuilder("<a style=\"color:#3B5998; text-decoration:none\" target=\"_blank\" href=\"");
		return completeActonLinkByNotificationType(notification2Save, actionLink, portalURL);
	}


	/**
	 * enqueue the message to send
	 * @param notification2Save
	 * @param vreName
	 * @param portalName
	 * @param senderEmail
	 */
	public void sendNotification(Notification notification2Save, String vreName, String portalName, String senderEmail, String ... hashtags)  {
		EmailNotificationProducer thread = new EmailNotificationProducer(new NotificationMail(notification2Save, vreName, portalName, senderEmail, hashtags));
		thread.start();
	}

	protected static Message getMessageNotification(Session session, Notification notification2Save, String vreName, String portalName,	String senderEmail, String ... hashtags) throws Exception {

		UserModel user = null;
		String portalUrl = null;
		try {
			user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), notification2Save.getUserid());
			portalUrl = PortalUtil.getPortalURL(OrganizationsUtil.getCompany().getVirtualHost(), 443, true);
		} catch (Exception e1) {
			e1.printStackTrace();
			_log.warn("While trying to get email for user/group: " + notification2Save.getUserid());
			return null;
		} 
		String email = user.getEmailAddress();

		Message msg2Return = new MimeMessage(session);

		if (notification2Save.getType() == NotificationType.POST_ALERT || 
				notification2Save.getType() == NotificationType.COMMENT || 
				notification2Save.getType() == NotificationType.MENTION ||  
				notification2Save.getType() == NotificationType.OWN_COMMENT) {
			String[] splits = senderEmail.split("@");
			senderEmail = splits[0] + "+" + notification2Save.getSubjectid()  + "$" + AppType.POST + "@" + splits[1];
		}
		if (notification2Save.getType() == NotificationType.MESSAGE) {
			String[] splits = senderEmail.split("@");
			senderEmail = splits[0] + "+" + notification2Save.getSubjectid()  + "$" + AppType.MSG + "@" + splits[1];
		}
		// EMAIL SENDER
		msg2Return.setHeader("Content-Type", "text/html; charset=UTF-8");
		msg2Return.setFrom(new InternetAddress(senderEmail, portalName));
		msg2Return.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

		// retrieve post/comments from its id (if possible)
		Feed feed = null;
		List<Comment> comments = null;
		String commentKey = null;

		try{
			String feedId = notification2Save.getSubjectid();
			feed = store.readFeed(feedId);
			comments = store.getAllCommentByFeed(feedId);
			Collections.sort(comments); // sort them
			commentKey = notification2Save.getCommentKey();
			
			// try to set vreName when notification is created in infrastructure scope
			String[] splittedVREName = feed.getVreid().split("/");
			if(vreName == null)
				vreName = splittedVREName[splittedVREName.length - 1];
			
		}catch(Exception e){

			_log.error("Unable to retrieve feeds/comments", e);

		}
		
		// set subject
		msg2Return.setSubject(getSubjectByNotificationType(notification2Save, portalUrl, vreName, user.getFirstName(), hashtags));

		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(getTextEmail(notification2Save, user.getFirstName(), portalUrl, email, feed, comments, commentKey, hashtags), "text/plain; charset=UTF-8"); 

		final MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(getHTMLEmail(notification2Save, user.getFirstName(), portalUrl, email, vreName, feed, comments, commentKey, hashtags), "text/html; charset=UTF-8");

		final Multipart mp = new MimeMultipart("alternative");
		mp.addBodyPart(textPart);
		mp.addBodyPart(htmlPart);
		// Set Multipart as the message's content
		msg2Return.setContent(mp);

		msg2Return.setSentDate(new Date());

		return msg2Return;
	}

	/**
	 * construct the subjec of the email
	 * @param notification2Save
	 * @param portalURL
	 * @param vreName
	 * @return
	 */
	private static String getSubjectByNotificationType(Notification notification2Save, String portalURL, String vreName, String userFirstName, String ...optionalParams) {
		switch (notification2Save.getType()) {
		case LIKE:
			return notification2Save.getSenderFullName()+" favorited your post in " + (vreName == null? "" : vreName);
		case COMMENT:
			return notification2Save.getSenderFullName()+" commented on the post in " + (vreName == null? "" : vreName);
		case MESSAGE:
			String messageSubject = (optionalParams != null && optionalParams.length > 0) ? 
					optionalParams[0] : notification2Save.getSenderFullName()+" sent you a message";
					return messageSubject;
		case WP_FOLDER_ADDEDUSER:
			return "New user in a shared folder";
		case WP_FOLDER_REMOVEDUSER:
			return "Removed user in a shared folder";
		case WP_FOLDER_SHARE:
			return notification2Save.getSenderFullName()+ " shared a folder with you";
		case WP_FOLDER_UNSHARE:
			return notification2Save.getSenderFullName()+ " unshared a folder of yours";
		case WP_ADMIN_UPGRADE:
			return notification2Save.getSenderFullName()+ " upgraded you as a folder administrator";
		case WP_ADMIN_DOWNGRADE:
			return notification2Save.getSenderFullName()+ " downgraded you as from folder administrator";
		case WP_ITEM_NEW:
			return notification2Save.getSenderFullName()+ " added an item in a shared folder";
		case WP_ITEM_DELETE:
			return notification2Save.getSenderFullName()+ " deleted an item in a shared folder";
		case WP_ITEM_UPDATED:
			return notification2Save.getSenderFullName()+ " updated an item in a shared folder";
		case WP_ITEM_RENAMED:
			return notification2Save.getSenderFullName()+ " renamed an item in a shared folder";
		case OWN_COMMENT:
			return notification2Save.getSenderFullName() + " commented on your post in " + (vreName == null? "" : vreName);
		case MENTION:
			return notification2Save.getSenderFullName() + " mentioned you in " + (vreName == null? "" : vreName);
		case POST_ALERT: 
			String toReturn = notification2Save.getSenderFullName() + " shared a news on " + vreName;
			if (optionalParams != null) { //in this case optionalParams are the hashtags
				for (int i = 0; i < optionalParams.length; i++) 
					toReturn += " " + optionalParams[i];	
			}
			return toReturn;
		case REQUEST_CONNECTION:
			return "Connection request";
		case JOB_COMPLETED_NOK:
			return "Job Completed";
		case JOB_COMPLETED_OK:
			return "Job Completed";
		case DOCUMENT_WORKFLOW_EDIT:
			return "Shared document workflow view";
		case DOCUMENT_WORKFLOW_VIEW:
			return "Shared document workflow view";
		case DOCUMENT_WORKFLOW_STEP_REQUEST_TASK:
			return vreName +": Request for task";
		case DOCUMENT_WORKFLOW_FIRST_STEP_REQUEST_INVOLVMENT:
			return vreName +": Request for task";
		case DOCUMENT_WORKFLOW_USER_FORWARD_TO_OWNER:
			return vreName +": Document workflow forward";
		case DOCUMENT_WORKFLOW_STEP_FORWARD_PEER:
			return vreName +": Done task notification";
		case DOCUMENT_WORKFLOW_FORWARD_STEP_COMPLETED_OWNER:
			return vreName +": Document workflow step completion";
		case CALENDAR_ADDED_EVENT:
			return vreName +": New event in a shared calendar";
		case CALENDAR_UPDATED_EVENT:
			return vreName +": Edited event in a shared calendar";
		case CALENDAR_DELETED_EVENT:
			return vreName +": Deleted in a shared calendar";
		case TDM_TAB_RESOURCE_SHARE:
			return notification2Save.getSenderFullName() + " shared a Tabular Resource with you on " + vreName;
		case TDM_RULE_SHARE:
			return notification2Save.getSenderFullName() + " shared a Tabular Data Manager Rule with you on " + vreName;
		case TDM_TEMPLATE_SHARE:
			return notification2Save.getSenderFullName() + " shared a Tabular Data Manager Template with you on " + vreName;
		default:
			return "You have a new Notification";
		}
	}

	/**
	 * generate the clickable link
	 * @param notification2Save
	 * @param actionLink
	 * @param portalURL
	 * @return
	 */
	private static String completeActonLinkByNotificationType(Notification notification2Save, StringBuilder actionLink,  String portalURL) {

		actionLink.append(portalURL).append(notification2Save.getUri());  

		switch (notification2Save.getType()) {
		case LIKE:		
			actionLink.append("\">").append(" Open Post").append("</a>");
			break;
		case COMMENT:
			actionLink.append("\">").append(" Open Post").append("</a>");
			break;
		case MENTION:
			actionLink.append("\">").append(" Open Post").append("</a>");
			break;
		case MESSAGE:
			actionLink.append("\">").append(" Go to Message").append("</a>");
			break;
		case WP_FOLDER_ADDEDUSER:
			actionLink.append("\">").append(" Go to Folder").append("</a>");
			break;
		case WP_FOLDER_REMOVEDUSER:
			actionLink.append("\">").append(" Go to Folder").append("</a>");
			break;
		case WP_FOLDER_SHARE:
			actionLink.append("\">").append(" Go to Folder").append("</a>");
			break;
		case WP_ITEM_NEW:
			actionLink.append("\">").append(" Go to Folder").append("</a>");
			break;
		case WP_ITEM_DELETE:
			actionLink.append("\">").append(" Go to Folder").append("</a>");
			break;
		case WP_ITEM_UPDATED:
			actionLink.append("\">").append(" Go to Folder").append("</a>");
			break;
		case OWN_COMMENT:
			actionLink.append("\">").append(" Open Post").append("</a>");
			break;
		case POST_ALERT:
			actionLink.append("\">").append("See this News").append("</a>");
			break;
		case REQUEST_CONNECTION:
			actionLink.append("\">").append(" Go to Contacts Center").append("</a>");
			break;
		case JOB_COMPLETED_NOK:
			actionLink.append("\">").append(" Go to Application").append("</a>");
			break;
		case JOB_COMPLETED_OK:
			actionLink.append("\">").append(" Go to Application").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_EDIT:
			actionLink.append("\">").append(" Go to Document Workflow Manager").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_VIEW:
			actionLink.append("\">").append(" Go to Document Workflow Manager").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_STEP_REQUEST_TASK:
			actionLink.append("\">").append(" Go to Your Document Workflows").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_FIRST_STEP_REQUEST_INVOLVMENT:
			actionLink.append("\">").append(" Go to Your Document Workflows").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_USER_FORWARD_TO_OWNER:
			actionLink.append("\">").append(" Go to Document Workflow Manager").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_STEP_FORWARD_PEER:
			actionLink.append("\">").append(" Go to Document Workflow Manager").append("</a>");
			break;
		case DOCUMENT_WORKFLOW_FORWARD_STEP_COMPLETED_OWNER:
			actionLink.append("\">").append(" Go to Document Workflow Manager").append("</a>");
			break;
		case CALENDAR_ADDED_EVENT:
			actionLink.append("\">").append(" Go to Calendar").append("</a>");
			break;
		case CALENDAR_UPDATED_EVENT:
			actionLink.append("\">").append(" Go to Calendar").append("</a>");
			break;
		case CALENDAR_DELETED_EVENT:
			actionLink.append("\">").append(" Go to Calendar").append("</a>");
			break;
		case TDM_TAB_RESOURCE_SHARE:
			actionLink.append("\">").append(" See this Tabular Resource").append("</a>");
			break;
		case TDM_RULE_SHARE:
		case TDM_TEMPLATE_SHARE:			
			actionLink.append("\">").append(" Go to Tabular Data Manager").append("</a>");
			break;
		default:
			actionLink.append("\">").append("").append("</a>");
			break;			
		}

		return actionLink.toString();
	}

}



