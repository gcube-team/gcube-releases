package org.gcube.common.homelibrary.jcr.sharing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.DelegateManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRWorkspaceMessage implements WorkspaceMessage {

	private static Logger logger = LoggerFactory.getLogger(JCRWorkspaceMessage.class);

	public enum WorkspaceMessageType {
		RECEIVED,
		SENT
	};


	private final JCRWorkspace workspace;
	private final ItemDelegate messageItem;
	private final WorkspaceMessageType type;
	private List<String> copyAttachmentIds;
	private boolean isRead;
	private boolean isOpened;


	public JCRWorkspaceMessage(JCRWorkspace workspace, ItemDelegate messageItem, WorkspaceMessageType type) throws RepositoryException {

		super();

		this.workspace = workspace;
		this.messageItem = messageItem;
		this.type = type;
	}



	public JCRWorkspaceMessage(JCRWorkspace workspace, ItemDelegate messageItem, WorkspaceMessageType type, String messageId, String subject, String body,
			User sender, List<String> attachmentIds, List<String> addresses) throws  RepositoryException,
			InternalErrorException {

		this.workspace = workspace;
		this.messageItem = messageItem;
		this.type = type;
		this.copyAttachmentIds = new ArrayList<String>();
		this.isRead = false;
		this.isOpened = false;


		Map<NodeProperty, String> properties = new HashMap<NodeProperty, String>();
		properties.put(NodeProperty.SUBJECT, subject);
		properties.put(NodeProperty.BODY, body);
		properties.put(NodeProperty.READ, new XStream().toXML(false));
		properties.put(NodeProperty.OPEN, new XStream().toXML(false));
		
		Map<NodeProperty, String> owner = new HashMap<NodeProperty, String>();
		owner.put(NodeProperty.USER_ID, sender.getId());
		owner.put(NodeProperty.PORTAL_LOGIN, sender.getPortalLogin());
		properties.put(NodeProperty.OWNER, new XStream().toXML(owner));

		properties.put(NodeProperty.ADDRESSES, new XStream().toXML(addresses));
		properties.put(NodeProperty.ATTACHMENTS_ID, new XStream().toXML(attachmentIds));

		messageItem.setProperties(properties);


		JCRServlets servlets = null;
		ItemDelegate saved;
		String rootAttachmentsId = null;
		try{
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			saved = servlets.saveItem(messageItem);

			rootAttachmentsId = saved.getProperties().get(NodeProperty.ATTACHMENTS_ID);
			
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			
			logger.error("Error retrieving attachments id" +  e.getMessage());
		}
		

		List<User> users = new LinkedList<User>();
		for (String address : addresses) {
			User user = workspace.getHome().getHomeManager().getUser(address);
			if(user != null)
				users.add(user);
		}


		try{

			logger.info("attachmentIds.size() " + attachmentIds.size());
			for(String attachmentId : attachmentIds) {
				ItemDelegate nodeItem = servlets.getItemById(attachmentId);

				WorkspaceItem item = workspace.getWorkspaceItem(nodeItem);			
				if(item.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
					try {
						logger.info("nodeItem.getName() " + nodeItem.getName());

						ItemDelegate newNode = ((JCRWorkspaceItem)item).internalCopy(servlets, servlets.getItemById(rootAttachmentsId), nodeItem.getName());

						if (this.type.equals(WorkspaceMessageType.SENT)){			
							//copy all remote content item child nodes.
							workspace.copyRemoteContent(servlets, newNode, null);

							servlets.saveItem(newNode);

							logger.info("add new id to copyAttachmentIds " + newNode.getId());
							copyAttachmentIds.add(newNode.getId());

						} else if (this.type.equals(WorkspaceMessageType.RECEIVED)){
							servlets.getItemById(rootAttachmentsId);
							//create hardlink

							String hardLinkRemotePath = messageItem.getPath() + "/"+ nodeItem.getName();	
							workspace.setHardLink(newNode, hardLinkRemotePath);
						}

						workspace.fireItemSentEvent(item, users);	

					} catch (ItemAlreadyExistException e) {
						throw new InternalErrorException(e);
					} catch (WrongDestinationException e) {
						throw new InternalErrorException(e);
					} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
						throw new InternalErrorException(e);
					} catch (ItemNotFoundException e) {
						throw new InternalErrorException(e);
					}
				}
			}

		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException | ItemNotFoundException e) {
			throw new InternalErrorException(e);

		}finally{
			servlets.releaseSession();
		}
	}


	@Override
	public String getId() {
		return messageItem.getName();
	}

	@Override
	public User getSender() {
		return new JCRUser(messageItem.getOwner(),messageItem.getOwner());

	}

	@Override
	public Calendar getSendTime() {
		return messageItem.getCreationTime();
	}

	@Override
	public String getSubject() {
		return messageItem.getProperties().get(NodeProperty.SUBJECT);
	}

	@Override
	public String getBody() {
		return messageItem.getProperties().get(NodeProperty.BODY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAttachmentsIds() {
		return (List<String>) new XStream().fromXML(messageItem.getProperties().get(NodeProperty.ATTACHMENTS));
	}

	@Override
	public List<String> getCopyAttachmentsIds() {
		return copyAttachmentIds;
	}

	@Override
	public boolean isRead() {
		return isRead;
	}

	@Override
	public void open() throws InternalErrorException {

		this.isOpened = true;

		JCRServlets servlets = null;
		ItemDelegate root = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			switch (type) {
			case RECEIVED:
				root = workspace.getRepository().getOwnInBoxFolder();
				break;
			case SENT:
				root = workspace.getRepository().getOutBoxFolder();
				break;
			}

			DelegateManager manager = new DelegateManager(root, workspace.getOwner().getPortalLogin());
			ItemDelegate node = manager.getNode(getId());
			node.getProperties().put(NodeProperty.OPEN, new XStream().toXML(true));
			//			node.setOpen(true);
			servlets.saveItem(node);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			if (servlets!=null)
				servlets.releaseSession();
		}
	}

	@Override
	public void setStatus(boolean status) throws InternalErrorException {
		this.isRead = status;

		JCRServlets servlets = null;
		ItemDelegate root = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			switch (type) {
			case RECEIVED:
				root = workspace.getRepository().getOwnInBoxFolder();
				break;
			case SENT:
				root = workspace.getRepository().getOutBoxFolder();
				break;
			}

			DelegateManager manager = new DelegateManager(root, workspace.getOwner().getPortalLogin());
			ItemDelegate node = manager.getNode(getId());
			node.getProperties().put(NodeProperty.READ, new XStream().toXML(status));
			servlets.saveItem(node);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			if (servlets!=null)
				servlets.releaseSession();
		}

	}

	@Override
	public void saveAttachments(String destinationFolderId) throws InternalErrorException, 
	WrongDestinationException, ItemNotFoundException {

		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());

			ItemDelegate folderNode;
			WorkspaceFolder folder;
			try {
				folderNode = servlets.getItemById(destinationFolderId);
				folder = (WorkspaceFolder)workspace.getItem(destinationFolderId);
			} catch (Exception e) {
				throw new WrongDestinationException(e.getMessage());
			}
			@SuppressWarnings("unchecked")
			List<String> attachs = (List<String>) new XStream().fromXML(messageItem.getProperties().get(NodeProperty.ATTACHMENTS));
			for(String attachmentId : attachs) {
				saveAttachment(attachmentId, folder, folderNode);
			}		
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}

	@Override
	public WorkspaceItem saveAttachment(String attachmentId, String destinationFolderId) throws InternalErrorException, 
	WrongDestinationException, ItemNotFoundException {

		ItemDelegate folderNode; 
		WorkspaceFolder folder;
		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			folderNode =  servlets.getItemById(destinationFolderId);	
			folder = (WorkspaceFolder)workspace.getItem(destinationFolderId);
		} catch (Exception e) {
			throw new WrongDestinationException(e.getMessage());
		} finally {
			servlets.releaseSession();
		}
		return saveAttachment(attachmentId, folder, folderNode);
	}

	private WorkspaceItem saveAttachment(String attachmentId,
			WorkspaceFolder folder, ItemDelegate folderNode) throws ItemNotFoundException,
			WrongDestinationException, InternalErrorException {

		JCRServlets servlets = null;
		try{
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			ItemDelegate attachment = servlets.getItemById(attachmentId);
			// TODO should implement a new method getUniqueName without open a new session
			String name = folder.getUniqueName(attachment.getTitle(), false);
			String pathDestination =  folderNode.getPath() 
					+ workspace.getPathSeparator() + Text.escapeIllegalJcrChars(name);

			try {
				servlets.copy(attachment.getPath(), pathDestination);
			} catch (HttpException e) {
				throw new InternalErrorException(e);
			} catch (IOException e) {
				throw new InternalErrorException(e);
			}

			//Save the new name to attachment node, useful at the method getUniqueName()
			ItemDelegate itemSaved = servlets.getItemByPath(pathDestination);
			itemSaved.setOwner(workspace.getOwner().getPortalLogin());
			itemSaved.setTitle(name);

			//every download creates a new copy
			//copy all remote content item child nodes.
			workspace.copyRemoteContent(servlets, itemSaved,folderNode);

			servlets.saveItem(itemSaved);	


			return workspace.getItem(itemSaved.getId());
		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException | RemoteBackendException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		} 
	}

	@Override
	public List<WorkspaceItem> getAttachments() throws InternalErrorException {

		List<WorkspaceItem> list = new LinkedList<WorkspaceItem>();
		try {

			@SuppressWarnings("unchecked")
			List<String> attachs = (List<String>) new XStream().fromXML(messageItem.getProperties().get(NodeProperty.ATTACHMENTS));
			for(String id : attachs) {
				list.add(workspace.getItem(id));
			}
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAddresses() {
		return (List<String>) new XStream().fromXML(messageItem.getProperties().get(NodeProperty.ADDRESSES));
	}

	public boolean isOpened() {
		return isOpened;
	}




}
