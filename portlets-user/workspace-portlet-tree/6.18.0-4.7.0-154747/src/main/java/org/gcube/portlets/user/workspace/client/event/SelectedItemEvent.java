package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class SelectedItemEvent extends GwtEvent<SelectedItemEventHandler> implements GuiEventInterface{
	public static Type<SelectedItemEventHandler> TYPE = new Type<SelectedItemEventHandler>();
	
	private FileModel fileTarget = null;
	
	public SelectedItemEvent(FileModel fileModel) {
		this.fileTarget = fileModel;
	}

	@Override
	public Type<SelectedItemEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedItemEventHandler handler) {
		handler.onSelectedItem(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.SELECTED_ITEM_EVENT;
	}

	public FileModel getFileTarget() {
		return fileTarget;
	}
}
