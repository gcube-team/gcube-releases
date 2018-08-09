package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class ImagePreviewEvent extends GwtEvent<ImagePreviewEventHandler> {
	public static Type<ImagePreviewEventHandler> TYPE = new Type<ImagePreviewEventHandler>();
	
	private FileModel sourceFileModel = null; //Image File source click
	private int clientX;
	private int clientY;
	
	public ImagePreviewEvent(FileModel fileSourceModel, int x, int y) {
		this.sourceFileModel = fileSourceModel;
		this.clientX = x;
		this.clientY = y;
	}

	@Override
	public Type<ImagePreviewEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(ImagePreviewEventHandler handler) {
		handler.onClickPreview(this);
		
	}

	public FileModel getSourceFileModel() {
		return sourceFileModel;
	}

	public int getClientX() {
		return clientX;
	}

	public int getClientY() {
		return clientY;
	}

}
