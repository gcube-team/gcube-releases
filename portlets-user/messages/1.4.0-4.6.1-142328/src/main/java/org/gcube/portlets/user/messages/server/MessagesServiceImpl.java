package org.gcube.portlets.user.messages.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portlets.user.messages.client.rpc.MessagesService;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */

@SuppressWarnings("serial")
public class MessagesServiceImpl extends RemoteServiceServlet implements MessagesService {



	
	public static Logger _log = Logger.getLogger(MessagesServiceImpl.class);
	/**
	 * The store interface
	 */
	private DatabookStore store;

	/**
	 * connect to cassandra at startup
	 */
	@Override
	public void init() {
		store = new DBCassandraAstyanaxImpl();
	}     

	protected GWTMessagesBuilder getGWTWorkspaceBuilder() {
		return 	new GWTMessagesBuilder();
	}

	@Override
	public boolean sentToByMessageModel(MessageModel message) throws Exception {
		return false;
	}

	/**

	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
	/**
	 * this set all the notifications for this user read
	 */
	@Override
	public boolean setAllUserMessageNotificationsRead() {

		_log.trace("setAllUserMessageNotificationsRead withinPortal?" + isWithinPortal());
		String username = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest()).getUsername();
		try {
			if (isWithinPortal()) {
				for (Notification notification :store.getUnreadNotificationsByUser(username)) {
					store.setNotificationRead(notification.getKey());
					_log.trace("setNotificationRead for user "+ username + " as "+notification.getKey());
				}
			}
			return true;

		} catch (Exception e) {
			_log.error("While trying to set User notifications Read "+e, e);
		}
		return false;
	}
	
	protected Workspace getWorkspace() throws Exception {
		String username = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest()).getUsername();
		try {

			return HomeLibrary.getUserWorkspace(username);
			
		} catch (WorkspaceFolderNotFoundException e) {

			_log.error(
					"An error occurred in get workspace WorkspaceFolderNotFoundException"
							+ e, e);
			throw new Exception("An error occurred in get user workspace");
		} catch (InternalErrorException e) {
			_log.error(
					"An error occurred in get workspace InternalErrorException"
							+ e, e);
			throw new Exception("An error occurred in get user workspace");
		} catch (HomeNotFoundException e) {
			_log.error(
					"An error occurred in get workspace HomeNotFoundException"
							+ e, e);
			throw new Exception("An error occurred in get user workspace");
		} catch (Exception e) {
			_log.error(
					"An error occurred in get workspace"
							+ e, e);
			throw new Exception("An error occurred in get user workspace");
		}

	}

	@Override
	public boolean sendToById(List<String> listContactsId, List<String> listAttachmentsId, String subject, String text)
			throws Exception {
		
		try {

			Workspace workspace = getWorkspace();

			_log.info("send To");
			_log.trace("######### SEND TO: ");
			_log.trace("subject " + subject);
			_log.trace("body " + text);

			for(String contactId : listContactsId)
				_log.trace("contactId " + contactId);

			for(String id : listAttachmentsId)
				_log.trace("attachId " + id);

			workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, text, listAttachmentsId, listContactsId);
			return true; 
		} catch (Exception e) {
			_log.error("Error in server sendTo ", e);
			//			workspaceLogger.trace("Error in server get sendTo " + e.getMessage());
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<MessageModel> getAllMessagesReceived() throws Exception {
		try {

			Workspace workspace = getWorkspace();
			_log.trace("get All Messages Received ");

			GWTMessagesBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceMessage> listMessages = workspace.getWorkspaceMessageManager().getReceivedMessages();
			return builder.buildGXTListMessageModelForGrid(listMessages, GXTCategoryItemInterface.MS_RECEIVED, isWithinPortal()); 

		} catch (Exception e) {
			_log.error("Error in server getAllMessagesReceived ", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<MessageModel> getNewMessagesReceived() throws Exception {
		try {
			
			_log.trace("in server getNewMessagesReceived ");
			Workspace workspace = getWorkspace();
			int count = workspace.getWorkspaceMessageManager().getMessagesNotOpened();
			List<MessageModel> listMessageModels = new ArrayList<MessageModel>();
			for(int i=0; i<count; i++)
				listMessageModels.add(new MessageModel());

			return listMessageModels;
		} catch (Exception e) {
			_log.error("Error in server getNewMessagesReceived by messageIdentifier ", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<MessageModel> getAllMessagesSent() throws Exception {
		try {

			Workspace workspace = getWorkspace();
			_log.info("getAllMessagesSent");
			_log.trace("get All Messages Sent ");
			GWTMessagesBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceMessage> listMessages = workspace.getWorkspaceMessageManager().getSentMessages();
			return builder.buildGXTListMessageModelForGrid(listMessages, GXTCategoryItemInterface.MS_SENT,  isWithinPortal()); 
			
		} catch (Exception e) {
			_log.error("Error in server getAllMessagesSent ", e);
			throw new Exception(e.getMessage());
		}	
	}

	@Override
	public List<MessageModel> searchInSentMessagesByText(String text)
			throws Exception {
		try {

			Workspace workspace = getWorkspace();
			_log.info("searchInSentMessagesByText");
			GWTMessagesBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceMessage> listMessages = workspace.getWorkspaceMessageManager().searchOutMessages(text);
			return builder.buildGXTListMessageModelForGrid(listMessages, GXTCategoryItemInterface.MS_SENT,  isWithinPortal()); 

		} catch (Exception e) {
			_log.error("Error in server searchInSentMessagesByText ", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<MessageModel> searchInReceivedMessagesByText(String text)
			throws Exception {
		try {

			Workspace workspace = getWorkspace();
			_log.info("searchInSentMessagesByText");
			GWTMessagesBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceMessage> listMessages = workspace.getWorkspaceMessageManager().searchInMessages(text);
			return builder.buildGXTListMessageModelForGrid(listMessages, GXTCategoryItemInterface.MS_RECEIVED, isWithinPortal()); 

		} catch (Exception e) {
			_log.error("Error in server searchInSentMessagesByText ", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public MessageModel getMessageById(String messageIdentifier, String messageType) throws Exception {

		try {
			Workspace workspace = getWorkspace();
			_log.info("get Message by Id " + messageIdentifier);
			GWTMessagesBuilder builder = getGWTWorkspaceBuilder();
			WorkspaceMessage message = null;
			if(messageType.equals(GXTCategoryItemInterface.MS_RECEIVED))
				message = workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier);
			else
				message = workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier);

			List<WorkspaceItem> listWorkspaceItems = getListWorkspaceItemById(workspace,message.getAttachmentsIds());
			return builder.buildGXTMessageModel(message, listWorkspaceItems, messageType, isWithinPortal()); 
		} catch (Exception e) {
			_log.error("Error in server get Message by Id ", e);
			throw new Exception(e.getMessage());
		}
	}

	private List<WorkspaceItem> getListWorkspaceItemById(Workspace workspace, List<String> listItemsId) throws ItemNotFoundException, InternalErrorException{

		List<WorkspaceItem> listWorkspaceItem = new ArrayList<WorkspaceItem>();

		for(String itemId: listItemsId){
			WorkspaceItem item = workspace.getItem(itemId);
			_log.trace("Attach name: " +item.getName());
			_log.trace("Attach id: " +item.getId());
			listWorkspaceItem.add(workspace.getItem(itemId)); //get item from workspace	
		}

		return listWorkspaceItem;
	}

	@Override
	public boolean saveAttachments(String messageIdentifier, String messageType)
			throws Exception {
		try {

			Workspace workspace = getWorkspace();
			_log.info("save attachments by messageIdentifier");
			_log.trace(" save attachments by messageIdentifier " + messageIdentifier);
			if(messageType.equals(GXTCategoryItemInterface.MS_SENT))
				workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).saveAttachments(workspace.getRoot().getId());
			else
				workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).saveAttachments(workspace.getRoot().getId());

			return true;
			
		} catch (Exception e) {
			_log.error("Error in server save attachments by messageIdentifier ", e);
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public String saveAttachment(String messageIdentifier, String attachmentId, String messageType) throws Exception {
		try {

			Workspace workspace = getWorkspace();
			_log.info("save attachment by attachmentId "+attachmentId);
			_log.trace("messageIdentifier "+messageIdentifier + " attachmentId "+ attachmentId + " messageType " + messageType);
			
			WorkspaceItem item;
			if(messageType.equals(GXTCategoryItemInterface.MS_SENT))
				item = workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).saveAttachment(attachmentId, workspace.getRoot().getId());
			else
				item = workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).saveAttachment(attachmentId, workspace.getRoot().getId());

			if(item!=null)
				return item.getId();
			
			return null;

		} catch (Exception e) {
			_log.error("Error in server attachment by attachmentId ", e);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public boolean saveAttach(String attachId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean markMessage(String messageIdentifier, String messageType,
			boolean boolMark, String markType) throws Exception {
		try {

			Workspace workspace = getWorkspace();
			
			_log.trace(" markMessageAsRead by messageIdentifier " + messageIdentifier + " boolMark " + boolMark + " messageType " +messageType);

			if(messageType.equals(GXTCategoryItemInterface.MS_RECEIVED)){
				//workspaceLogger.trace("#######################################Message identifier: "+messageIdentifier);
				//workspaceLogger.trace("#######################################Message messageType: "+messageType);
				if(markType.equals("READ")){
					workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).setStatus(boolMark);
				}else if(markType.equals("OPEN")){
					workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).open();
				}else if(markType.equals("BOTH")){
					workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).setStatus(boolMark);
					workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).open();
				}
			}else{
				//workspaceLogger.trace("#######################################Message sent identifier: "+messageIdentifier);
				//workspaceLogger.trace("#######################################Message sent messageType: "+messageType);
				if(markType.equals("READ")){
					workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).setStatus(boolMark);
				}else if(markType.equals("OPEN")){
					workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).open();
				}else if(markType.equals("BOTH")){
					workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).setStatus(boolMark);
					workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).open();
				}
			}


			return true;


		} catch (Exception e) {
			_log.error("Error in server markMessageAsRead by messageIdentifier ", e);
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public boolean deleteMessage(String messageIdentifier, String messageType)
			throws Exception {
		try {

			Workspace workspace = getWorkspace();
			
			_log.trace("deleteMessage by messageIdentifier " + messageIdentifier);

			if(messageType.equals(GXTCategoryItemInterface.MS_RECEIVED))
				workspace.getWorkspaceMessageManager().deleteReceivedMessage(messageIdentifier);
			else
				workspace.getWorkspaceMessageManager().deleteSentMessage(messageIdentifier);

			return true;
			
		} catch (Exception e) {
			_log.error("Error in server deleteMessage by messageIdentifier ", e);
			throw new Exception(e.getMessage());
		}
	}


	
	@Override
	public String getMyLogin() {
		return PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest()).getUsername();
	}
	
}
