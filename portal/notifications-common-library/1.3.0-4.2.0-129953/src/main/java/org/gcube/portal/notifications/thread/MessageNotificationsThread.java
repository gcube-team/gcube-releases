package org.gcube.portal.notifications.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class MessageNotificationsThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(MessageNotificationsThread.class);

	private String messageText;
	private String messageId;
	private String subjectText;
	private List<GenericItemBean> recipients;
	private NotificationsManager nm;

	public MessageNotificationsThread(List<GenericItemBean> recipients, String postId, String subjectText, String messageText, NotificationsManager nm) {
		super();
		this.messageId = postId;
		this.messageText = messageText;
		this.subjectText = subjectText;
		this.recipients = recipients;
		this.nm = nm;
	}

	@Override
	public void run() {
		try {
			String checkedText = escapeHtmlAndTransformNewlines(messageText);
			for (GenericItemBean userToNotify : recipients) {
				String userIdToNotify = userToNotify.getName();
				List<GenericItemBean> temp = new ArrayList<GenericItemBean>(recipients.size());
				temp = copy(recipients);
				temp.remove(userToNotify);
				String[] otherRecipientsFullNames =  getFullNamesOnly(temp);

				if (nm.notifyMessageReceived(userIdToNotify, messageId, subjectText, checkedText, otherRecipientsFullNames))
					_log.trace("Sending message notifications to: " + userIdToNotify + " OK");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private String[] getFullNamesOnly(List<GenericItemBean> toCopy) {
		String[] toReturn = new String[toCopy.size()];
		int i = 0;
		for (GenericItemBean item : toCopy) {
			toReturn[i] = item.getAlternativeName();
			i++;
		}
		return toReturn;
	}
	
	private List<GenericItemBean> copy(List<GenericItemBean> toCopy) {
		List<GenericItemBean> toReturn = new ArrayList<GenericItemBean>();
		for (GenericItemBean genericItemBean : toCopy) {
			toReturn.add(genericItemBean);
		}
		return toReturn;
	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private static String escapeHtmlAndTransformNewlines(String html) {
		if (html == null) {
			return null;
		}
		String toReturn = html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");

		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");		
		// then replace all the double spaces by the html version &nbsp;
		toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
		return toReturn;
	}
}
