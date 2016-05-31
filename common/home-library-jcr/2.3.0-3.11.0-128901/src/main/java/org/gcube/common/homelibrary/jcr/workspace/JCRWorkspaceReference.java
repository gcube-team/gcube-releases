package org.gcube.common.homelibrary.jcr.workspace;

import java.util.List;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceReference;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;


public class JCRWorkspaceReference extends JCRWorkspaceItem implements WorkspaceReference {

	public JCRWorkspaceReference(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {	
		super(workspace,delegate);	
	}

	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.FOLDER_ITEM;
	}

	@Override
	public WorkspaceItem getLink() throws InternalErrorException {
	
		return null;
	}

	@Override
	public List<? extends WorkspaceItem> getChildren()
			throws InternalErrorException {
		return null;
	}

	@Override
	public void removeChild(WorkspaceItem child) throws InternalErrorException,
			InsufficientPrivilegesException {
		super.remove();
	}




}
