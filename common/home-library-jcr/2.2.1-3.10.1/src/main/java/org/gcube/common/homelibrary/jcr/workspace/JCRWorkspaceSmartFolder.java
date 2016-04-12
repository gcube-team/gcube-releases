package org.gcube.common.homelibrary.jcr.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRWorkspaceFolderItem;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class JCRWorkspaceSmartFolder extends JCRWorkspaceFolderItem implements WorkspaceSmartFolder {

	public JCRWorkspaceSmartFolder(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException,
	InternalErrorException{
		super(workspace, delegate);
	}


	@Override
	public void remove() throws InternalErrorException,
	InsufficientPrivilegesException {
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			servlets.removeItem(delegate.getPath());
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} finally {
			servlets.releaseSession();
		}

	}


	public JCRWorkspaceSmartFolder(JCRWorkspace workspace, ItemDelegate node, String name,
			String description, String query, String folderId) throws RepositoryException  {
		super(workspace, node, name, description);

		delegate.setContent(new HashMap<NodeProperty, String>());
//		delegate.getProperties().put(NodeProperty.CONTENT, ContentType.SMART.toString());
		delegate.getContent().put(NodeProperty.QUERY, query);
		delegate.getContent().put(NodeProperty.FOLDER_ID, folderId);

	}


	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.SMART_FOLDER;
	}

	@Override
	public List<? extends WorkspaceItem> getChildren()
			throws InternalErrorException {

		return null;
	}


	@Override
	public FolderItemType getFolderItemType() {
		return null;
	}


	@Override
	public long getLength() throws InternalErrorException {
		return 0;
	}


	@Override
	public List<? extends SearchItem> getSearchItems()
			throws InternalErrorException {
		String folderId = null;
		try{
			folderId = delegate.getContent().get(NodeProperty.FOLDER_ID);
		} catch (Exception e) {
//			throw new InternalErrorException(e);
		} 
		if (folderId == null)
			folderId = workspace.getRoot().getId();
		
		return workspace.searchByName(delegate.getContent().get(NodeProperty.QUERY), folderId);

	}

	@Override
	public String getMimeType() throws InternalErrorException {
		return null;
	}


	@Override
	public ACLType getACLUser() throws InternalErrorException {
		return null;
	}


	@Override
	public Map<ACLType, List<String>> getACLOwner()
			throws InternalErrorException {
		return null;
	}
	
	



}
