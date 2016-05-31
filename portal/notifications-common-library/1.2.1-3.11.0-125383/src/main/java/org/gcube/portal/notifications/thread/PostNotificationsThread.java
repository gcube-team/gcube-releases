package org.gcube.portal.notifications.thread;

import java.util.List;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class PostNotificationsThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(PostNotificationsThread.class);

	private String postText;
	private String postId;
	private String groupId;
	private List<String> hashtags;
	private NotificationsManager nm;

	public PostNotificationsThread(String postId, String postText, String groupId, NotificationsManager nm, List<String> hashtags) {
		super();
		this.postId = postId;
		this.postText = postText;
		this.groupId = groupId;
		this.hashtags = hashtags;
		this.nm = nm;
	}

	@Override
	public void run() {
		UserManager um = new LiferayUserManager();
		String[] hashtagsToPass = hashtags.toArray(new String[hashtags.size()]);
		try {			
			for (UserModel user : um.listUsersByGroup(groupId)) {
				boolean result = nm.notifyPost(user.getScreenName(), postId, postText, hashtagsToPass);
				_log.trace("Sending Notification for post alert to: " + user.getScreenName() + " result?"+ result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
