package org.gcube.portal.notifications.thread;

import java.util.ArrayList;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.portal.databook.shared.Like;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class LikeNotificationsThread implements Runnable {
	private static final Logger _log = LoggerFactory.getLogger(LikeNotificationsThread.class);
	private String commentText;
	private String commentKey;
	private NotificationsManager nm;
	private ArrayList<Like> likes;
	private String feedOwnerId;


	public LikeNotificationsThread(String commentText, NotificationsManager nm, ArrayList<Like> likes, String feedOwnerId, String commentKey) {
		super();
		this.feedOwnerId = feedOwnerId;
		this.commentText = commentText;
		this.nm = nm;
		this.likes = likes;
		this.commentKey = commentKey;
	}

	@Override
	public void run() {
		for (Like fav : likes) {
			if (fav.getUserid().compareTo(feedOwnerId) != 0) { //avoid notifying the owner twice (if the post owner commented he gets the notification regardless)
				boolean result = nm.notifyCommentOnFavorite(fav.getUserid(), fav.getFeedid(), commentText, commentKey);
				_log.trace("Sending Notification for favorited post comment added to: " + fav.getFullName() + " result?"+ result);
			}
		}
	}
}
