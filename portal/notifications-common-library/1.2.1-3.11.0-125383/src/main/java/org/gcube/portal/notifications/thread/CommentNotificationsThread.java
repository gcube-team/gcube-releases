package org.gcube.portal.notifications.thread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Like;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class CommentNotificationsThread implements Runnable {

	private static final Logger _log = LoggerFactory.getLogger(CommentNotificationsThread.class);

	String commenterUserId;
	String commentedFeedId;
	private String commentText;
	private String feedOwnerId;
	private NotificationsManager nm;
	private String commentKey;
	private HashSet<String> userIdsToNotify;
	//needed to avoid sending notification twice (the user who favorited gets the notification anyways)
	private ArrayList<Like> favorites;


	public CommentNotificationsThread(DatabookStore storeInstance, String commenterUserId, 
			String commentedFeedId, String commentText, NotificationsManager nm, String feedOwnerId, String commentKey, ArrayList<Like> favorites) {
		super();
		this.nm = nm;
		this.commenterUserId = commenterUserId;
		this.commentedFeedId = commentedFeedId;
		this.commentText = commentText;
		this.feedOwnerId = feedOwnerId;
		this.commentKey = commentKey;
		this.favorites = favorites;

		userIdsToNotify = new HashSet<String>();
		List<Comment> feedComments = storeInstance.getAllCommentByFeed(commentedFeedId);
		for (Comment comment : feedComments) {
			if (comment.getUserid().compareTo(commenterUserId) != 0) {
				userIdsToNotify.add(comment.getUserid());
			}
		}

		//clean
		//this.comments = comments;
	}

	@Override
	public void run() {
		String feedOwnerFullName = "";
		UserManager um = new LiferayUserManager();
		try {
			feedOwnerFullName = um.getUserByScreenName(feedOwnerId).getFullname();
		} catch (Exception e) {
			feedOwnerFullName = feedOwnerId;
		} 
		//get the list of userid who liked the post
		ArrayList<String> favoriteUserIds = new ArrayList<>();
		for (Like favorite : favorites) {
			favoriteUserIds.add(favorite.getUserid());
		}
		
		if (userIdsToNotify != null) {
			for (String userId : userIdsToNotify) {	
				if (userId.compareTo(feedOwnerId) != 0 && !(favoriteUserIds.contains(userId)) ) { //avoid notifying the owner and the user who liked twice 
					boolean result = nm.notifyCommentReply(userId, commentedFeedId, commentText, feedOwnerFullName, feedOwnerId, commentKey);
					_log.trace("Sending Notification for also commented to: " + feedOwnerFullName + " result?"+ result);
				}
			}
		}

	}	
}
