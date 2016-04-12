package org.gcube.portlets.widgets.workspaceuploader.client.events;

import org.gcube.portlets.widgets.workspaceuploader.client.uploader.UploaderProgressView;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class CancelUploadEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 7, 2015
 */
public class CancelUploadEvent extends GwtEvent<CancelUploadEventHandler> {
	public static Type<CancelUploadEventHandler> TYPE = new Type<CancelUploadEventHandler>();
	private UploaderProgressView uploaderProgressView;
	private String fileName;
	private WorkspaceUploaderItem uploader;


	/**
	 * @return the hp
	 */
	public UploaderProgressView getProgessView() {
		return uploaderProgressView;
	}

	/**
	 * Instantiates a new cancel upload event.
	 */
	public CancelUploadEvent(WorkspaceUploaderItem uploader, UploaderProgressView uploaderProgressView, String fileName) {
		this.uploader = uploader;
		this.uploaderProgressView = uploaderProgressView;
		this.fileName = fileName;
	}

	/**
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
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
}
