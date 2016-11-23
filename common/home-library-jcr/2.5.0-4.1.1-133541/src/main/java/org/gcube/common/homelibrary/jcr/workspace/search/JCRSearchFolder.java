package org.gcube.common.homelibrary.jcr.workspace.search;

import java.util.Calendar;

import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class JCRSearchFolder implements SearchFolder {

//	private Logger logger;

	protected SearchItemDelegate delegate;
	
	public JCRSearchFolder(SearchItemDelegate delegate){
		this.delegate = delegate;
//		this.logger = LoggerFactory.getLogger(JCRSearchFolder.class);
	}

	@Override
	public String getId() {
		return delegate.getId();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Calendar getCreationDate() {
		return delegate.getCreationTime();
	}

	@Override
	public Calendar getLastModified() {		
		return delegate.getLastModificationTime();
	}

	@Override
	public String getOwner() {
		return delegate.getOwner();
	}

	@Override
	public boolean isVreFolder() {
		return delegate.isVreFolder();
	}

	@Override
	public boolean isShared() {
			return delegate.isShared();
	}

	@Override
	public WorkspaceItemType getType() {

		return delegate.getType();

	}



}
