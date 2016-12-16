package org.gcube.portlets.user.messages.client.interfaces;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.MessageModel;


//Implements this interface to receive events by messages panel
/**
 * The Interface SubscriberInterface.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 7, 2015
 */
public interface SubscriberInterface {
	
	/**
	 * Root loaded.
	 *
	 * @param root the root
	 */
	void rootLoaded(FileModel root);
	
	/**
	 * Load sent messages.
	 */
	void loadSentMessages();
	
	/**
	 * Load received messages.
	 */
	void loadReceivedMessages(); //upload received messages
	
	/**
	 * Delete message.
	 *
	 * @param id the id
	 */
	void deleteMessage(String id);
	
	/**
	 * Mark as read message.
	 *
	 * @param id the id
	 * @param isRead the is read
	 */
	void markAsReadMessage(String id, boolean isRead);
	
	/**
	 * Update previe message.
	 *
	 * @param message the message
	 */
	void updatePrevieMessage(MessageModel message);
	
	/**
	 * Creates the new message.
	 *
	 * @param hashAttachs the hash attachs
	 */
	void createNewMessage(HashMap<String, String> hashAttachs);
	
	/**
	 * Forward message.
	 *
	 * @param fromLogin the from login
	 * @param subject the subject
	 * @param listContactsLogin the list contacts login
	 * @param date the date
	 * @param hashAttachs the hash attachs
	 * @param textMessage the text message
	 */
	void forwardMessage(String fromLogin, String subject, List<String> listContactsLogin, Date date, HashMap<String, String> hashAttachs, String textMessage);
	
	/**
	 * Reply message.
	 *
	 * @param fromLogin the from login
	 * @param subject the subject
	 * @param listContactsLogin the list contacts login
	 * @param date the date
	 * @param textMessage the text message
	 */
	void replyMessage(String fromLogin, String subject, List<String>listContactsLogin, Date date, String textMessage);
}
