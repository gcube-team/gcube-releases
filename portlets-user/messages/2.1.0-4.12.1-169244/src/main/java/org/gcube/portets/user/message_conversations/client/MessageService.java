package org.gcube.portets.user.message_conversations.client;

import java.util.ArrayList;

import org.gcube.portets.user.message_conversations.shared.ConvMessage;
import org.gcube.portets.user.message_conversations.shared.CurrUserAndPortalUsersWrapper;
import org.gcube.portets.user.message_conversations.shared.WSUser;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * The client side stub for the RPC service.
 */
public interface MessageService extends RemoteService {

  ArrayList<ConvMessage> getMessages(boolean sent);
  ConvMessage getMessageById(String messageId, boolean sent);
  CurrUserAndPortalUsersWrapper getWorkspaceUsers();
  ArrayList<WSUser> getUsersInfo(String[] usernames);
  ArrayList<WSUser> searchUsers(String keyword);
  boolean sendToById(ArrayList<String> recipientIds, ArrayList<String> listAttachmentsId, String subject, String body);
  boolean deleteMessageById(String messageId, boolean sent);
  String getAttachmentDownloadURL(String itemId);
  boolean saveAttachmentToWorkspaceFolder(String itemId, String destinationFolderId);
  boolean markMessageUnread(String messageId, boolean sent);
}
