package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 23, 2013
 * 
 */
public class TrashEvent extends GwtEvent<TrashEventHandler> implements GuiEventInterface{
	
	public static Type<TrashEventHandler> TYPE = new Type<TrashEventHandler>();
	
	private List<? extends FileModel> targetFileModels;
	private WorkspaceTrashOperation trashOperation;

	public TrashEvent(WorkspaceTrashOperation trashOperation, List<? extends FileModel> targets) {
		this.trashOperation = trashOperation;
		this.setTargetFileModels(targets);
	}

	@Override
	public Type<TrashEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TrashEventHandler handler) {
		handler.onTrashEvent(this);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.TRASH_EVENT;
	}

	public void setTrashOperation(WorkspaceTrashOperation trashOperation) {
		this.trashOperation = trashOperation;
	}

	@SuppressWarnings("unchecked")
	public List<FileModel> getTargetFileModels() {
		return (List<FileModel>) targetFileModels;
	}

	public void setTargetFileModels( List<? extends FileModel> targetFileModels) {
		this.targetFileModels = targetFileModels;
	}

	public WorkspaceTrashOperation getTrashOperation() {
		return trashOperation;
	}
	

}
