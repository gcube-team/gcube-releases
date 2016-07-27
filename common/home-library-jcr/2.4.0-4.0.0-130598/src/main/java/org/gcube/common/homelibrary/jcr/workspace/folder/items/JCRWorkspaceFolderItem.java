package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRProperties;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

public abstract class JCRWorkspaceFolderItem extends JCRWorkspaceItem implements
FolderItem {

	protected static final String CONTENT			= "jcr:content";
	protected static final String FOLDER_ITEM_TYPE 	= "hl:workspaceItemType";


	public JCRWorkspaceFolderItem(JCRWorkspace workspace,
			ItemDelegate delegate)throws RepositoryException {
		super(workspace, delegate);
	}
	

	public JCRWorkspaceFolderItem(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description)  throws RepositoryException {
		super(workspace, delegate, name, description);
	}
	
	public JCRWorkspaceFolderItem(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, Map<String, String> properties)  throws RepositoryException {
		this(workspace, delegate, name, description);
		super.setMetadata(properties);

	}


	@Override
	public ItemDelegate save() throws RepositoryException {
		return super.save();
	}
	

	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.FOLDER_ITEM;
	}

	@Override
	public abstract FolderItemType getFolderItemType();

	@Override
	public abstract long getLength() throws InternalErrorException;

	@Override
	public abstract String getMimeType() throws InternalErrorException;


	@Override
	public  List<? extends WorkspaceItem> getChildren() throws InternalErrorException {
		return new ArrayList<WorkspaceItem>();
	}


	@Override
	public void removeChild(WorkspaceItem child) {
		return;
	}

	@Override
	public Properties getProperties() throws InternalErrorException {
		try {
			return new JCRProperties(delegate, workspace.getOwner().getPortalLogin());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}


	public void removeRemoteContent(Node node, WorkspaceItemType workspaceItemType) throws RepositoryException, RemoteBackendException {
		try {
			Node contentNode = node.getNode(CONTENT);
			if (contentNode.hasProperty(JCRFile.REMOTE_STORAGE_PATH)) {	

				String remotePath = contentNode.getProperty(JCRFile.REMOTE_STORAGE_PATH).getString();
				try{
					if (workspaceItemType == WorkspaceItemType.FOLDER)
						workspace.getStorage().removeRemoteFolder(remotePath);
					else
						workspace.getStorage().removeRemoteFile(remotePath);
					

				}catch (Exception e) {
					logger.warn(node.getName() + " payload not available", e);
				}
			}	
		} catch (javax.jcr.RepositoryException e) {
			logger.error("Content node "+ CONTENT + " not found",e);
		} 
	}

	//create a new payload
	public void copyRemoteContent(JCRSession servlets, ItemDelegate node) throws RepositoryException, RemoteBackendException {
//		JCRServlets servlets = null;
		try {
//			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			Map<NodeProperty, String> contentNode = node.getContent();
			if (contentNode.containsKey(NodeProperty.REMOTE_STORAGE_PATH)) {	

				String remotePath = contentNode.get(NodeProperty.REMOTE_STORAGE_PATH);
				String newRemotePath = node.getPath();

				logger.debug("copy from remote path: " + remotePath + " to: "+ newRemotePath);
				
				workspace.getStorage().copyRemoteFile(remotePath, newRemotePath);		
				contentNode.put(NodeProperty.REMOTE_STORAGE_PATH, newRemotePath);
				node.setOwner(workspace.getOwner().getPortalLogin());

				// Store url as byte stream in jcr:data binary property
//				ByteArrayInputStream  binaryUrl = new ByteArrayInputStream(url.getBytes());
//				Binary binary = contentNode.getSession().getValueFactory().createBinary(binaryUrl);
				contentNode.put(NodeProperty.DATA, Integer.toBinaryString(0));
				
				servlets.saveItem(node);

			}	
		} catch (Exception e) {
			logger.error("Content property "+ CONTENT + " not found",e);
		} 
//		finally {
//			servlets.releaseSession();
//		}

	}



	public void setHardLink(ItemDelegate node, String hardLinkRemotePath) throws RepositoryException {
		logger.info("hardLinkRemotePath: " + hardLinkRemotePath);
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			Map<NodeProperty, String> content = node.getContent();
			if (content.containsKey(NodeProperty.REMOTE_STORAGE_PATH)){
	
				String remotePath = content.get(NodeProperty.REMOTE_STORAGE_PATH);
				workspace.getStorage().createHardLink(remotePath, hardLinkRemotePath);

				content.put(NodeProperty.REMOTE_STORAGE_PATH, hardLinkRemotePath);
				servlets.saveItem(node);

			}	
		} catch (Exception e) {
			logger.error("Content node "+ CONTENT + " not found",e);
		} finally {
			servlets.releaseSession();
		}

	}



}
