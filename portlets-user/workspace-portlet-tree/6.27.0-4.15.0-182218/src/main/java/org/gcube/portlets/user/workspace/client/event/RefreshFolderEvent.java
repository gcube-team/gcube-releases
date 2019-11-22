package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class RefreshFolderEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 1, 2019
 */
public class RefreshFolderEvent extends GwtEvent<RefreshItemEventHandler> implements GuiEventInterface {
	public static Type<RefreshItemEventHandler> TYPE = new Type<RefreshItemEventHandler>();

	private FileModel folderTarget= null;

	private boolean expandFolder = true; //DEFAULT EXPAND FOLDER

	private boolean forceRefresh;

	private boolean isCalledTreeSide = false;

	private boolean forceReloadBreadCrumb = false;


	/**
	 * Instantiates a new refresh folder event.
	 *
	 * @param folderTarget the folder target
	 * @param expandFolder the expand folder
	 * @param forceRefresh the force refresh
	 * @param calledTreeSide the called tree side
	 */
	public RefreshFolderEvent(FileModel folderTarget, boolean expandFolder, boolean forceRefresh, boolean calledTreeSide) {
		this.folderTarget = folderTarget;
		this.expandFolder = expandFolder;
		this.forceRefresh = forceRefresh;
		this.isCalledTreeSide = calledTreeSide;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<RefreshItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(RefreshItemEventHandler handler) {
		handler.onRefreshItem(this);

	}

	/**
	 * Gets the folder target.
	 *
	 * @return the folder target
	 */
	public FileModel getFolderTarget() {
		return folderTarget;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.REFRESH_FOLDER;
	}


	/**
	 * Checks if is expand folder.
	 *
	 * @return true, if is expand folder
	 */
	public boolean isExpandFolder() {
		return expandFolder;
	}


	/**
	 * Sets the expand folder.
	 *
	 * @param expandFolder the new expand folder
	 */
	public void setExpandFolder(boolean expandFolder) {
		this.expandFolder = expandFolder;
	}


	/**
	 * Checks if is force refresh.
	 *
	 * @return true, if is force refresh
	 */
	public boolean isForceRefresh() {
		return forceRefresh;
	}


	/**
	 * Sets the force refresh.
	 *
	 * @param forceRefresh the new force refresh
	 */
	public void setForceRefresh(boolean forceRefresh) {
		this.forceRefresh = forceRefresh;
	}


	/**
	 * Checks if is called tree side.
	 *
	 * @return true, if is called tree side
	 */
	public boolean isCalledTreeSide() {
		return isCalledTreeSide;
	}

	/**
	 * Checks if is force reload bread crumb.
	 *
	 * @return true, if is force reload bread crumb
	 */
	public boolean isForceReloadBreadCrumb() {
		return forceReloadBreadCrumb;
	}


	/**
	 * Sets the force reload bread crumb.
	 *
	 * @param forceReloadBreadCrumb the new force reload bread crumb
	 */
	public void setForceReloadBreadCrumb(boolean forceReloadBreadCrumb) {
		this.forceReloadBreadCrumb = forceReloadBreadCrumb;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("RefreshFolderEvent [folderTarget=");
		builder.append(folderTarget);
		builder.append(", expandFolder=");
		builder.append(expandFolder);
		builder.append(", forceRefresh=");
		builder.append(forceRefresh);
		builder.append(", forceReloadBreadCrumb=");
		builder.append(forceReloadBreadCrumb);
		builder.append("]");
		return builder.toString();
	}


}
