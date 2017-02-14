package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import com.thoughtworks.xstream.XStream;

public class JCRGCubeItem extends JCRWorkspaceItem implements GCubeItem {

	public JCRGCubeItem(JCRWorkspace workspace, ItemDelegate node, String name,
			String description, List<String> scopes, String creator, String itemType, Map<String, String> properties) throws RepositoryException, ItemNotFoundException {
		super(workspace, node, name, description);

		Validate.notNull(scopes, "scopes must be not null");
		Validate.notEmpty(scopes, "scopes must be not empty");
		Validate.notNull(creator, "creator must be not null");
		Validate.notNull(itemType, "item type must be not null");

		delegate.setProperties(new HashMap<NodeProperty, String>());
		delegate.getProperties().put(NodeProperty.CREATOR, creator);
		delegate.getProperties().put(NodeProperty.ITEM_TYPE, itemType);
		delegate.getProperties().put(NodeProperty.SCOPES, new XStream().toXML(scopes));
		delegate.setMetadata(properties);
		//		delegate.getProperties().put(NodeProperty.PROPERTY, new XStream().toXML(properties));
		delegate.getProperties().put(NodeProperty.IS_SHARED, new XStream().toXML(false));

	}

	public JCRGCubeItem(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate);
	}


	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.FOLDER_ITEM;
	}

	@Override
	public List<? extends WorkspaceItem> getChildren()
			throws InternalErrorException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getScopes() throws InternalErrorException {
		return (List<String>) new XStream().fromXML(delegate.getProperties().get(NodeProperty.SCOPES));
	}

	@Override
	public String getItemType() {
		return delegate.getProperties().get(NodeProperty.ITEM_TYPE);
	}

	@Override
	public String getCreator() {
		return delegate.getProperties().get(NodeProperty.CREATOR);
	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.GCUBE_ITEM;
	}

	@Override
	public long getLength() throws InternalErrorException {
		return 0;
	}

	@Override
	public String getMimeType() throws InternalErrorException {
		return null;
	}


	@Override
	public void removeChild(WorkspaceItem child) throws InternalErrorException,
	InsufficientPrivilegesException {

	}

	@Override
	public boolean isShared() throws InternalErrorException {
		return delegate.isShared();
	}

	@Override
	public WorkspaceSharedFolder share(List<String> users) throws InternalErrorException {
		WorkspaceSharedFolder folder = null;
		try {
			folder = workspace.shareFolder(users, getId());
		} catch (InsufficientPrivilegesException e) {
			throw new InternalErrorException(e);
		} catch (WrongDestinationException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		}
		return folder;
	}


	public void setSharedRootId(String id) {
		Map<NodeProperty, String> properties = delegate.getProperties();
		properties.put(NodeProperty.SHARED_ROOT_ID, id);
		properties.put(NodeProperty.IS_SHARED, new XStream().toXML(true));

		delegate.setProperties(properties);

	}


	@Override
	public ItemDelegate save() throws RepositoryException {
		return super.save();
	}


	@Override
	public String getIdSharedFolder() throws InternalErrorException {
		JCRSession servlets = null;
		ItemDelegate item = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			item = servlets.getItemById(getId());
		} catch (ItemNotFoundException e) {
			logger.error("impossible to retrieve item id: " + getId());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

		return item.getProperties().get(NodeProperty.SHARED_ROOT_ID);
	}

	@Override
	public InputStream getData() throws InternalErrorException {

		InputStream stream = null;

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);

			ItemDelegate node = servlets.getItemById(delegate.getId());

			String remotePath = null;
			String storageID = null;
			try {	
				storageID = node.getProperties().get(NodeProperty.STORAGE_ID);
			} catch (Exception e) {
				logger.trace("Storage ID not found");
				try {
					remotePath = node.getProperties().get(NodeProperty.REMOTE_STORAGE_PATH);
				} catch (Exception e1) {
					return null; 
					//				throw new InternalErrorException(e);
				}
			}
			if (storageID != null) 
				remotePath = storageID;

			//			System.out.println("remotePath " + remotePath );
			// The remote data is stored on GCUBE storage.
			if (remotePath != null) {
				logger.trace("Content retrieved from remote storage...");

				try{
					stream = workspace.getStorage().getRemoteFile(remotePath);				
				}catch (Exception e) {
					logger.error("no payload for " + getName());
				}	
			} 

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}
		return stream;

	}

	@Override
	public String getRemotePath() throws InternalErrorException {
		String remotePath = null;
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			ItemDelegate node = servlets.getItemById(delegate.getId());
			remotePath = node.getProperties().get(NodeProperty.REMOTE_STORAGE_PATH);
		} catch (Exception e) {
			throw new InternalErrorException(e.getMessage());
		} finally {
			servlets.releaseSession();
		}
		return remotePath;
	}



}
