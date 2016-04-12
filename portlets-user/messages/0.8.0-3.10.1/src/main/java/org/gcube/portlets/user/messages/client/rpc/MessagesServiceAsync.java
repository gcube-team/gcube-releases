package org.gcube.portlets.user.messages.client.rpc;

import java.util.List;

import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface MessagesServiceAsync {

	void getAllMessagesReceived(AsyncCallback<List<MessageModel>> callback);

	void getAllMessagesSent(AsyncCallback<List<MessageModel>> callback);

	void getMessageById(String messageIdentifier, String messageType,
			AsyncCallback<MessageModel> callback);

	void getNewMessagesReceived(AsyncCallback<List<MessageModel>> callback);

	void markMessage(String messageIdentifier, String messageType,
			boolean boolMark, String markType, AsyncCallback<Boolean> callback);

	void saveAttach(String attachId, AsyncCallback<Boolean> callback);

	void saveAttachments(String messageIdentifier, String messageType,
			AsyncCallback<Boolean> callback);

	void searchInReceivedMessagesByText(String text,
			AsyncCallback<List<MessageModel>> callback);

	void searchInSentMessagesByText(String text,
			AsyncCallback<List<MessageModel>> callback);

	void sendToById(List<String> listContactsId,
			List<String> listAttachmentsId, String subject, String text,
			AsyncCallback<Boolean> callback);

	void sentToByMessageModel(MessageModel message,
			AsyncCallback<Boolean> callback);

	void deleteMessage(String messageIdentifier, String messageType,
			AsyncCallback<Boolean> callback);

	void setAllUserMessageNotificationsRead(AsyncCallback<Boolean> callback);

	void getURLFromApplicationProfile(String identifier,
			AsyncCallback<String> asyncCallback);

	void saveAttachment(String messageIdentifier, String attachmentId,
			String messageType, AsyncCallback<String> callback);

	void isSessionExpired(AsyncCallback<Boolean> callback);

	void getMyLogin(AsyncCallback<String> callback);
}
