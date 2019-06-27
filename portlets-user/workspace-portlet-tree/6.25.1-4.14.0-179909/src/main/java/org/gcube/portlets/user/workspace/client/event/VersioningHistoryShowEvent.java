package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class FileVersioningEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Feb 20, 2017
 */
public class VersioningHistoryShowEvent extends GwtEvent<VersioningHistoryShowEventHandler> implements GuiEventInterface{

	public static Type<VersioningHistoryShowEventHandler> TYPE = new Type<VersioningHistoryShowEventHandler>();

	private  FileModel targetFileModel;
	private WorkspaceVersioningOperation workspaceVersioningOperation;

	/**
	 * Instantiates a new file versioning event.
	 *
	 * @param workspaceVersioningOperation the workspace versioning operation
	 * @param target the target
	 */
	public VersioningHistoryShowEvent(WorkspaceVersioningOperation workspaceVersioningOperation, FileModel target) {
		this.workspaceVersioningOperation = workspaceVersioningOperation;
		this.targetFileModel = target;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<VersioningHistoryShowEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(VersioningHistoryShowEventHandler handler) {
		handler.onFileVersioning(this);
	}

	/**
	 * Gets the target file model.
	 *
	 * @return the targetFileModel
	 */
	public FileModel getTargetFileModel() {

		return targetFileModel;
	}


	/**
	 * Gets the workspace versioning operation.
	 *
	 * @return the workspaceVersioningOperation
	 */
	public WorkspaceVersioningOperation getVersioningOperation() {

		return workspaceVersioningOperation;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.FILE_VERSIONING_HISTORY_EVENT;
	}

}
