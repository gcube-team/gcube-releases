package org.gcube.portal.socialmail;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.http.HttpServletRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.GenderType;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.mailing.AppType;
import org.gcube.applicationsupportlayer.social.mailing.SocialMailingUtil;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.ex.ColumnNameNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.PrivacyLevelTypeNotFoundException;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.CommentNotificationsThread;
import org.gcube.portal.notifications.thread.LikeNotificationsThread;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class PeriodicTask implements Runnable {
	private static final Log _log = LogFactoryUtil.getLog(PeriodicTask.class);
	private static final String APP_ID_NEWSFEED =  "org.gcube.portlets.user.newsfeed.server.NewsServiceImpl";

	private DatabookStore socialStore;
	private EmailPopAccount popAccount;
	private SocialNetworkingSite site;

	public PeriodicTask(DatabookStore store, EmailPopAccount popAccount, HttpServletRequest request) {
		super();
		this.socialStore = store;
		this.popAccount = popAccount;
		this.site = new SocialNetworkingSite(request);
		String serverName =  request.getServerName();
		//needed because the servlet is called via http
		this.site.setSiteURL("https://"+serverName);
		_log.debug("PeriodicTask instanciated, request serverName=" + serverName);
	}


	@Override
	public void run() {
		try {
			_log.debug("PeriodicTask starting for portal/site having name: " + popAccount.getPortalName());
			check(popAccount.getPortalName(), popAccount.getPop3Server(), popAccount.getPop3user(), popAccount.getPop3password());
		} catch (Exception e) {
			_log.error("Exception during check email account, exiting ans stopping thread... ");
			Thread.currentThread().interrupt();
		}
	}


	/**
	 * @return a fake session usuful for Notifications
	 */
	private ASLSession getFakeASLSession(String emailAddress) {
		ASLSession toReturn = null;
		String sessionID = UUID.randomUUID().toString();
		PortalContext context = PortalContext.getConfiguration();	
		String scope = "/" + context.getInfrastructureName();
		String username = "";
		try {
			UserManager um = new LiferayUserManager();
			GCubeUser user = um.getUserByEmail(emailAddress);
			username = user.getUsername();
			SessionManager.getInstance().getASLSession(sessionID, username).setScope(scope);

			//add the social information needed by apps
			String fullName = user.getFirstName() + " " + user.getLastName();
			String email = user.getEmail();
			String thumbnailURL = user.getUserAvatarURL();
			boolean isMale = user.isMale();

			SessionManager.getInstance().getASLSession(sessionID, username).setUserFullName(fullName);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserEmailAddress(email);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserAvatarId(thumbnailURL);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserGender(isMale? GenderType.MALE : GenderType.FEMALE);

			_log.debug("Created fakesession for user " + username + " email="+emailAddress);
			_log.debug("Setting token for user " + username);
			toReturn = SessionManager.getInstance().getASLSession(sessionID, username);
			setAuthorizationToken(toReturn);

		} catch (Exception e) {
			_log.error("Exception while trying to get the user from email address: " + e.getMessage());
			return null;
		}
		return toReturn;
	}
	private final static String DEFAULT_ROLE = "OrganizationMember";

	private static void setAuthorizationToken(ASLSession session) throws Exception {
		String username = session.getUsername();
		String scope = session.getScope();
		ScopeProvider.instance.set(scope);
		_log.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		session.setSecurityToken(null);
		String token = authorizationService().generateUserToken(new UserInfo(session.getUsername(), userRoles), scope);

		_log.debug("received token: "+token);
		session.setSecurityToken(token);
		_log.debug("PeriodicTask EmailParser: Security token set in session for: "+username + " on " + scope);
	}

	public void check(String portalName, String host, String user, String password) {
		try {
			MailSSLSocketFactory sf = null;
			try {
				sf = new MailSSLSocketFactory();
			}
			catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			sf.setTrustAllHosts(true);

			Properties pop3Props = new Properties();
			pop3Props.setProperty("mail.pop3.ssl.enable", "true");
			pop3Props.setProperty("mail.protocol.ssl.trust", host);
			pop3Props.put("mail.pop3s.ssl.socketFactory", sf);
			pop3Props.setProperty("mail.pop3s.port", "995");
			pop3Props.setProperty("mail.pop3.timeout", "3000"); 
			pop3Props.setProperty("mail.pop3.connectiontimeout", "3000");


			Session emailSession = Session.getDefaultInstance(pop3Props);
			emailSession.setDebug(false);

			//create the POP3 socialStore object and connect with the pop server
			Store store = emailSession.getStore("pop3s");
			_log.debug("Trying to connect to " + host + ", user="+user + " passwd (first 3 char)=" + password.substring(0,3)+"******");
			store.connect(host, user, password);

			//create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_WRITE);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			_log.debug("Found " + messages.length + " new messages ...");

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				_log.info("--------------- FOUND EMAIL ------------------");
				String subject =  message.getSubject();
				_log.info("Parsing email of " + message.getFrom()[0] + " with subject: " + subject);
				if (isValidReply(message)) {
					String subAddressField = extractSubaddress(message);
					_log.info("extractSubaddress filed = " + subAddressField);
					Address[] froms = message.getFrom();
					String emailAddress = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
					ASLSession fakeSession = getFakeASLSession(emailAddress);

					if (fakeSession != null && subAddressField != null) {
						if (subAddressField.endsWith(AppType.POST.toString()) || subAddressField.endsWith(AppType.POST.toString().toLowerCase())) { //it is a post, a comment on a post or a mention
							_log.debug("Looks like a post, a comment on a post or a mention to me");
							String feedId = extractIdentifier(subAddressField);	
							handlePostReply(portalName, feedId, message, fakeSession);
						} 
						else if (subAddressField.endsWith(AppType.MSG.toString()) || subAddressField.endsWith(AppType.MSG.toString().toLowerCase())) { //it is a message
							_log.debug("Looks like a message reply to me");
							String messageId = extractIdentifier(subAddressField);	
							handleMessageReply(portalName, messageId, message, fakeSession);
						} else {
							_log.warn("cannot identify the type of this email reply from " + message.getFrom()[0] + " with subject: " + subject);
						}
					}
					else {
						_log.warn("User Not Recognized, going to discard Message from emailAddress = " + emailAddress);
					}
				}
				else {
					_log.warn("Message is not a valid reply, going to discard Message with subject = " + subject);
				}
				//delete this message				
				message.setFlag(Flags.Flag.DELETED, true);
				_log.debug("Marked DELETE for message: " + subject);
			}

			//close the socialStore and folder objects
			emailFolder.close(true);
			store.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * this method manages the replies coming from message notifications
	 * @param portalName
	 * @param messageId the message identifier in the System managing the messages (currently in the Home Library)
	 * @param message the javax mail Message instance
	 * @param fakeSession
	 * @throws Exception
	 */
	private void handleMessageReply(String portalName, String messageId, Message message, ASLSession fakeSession) {
		String subject =  "";
		String messageText = "";
		try {
			subject = message.getSubject();
			messageText = extractText(portalName, messageId, message);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		_log.debug("Found message reply, subject: " + subject + " body: " + messageText);
		String newMessageId = null;
		Workspace workspace;
		List<String> recipientIds = null;
		try {
			workspace = getWorkspace(fakeSession);

			WorkspaceMessageManager messageManager = workspace.getWorkspaceMessageManager();
			WorkspaceMessage theMessage = messageManager.getReceivedMessage(messageId);
			recipientIds = theMessage.getAddresses();
			//add the sender and remove the person who is replying from the recipients 
			String sender = theMessage.getSender().getPortalLogin();
			String originalText = theMessage.getBody();
			Date originalSentTime = theMessage.getSendTime().getTime();
			recipientIds.add(sender);
			recipientIds.remove(fakeSession.getUsername());

			_log.debug("Message Recipients:");
			for (String rec : recipientIds) {
				_log.debug(rec);
			}
			_log.debug("Constructing reply message");
			messageText += getReplyHeaderMessage(sender, originalSentTime, originalText);

			_log.debug("Trying to send message with subject: " + subject + " to: " + recipientIds.toString());
			newMessageId = messageManager.sendMessageToPortalLogins(subject, messageText, new ArrayList<String>(), recipientIds);

		} catch (WorkspaceFolderNotFoundException | InternalErrorException	| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
		}

		_log.debug("Message with subject: " + subject + " hase been sent, returned id: " + newMessageId);

		if (newMessageId != null) {
			_log.debug("Sending message notifications ... site server URL= " + site.getSiteURL());
			List<GenericItemBean> recipients = getUsersbyUserId(recipientIds);

			NotificationsManager nm = new ApplicationNotificationsManager(
					site,
					fakeSession.getScope(), 
					new SocialNetworkingUser(fakeSession.getUsername(), fakeSession.getUserEmailAddress(), fakeSession.getUserFullName(), fakeSession.getUserAvatarId())
					);
			Thread thread = new Thread(new MessageNotificationsThread(recipients, newMessageId, subject, messageText, nm));
			thread.start();
		} else {
			_log.debug("Could not send message reply");
		}

	}
	private String getReplyHeaderMessage(String senderId, Date date, String message) {		
		List<String> toPass = new ArrayList<String>();
		toPass.add(senderId);
		List<GenericItemBean> theSender = getUsersbyUserId(toPass);
		String senderFullName = senderId;
		if (theSender != null && !theSender.isEmpty())
			senderFullName = theSender.get(0).getAlternativeName();

		String toReturn =  "\n\n---\n on " + date +  " " + senderFullName + " wrote:";
		toReturn += "\n\n"+message;
		return toReturn;
	}
	/**
	 * return the User instance given his id
	 * @param recipientIds
	 * @return
	 */
	private List<GenericItemBean> getUsersbyUserId(List<String> recipientIds)  {
		List<GenericItemBean> recipients = new ArrayList<GenericItemBean>();
		for (String userid : recipientIds) {
			GCubeUser user;
			try {
				user = new LiferayUserManager().getUserByUsername(userid);
				recipients.add(new GenericItemBean(""+user.getUserId(), user.getUsername(), user.getFullname(), ""));
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}	
		return recipients;
	}	

	/**
	 * this method manages the replies coming from post notifications
	 * @param portalName
	 * @param feedId the identifier in the System managing the feeds 
	 * @param message the javax mail Message instance
	 * @param fakeSession
	 * @throws Exception
	 */
	private void handlePostReply(String portalName, String feedId, Message message, ASLSession fakeSession) throws Exception {
		String commentText = extractText(portalName, feedId, message);
		_log.info("Extracted id: " + feedId + " text=" + commentText);
		String escapedCommentText = org.gcube.portal.socialmail.Utils.escapeHtmlAndTransformUrl(commentText);
		String subject =  message.getSubject();

		if (escapedCommentText.trim().compareTo("") == 0) {//it is a favorite/subscription
			_log.debug("Found favorite/subscription for feed with subject: " + subject);
			favoriteFeed(feedId, fakeSession);
		}
		else {
			Comment comment = new Comment(UUID.randomUUID().toString(), fakeSession.getUsername(), 
					new Date(), feedId, escapedCommentText, fakeSession.getUserFullName(), fakeSession.getUserAvatarId());

			_log.debug("The EscapedCommentText =>" + escapedCommentText);
			boolean commentCommitResult = false;
			try {
				if (socialStore.addComment(comment)) 
					commentCommitResult = true;
			} catch (FeedIDNotFoundException e) {
				_log.error("Related post not found for this comment " + e.getMessage());
				e.printStackTrace();
			}
			if (commentCommitResult) { //the notifications should start
				notifyUsersInvolved(comment, escapedCommentText, feedId, fakeSession);
			}
		}

	}

	/**
	 * favorite the feed to subscribe to further comments
	 * @param feedId
	 * @param fakeSession
	 */
	private void favoriteFeed(String feedId, ASLSession fakeSession) {
		if (feedId == null || feedId.compareTo("") == 0) {
			_log.warn("Found email with no feedId from " + fakeSession.getUserEmailAddress() + ". Going to trash it");
			return;
		}


		Like like = new Like(UUID.randomUUID().toString(), fakeSession.getUsername(), 
				new Date(), feedId, fakeSession.getUserFullName(), fakeSession.getUserAvatarId());

		boolean likeCommitResult = false;
		try {
			if (socialStore.like(like)); 
			likeCommitResult = true;
		} catch (FeedIDNotFoundException e) {
			_log.error("Related post not found for this like " + e.getMessage());
			e.printStackTrace();
		}
		if (likeCommitResult) { //the notification should be delivered to the post owner
			try {				
				Feed feed = socialStore.readFeed(feedId);
				String feedOwnerId = feed.getEntityId();
				boolean isAppFeed = feed.isApplicationFeed();

				NotificationsManager nm = new ApplicationNotificationsManager(
						site,
						fakeSession.getScope(), 
						new SocialNetworkingUser(fakeSession.getUsername(), fakeSession.getUserEmailAddress(), fakeSession.getUserFullName(), fakeSession.getUserAvatarId()),
						APP_ID_NEWSFEED
						);
				if (! fakeSession.getUsername().equals(feedOwnerId) && (!isAppFeed)) {				
					boolean result = nm.notifyLikedFeed(feedOwnerId, feedId, "");
					_log.trace("Like Notification to post owner added? " + result);
				} 
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	/**
	 * this method take care of notify all the users that need to be notified when someone comment
	 * @param comment
	 * @param feedId
	 * @param fakeSession
	 * @throws PrivacyLevelTypeNotFoundException
	 * @throws FeedTypeNotFoundException
	 * @throws FeedIDNotFoundException
	 * @throws ColumnNameNotFoundException
	 */
	private void notifyUsersInvolved(Comment comment, String commentText, String feedId, ASLSession fakeSession) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException {
		Feed feed = socialStore.readFeed(feedId);
		String feedOwnerId = feed.getEntityId();
		boolean isAppFeed = feed.isApplicationFeed();

		//if the user who commented this post is not the user who posted it notifies the poster user (Feed owner) 			
		NotificationsManager nm = new ApplicationNotificationsManager(
				site,
				feed.getVreid(), 
				new SocialNetworkingUser(fakeSession.getUsername(), fakeSession.getUserEmailAddress(), fakeSession.getUserFullName(), fakeSession.getUserAvatarId()), 
				APP_ID_NEWSFEED
				);
		if (! fakeSession.getUsername().equals(feedOwnerId) && (!isAppFeed)) {				
			boolean result = nm.notifyOwnCommentReply(feedOwnerId, feedId, commentText, comment.getKey());
			_log.trace("Comment Notification to post owner added? " + result);
		} 

		//if there are users who liked this post they get notified, asynchronously with this thread
		ArrayList<Like> favorites = getAllLikesByFeed(feedId);
		Thread likesThread = new Thread(new LikeNotificationsThread(commentText, nm, favorites, feedOwnerId, comment.getKey()));
		likesThread.start();

		UserManager userManager = new LiferayUserManager();
		//notify the other users who commented this post (excluding the ones above)
		Thread commentsNotificationthread = new Thread(new CommentNotificationsThread(socialStore, userManager, fakeSession.getUsername(), comment.getFeedid(), commentText, nm, feedOwnerId, comment.getKey(), favorites));
		commentsNotificationthread.start();	
	}
	/**
	 * 
	 * @param feedid
	 * @return
	 */
	public ArrayList<Like> getAllLikesByFeed(String feedid) {
		_log.trace("Asking likes for " + feedid);
		ArrayList<Like> toReturn = (ArrayList<Like>) socialStore.getAllLikesByFeed(feedid);
		return toReturn;
	}
	/**
	 * extracts the sub-address (the part after the + and before the @) from the email addressee
	 * @param message
	 * @return the sub-address without + and @
	 * @throws MessagingException
	 */
	private static String extractSubaddress(Message message) throws MessagingException {
		Address[] emails = message.getRecipients(RecipientType.TO);
		String subAddressToReturn = null;
		for (int i = 0; i < emails.length; i++) {
			String toParse = emails[i].toString();
			int plus = toParse.lastIndexOf('+');
			int at = toParse.indexOf('@');
			if (plus >= 0) {
				subAddressToReturn =  toParse.substring(plus+1, at);
				break;
			} 
			else {
				_log.warn("Found recipient with no subaddress, skipping " + toParse);
			}
		}
		return subAddressToReturn;
	}
	/**
	 * extracts the identifier (the part before the $) from the Subaddress
	 * @param message
	 * @return the identifier
	 * @throws MessagingException
	 */
	private static String extractIdentifier(String subAddress) {
		String id = subAddress; //for backward compatibility
		int at = subAddress.indexOf('$');
		if (at > -1)
			id = subAddress.substring(0, at);
		return id;
	}

	/**
	 * the email is considered a valid reply if and only of it contains the EmailPlugin.WRITE_ABOVE_TO_REPLY text in the body
	 * @param message the message to check
	 * @return true if the email is a valid reply, false otherwise
	 */
	private boolean isValidReply(Message message) {
		Object messageContent;
		try {
			messageContent = message.getContent();
			String toParse = null;
			final String SEPARATOR = SocialMailingUtil.WRITE_ABOVE_TO_REPLY.substring(0, 23);
			// Check if content is pure text/html or in parts
			if (messageContent instanceof Multipart) {
				_log.debug("Checking if isValidReply, found Multipart Message, getting text part ... looking for separator " + SEPARATOR);
				Multipart multipart = (Multipart) messageContent;
				BodyPart part = multipart.getBodyPart(0);
				part.toString();
				toParse = part.getContent().toString();
			}
			else  {
				_log.debug("Found a text/plain Message, getting text ... looking for separator " + SEPARATOR);
				toParse = messageContent.toString();
			}
			String[] lines = toParse.split(System.getProperty("line.separator"));
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].contains(SEPARATOR)) {
					_log.debug("Yes, it is a valid Reply");
					return true;
				}
			}
			_log.debug("NOT a valid Reply");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("Exceptiom returning NOT a valid Reply");
			return false;
		} 
	}
	/**
	 * 
	 * @param portalName
	 * @param subjectId
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private static String extractText(String portalName, String subjectId, Message message) throws Exception {
		Object messageContent = message.getContent();
		String toParse = null;
		final String SEPARATOR = SocialMailingUtil.WRITE_ABOVE_TO_REPLY.substring(0, 23);
		// Check if content is pure text/html or in parts
		if (messageContent instanceof Multipart) {
			_log.debug("Found Multipart Message, getting text part ... looking for separator " + SEPARATOR);
			Multipart multipart = (Multipart) messageContent;
			BodyPart part = multipart.getBodyPart(0);
			part.toString();
			toParse = part.getContent().toString();
		}
		else  {
			_log.debug("Found text/plain Message, getting text");
			toParse = messageContent.toString();
		}
		_log.debug("Got Message content = " + toParse);

		//cut the text below the WRITE_ABOVE_TO_REPLY text
		String[] lines = toParse.split(System.getProperty("line.separator"));
		int until = -1;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(SEPARATOR)) {
				until = i;
				break;
			}		
		}

		StringBuilder sb = new StringBuilder();
		/*
		 * this is needed to remove the text added by the email client like when you reply, e.g. On <day> <sender> wrote ... 
		 * it also handles the case where the user writes between that and the WRITE_ABOVE_TO_REPLY text
		 */
		for (int i = 0; i < until; i++) {
			if (! ( lines[i].contains(portalName) || lines[i].contains(subjectId) || (lines[i].startsWith("> ") && lines[i].trim().length() < 2)) ) {
				sb.append(lines[i]);
			} 
			if (! (i == until -1) )
				sb.append("\n");
		}		

		String toReturn = sb.toString().trim();
		_log.debug("Returning text extracted = " + toReturn);
		return toReturn;
	}

	/**
	 * 
	 * @return the workspace instance
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 * @throws WorkspaceFolderNotFoundException
	 */
	private Workspace getWorkspace(ASLSession session) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException {
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
		return workspace;
	}

}
