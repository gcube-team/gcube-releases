package org.gcube.common.homelibrary.home.workspace;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

public interface WorkspaceCatalogueItem extends WorkspaceItem {
	
	public String getWorkspaceId(String id) throws InternalErrorException;

}
