package org.gcube.portlets.widgets.fileupload.client.events;

import org.gcube.portlets.widgets.fileupload.shared.event.UploadProgressChangeEvent;

import com.google.gwt.event.shared.GwtEvent;



public class FileUploadCompleteEvent  extends GwtEvent<FileUploadCompleteEventHandler> {
	public static Type<FileUploadCompleteEventHandler> TYPE = new Type<FileUploadCompleteEventHandler>();
	
	private UploadProgressChangeEvent uploadedFile;
	
	
	public UploadProgressChangeEvent getUploadedFileInfo() {
		return uploadedFile;
	}
	
	public FileUploadCompleteEvent(UploadProgressChangeEvent uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	@Override
	public Type<FileUploadCompleteEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileUploadCompleteEventHandler handler) {
		handler.onUploadComplete(this);
	}
}
