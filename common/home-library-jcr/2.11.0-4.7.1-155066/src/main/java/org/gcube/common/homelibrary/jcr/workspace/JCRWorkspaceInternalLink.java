package org.gcube.common.homelibrary.jcr.workspace;

import java.util.List;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceInternalLink;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import com.thoughtworks.xstream.XStream;


public class JCRWorkspaceInternalLink extends JCRWorkspaceItem implements WorkspaceInternalLink {
	
	public JCRWorkspaceInternalLink(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {	
		super(workspace,delegate);	
	}

	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.FOLDER_ITEM;
	}

	
	public WorkspaceItem getReference() throws InternalErrorException {
		WorkspaceItem link = null;
		try{
			ItemDelegate refDelegate = (ItemDelegate) new XStream().fromXML(delegate.getProperties().get(NodeProperty.REFERENCE));
			link = workspace.getWorkspaceItem(refDelegate);
		}catch (Exception e) {
			throw new InternalErrorException("Impossible to get Reference link for item " + delegate.getPath(), e);
		}
		return link;
	}

	@Override
	public List<? extends WorkspaceItem> getChildren()
			throws InternalErrorException {

		List<WorkspaceItem> children = null;
		try{
			WorkspaceFolder folder = (WorkspaceFolder) getReference();
			children = folder.getChildren();
		}catch (Exception e) {
			throw new InternalErrorException("Impossible to get children of reference link " + delegate.getPath(), e);
		}
		return children;

	}

	@Override
	public void removeChild(WorkspaceItem child) throws InternalErrorException, InsufficientPrivilegesException {
		super.remove();
		
	}



}
