/*
 *
 */
package org.gcube.portlets.user.workspace.client.gridevent;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.view.versioning.WindowVersioning;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class FileVersioningEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Feb 20, 2017
 */
public class FileVersioningEvent extends GwtEvent<FileVersioningEventHandler> implements GuiEventInterface{

	public static Type<FileVersioningEventHandler> TYPE = new Type<FileVersioningEventHandler>();
	private WorkspaceVersioningOperation workspaceVersioningOperation;
	private FileModel currentVersion;
	private List<FileVersionModel> olderVersions;
	private WindowVersioning winVersioning;
	private List<FileVersionModel> selectedVersions;


	/**
	 * Instantiates a new file versioning event.
	 *
	 * @param workspaceVersioningOperation the workspace versioning operation
	 * @param currentVersion the current version
	 * @param selectedVersions the selected versions
	 * @param olderVersions the older versions
	 * @param winVersioning the win versioning
	 */
	public FileVersioningEvent(WorkspaceVersioningOperation workspaceVersioningOperation, FileModel currentVersion, List<FileVersionModel> selectedVersions, List<FileVersionModel> olderVersions, WindowVersioning winVersioning) {
		this.workspaceVersioningOperation = workspaceVersioningOperation;
		this.currentVersion = currentVersion;
		this.selectedVersions = selectedVersions;
		this.olderVersions = olderVersions;
		this.winVersioning = winVersioning;

	}

	/**
	 * Gets the win versioning.
	 *
	 * @return the winVersioning
	 */
	public WindowVersioning getWinVersioning() {

		return winVersioning;
	}

	/**
	 * Gets the current version.
	 *
	 * @return the currentVersion
	 */
	public FileModel getCurrentVersion() {

		return currentVersion;
	}



	/**
	 * Sets the current version.
	 *
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(FileModel currentVersion) {

		this.currentVersion = currentVersion;
	}


	/**
	 * Gets the older versions.
	 *
	 * @return the older versions
	 */
	public List<FileVersionModel>  getOlderVersions() {

		return olderVersions;
	}


	/**
	 * Sets the workspace versioning operation.
	 *
	 * @param workspaceVersioningOperation the workspaceVersioningOperation to set
	 */
	public void setWorkspaceVersioningOperation(WorkspaceVersioningOperation workspaceVersioningOperation) {

		this.workspaceVersioningOperation = workspaceVersioningOperation;
	}


	/**
	 * Sets the older versions.
	 *
	 * @param olderVersions the new older versions
	 */
	public void setOlderVersions(List<FileVersionModel> olderVersions) {

		this.olderVersions = olderVersions;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<FileVersioningEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(FileVersioningEventHandler handler) {
		handler.onFileVersioning(this);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.FILE_VERSIONING_HISTORY_EVENT;
	}


	/**
	 * Gets the selected versions.
	 *
	 * @return the selectedVersions
	 */
	public List<FileVersionModel> getSelectedVersions() {

		return selectedVersions;
	}

	/**
	 * Sets the selected versions.
	 *
	 * @param selectedVersions the selectedVersions to set
	 */
	public void setSelectedVersions(List<FileVersionModel> selectedVersions) {

		this.selectedVersions = selectedVersions;
	}


	/**
	 * Sets the versioning operation.
	 *
	 * @param workspaceVersioningOperation the new versioning operation
	 */
	public void setVersioningOperation(WorkspaceVersioningOperation workspaceVersioningOperation) {
		this.workspaceVersioningOperation = workspaceVersioningOperation;
	}


	/**
	 * Gets the versioning operation.
	 *
	 * @return the versioning operation
	 */
	public WorkspaceVersioningOperation getVersioningOperation() {

		return workspaceVersioningOperation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("FileVersioningEvent [workspaceVersioningOperation=");
		builder.append(workspaceVersioningOperation);
		builder.append(", currentVersion=");
		builder.append(currentVersion);
		builder.append(", olderVersions=");
		builder.append(olderVersions);
		builder.append(", winVersioning=");
		builder.append(winVersioning);
		builder.append(", selectedVersions=");
		builder.append(selectedVersions);
		builder.append("]");
		return builder.toString();
	}



}
