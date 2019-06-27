package org.gcube.portal.notifications.thread;

import java.util.Set;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.vomanagement.usermanagement.UserManager;
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
	private Set<String> hashtags;
	private NotificationsManager nm;
	private UserManager userManager;
	private Set<String> mentionedVREGroups;
	
	/**
	 * 
	 * @param userManager
	 * @param postId
	 * @param postText
	 * @param groupId
	 * @param nm
	 * @param hashtags
	 * @param mentionedVREGroups
	 */
	public PostNotificationsThread(UserManager userManager, String postId, String postText, String groupId, NotificationsManager nm, Set<String> hashtags, Set<String> mentionedVREGroups) {
		super();
		this.postId = postId;
		this.postText = postText;
		this.groupId = Long.parseLong(groupId);
		this.hashtags = hashtags;
		this.nm = nm;
		this.userManager = userManager;
		this.mentionedVREGroups = mentionedVREGroups;
	}

	@Override
	public void run() {
		try {			
			for (GCubeUser user : userManager.listUsersByGroup(groupId)) {
				boolean result = nm.notifyPost(user.getUsername(), postId, postText, mentionedVREGroups, hashtags);
				_log.trace("Sending Notification for post alert to: " + user.getUsername() + " result?"+ result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
