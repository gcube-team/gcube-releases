package org.gcube.portal.invites;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante
 *
 */
public class InvitesManager {
	private static final Logger _log = LoggerFactory.getLogger(InvitesManager.class);
	private final static String MAIL_SERVICE_HOST = "localhost";
	private final static String MAIL_SERVICE_PORT = "25";

	private static InvitesManager instance;
	private static DatabookStore store;

	private InvitesManager() { }

	public static InvitesManager getInstance(){
		instance = new InvitesManager();
		initStore();
		return instance;
	}
	/**
	 * 
	 * @return the unique instance of the store
	 */
	public static synchronized DatabookStore initStore() {
		if (store == null) {
			store = new DBCassandraAstyanaxImpl();
		}
		return store;
	}
	
	public InviteOperationResult sendInvite(
			ASLSession aslSession, 
			String portalSenderEmail,
			String portalURL,
			String name, 
			String lastName,
			String email,
			String vreDescription) {
		
		String username = aslSession.getUsername();
		String fromFullName = aslSession.getUserFullName(); 
		String controlcode = UUID.randomUUID().toString();
		
		Invite invite = new Invite(UUID.randomUUID().toString(), username, aslSession.getScopeName(), email, controlcode, InviteStatus.PENDING, new Date(), fromFullName); 
		InviteOperationResult result = null;
		boolean emailResult = false;
		try {
			result = store.saveInvite(invite);
			emailResult = sendInviteEmail(aslSession, portalSenderEmail, portalURL, name, lastName, email, vreDescription);
			notifyInviteSent(username,  aslSession.getScopeName(), portalURL, EmailNotification.getPortalInstanceName(), invite);
			
		} catch (AddressException e) {
			_log.error("Email not valid " + e.getMessage());
			e.printStackTrace();
			return InviteOperationResult.FAILED;			
		}		
				
		return (emailResult) ? result : InviteOperationResult.FAILED;		
	}
	
	private Boolean sendInviteEmail(
			ASLSession aslSession, 
			String portalSenderEmail,
			String portalURL,
			String name, 
			String lastName,
			String email,
			String vreDescription) {
		
		String vreName =  aslSession.getGroupName();
		String fromFullName = aslSession.getUserFullName(); 
	
		
		Properties props = System.getProperties();
		Session session = null;
		props.put("mail.smtp.host", MAIL_SERVICE_HOST);
		props.put("mail.smtp.port", MAIL_SERVICE_PORT);
		session = Session.getDefaultInstance(props);
		session.setDebug(true);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setHeader("Content-Type", "text/plain; charset=UTF-8");
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(portalSenderEmail, fromFullName));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(aslSession.getUserEmailAddress()));

			// Set Subject: header field
			message.setSubject("Join me on " + vreName);

			// Now set the actual message
			message.setText(getTextEmail(aslSession, portalURL, name, lastName, email, fromFullName, vreName, vreDescription));

			// Send message
			Transport.send(message);
			_log.debug("Sent message successfully to " + email );
		} catch (Exception mex) {
			mex.printStackTrace();
			_log.error("Sent message ERROR to " + email );
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param aslSession
	 * @param portalUrl the url of the portal
	 * @param name the name of the invited person
	 * @param lastName the last name of the invited person
	 * @param email the email address of the invite person
	 * @param fromFullName who is inviting
	 * @param vreName the name of the environment where you are inviting the person
	 * @param vreDescription the description of the environment where you are inviting the person
	 * @return the email text
	 */
	private String getTextEmail(
			ASLSession aslSession, 
			String portalUrl, 
			String name,
			String lastName,
			String email,
			String fromFullName,
			String vreName,
			String vreDescription) {
	
		long organizationId = aslSession.getGroupId();
		
		StringBuilder body = new StringBuilder();
		
		body.append("Dear " + name)
		.append(",\n")
		.append(fromFullName).append(" has invited you to " + vreName + ", you can find a brief description below:")
		.append("\n")
		.append("\n").append(convertHTML2Text(vreDescription))
		.append("\n\n")
		.append("To accept the invite just follow this link: " + portalUrl + "/group/data-e-infrastructure-gateway/join-new?orgid="+organizationId)
		.append("\n\n")
		.append("Please note: if you do not have an account yet, sign up first: " + portalUrl + "/web/guest/home?p_p_id=58&_58_struts_action=%2Flogin%2Fcreate_account")
		.append("\n\n\n\n")
		.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain")
		.append("information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. "
				+ "If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, "
				+ "you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.")
				.append("If you have received this communication in error, please notify the <sender> and destroy and delete any copies you may have received.");

		return body.toString();

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
			if ("p".equals(tagName) || "br".equals(tagName) || "div".equals(tagName) || "h1".equals(tagName) || "h2".equals(tagName) || "h3".equals(tagName) || "h4".equals(tagName)) {
				buffer.append("\n");
			}
		}

		return buffer;
	}
	
	private static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		String groupId = null;
		try {
			List<org.gcube.vomanagement.usermanagement.model.GroupModel> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				String grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getScope(grId);
				System.out.println("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (UserManagementSystemException e) {
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		}
		HashMap<UserModel, List<RoleModel>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<UserModel> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (UserModel usr:users) {
			List<RoleModel> roles = usersAndRoles.get(usr);
			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getRoleName().equals("VO-Admin") || roles.get(i).getRoleName().equals("VRE-Manager")) {
					adminEmailsList.add(usr.getEmail());
					_log.debug("Admin: " + usr.getFullname());
					break;
				}
			}
		}
		return adminEmailsList;
	}
	
	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void notifyInviteSent(String username, String scope, String portalbasicurl, String gatewayName, Invite invite) {
			
		ArrayList<String> adminEmails = getAdministratorsEmails(scope);
				
		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+invite.getSenderFullName() + " (" + invite.getSenderUserId() +")</b> has invited " + invite.getInvitedEmail() + " to the following environment:");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("</p>");
		body.append("<p>");
		body.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain"+
		" information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. " +
		"If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.");
		body.append("</p>");
		
		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification("no-reply@d4science.org", allMails , "[" + gatewayName + "] - Sent Invitation", body.toString());

		mailToAdmin.sendEmail();
	}
	
}
