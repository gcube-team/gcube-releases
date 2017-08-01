package org.gcube.applicationsupportlayer.social;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gcube.applicationsupportlayer.social.mailing.EmailPlugin;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.RunningJob;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 * use to notify users from within your application 
 */
public class ApplicationNotificationsManager extends SocialPortalBridge implements NotificationsManager {
	private static final Logger _log = LoggerFactory.getLogger(ApplicationNotificationsManager.class);
	public static final String USER_WORKSPACE_FRIENDLY_URL = "/workspace";
	public static final String USER_MESSAGES_FRIENDLY_URL = "/messages";
	public static final String USER_NOTIFICATION_FRIENDLY_URL = "/notifications";

	private String portalName;
	private String senderEmail;
	private String portalURL;
	private String siteLandingPagePath;
	private UserManager userManager;
	/**
	 * <p>
	 * Use this constructor if you do not need notifications to point back to your applications
	 * </p>
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 * @param site an instance of {@link SocialNetworkingSite} filled with the required data
	 */
	public ApplicationNotificationsManager(SocialNetworkingSite site, String scope, SocialNetworkingUser currUser) {
		super(scope, currUser);
		setContext(site);
		userManager = new LiferayUserManager();
		_log.warn("Asked for Simple Notification (without redirect to creator)");
	}

	/**
	 * <p>
	 * Use this constructor if your artifact is not deployed on the portal and do not need notifications to point back to your applications 
	 * </p>
	 * @param userManager a remote implementation of {@link UserManager},see {@link LiferayWSUserManager}
	 * @param site an instance of {@link SocialNetworkingSite} filled with the required data
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 */
	public ApplicationNotificationsManager(UserManager userManager, SocialNetworkingSite site, String scope, SocialNetworkingUser currUser) {
		super(scope, currUser);
		setContext(site);
		this.userManager = userManager;
		_log.warn("Asked for Simple Notification (without redirect to creator)");
	}
	/**
	 * <p>
	 * Use this constructor if you do need notifications to point back to your applications, 
	 * make sure you create your application profile on the infrastructure.
	 * </p>
	 * @see http://wiki.gcube-system.org/gcube/index.php/Social_Networking_Library#Create_Your_Application_Profile
	 * 
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 * @param site an instance of {@link SocialNetworkingSite} filled with the required data
	 * @param portletClassName your portlet class name will be used ad unique identifier for your applicationProfile
	 */
	public ApplicationNotificationsManager(SocialNetworkingSite site, String scope, SocialNetworkingUser currUser, String portletClassName) {
		super(scope, currUser, portletClassName);
		setContext(site);
		userManager = new LiferayUserManager();
	}
	/**
	 * <p>
	 * Use this constructor if your artifact is not deployed on the portal and you do need notifications to point back to your applications, 
	 * make sure you create your application profile on the infrastructure.
	 * </p>
	 * @see http://wiki.gcube-system.org/gcube/index.php/Social_Networking_Library#Create_Your_Application_Profile
	 * 
	 * @param userManager a remote implementation of {@link UserManager},see {@link LiferayWSUserManager}
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 * @param site an instance of {@link SocialNetworkingSite} filled with the required data
	 * @param portletClassName your portlet class name will be used ad unique identifier for your applicationProfile
	 */
	public ApplicationNotificationsManager(UserManager userManager, SocialNetworkingSite site, String scope, SocialNetworkingUser currUser, String portletClassName) {
		super(scope, currUser, portletClassName);
		setContext(site);
		this.userManager = userManager;
	}
	/**
	 * set the current portal context (name, emal, url)
	 * @param request
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private void setContext(SocialNetworkingSite site) {
		siteLandingPagePath = site.getSiteLandingPagePath();
		portalName = site.getName();
		senderEmail = site.getSenderEmail();
		portalURL = site.getSiteURL();
		_log.info("ApplicationNotificationsManager setContext for Site:  " + portalURL);
		_log.info("siteLandingPagePath:  " + siteLandingPagePath);
		_log.info("senderEmail:  " + senderEmail);
		_log.info("portalName:  " + senderEmail);
	}
	
	/**
	 * actually save the notification to the store
	 * @param notification2Save the notification instance to save
	 * @return true if the notification was sent ok
	 */
	private boolean saveNotification(Notification notification2Save, String ... hashtags) {
		_log.trace("Trying to send notification to: " + notification2Save.getUserid() +  " Type: " + notification2Save.getType());
		if (notification2Save.getSenderid().compareTo(notification2Save.getUserid()) == 0) {
			_log.trace("Sender and Receiver are the same " + notification2Save.getUserid() +  " Notification Stopped");
			return true;  //I'm not sending notifications to the person who triggered it, pretend I sent it though
		}
		List<NotificationChannelType> channels = null;
		try {
			channels = getStoreInstance().getUserNotificationChannels(notification2Save.getUserid(), notification2Save.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean result = false;
		if (channels.contains(NotificationChannelType.PORTAL)) {
			result = getStoreInstance().saveNotification(notification2Save);
			if (result)
				_log.trace("Notification Saved Successfully! ");
			else
				_log.error("Error While trying to save Notification");
		}
		if (channels.contains(NotificationChannelType.EMAIL)) {
			EmailPlugin.getInstance(userManager, currScope).sendNotification(portalURL, siteLandingPagePath, notification2Save, currGroupName, portalName, senderEmail, null, hashtags);
		}
		if (channels.isEmpty()) {
			_log.info("Notification was not needed as "+ notification2Save.getUserid()  +" decided not to be notified for " + notification2Save.getType());
			result = true;
		}
		return result;
	}
	
	/**
	 * actually save the notification to the store
	 * @param notification2Save the notification instance to save
	 * @return true if the notification was sent ok
	 */
	private boolean saveNotification(Notification notification2Save, Set<String> mentionedVREGroups, String ... hashtags) {
		_log.trace("Trying to send notification to: " + notification2Save.getUserid() +  " Type: " + notification2Save.getType());
		if (notification2Save.getSenderid().compareTo(notification2Save.getUserid()) == 0) {
			_log.trace("Sender and Receiver are the same " + notification2Save.getUserid() +  " Notification Stopped");
			return true;  //I'm not sending notifications to the person who triggered it, pretend I sent it though
		}
		List<NotificationChannelType> channels = null;
		try {
			channels = getStoreInstance().getUserNotificationChannels(notification2Save.getUserid(), notification2Save.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean result = false;
		if (channels.contains(NotificationChannelType.PORTAL)) {
			result = getStoreInstance().saveNotification(notification2Save);
			if (result)
				_log.trace("Notification Saved Successfully! ");
			else
				_log.error("Error While trying to save Notification");
		}
		if (channels.contains(NotificationChannelType.EMAIL)) {
			EmailPlugin.getInstance(userManager, currScope).sendNotification(portalURL, siteLandingPagePath, notification2Save, currGroupName, portalName, senderEmail, mentionedVREGroups, hashtags);
		}
		if (channels.isEmpty()) {
			_log.info("Notification was not needed as "+ notification2Save.getUserid()  +" decided not to be notified for " + notification2Save.getType());
			result = true;
		}
		return result;
	}

	/**
	 * return the url of the application if exists in the profile
	 * @return .
	 */
	private String getApplicationUrl() {
		if (applicationProfile != null && applicationProfile.getUrl() != null) {
			_log.trace("getApplicationUrl="+applicationProfile.getUrl());
			return applicationProfile.getUrl();			
		}
		else {
			_log.warn("applicationProfile NULL or url is empty returning " + siteLandingPagePath);
			return siteLandingPagePath;
		}
	}
	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyFolderSharing(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws InternalErrorException {
		String sharedFolderName = sharedFolder.getName();
		if (sharedFolder.isShared()) {
			WorkspaceSharedFolder sharedWSFolder = (WorkspaceSharedFolder) sharedFolder;
			if (sharedWSFolder.isVreFolder())
				sharedFolderName = sharedWSFolder.getDisplayName(); 
		}
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_FOLDER_SHARE, 
				userIdToNotify, //user no notify
				sharedFolder.getId(),  
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(), 
				"shared the workspace folder \""+ sharedFolderName +"\" with you",
				false, 
				currUser.getUsername(),
				currUser.getFullname(),
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public boolean notifyFolderUnsharing(String userIdToNotify,	 String unsharedFolderId, String unsharedFolderName) throws Exception {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_FOLDER_UNSHARE, 
				userIdToNotify, //user no notify
				unsharedFolderId, 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+unsharedFolderId, 
				"unshared the workspace folder \""+ unsharedFolderName+"\"",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public boolean notifyAdministratorUpgrade(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws Exception {
		String sharedFolderName = sharedFolder.getName();
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_ADMIN_UPGRADE, 
				userIdToNotify, //user no notify
				sharedFolder.getId(),   
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(),  
				"upgraded you to Administrator of the workspace folder \""+ sharedFolderName+"\"",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public boolean notifyAdministratorDowngrade(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws Exception {
		String sharedFolderName = sharedFolder.getName();
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_ADMIN_DOWNGRADE, 
				userIdToNotify, //user no notify
				sharedFolder.getId(),   
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(),  
				"downgraded you from Administrator of the workspace folder \""+ sharedFolderName+"\"",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}

	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyFolderRenaming(String userIdToNotify, String previousName, String newName, String renamedFolderId) throws InternalErrorException {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_FOLDER_RENAMED, 
				userIdToNotify, //user no notify
				renamedFolderId,  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+renamedFolderId, 
				"renamed your shared folder \""+ previousName +"\" as \"" + newName+"\"",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws UserManagementPortalException 
	 * @throws UserRetrievalFault 
	 * @throws UserManagementSystemException 
	 */
	@Override
	public boolean notifyFolderAddedUser(String userIdToNotify, WorkspaceSharedFolder sharedFolder, String newAddedUserId) throws InternalErrorException, UserManagementSystemException, UserRetrievalFault, UserManagementPortalException {
		UserManager um = new LiferayUserManager();
		GCubeUser user = um.getUserByUsername(newAddedUserId);
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_FOLDER_ADDEDUSER, 
				userIdToNotify, //user no notify
				sharedFolder.getId(),  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(),
				"added "+ user.getFullname() +" to your workspace shared folder \""+ sharedFolder.getName()+"\"",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws UserManagementPortalException 
	 * @throws UserRetrievalFault 
	 * @throws UserManagementSystemException 
	 */
	@Override
	public boolean notifyFolderAddedUsers(String userIdToNotify, WorkspaceSharedFolder sharedFolder, List<String> newAddedUserIds) throws InternalErrorException, UserManagementSystemException, UserRetrievalFault, UserManagementPortalException  {
		if (newAddedUserIds != null && newAddedUserIds.size() > 0) {
			if (newAddedUserIds.size() == 1)
				return notifyFolderAddedUser(userIdToNotify, sharedFolder, newAddedUserIds.get(0));
			StringBuilder addedUsersFullNames = new StringBuilder();
			UserManager um = new LiferayUserManager();
			for (String userId : newAddedUserIds) 
				addedUsersFullNames.append(um.getUserByUsername(userId).getFullname()).append(" ");

			Notification not = new Notification(
					UUID.randomUUID().toString(), 
					NotificationType.WP_FOLDER_ADDEDUSER, 
					userIdToNotify, //user no notify
					sharedFolder.getId(),  //the 
					new Date(),
					siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(),
					"added "+ addedUsersFullNames +" to your workspace shared folder \""+ sharedFolder.getName()+"\"",
					false, 
					currUser.getUsername(),
					currUser.getFullname(), 
					currUser.getUserAvatarId());

			return saveNotification(not);
		}

		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyFolderRemovedUser(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws InternalErrorException, UserManagementSystemException, UserRetrievalFault, UserManagementPortalException {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_FOLDER_REMOVEDUSER, 
				userIdToNotify, //user no notify
				sharedFolder.getId(),  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(), 
				"unshared his shared folder \""+ sharedFolder.getName() + "\" from your workspace",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyAddedItem(String userIdToNotify, WorkspaceItem item, WorkspaceSharedFolder sharedFolder) throws InternalErrorException {
		String notifyText = sharedFolder.isVreFolder() ? 
				"added "+ item.getName() +" to the workspace group folder " + sharedFolder.getDisplayName() :
					"added "+ item.getName() +" to your workspace shared folder "+ item.getPath().substring(0,item.getPath().lastIndexOf('/'));

				Notification not = new Notification(
						UUID.randomUUID().toString(), 
						NotificationType.WP_ITEM_NEW, 
						userIdToNotify, //user no notify
						item.getId(),  //the 
						new Date(),
						siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+item.getParent().getId(), 
						notifyText,
						false, 
						currUser.getUsername(),
						currUser.getFullname(), 
						currUser.getUserAvatarId());

				return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyMovedItem(String userIdToNotify, WorkspaceItem item, WorkspaceSharedFolder sharedFolder) throws InternalErrorException {
		String notifyText = sharedFolder.isVreFolder() ? "removed item  "+ item.getName() +" from the workspace group folder " + sharedFolder.getDisplayName():
			"removed item "+ item.getName() +" from your workspace shared folder \""+ sharedFolder.getName()+"\"";

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_ITEM_DELETE, 
				userIdToNotify, //user no notify
				item.getId(),  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(), 
				notifyText,
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyRemovedItem(String userIdToNotify, String itemName, WorkspaceSharedFolder sharedFolder) throws InternalErrorException {
		String notifyText = sharedFolder.isVreFolder() ? "deleted item  \""+ itemName +"\" from the workspace group folder " + sharedFolder.getDisplayName():
			" deleted item \""+ itemName +"\" from your workspace shared folder \""+ sharedFolder.getName()+"\"";

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_ITEM_DELETE, 
				userIdToNotify, //user no notify
				sharedFolder.getId(),  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+sharedFolder.getId(), 
				notifyText,
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyUpdatedItem(String userIdToNotify,	WorkspaceItem item, WorkspaceSharedFolder sharedFolder) throws InternalErrorException {
		String notifyText = sharedFolder.isVreFolder() ? " updated  \""+ item.getName() +"\" from the workspace group folder \"" + sharedFolder.getDisplayName()+"\"":
			" updated \""+ item.getName() +"\" to your workspace shared folder \""+ item.getPath().substring(0,item.getPath().lastIndexOf('/'))+"\"";

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_ITEM_UPDATED, 
				userIdToNotify, //user no notify
				item.getId(),  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+item.getParent().getId(), 
				notifyText,
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 * @throws InternalErrorException 
	 */
	@Override
	public boolean notifyItemRenaming(String userIdToNotify, String previousName, WorkspaceItem renamedItem,  WorkspaceSharedFolder rootSharedFolder) throws InternalErrorException {
		String notifyText = rootSharedFolder.isVreFolder() ? "renamed \""+ previousName +"\" as \"" + renamedItem.getName() +"\" in the workspace group folder " + rootSharedFolder.getDisplayName():
			"renamed \"" + previousName +"\" as \"" + renamedItem.getName() +"\" in your shared folder \"" + renamedItem.getPath().substring(0, renamedItem.getPath().lastIndexOf('/'))+"\"";

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.WP_ITEM_RENAMED, 
				userIdToNotify, //user no notify
				renamedItem.getId(),  //the 
				new Date(),
				siteLandingPagePath + USER_WORKSPACE_FRIENDLY_URL +"?itemid="+renamedItem.getParent().getId(), 
				notifyText,
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyMessageReceived(String userIdToNotify,	String messageId, String subject, String messageText,  String ... otherRecipientsFullNames) {
		String[] optionalParams = {subject};
		String otherRecipientNames = "";
		if (otherRecipientsFullNames != null && otherRecipientsFullNames.length > 0) {
			otherRecipientNames = "<br/><div> - This message was also sent to: <ul>";
			for (int i = 0; i < otherRecipientsFullNames.length; i++) {
				otherRecipientNames += "<li> " + otherRecipientsFullNames[i] + " </li>";
			}
			otherRecipientNames += "</ul></div><div>If you reply, your message will be also delivered to them.</div><br/>";
		}


		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.MESSAGE, 
				userIdToNotify, //user no notify
				messageId,  //the unique identifier of the message
				new Date(),
				siteLandingPagePath + USER_MESSAGES_FRIENDLY_URL, 
				"sent you a message: " 
						+ "<br/><div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  border-left: 3px solid #ccc; font-style: italic;\">"
						+ messageText +"</div>"
						+ otherRecipientNames,
						false, 
						currUser.getUsername(),
						currUser.getFullname(), 
						currUser.getUserAvatarId());

		return saveNotification(not, optionalParams);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyPost(String userIdToNotify, String feedid,	String feedText, String ... hashtags) {
		StringBuilder notificationText = new StringBuilder();
		notificationText.append("posted on <b> ").append(currGroupName).append(":</b>") // has done something
		.append("<br /><br /> ").append(feedText).append(" ")
		.append("<br /><br />");

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.POST_ALERT, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				notificationText.toString(),
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());
		return saveNotification(not, hashtags);
	}
	
	@Override
	public boolean notifyPost(String userIdToNotify, String feedid,
			String feedText, Set<String> mentionedVREGroups,
			Set<String> hashtags) {
		StringBuilder notificationText = new StringBuilder();
		notificationText.append("posted on <b> ").append(currGroupName).append(":</b>") // has done something
		.append("<br /><br /> ").append(feedText).append(" ")
		.append("<br /><br />");

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.POST_ALERT, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				notificationText.toString(),
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());
		return saveNotification(not, mentionedVREGroups, hashtags.toArray(new String[hashtags.size()]));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyOwnCommentReply(String userIdToNotify, String feedid, String feedText, String commentKey) {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.OWN_COMMENT, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				"commented on your post: " 
						+ "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  "
						+ "border-left: 3px solid #ccc; font-style: italic;\">\""
						+ feedText +"\"</div>",
						false, 
						currUser.getUsername(),
						currUser.getFullname(), 
						currUser.getUserAvatarId(),
						commentKey);
		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyCommentReply(String userIdToNotify, String feedid, String commentText, String feedOwnerFullName, String feedOwnerId, String commentKey) {
		String notificationText =  (currUser.getUsername().compareTo(feedOwnerId) == 0) ? 
				"also commented on his post: "  
				+ "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  border-left: 3px solid #ccc; font-style: italic;\">\""
				+ commentText  +"\"</div>"
				: 
					"also commented on " + feedOwnerFullName + "'s post: " 
					+ "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  border-left: 3px solid #ccc; font-style: italic;\">\""
					+ commentText +"\"</div>";
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.COMMENT, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				notificationText,
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId(),
				commentKey);
		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyCommentOnFavorite(String userIdToNotify, String feedid, String commentText, String commentKey) {
		return notifyCommentOnLike(userIdToNotify, feedid, commentText, commentKey);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyCommentOnLike(String userIdToNotify, String feedid, String commentText, String commentKey) {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.COMMENT, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				"commented on one of your liked posts: " 
						+ "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  border-left: 3px solid #ccc; font-style: italic;\">\""
						+ commentText +"\"</div>",
						false, 
						currUser.getUsername(),
						currUser.getFullname(), 
						currUser.getUserAvatarId(),
						commentKey);
		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyUserTag(String userIdToNotify, String feedid, String feedText, String commentKey) {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.MENTION, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				"mentioned you: " + "<div style=\"margin-top: 10px;  margin-bottom: 10px;  margin-left: 50px;  padding-left: 15px;  border-left: 3px solid #ccc; font-style: italic;\">\""
						+ feedText +"\"</div>",
						false, 
						currUser.getUsername(),
						currUser.getFullname(), 
						currUser.getUserAvatarId(),
						commentKey);
		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyLikedFeed(String userIdToNotify, String feedid, String feedText) {

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.LIKE, 
				userIdToNotify, //user no notify
				feedid,  //the post 
				new Date(),
				getApplicationUrl()+"?oid="+feedid, 
				"liked/subscribed to one of your post",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());
		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyJobStatus(String userIdToNotify, RunningJob job) {

		//get job status 
		NotificationType statusToUse = null;

		// notification final part
		String notificationFinalPart = null;

		switch(job.getStatus()){
		case CANCELLED:
			notificationFinalPart = " has been cancelled.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		case CANCELLING:
			notificationFinalPart = " is going to be cancelled.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		case DELETED:
			notificationFinalPart = " has been deleted.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		case DELETING:
			notificationFinalPart = " is going to be deleted.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		case EXECUTING:
			notificationFinalPart = " is executing.";
			statusToUse = NotificationType.JOB_COMPLETED_OK;
			break;
		case FAILED:
			notificationFinalPart = " is failed.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		case NEW:
			notificationFinalPart = " has been instanciated.";
			statusToUse = NotificationType.JOB_COMPLETED_OK;
			break;
		case SUBMITTED:
			notificationFinalPart = " has been submitted.";
			statusToUse = NotificationType.JOB_COMPLETED_OK;
			break;
		case SUCCEEDED:
			notificationFinalPart = " terminated correctly.";
			statusToUse = NotificationType.JOB_COMPLETED_OK;
			break;
		case TIMED_OUT:
			notificationFinalPart = " went in time out.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		case WAITING:
			notificationFinalPart = " is waiting.";
			statusToUse = NotificationType.JOB_COMPLETED_NOK;
			break;
		default:
			_log.error("job status is missing, returning without sending notification");
			return false;
		}

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				statusToUse, 
				userIdToNotify, //user to notify
				job.getServiceName(),
				new Date(),
				null, 
				"'s job with id " + job.getJobId() +  ", named <b>" + job.getJobName() +"</b>" + notificationFinalPart + 
				(job.getMessage() != null && !job.getMessage().isEmpty() ? 
						" Additional information: " + job.getMessage() : ""),
						false, 
						currUser.getUsername(),
						job.getServiceName(), 
						currUser.getUserAvatarId());
		return saveNotification(not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyNewCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate) {
		SimpleDateFormat spf = new SimpleDateFormat("EEE dd MMMMM, yyyy");

		String endDateToDisplay="";
		if (endingDate != null) {
			endDateToDisplay = " to " + spf.format(endingDate);
		}

		StringBuilder notificationText = new StringBuilder();
		notificationText.append("added the following event in the <b>").append(currGroupName).append("</b> shared calendar: ") // has done something
		.append("<strong>").append(eventTitle).append("</strong><br />")
		.append("<br /><strong> Time:</strong> ").append(spf.format(startDate)).append(endDateToDisplay).append("<br />")
		.append("<br /><strong> Category: </strong> ").append(eventType);

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.CALENDAR_ADDED_EVENT, 
				userIdToNotify, //user no notify
				"",  //
				new Date(),
				getApplicationUrl(), 
				notificationText.toString(),
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyEditedCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate) {
		SimpleDateFormat spf = new SimpleDateFormat("EEE dd MMMMM, yyyy");

		String endDateToDisplay="";
		if (endingDate != null) {
			endDateToDisplay = " to " + spf.format(endingDate);
		}

		StringBuilder notificationText = new StringBuilder();
		notificationText.append("edited the following event in the <b>").append(currGroupName).append("</b> shared calendar: ") // has done something
		.append("<strong>").append(eventTitle).append("</strong><br />")
		.append("<br /><strong> Time:</strong> ").append(spf.format(startDate)).append(endDateToDisplay).append("<br />")
		.append("<br /><strong> Category: </strong> ").append(eventType);

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.CALENDAR_UPDATED_EVENT, 
				userIdToNotify, //user no notify
				"",  //
				new Date(),
				getApplicationUrl(), 
				notificationText.toString(),
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyDeletedCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate) {
		SimpleDateFormat spf = new SimpleDateFormat("EEE dd MMMMM, yyyy");

		String endDateToDisplay="";
		if (endingDate != null) {
			endDateToDisplay = " to " + spf.format(endingDate);
		}

		StringBuilder notificationText = new StringBuilder();
		notificationText.append("deleted the following event in the <b>").append(currGroupName).append("</b> shared calendar: ") // has done something
		.append("<strong>").append(eventTitle).append("</strong><br />")
		.append("<br /><strong> Was planned:</strong> ").append(spf.format(startDate)).append(endDateToDisplay).append("<br />")
		.append("<br /><strong> Category: </strong> ").append(eventType);

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.CALENDAR_DELETED_EVENT, 
				userIdToNotify, //user no notify
				"",  //
				new Date(),
				getApplicationUrl(), 
				notificationText.toString(),
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyTDMTabularResourceSharing(String userIdToNotify, String tabularResourceName, String encodedTabularResourceParams) throws Exception {
		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				NotificationType.TDM_TAB_RESOURCE_SHARE, 
				userIdToNotify, //user no notify
				tabularResourceName,  
				new Date(),
				getApplicationUrl()+"?"+encodedTabularResourceParams, 
				"shared the Tabular Resource \""+ tabularResourceName +"\" with you",
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notifyTDMObjectSharing(String userIdToNotify, NotificationType type, String tdmObjectName, String encodedTabularResourceParams) throws Exception {
		if  (! (type == NotificationType.TDM_RULE_SHARE || type ==  NotificationType.TDM_TEMPLATE_SHARE))
			throw new IllegalArgumentException("Type must be either TDM_RULE_SHARE or TDM_TEMPLATE_SHARE");

		String notificationText = "shared the Tabular Data Manager";
		notificationText += (type == NotificationType.TDM_RULE_SHARE) ?  " Rule " : " Template ";
		notificationText += "\"" + tdmObjectName + "\" with you";

		String url = getApplicationUrl();
		if (encodedTabularResourceParams != null && encodedTabularResourceParams.compareTo("") != 0)
			url += "?"+encodedTabularResourceParams;

		Notification not = new Notification(
				UUID.randomUUID().toString(), 
				type, 
				userIdToNotify, //user no notify
				tdmObjectName,  
				new Date(),
				url, 
				notificationText,
				false, 
				currUser.getUsername(),
				currUser.getFullname(), 
				currUser.getUserAvatarId());

		return saveNotification(not);
	}
}
