package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;

import com.google.gwt.event.shared.GwtEvent;

public class AddFolderEvent extends GwtEvent<AddFolderEventHandler> implements GuiEventInterface{
	public static Type<AddFolderEventHandler> TYPE = new Type<AddFolderEventHandler>();
	
	private FileModel fileSourceModel = null; //File or Folder source click
	private FileModel parentFileModel = null;
	private FolderModel newFolder;
	
	public AddFolderEvent(FileModel folderSelected, FileModel parentFolderSelected) {
		this.fileSourceModel = folderSelected;
		this.parentFileModel = parentFolderSelected;
	}

	@Override
	public Type<AddFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddFolderEventHandler handler) {
		handler.onAddItem(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.ADDED_FOLDER_EVENT;
	}

	public FileModel getFileSourceModel() {
		return fileSourceModel;
	}

	public FileModel getParentFileModel() {
		return parentFileModel;
	}
	
	public void setNewFolder(FolderModel folder){
		this.newFolder = folder;
	}

	public FolderModel getNewFolder() {
		return newFolder;
	}
}
