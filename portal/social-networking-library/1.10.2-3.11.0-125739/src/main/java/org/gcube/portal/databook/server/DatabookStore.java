package org.gcube.portal.databook.server;

import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;

import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.RangeFeeds;
import org.gcube.portal.databook.shared.ex.ColumnNameNotFoundException;
import org.gcube.portal.databook.shared.ex.CommentIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.InviteIDNotFoundException;
import org.gcube.portal.databook.shared.ex.InviteStatusNotFoundException;
import org.gcube.portal.databook.shared.ex.LikeIDNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationChannelTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationIDNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.PrivacyLevelTypeNotFoundException;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

/**
 * @author Massimiliano Assante ISTI-CNR
 * 
 * <class>DatabookStore</class> is the high level interface for querying and adding data to DatabookStore 
 */
public interface DatabookStore {
	/**
	 * userid from requests a friendship to userid to 
	 * @return true if everything went fine
	 */
	boolean requestFriendship(String from, String to);
	/**
	 * userid from approves a friendship to userid to 
	 * @return true if everything went fine
	 */
	boolean approveFriendship(String from, String to);
	/**
	 * userid from denies a friendship to userid to 
	 * @return true if everything went fine
	 */
	boolean denyFriendship(String from, String to);
	/**
	 * @param userid the user id you want to know friends
	 * @return a List of userid representing the friends for the given userid
	 */
	List<String> getFriends(String userid);
	/**
	 * @param userid the user id you want to know the pending friend requests
	 * @return a List of userid representing the friends for the given userid
	 */
	List<String> getPendingFriendRequests(String userid);
	/**
	 * save a Feed instance in the store
	 * @return true if everything went fine
	 */
	boolean saveUserFeed(Feed feed);
	/**
	 * Save a Feed instance in the store having more than one attachment
	 * Use this if you need to attach more than one file to the post
	 *
	 * @param attachments, a list of attachments starting from the second
	 * @return true if everything went fine
	 */
	boolean saveUserFeed(Feed feed, List<Attachment> attachments);
	/**
	 * delete a Feed from the store
	 * @throws FeedIDNotFoundException
	 * @return true if everything went fine
	 */
	boolean deleteFeed(String feedid) throws FeedIDNotFoundException, PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException;
	/**
	 *  save a post in the VRES TimeLine in the store
	 * @param feedKey feedKey
	 * @param vreid vre identifier
	 * @return
	 * @throws FeedIDNotFoundException
	 */
	boolean saveFeedToVRETimeline(String feedKey, String vreid) throws FeedIDNotFoundException;
	/**
	 * save a Post instance in the store
	 * @return true if everything went fine
	 */
	boolean saveAppFeed(Feed feed);	
	/**
	 * Save a feed instance in the store
	 * Use this if your app needs to attach more than one file to the post
	 *
	 * @param attachments, a list of attachments starting from the second
	 * @return true if everything went fine
	 */
	boolean saveAppFeed(Feed feed, List<Attachment> attachments);
	/**
	 * read a feed from a given id 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws FeedTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */	
	Feed readFeed(String feedid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException;	
	/**
	 * @param userid user identifier
	 * return all the feeds belonging to the userid
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Feed> getAllFeedsByUser(String userid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException;
	/**
	 * @param appid application identifier
	 * return all the feeds belonging to the appid
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Feed> getAllFeedsByApp(String appid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException;

	/**
	 * return all the feeds whose Level is PORTAL
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 */
	List<Feed> getAllPortalPrivacyLevelFeeds() throws FeedTypeNotFoundException, ColumnNameNotFoundException, PrivacyLevelTypeNotFoundException;

	/**
	 * return the most recent feeds for this user up to quantity param 
	 * @param userid user identifier
	 * @param  quantity the number of most recent feeds for this user
	 * @return  a <class>List</class> of most recent feeds for this user
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Feed> getRecentFeedsByUser(String userid, int quantity) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException;
	/**
	 * @param vreid vre identifier
	 * return all the feeds belonging to the userid
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Feed> getAllFeedsByVRE(String vreid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException;

	/**
	 * return the most recent feeds for this vre up to quantity param 
	 * @param vreid VRES identifier
	 * @param quantity the number of most recent feeds for this vre
	 * @return a <class>List</class> of most recent feeds for this vre
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Feed> getRecentFeedsByVRE(String vreid,  int quantity) throws IllegalArgumentException, PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException;
	/**
	 * return the most recent feeds for this vre up to quantity param and the last index of the feeds in the timeline
	 * lastReturnedFeedTimelineIndex is usuful to know from  where to start the range the second time you ask
	 * because there are deletions
	 * 
	 * @param vreid VRES identifier
	 * @param from the range start (most recent feeds for this vre) has to be greater than 0
	 * @param quantity the number of most recent feeds for this vre starting from "from" param
	 * @return a <class>lastReturnedFeedTimelineIndex</class> containing of most recent feeds for this vre
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	RangeFeeds getRecentFeedsByVREAndRange(String vreid,  int from, int quantity) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException;
	/**
	 * @param userid user identifier
	 * @param timeInMillis time in milliseconds from which you want to start retrieve the feeds
	 * @return the number of feeds in the range from: today to: timeInMillis
	 */
	List<Feed> getRecentFeedsByUserAndDate(String userid, long timeInMillis) throws IllegalArgumentException;
	/**
	 * save a Notification instance in the store
	 * @return true if everything went fine
	 */
	boolean saveNotification(Notification notification);
	/**
	 * set an existing Notification instance in the to read
	 * @return true if everything went fine
	 */
	boolean setNotificationRead(String notificationidToSet) throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException;
	/**
	 * read a notification from a given id 
	 * @throws {@link ColumnNameNotFoundException} 
	 * @throws {@link NotificationIDNotFoundException}
	 * @throws {@link NotificationTypeNotFoundException}
	 */	
	Notification readNotification(String notificationid) throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException;	

	/**
	 * @param userid user identifier
	 * @param limit set 0 to get everything, an int to get the most recent -limit- notifications
	 * return all the notifications belonging to the userid up to limit, set 0 to get everything
	 * @throws NotificationTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Notification> getAllNotificationByUser(String userid, int limit) throws NotificationTypeNotFoundException,	ColumnNameNotFoundException, NotificationIDNotFoundException;
	/**
	 * 
	 * @param userid user identifier
	 * @param from the range start has to be greater than 0
	 * @param quantity the number of most recent notifications for this user starting from "from" param
	 * @return all the notifications for the userid in the range requested
	 * @throws NotificationTypeNotFoundException
	 * @throws ColumnNameNotFoundException
	 * @throws NotificationIDNotFoundException
	 */
	List<Notification> getRangeNotificationsByUser(String userid, int from, int quantity) throws NotificationTypeNotFoundException,	ColumnNameNotFoundException, NotificationIDNotFoundException;
	/**
	 * This is a fast way to set all notification to read quickly
	 * @param userid
	 * @return true if everything went fine
	 * @throws {@link ColumnNameNotFoundException} 
	 * @throws {@link NotificationIDNotFoundException}
	 * @throws {@link NotificationTypeNotFoundException}
	 */
	boolean setAllNotificationReadByUser(String userid) throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException;
	/**
	 * return the not yet read notifications (not including messages)
	 * @param userid user identifier
	 * @return  a <class>List</class> of not yet read notifications for this user
	 * @throws NotificationTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Notification> getUnreadNotificationsByUser(String userid)  throws NotificationTypeNotFoundException,	ColumnNameNotFoundException, NotificationIDNotFoundException;
	/**
	 * return the not yet read notification messages
	 * @param userid user identifier
	 * @return  a <class>List</class> of not yet read notifications for this user
	 * @throws NotificationTypeNotFoundException 
	 * @throws ColumnNameNotFoundException 
	 */
	List<Notification> getUnreadNotificationMessagesByUser(String userid)  throws NotificationTypeNotFoundException,	ColumnNameNotFoundException, NotificationIDNotFoundException;

	/**
	 * 
	 * @param userid user identifier
	 * @throws ColumnNameNotFoundException 
	 * @throws NotificationTypeNotFoundException 
	 * @throws NotificationIDNotFoundException 
	 * @return true if there are unread notifications (not including messages), false if they are all read
	 */
	boolean checkUnreadNotifications(String userid)  throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException;
	/**
	 * 
	 * @param userid user identifier
	 * @throws ColumnNameNotFoundException 
	 * @throws NotificationTypeNotFoundException self explaining
	 * @throws NotificationChannelTypeNotFoundException self explaining
	 * @throws NotificationIDNotFoundException 
	 * @return true if there are unread messages notifications (including messages), false if they are all read
	 */
	boolean checkUnreadMessagesNotifications(String userid)  throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException;

	/**
	 * return the channels a user chose for being notified for a given notification type
	 * @param userid  user identifier
	 * @param notificationType the type of the notification
	 * @return a list of <class>NotificationChannelType</class> that represents the channels this user wants to be notified
	 */
	List<NotificationChannelType> getUserNotificationChannels(String userid, NotificationType notificationType) throws NotificationChannelTypeNotFoundException, NotificationTypeNotFoundException;
	/**
	 * set the notification preferences map (enable or disable the channels to be used for notifying the user)
	 * @param userid  user identifier
	 * @param notificationType the type of the notification
	 * @param enabledChannels a map of the channels to which reach the user per notification, empty array or null values to set the key notification type off
	 * @return true if everything was fine
	 */
	boolean setUserNotificationPreferences(String userid, Map<NotificationType, NotificationChannelType[]> enabledChannels);
	/**
	 * get the notification preferences map (enableor disable the channels to be used for notifying the user)
	 * @param userid  user identifier
	 * @return the map
	 * @throws NotificationTypeNotFoundException self explaining
	 * @throws NotificationChannelTypeNotFoundException self explaining
	 */
	Map<NotificationType, NotificationChannelType[]> getUserNotificationPreferences(String userid) throws NotificationTypeNotFoundException, NotificationChannelTypeNotFoundException;
	
	/**
	 * @param commentId comment unique identifier
	 * @return the comment belonging to the commentId
	 * @throws CommentIDNotFoundException
	 */
	Comment readCommentById(String commentId) throws CommentIDNotFoundException;
	/**
	 * add a comment to a feed
	 * @param comment the Comment instance to add
	 */
	boolean addComment(Comment comment) throws FeedIDNotFoundException;
	/**
	 * @param feedid feed identifier
	 * return all the comments belonging to the feedid
	 */
	List<Comment> getAllCommentByFeed(String feedid);
	/**
	 * edit a comment 
	 * @param commentid the comment identifier to edit
	 * @return true if success, false otherwise
	 */
	boolean editComment(Comment comment) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, CommentIDNotFoundException, FeedIDNotFoundException;

	/**
	 * delete a comment 
	 * @param commentid the comment identifier to delete
	 * @parma feedid the feedid to which the comment is associated
	 * @return true if success, false otherwise
	 */
	boolean deleteComment(String commentid, String feedid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, CommentIDNotFoundException, FeedIDNotFoundException;
	/**
	 * add a like to a feed
	 * @param the like instance
	 * @throws FeedIDNotFoundException 
	 */
	boolean like(Like like) throws FeedIDNotFoundException;
	/**
	 * unlike a feed 
	 * @param userid user identifier
	 * @param likeid the like identifier to delete
	 * @param feedid the feedid to which the comment is associated
	 * @return true if success, false otherwise
	 */
	boolean unlike(String userid, String likeid, String feedid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, LikeIDNotFoundException, FeedIDNotFoundException;
	/**
	 * @param userid user identifier
	 * return all the feedids a user has liked
	 */
	List<String> getAllLikedFeedIdsByUser(String userid);
	/**
	 * @param userid user identifier
	 * @param limit set 0 to get everything, an int to get the most recent -limit- liked feeds
	 * @throws ColumnNameNotFoundException .
	 * @throws FeedIDNotFoundException .
	 * @throws FeedTypeNotFoundException . 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws FeedIDNotFoundException .
	 * return all the feeds a user has liked
	 */
	List<Feed> getAllLikedFeedsByUser(String userid, int limit) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException;
	/**
	 * @param feedid feed identifier
	 * return all the likes belonging to the feedid
	 */
	List<Like> getAllLikesByFeed(String feedid);
	/**
	 * 
	 * @param hashtags the hashtag including the '#'
	 * @param feedid the feedid to which the hashtag is associated
	 * @param vreid VRE identifier
	 * @return true if success, false otherwise
	 * @throws FeedIDNotFoundException
	 */
	boolean saveHashTags(String feedid, String vreid, List<String> hashtags) throws FeedIDNotFoundException;
	/**
	 * 
	 * @param hashtags the hashtag including the '#'
	 * @param feedid the feedid to which the hashtag is associated
	 * @param vreid VRE identifier
	 * @return true if success, false otherwise
	 * @throws FeedIDNotFoundException
	 */
	boolean deleteHashTags(String feedid, String vreid, List<String> hashtags) throws FeedIDNotFoundException;
	/**
	 * get a map of vre hashtags where the key is the hashtag and the value is the occurrence of the hashtag in the VRE
	 * @param vreid vre identifier (scope)
	 * @return a HashMap<String, Integer> of vre Hashtags associated with their occurrence
	 */
	Map<String, Integer> getVREHashtagsWithOccurrence(String vreid);
	/**
	 * 
	 * @param vreid VRE identifier
	 * @param hashtag the hashtag to look for including the '#', it is case sensitive
	 * @throws ColumnNameNotFoundException .
	 * @throws FeedIDNotFoundException .
	 * @throws FeedTypeNotFoundException . 
	 * @throws PrivacyLevelTypeNotFoundException 
	 * @throws FeedIDNotFoundException .
	 * @return all the feeds having the hashtag passed as parameter 
	 */
	List<Feed> getVREFeedsByHashtag(String vreid, String hashtag) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException;
	/**
	 * Save the invite for a given email into a given vre
	 * @param invite the invite object instanc to save
	 * @return {@link InviteOperationResult}  SUCCESS, FAILED or ALREADY_INVITED (if an invite is sent to en existing email in the same environment more than once)
	 */
	InviteOperationResult saveInvite(Invite invite) throws AddressException;
	/**
	 * 
	 * @param vreid the environment where you want to check the invite
	 * @param email the email of the invite to check in the environmnet
	 * @return the InviteId if present, null otherwise
	 */
	String isExistingInvite(String vreid, String email);
	/**
	 * read an invite from a given id 
	 * @throws InviteIDNotFoundException 
	 * @throws InviteStatusNotFoundException 
	 */	
	Invite readInvite(String inviteid) throws InviteIDNotFoundException, InviteStatusNotFoundException;
	/**
	 * set the status of an invite, see {@link InviteStatus}
	 * @throws InviteIDNotFoundException 
	 */	
	boolean setInviteStatus(String vreid, String email, InviteStatus status) throws InviteIDNotFoundException, InviteStatusNotFoundException;
	/**
	 * Use to get the list of invites per VRE
	 * @param vreid the vre id
	 * @param status optional, if you want to restict on the status, e.g. all pending invites 
	 * @return return the list of invites 
	 * @throws InviteIDNotFoundException 
	 * @throws InviteStatusNotFoundException 
	 */
	List<Invite> getInvitedEmailsByVRE(String vreid, InviteStatus... status) throws InviteIDNotFoundException, InviteStatusNotFoundException;
	/**
	 * 
	 * @param feedId
	 * @return the list of attachments of the feed feedId, starting from the second one (first attachment is included in Feed instance already)
	 */
	List<Attachment> getAttachmentsByFeedId(String feedId) throws FeedIDNotFoundException;
	
	/**
	 * Retrieve all the ids of the vre
	 * @return the set of ids of the vre available or empty list in case of errors.
	 * @throws ConnectionException 
	 */
	public List<String> getAllVREIds() throws ConnectionException;
	
	/**
	 * close the connection to the underlying database
	 */
	void closeConnection();
}
