package org.gcube.applicationsupportlayer.social;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gcube.applicationsupportlayer.social.shared.SocialFileItem;
import org.gcube.applicationsupportlayer.social.shared.SocialSharedFolder;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.RunningJob;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public interface NotificationsManager {
	/**
	 * use to notify a user he got a workspace folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link SocialSharedFolder} instance 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderSharing(String userIdToNotify, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param unsharedFolderId the unshared folder id
	 * @param unsharedFolderName the unshared folder name
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderUnsharing(String userIdToNotify, String unsharedFolderId, String unsharedFolderName) throws Exception;
	/**
	 * use to notify a user he got upgraded to Administrator of a folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link SocialSharedFolder} instance
	 * @return true if the notification is correctly delivered, false otherwise
	 * 
	 */
	boolean notifyAdministratorUpgrade(String userIdToNotify, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got downgraded from Administrator of a folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link SocialSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyAdministratorDowngrade(String userIdToNotify, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace folder renamed
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param previousName the previous name of the folder
	 * @param newName the new name of the folder
	 * @param renamedFolderId the folderId
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderRenaming(String userIdToNotify, String previousName, String newName, String renamedFolderId) throws Exception;
	/**
	 * use to notify a user that a new user was added in on of his workspace shared folder
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link SocialSharedFolder} instance
	 * @param newAddedUserId the new user that was added
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderAddedUser(String userIdToNotify, SocialSharedFolder sharedFolder, String newAddedUserId) throws Exception;
	/**
	 * use to notify a user that a new user was added in on of his workspace shared folder
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link SocialSharedFolder} instance  
	 * @param newAddedUserIds List of new users that were added
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderAddedUsers(String userIdToNotify, SocialSharedFolder sharedFolder, List<String> newAddedUserIds) throws Exception;
	/**
	 * use to notify a user that an existing user was removed from one of his workspace shared folder
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link SocialSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderRemovedUser(String userIdToNotify, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item new in some of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param newItem the new shared {@link SocialFileItem} instance  
	 * @param sharedFolder the shared folder {@link SocialSharedFolder} instance
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyAddedItem(String userIdToNotify, SocialFileItem item, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item deleted from one of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param removedItem the removed item instance of {@link SocialFileItem}  
	 * @param sharedFolder the shared folder {@link SocialSharedFolder} 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyMovedItem(String userIdToNotify, SocialFileItem item, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item deleted from one of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param removedItem the removed name
	 * @param sharedFolder the shared folder {@link SocialSharedFolder} 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyRemovedItem(String userIdToNotify, String item, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item updated from one of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param updatedItem the updated shared {@link SocialFileItem}  
	 * @param sharedFolder the shared folder {@link SocialSharedFolder} 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyUpdatedItem(String userIdToNotify, SocialFileItem item, SocialSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item renamed
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param previousName the previous name of the folder
	 * @param renamedItem the renamed {@link SocialFileItem}
	 * @param rootSharedFolder the root shared {@link SocialSharedFolder} of the {@link SocialFileItem}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyItemRenaming(String userIdToNotify, String previousName, SocialFileItem renamedItem, SocialSharedFolder rootSharedFolder) throws Exception;
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param messageUniqueIdentifier the unique identifier of the message
	 * @param subject the subject of the message sent
	 * @param messageText the text of the message (text/plain)
	 * @param otherRecipientsFullNames the Full Names of the other recipients. if any
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyMessageReceived(String userIdToNotify, String messageUniqueIdentifier, String subject, String messageText, String ... otherRecipientsFullNames);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param eventTitle the title of the event
	 * @param eventType the type of the event
	 * @param startDate staring date
	 * @param endingDate ending date
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyNewCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param eventTitle the title of the event
	 * @param eventType the type of the event
	 * @param startDate staring date
	 * @param endingDate ending date
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyEditedCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param eventTitle the title of the event
	 * @param eventType the type of the event
	 * @param startDate staring date
	 * @param endingDate ending date
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDeletedCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate);
	
	/**
	 * use to notify a user that someone created this post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param postText the liked post text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	@Deprecated
	boolean notifyPost(String userIdToNotify, String postid, String postText, String ... hashtags);
	
	/**
	 * use to notify a user that someone created this post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param postText the liked post text or a portion of it
	 * @param mentionedVREGroups the names of the mentioned vre's groups, if any
	 * @param hashtags the set of hashtags in the post, if any
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyPost(String userIdToNotify, String postid, String postText, Set<String> mentionedVREGroups, Set<String> hashtags);
	
	/**
	 * use to notify a user that someone commented on his post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param postText the liked post text or a portion of it
	 * @param commentKey when sending email, stop the shown discussion at that comment
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyOwnCommentReply(String userIdToNotify, String postid, String postText, String commentKey);
	/**
	 * use to notify a user that commented on a post (Not his) that someone commented too 
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param postText the liked post text or a portion of it
	 * @param postOwnerFullName the full name of the user who created the post
	 * @param postOwnerId the username of the user who created the post
	 * @param commentKey when sending email, stop the shown discussion at that comment
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyCommentReply(String userIdToNotify, String postid, String postText, String postOwnerFullName, String postOwnerId, String commentKey);
	/**
	 * @deprecated use notifyCommentOnLike
	 * use to notify a user that someone commented on one of his liked posts 
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param commentText the commentText
	 * @param commentKey when sending email, stop the shown discussion at that comment
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyCommentOnFavorite(String userIdToNotify, String postid, String commentText, String commentKey);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param commentText the commentText
	 * @param commentKey when sending email, stop the shown discussion at that comment
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyCommentOnLike(String userIdToNotify, String postid, String commentText, String commentKey);
	/**
	 * use to notify a user that he was mentioned (tagged) on a post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param feedText the liked feed text or a portion of it
	 * @param commentKey when sending email, stop the shown discussion at that comment
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyUserTag(String userIdToNotify, String postid, String commentText, String commentKey);
	/**
	 * use to notify a user he got one of his post liked
	 *  
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param postText the liked post text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 * @deprecated use notifyLikedPost
	 */
	@Deprecated
	boolean notifyLikedFeed(String userIdToNotify, String postid, String postText);
	/**
	 * use to notify a user he got one of his post liked
	 *  
	 * @param userIdToNotify the user you want to notify
	 * @param postid the liked postid 
	 * @param postText the liked post text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyLikedPost(String userIdToNotify, String postid, String postText);
	/**
	 * use to notify a user he got one of his job finished
	 *  
	 * @param userIdToNotify the user you want to notify
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyJobStatus(String userIdToNotify, RunningJob job);
	
	boolean notifyTDMTabularResourceSharing(String userIdToNotify, String tabularResourceName, String encodedTabularResourceParams) throws Exception;
	/**
	 * use to notify a user he got a Tabular Data Resource shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param type type of the shared tdm object (TDM Rule or TDM Template at the moment)
	 * @param tdmObjectName the name
	 * @param encodedTabularResourceParams the parameters to be placed in the HTTP GET Request (must be encoded)
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyTDMObjectSharing(String userIdToNotify, NotificationType type, String tdmObjectName, String encodedTabularResourceParams) throws Exception;
	
}
