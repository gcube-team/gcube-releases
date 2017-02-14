package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class RefreshFolderEvent extends GwtEvent<RefreshItemEventHandler> implements GuiEventInterface {
	public static Type<RefreshItemEventHandler> TYPE = new Type<RefreshItemEventHandler>();
	
	private FileModel folderTarget= null;

	private boolean expandFolder = true; //DEFAULT EXPAND FOLDER

	private boolean forceRefresh;

	private boolean ifExists = false;
	
	private boolean forceReloadBreadCrumb = false;

	/**
	 * 
	 * @param folderTarget
	 * @param expandFolder - used to expand the folder into tree after refresh
	 * @param forceRefresh - used to force refresh into grid
	 * @param ifExists - refresh only if items exists (into tree)
	 */
	public RefreshFolderEvent(FileModel folderTarget, boolean expandFolder, boolean forceRefresh, boolean ifExists) {
		this.folderTarget = folderTarget;
		this.expandFolder = expandFolder;
		this.forceRefresh = forceRefresh;
		this.ifExists = ifExists;
	}
	

	@Override
	public Type<RefreshItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RefreshItemEventHandler handler) {
		handler.onRefreshItem(this);
		
	}

	public FileModel getFolderTarget() {
		return folderTarget;
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.REFRESH_FOLDER;
	}


	public boolean isExpandFolder() {
		return expandFolder;
	}


	public void setExpandFolder(boolean expandFolder) {
		this.expandFolder = expandFolder;
	}


	public boolean isForceRefresh() {
		return forceRefresh;
	}


	public void setForceRefresh(boolean forceRefresh) {
		this.forceRefresh = forceRefresh;
	}


	public boolean isIfExists() {
		return ifExists;
	}

	public boolean isForceReloadBreadCrumb() {
		return forceReloadBreadCrumb;
	}


	public void setForceReloadBreadCrumb(boolean forceReloadBreadCrumb) {
		this.forceReloadBreadCrumb = forceReloadBreadCrumb;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RefreshFolderEvent [folderTarget=");
		builder.append(folderTarget);
		builder.append(", expandFolder=");
		builder.append(expandFolder);
		builder.append(", forceRefresh=");
		builder.append(forceRefresh);
		builder.append(", ifExists=");
		builder.append(ifExists);
		builder.append(", forceReloadBreadCrumb=");
		builder.append(forceReloadBreadCrumb);
		builder.append("]");
		return builder.toString();
	}
	
}
