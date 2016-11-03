package org.gcube.portlets.widgets.workspaceuploader.client.events;

import org.gcube.portlets.widgets.workspaceuploader.client.uploader.UploaderProgressView;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class CancelUploadEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 10, 2016
 */
public class CancelUploadEvent extends GwtEvent<CancelUploadEventHandler> {
	public static Type<CancelUploadEventHandler> TYPE = new Type<CancelUploadEventHandler>();
	private UploaderProgressView uploaderProgressView;
	private String fileName;
	private WorkspaceUploaderItem uploader;

	/**
	 * Instantiates a new cancel upload event.
	 *
	 * @param uploader the uploader
	 * @param uploaderProgressView the uploader progress view
	 * @param fileName the file name
	 */
	public CancelUploadEvent(WorkspaceUploaderItem uploader, UploaderProgressView uploaderProgressView, String fileName) {
		this.uploader = uploader;
		this.uploaderProgressView = uploaderProgressView;
		this.fileName = fileName;
	}

	/**
	 * Gets the uploader.
	 *
	 * @return the uploader
	 */
	public WorkspaceUploaderItem getUploader() {
		return uploader;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CancelUploadEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CancelUploadEventHandler handler) {
		handler.onCancelUpload(this);
	}

	/**
	 * Gets the progess view.
	 *
	 * @return the progess view
	 */
	public UploaderProgressView getProgessView() {
		return uploaderProgressView;
	}


	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}
}
