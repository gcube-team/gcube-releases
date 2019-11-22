package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class FileUploadEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 6, 2015
 */
public class FileUploadEvent extends GwtEvent<FileUploadEventHandler> implements GuiEventInterface{
	public static Type<FileUploadEventHandler> TYPE = new Type<FileUploadEventHandler>();
	private FileModel targetFolder = null;
	private WS_UPLOAD_TYPE uploadType;
	
	/**
	 * Instantiates a new file upload event.
	 *
	 * @param targetFolder the target folder
	 * @param uploadType the upload type
	 */
	public FileUploadEvent(FileModel targetFolder, WS_UPLOAD_TYPE uploadType) {
		this.targetFolder = targetFolder;
		this.uploadType = uploadType;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<FileUploadEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(FileUploadEventHandler handler) {
		handler.onFileUploadEvent(this);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.FILE_UPLOAD_EVENT;
	}

	/**
	 * Gets the target folder model.
	 *
	 * @return the target folder model
	 */
	public FileModel getTargetFolderModel() {
		return targetFolder;
	}

	/**
	 * Gets the upload type.
	 *
	 * @return the upload type
	 */
	public WS_UPLOAD_TYPE getUploadType() {
		return uploadType;
	}
}
