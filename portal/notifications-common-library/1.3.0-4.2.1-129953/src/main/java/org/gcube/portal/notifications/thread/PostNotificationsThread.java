package org.gcube.portal.notifications.thread;

import java.util.List;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
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
	private long groupId;
	private List<String> hashtags;
	private NotificationsManager nm;
	/**
	 * 
	 * @param postId
	 * @param postText
	 * @param groupId the LR groupId
	 * @param nm
	 * @param hashtags
	 */
	public PostNotificationsThread(String postId, String postText, String groupId, NotificationsManager nm, List<String> hashtags) {
		super();
		this.postId = postId;
		this.postText = postText;
		this.groupId = Long.parseLong(groupId);
		this.hashtags = hashtags;
		this.nm = nm;
	}

	@Override
	public void run() {
		UserManager um = new LiferayUserManager();
		String[] hashtagsToPass = hashtags.toArray(new String[hashtags.size()]);
		try {			
			for (GCubeUser user : um.listUsersByGroup(groupId)) {
				boolean result = nm.notifyPost(user.getUsername(), postId, postText, hashtagsToPass);
				_log.trace("Sending Notification for post alert to: " + user.getUsername() + " result?"+ result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
