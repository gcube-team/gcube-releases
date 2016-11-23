package org.gcube.portal.invites;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
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
			HttpServletRequest request,
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
			String vreName =  aslSession.getGroupName();
			result = store.saveInvite(invite);
			emailResult = sendInviteEmail(request, aslSession, portalSenderEmail, portalURL, name, lastName, email, vreDescription);
			notifyInviteSent(request, username, aslSession.getScopeName(), invite, vreName);

		} catch (AddressException e) {
			_log.error("Email not valid " + e.getMessage());
			e.printStackTrace();
			return InviteOperationResult.FAILED;			
		}		

		return (emailResult) ? result : InviteOperationResult.FAILED;		
	}

	private Boolean sendInviteEmail(
			HttpServletRequest request,
			ASLSession aslSession, 
			String portalSenderEmail,
			String portalURL,
			String name, 
			String lastName,
			String email,
			String vreDescription) {

		String vreName =  aslSession.getGroupName();
		String fromFullName = aslSession.getUserFullName(); 


		try {
			String subject = "Join me on " + vreName;

			long groupId = aslSession.getGroupId();
			final String linkToAcceptInvite = portalURL + PortalContext.getConfiguration().getSiteLandingPagePath(request)+"/explore?siteId="+groupId;
			final String linkToCreateAccount = portalURL + "/?p_p_id=58&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&saveLastPath=false&_58_struts_action=%2Flogin%2Fcreate_account";

			EmailNotification mailToAdmin = new EmailNotification(
					email , 
					subject, 
					getHTMLEmail(aslSession, name, lastName, email, fromFullName, vreName, vreDescription, linkToAcceptInvite, linkToCreateAccount), 
					request);
			mailToAdmin.sendEmail();
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
	private String getHTMLEmail(
			ASLSession aslSession, 
			String name,
			String lastName,
			String email,
			String fromFullName,
			String vreName,
			String vreDescription,
			String linkToAcceptInvite,
			String linkToCreateAccount) {

		linkToAcceptInvite = " <a href=\"" + linkToAcceptInvite + "\">" + linkToAcceptInvite + "</a> ";
		linkToCreateAccount = " <a href=\"" + linkToCreateAccount + "\">" + linkToCreateAccount + "</a> ";

		StringBuilder body = new StringBuilder();
		body.append("<div style=\"color:#000; font-size:13px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif;\">")
		.append("Dear " + name)
		.append(", <p>")
		.append(fromFullName).append(" has invited you to " + vreName + ", you can find a brief description below:")
		.append("</p>")
		.append("<p>").append(vreDescription)
		.append("</p>")
		.append("<p>To accept the invite just follow this link: " + linkToAcceptInvite) 
		.append("</p>")
		.append("</p>")
		.append("Please note: if you do not have an account yet, sign up first: " + linkToCreateAccount)
		.append("</p>")
		.append("</div>");
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
		long groupId = -1;
		try {
			List<GCubeGroup> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				long grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getInfrastructureScope(grId);
				_log.debug("Comparing: " + groupScope + " " + scope);
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
		Map<GCubeUser, List<GCubeRole>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<GCubeUser> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (GCubeUser usr:users) {
			List<GCubeRole> roles = usersAndRoles.get(usr);
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
	public static void notifyInviteSent(HttpServletRequest request, String username, String scope, Invite invite, String vreName) {

		ArrayList<String> adminEmails = getAdministratorsEmails(scope);
		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + PortalContext.getConfiguration().getGatewayURL(request) +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+invite.getSenderFullName() + " (" + invite.getSenderUserId() +")</b> has invited " + invite.getInvitedEmail() + " to the following environment:");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("</p>");

		String[] allMails = new String[adminEmails.size()];
		adminEmails.toArray(allMails);
		EmailNotification mailToAdmin = new EmailNotification(allMails , "An invite was sent on " + vreName + " by " + invite.getSenderFullName(), body.toString(), request);
		mailToAdmin.sendEmail();
	}

}
