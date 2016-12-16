package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;

import com.google.gwt.event.shared.GwtEvent;

public class MoveItemEvent extends GwtEvent<MoveItemEventHandler> implements GuiEventInterface{
	public static Type<MoveItemEventHandler> TYPE = new Type<MoveItemEventHandler>();
	
	private FileModel source = null;
	private FolderModel target = null;
//	private FileModel target = null;
	
	public MoveItemEvent(FileModel fileSourceModel, FolderModel parentFileModel) {
		this.source = fileSourceModel;
		this.target = parentFileModel;
	}
	
//	public MoveItemEvent(FileModel fileSourceModel, FileModel parentFileModel) {
//		this.source = fileSourceModel;
//		this.target = parentFileModel;
//	}

	@Override
	public Type<MoveItemEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(MoveItemEventHandler handler) {
		handler.onMoveItem(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.MOVED_ITEM_EVENT;
	}

	public FileModel getFileSourceModel() {
		return source;
	}

	public FolderModel getTargetParentFileModel() {
		return target;
	}
	
//	public FileModel getTargetParentFileModel() {
//		return target;
//	}
}
