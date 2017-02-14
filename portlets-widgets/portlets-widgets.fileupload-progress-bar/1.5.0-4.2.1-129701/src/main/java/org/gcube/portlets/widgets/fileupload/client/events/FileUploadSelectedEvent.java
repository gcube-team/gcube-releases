package org.gcube.portlets.widgets.fileupload.client.events;

import com.google.gwt.event.shared.GwtEvent;



public class FileUploadSelectedEvent  extends GwtEvent<FileUploadSelectedEventHandler> {
	public static Type<FileUploadSelectedEventHandler> TYPE = new Type<FileUploadSelectedEventHandler>();
	
	private String filename;
	
	
	public String getSelectedFileName() {
		return filename;
	}
	
	public FileUploadSelectedEvent(String filename) {
		this.filename = filename;
	}

	@Override
	public Type<FileUploadSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileUploadSelectedEventHandler handler) {
		handler.onFileSelected(this);
	}
}
