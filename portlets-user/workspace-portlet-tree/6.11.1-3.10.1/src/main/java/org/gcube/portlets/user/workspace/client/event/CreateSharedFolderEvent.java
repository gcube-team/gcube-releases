package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class CreateSharedFolderEvent extends GwtEvent<CreateSharedFolderEventHandler>{
	public static Type<CreateSharedFolderEventHandler> TYPE = new Type<CreateSharedFolderEventHandler>();

	private FileModel fileSourceModel = null; //File or Folder source click
	private FileModel parentFileModel = null;
	private boolean isNewFolder;
	
	public boolean isNewFolder() {
		return isNewFolder;
	}

	@Override
	public Type<CreateSharedFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CreateSharedFolderEventHandler handler) {
		handler.onCreateSharedFolder(this);
	}

	/**
	 * 
	 * @param fileSourceModel
	 * @param parentFileModel
	 * @param isNewFolder
	 */
	public CreateSharedFolderEvent(FileModel fileSourceModel, FileModel parentFileModel, boolean isNewFolder) {
		this.fileSourceModel = fileSourceModel;
		this.parentFileModel = parentFileModel;
		this.isNewFolder = isNewFolder;
	}

	
	public FileModel getFileSourceModel() {
		return fileSourceModel;
	}

	public FileModel getParentFileModel() {
		return parentFileModel;
	}
}
