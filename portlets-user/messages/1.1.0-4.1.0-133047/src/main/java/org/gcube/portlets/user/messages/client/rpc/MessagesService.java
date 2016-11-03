package org.gcube.portlets.user.messages.client.rpc;

import java.util.List;

import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("message")
public interface MessagesService extends RemoteService {
	
	public boolean sentToByMessageModel(MessageModel message) throws Exception;

	public boolean sendToById(List<String> listContactsId, List<String> listAttachmentsId, String subject, String text) throws Exception;
	
	public List<MessageModel> getAllMessagesReceived() throws Exception;
	
	public List<MessageModel> getNewMessagesReceived() throws Exception;
	
	public List<MessageModel> getAllMessagesSent() throws Exception;
	
	public List<MessageModel> searchInSentMessagesByText(String text) throws Exception;
	
	public List<MessageModel> searchInReceivedMessagesByText(String text) throws Exception;
	
	public MessageModel getMessageById(String messageIdentifier, String messageType) throws Exception;
	
	public boolean saveAttachments(String messageIdentifier, String messageType) throws Exception;
	
	public boolean saveAttach(String attachId) throws Exception;
	
	public boolean markMessage(String messageIdentifier, String messageType, boolean boolMark, String markType) throws Exception;
	
	public boolean deleteMessage(String messageIdentifier, String messageType) throws Exception;

	boolean setAllUserMessageNotificationsRead();

	String getURLFromApplicationProfile(String identifier) throws Exception;

	String saveAttachment(String messageIdentifier, String attachmentId, String messageType) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	boolean isSessionExpired() throws Exception;

	/**
	 * @return
	 * @throws Exception 
	 */
	String getMyLogin() throws Exception;
}
