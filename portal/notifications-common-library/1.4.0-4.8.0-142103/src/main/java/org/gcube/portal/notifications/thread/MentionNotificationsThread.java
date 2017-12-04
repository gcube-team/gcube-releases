package org.gcube.portal.notifications.thread;

import java.util.ArrayList;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class MentionNotificationsThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(MentionNotificationsThread.class);
	
	private String postText;
	private String postId;
	private NotificationsManager nm;
	private String commentKey;
	private ArrayList<GenericItemBean> users;
	
	
	public MentionNotificationsThread(String postId, String postText, NotificationsManager nm, String commentKey, ArrayList<GenericItemBean> users) {
		super();
		this.postId = postId;
		this.postText = postText;
		this.nm = nm;
		this.users = users;
		this.commentKey = commentKey;
	}

	@Override
	public void run() {
		for (GenericItemBean userToNotify : users) {
			boolean result = nm.notifyUserTag(userToNotify.getName(), postId, postText, commentKey);
			_log.trace("Sending Notification for post mention to: " + userToNotify.getName() + " result?"+ result);
		}
	}
}
