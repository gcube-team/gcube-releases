package org.gcube.common.homelibrary.jcr.sharing;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.httpclient.HttpException;
import org.apache.tika.metadata.Message;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessage.WorkspaceMessageType;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRWorkspaceMessageManager implements WorkspaceMessageManager {

	private JCRWorkspace workspace;
	private static Logger logger = LoggerFactory.getLogger(JCRWorkspaceMessageManager.class);
	protected static final String CONTENT			= "jcr:content";
	protected static final String ATTACHMENTS		= "hl:attachments";

	public JCRWorkspaceMessageManager(JCRWorkspace workspace) {
		super();
		this.workspace = workspace;
	}

	@Override
	public String sendMessageToPortalLogins(String subject, String body,
			List<String> attachmentIds, List<String> addresses)
					throws InternalErrorException {
		JCRWorkspaceMessage itemToSend = null;
		Session session = JCRRepository.getSession();
		try {

			String messageId = UUID.randomUUID().toString();

			//node for HiddenFolder
			//			Node nodeHiddenFolder = workspace.getRepository().getHiddenFolder(session);

			//node for OutBox
			ItemDelegate nodeOutBox = workspace.getRepository().getOutBoxFolder();	

			//message for folder OutBox
			JCRServlets servlets = null;
			JCRWorkspaceMessage itemInSentFolder = null;
			try{
				servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
	
				DelegateManager wrap = new DelegateManager(nodeOutBox, workspace.getOwner().getPortalLogin());

				ItemDelegate nodeSentMessage = wrap.addNode(messageId, PrimaryNodeType.NT_ITEM_SENT);
				
				itemInSentFolder = new JCRWorkspaceMessage(workspace, nodeSentMessage, WorkspaceMessageType.SENT, 
						messageId, subject, body, workspace.getOwner(), attachmentIds, addresses);
			} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
				logger.error("Internal error exception",e);
				throw new InternalErrorException(e);
			}


			List<String> copyAttachmentsIds = null;
			if (itemInSentFolder!=null)
				copyAttachmentsIds = itemInSentFolder.getCopyAttachmentsIds();

			Set<String> set = new HashSet<String>(addresses);

			for (String user: set) {

				logger.debug("Send message to user " + user);
				HomeManager homeManager = workspace.getHome().getHomeManager();
				homeManager.getHome(user);

				ItemDelegate inBoxFolder = workspace.getRepository().getInBoxFolder(user);

				try{
					servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
					
					DelegateManager wrap = new DelegateManager(inBoxFolder, workspace.getOwner().getPortalLogin());
					ItemDelegate sentMessage = wrap.addNode(messageId, PrimaryNodeType.NT_ITEM_SENT);
				
					//message to send
					itemToSend = new JCRWorkspaceMessage(workspace, sentMessage, WorkspaceMessageType.RECEIVED,
							messageId, subject, body, workspace.getOwner(), copyAttachmentsIds, addresses);				
				} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
					logger.error("Internal error exception",e);
					throw new InternalErrorException(e);
				}
				
				
//				ItemDelegate itemNode = null ;
//
//				//message to send
//				itemToSend = new JCRWorkspaceMessage(workspace, itemNode, WorkspaceMessageType.RECEIVED,
//						messageId, subject, body, workspace.getOwner(), copyAttachmentsIds, addresses);				
//
////				session.save();
			}
		} catch (HomeNotFoundException e) {
			logger.error("User home not found",e);
			throw new InternalErrorException(e);
		} catch (RepositoryException e) {
			logger.error("Internal error exception",e);
			throw new InternalErrorException(e);
		} catch (UserNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			session.logout();
		}

		return itemToSend.getId();


	}

	//	private List<String> getHardLinks(Session session, List<String> attachmentIds,
	//			Node nodeHiddenFolder) throws ItemNotFoundException, InternalErrorException{
	//		 List<String> hardLinks = new ArrayList<String>();
	//			for (String id : attachmentIds) {
	//				try {
	//					Node nodeItem = session.getNodeByIdentifier(id);
	//					WorkspaceItem item = workspace.getWorkspaceItem(nodeItem);
	//			
	//				} catch (RepositoryException e) {
	//					logger.error("Internal error exception",e);
	//					throw new InternalErrorException(e);				
	//				} catch (InternalErrorException e) {
	//					logger.error("Internal error exception",e);
	//					throw new InternalErrorException(e);
	//				}
	//				
	////				hardLinks.add(user.getPortalLogin());
	//			}
	//		return hardLinks;
	//	}

	@Override
	public String sendMessageToUsers(String subject, String body,
			List<String> attachmentIds, List<User> addresses)
					throws InternalErrorException {

		Set<User> set = new HashSet<User>(addresses);

		List<String> list = new LinkedList<String>();
		for (User user : set) {
			list.add(user.getPortalLogin());
		}

		return sendMessageToPortalLogins(subject, body, attachmentIds, list);

	}

	@Override
	public WorkspaceMessage getSentMessage(String id) throws InternalErrorException,
	ItemNotFoundException {

		try {
			ItemDelegate outBoxFolder = workspace.getRepository().getOutBoxFolder();
			ItemDelegate messageNode = new DelegateManager(outBoxFolder, workspace.getOwner().getPortalLogin()).getNode(id);

			WorkspaceMessage message = new JCRWorkspaceMessage(workspace, messageNode, WorkspaceMessageType.SENT);
			return message;
		} catch (PathNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public WorkspaceMessage getReceivedMessage(String id) throws InternalErrorException,
	ItemNotFoundException {

		try {

			ItemDelegate ownInBoxFolder = workspace.getRepository().getOwnInBoxFolder();
			ItemDelegate messageNode = new DelegateManager(ownInBoxFolder, workspace.getOwner().getPortalLogin()).getNode(id);

			WorkspaceMessage message = new JCRWorkspaceMessage(workspace, messageNode, WorkspaceMessageType.RECEIVED);
			return message;
		} catch (PathNotFoundException e) {
			throw new ItemNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}

	@Override
	public void deleteReceivedMessage(String id) {

		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());

			ItemDelegate ownInBoxFolder = workspace.getRepository().getOwnInBoxFolder();
			ItemDelegate messageNode = new DelegateManager(ownInBoxFolder, workspace.getOwner().getPortalLogin()).getNode(id);

			//delete msg in Storage			
			//			System.out.println("attachNode.getPath() " + messageNode.getPath());
			workspace.getStorage().removeRemoteFolder(messageNode.getPath());

			//delete msg from JR	
			servlets.removeItem(messageNode.getPath());

		} catch (Exception e) {
			logger.error("Internal error exception",e);
		} finally {
			if(servlets	!= null)
				servlets.releaseSession();
		}
	}



	@Override
	public void deleteSentMessage(String id) {

		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			DelegateManager wrap = new DelegateManager(workspace.getRepository().getOutBoxFolder(), workspace.getOwner().getPortalLogin());
			ItemDelegate messageNode = wrap.getNode(id);

			//delete msg in Storage			
			//			System.out.println("attachNode.getPath() " + messageNode.getPath());
			//			GCUBEStorage.removeRemoteFolder(messageNode.getPath());

			workspace.getStorage().removeRemoteFolder(messageNode.getPath());

			//delete msg from JR	
			servlets.removeItem(messageNode.getPath());

		} catch (Exception e) {
			logger.error("Internal error exception",e);
		} finally {
			if(servlets	!= null)
				servlets.releaseSession();
		}
	}

	@Override
	public List<WorkspaceMessage> getReceivedMessages() {

		List<WorkspaceMessage> list = new LinkedList<WorkspaceMessage>();
		try {	
			DelegateManager wrap = new DelegateManager(workspace.getRepository().getInBoxFolder(workspace.getOwner().getPortalLogin()), workspace.getOwner().getPortalLogin());
			for( ItemDelegate messageNode : wrap.getNodes()) 
				list.add(new JCRWorkspaceMessage(workspace, messageNode, WorkspaceMessageType.RECEIVED));
		} catch (Exception e) {
			logger.error("Internal error exception",e);
		} 
		return list;
	}	

	@Override
	public List<WorkspaceMessage> getSentMessages() {

		List<WorkspaceMessage> list = new LinkedList<WorkspaceMessage>();
		try {

			DelegateManager wrap = new DelegateManager(workspace.getRepository().getOutBoxFolder(), workspace.getOwner().getPortalLogin());
			for( ItemDelegate messageNode : wrap.getNodes()) 
				list.add(new JCRWorkspaceMessage(workspace, messageNode, WorkspaceMessageType.SENT));
		} catch (Exception e) {
			logger.error("Internal error exception",e);
		}
		return list;
	}

	@Override
	public int getMessagesNotOpened() {

		int count = 0;
		for(WorkspaceMessage message : getReceivedMessages()) {
			if(!((JCRWorkspaceMessage)message).isOpened())
				count ++;
		}
		return count;
	}

	@Override
	public List<WorkspaceMessage> searchInMessages(String word) throws InternalErrorException {

		try {
			ItemDelegate nodeInBox = workspace.getRepository().getOwnInBoxFolder();
			List<ItemDelegate> iterator = getNodeSearched(nodeInBox, word); 
			List<WorkspaceMessage> list = getMessagesSearched(iterator, WorkspaceMessageType.RECEIVED);
			return list;
		} catch (RepositoryException e) {
			logger.error("Error ",e);
			throw new InternalErrorException(e);
		} 

	}

	@Override
	public List<WorkspaceMessage> searchOutMessages(String word) throws InternalErrorException {

		try {
			ItemDelegate nodeOutBox = workspace.getRepository().getOutBoxFolder();
			List<ItemDelegate> iterator = getNodeSearched(nodeOutBox, word); 
			List<WorkspaceMessage> list = getMessagesSearched(iterator, WorkspaceMessageType.SENT);
			return list;
		} catch (RepositoryException e) {
			logger.error("Error",e);
			throw new InternalErrorException(e);
		} 

	}

	private List<WorkspaceMessage> getMessagesSearched(List<ItemDelegate> messages, WorkspaceMessageType type) {

		List<WorkspaceMessage> list = new LinkedList<WorkspaceMessage>();
		for(ItemDelegate message : messages) 
			try {
				list.add(new JCRWorkspaceMessage(workspace, message, type));
			} catch (RepositoryException e) {
				logger.error("Message Item " + message.getTitle() + " unknow"); 
			}

		return list;
	}

	private List<ItemDelegate> getNodeSearched(ItemDelegate searchRoot, String word)
			throws InternalErrorException {
		JCRServlets servlets = null;
		List<ItemDelegate> itemDelegateList = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());		

			String xpath = "/jcr:root" 
					+ searchRoot.getPath() + "//element()[jcr:contains(@hl:subject,'*" + word + "*') or " +
					"jcr:contains(@hl:body,'*" + word + "*') ] ";

			itemDelegateList = servlets.searchItems(xpath, javax.jcr.query.Query.XPATH);

		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (HttpException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
		return itemDelegateList ;
	}


}
