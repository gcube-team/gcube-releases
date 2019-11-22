package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class UnShareFolderEvent extends GwtEvent<UnShareFolderEventHandler>{
	public static Type<UnShareFolderEventHandler> TYPE = new Type<UnShareFolderEventHandler>();

	private FileModel targetFileModel = null; //File or Folder source click

	@Override
	public Type<UnShareFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UnShareFolderEventHandler handler) {
		handler.onUnShareFolder(this);
	}

	
	public UnShareFolderEvent(FileModel fileSourceModel) {
		this.targetFileModel = fileSourceModel;
	}

	
	public FileModel getTargetFileModel() {
		return targetFileModel;
	}
}
