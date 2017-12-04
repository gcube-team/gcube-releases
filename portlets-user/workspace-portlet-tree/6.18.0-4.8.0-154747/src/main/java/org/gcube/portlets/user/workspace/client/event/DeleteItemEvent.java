package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class DeleteItemEvent extends GwtEvent<DeleteItemEventHandler> implements GuiEventInterface{
	public static Type<DeleteItemEventHandler> TYPE = new Type<DeleteItemEventHandler>();
	
	private FileModel fileTarget = null;

	private List<? extends FileModel> listTarget;

	private boolean isMultiSelection;
	
	public DeleteItemEvent(FileModel fileModel) {
		this.fileTarget = fileModel;
		this.isMultiSelection = false;
	}
	
	public DeleteItemEvent(List<? extends FileModel> listTarget) {
		this.listTarget = listTarget;
		this.isMultiSelection = true;
	}

	@Override
	public Type<DeleteItemEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DeleteItemEventHandler handler) {
		handler.onDeleteItem(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.DELETE_ITEM_EVENT;
	}

	public FileModel getFileTarget() {
		return fileTarget;
	}

	public List<? extends FileModel> getListTarget() {
		return listTarget;
	}

	public boolean isMultiSelection() {
		return isMultiSelection;
	}
}
