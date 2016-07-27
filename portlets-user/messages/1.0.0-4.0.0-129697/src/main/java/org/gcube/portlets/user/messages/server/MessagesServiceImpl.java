package org.gcube.portlets.user.messages.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portlets.user.messages.client.rpc.MessagesService;
import org.gcube.portlets.user.messages.server.util.SessionUtil;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;
import org.gcube.portlets.user.messages.shared.SessionExpiredException;
import org.gcube.portlets.user.workspaceapplicationhandler.ApplicationReaderFromGenericResource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MessagesServiceImpl extends RemoteServiceServlet implements MessagesService {

	public static final String TEST_SCOPE = "/gcube/devsec";
	public static final String TEST_USER = "test.user";
	public static final String TEST_USER_FULLNAME = "Test User";
//	public static final String TEST_USER = "francesco.mangiacrapa";
//	public static final String TEST_USER_FULLNAME = "Francesco Mangiacrapa";
	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String TEST_USER_MAIL = "test.user@mail.it";

	
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

	protected GWTMessagesBuilder getGWTWorkspaceBuilder()
	{
		return SessionUtil.getGWTWorkspaceBuilder(getASLSession());
	}

	@Override
	public boolean sentToByMessageModel(MessageModel message) throws Exception {
		return false;
	}

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.error(" STARTING IN TEST MODE - NO USER FOUND");
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, TEST_USER);
			session.setScope(TEST_SCOPE);
			session.setUserEmailAddress(TEST_USER_MAIL);
			session.setUserFullName(TEST_USER_FULLNAME);
			
			return session;
		}
		else {
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
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
		String username = getASLSession().getUsername();
		try {
			if (isWithinPortal()) {
				for (Notification notification :store.getUnreadNotificationsByUser(getASLSession().getUsername()) ) {
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
		try {

			return SessionUtil.getWorkspace(getASLSession());
			
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
		
		if(isSessionExpired())
			throw new SessionExpiredException();
		
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
		
		
		if(isSessionExpired())
			throw new SessionExpiredException();
		
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
		

		if(isSessionExpired())
			throw new SessionExpiredException();
		
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

		if(isSessionExpired())
			throw new SessionExpiredException();
		
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
	public String getURLFromApplicationProfile(String identifier) throws Exception{
		
		String urlPortlet = "";
		ApplicationReaderFromGenericResource app = new ApplicationReaderFromGenericResource();
		
		try{
			urlPortlet = app.getURLFromApplicationProfile(identifier, getASLSession(),this.getThreadLocalRequest().getSession());
		} catch (Exception e) {
			_log.error("getURLFromApplicationProfile", e);
			throw new Exception("Sorry, an error occurred in retrieve application profile, try again");
		}
		
		return urlPortlet;
	}
	
	@Override
	public String getMyLogin() throws Exception{
		try{
		ASLSession asl = getASLSession();
		return asl.getUsername();
		}catch(Exception e){
			_log.trace("an error occurred on recovering my login",e);
			throw new Exception("An error occurred on recovering my login");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#isSessionExpired()
	 */
	@Override
	public boolean isSessionExpired() throws Exception {
		_log.trace("messages session validating...");
		//READING USERNAME FROM ASL SESSION
		String userUsername = getASLSession().getUsername();
		boolean isTestUser = userUsername.compareTo(MessagesServiceImpl.TEST_USER)==0;
		
		//TODO UNCOMMENT THIS FOR DEVELOPEMENT
//		_log.trace("is "+MessagesServiceImpl.TEST_USER+" user: "+isTestUser);
		/*
		if(isTestUser){
			_log.error("workspace session is expired! username is: "+MessagesServiceImpl.TEST_USER);
			return false;
		}*/
//		
		//TODO UNCOMMENT THIS FOR RELEASE
		_log.trace("is "+MessagesServiceImpl.TEST_USER+" user: "+isTestUser +" is into portal: "+isWithinPortal());
		
		if(isTestUser && isWithinPortal()){
			_log.error("workspace session is expired! username is: "+MessagesServiceImpl.TEST_USER);
			return true; //is TEST_USER, session is expired
		}
		
		_log.trace("workspace session is valid! current username is: "+userUsername);
		
		return false;
	}
}
