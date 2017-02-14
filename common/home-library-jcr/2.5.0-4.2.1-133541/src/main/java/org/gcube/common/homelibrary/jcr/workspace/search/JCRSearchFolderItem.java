package org.gcube.common.homelibrary.jcr.workspace.search;

import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;



public class JCRSearchFolderItem extends JCRSearchFolder implements SearchFolderItem {


	private FolderItemType folderItemType;

	public JCRSearchFolderItem(SearchItemDelegate delegate, FolderItemType type) throws RepositoryException  {
		super(delegate);
		this.folderItemType = type;
	}


	@Override
	public long getSize() {
		return delegate.getSize();
	}

	@Override
	public String getMimeType() {
		return delegate.getMimeType();
	}

	@Override
	public WorkspaceItemType getType() {		
		return WorkspaceItemType.FOLDER_ITEM;	
	}

	@Override
	public FolderItemType getFolderItemType() {
		return folderItemType;
	}


}
