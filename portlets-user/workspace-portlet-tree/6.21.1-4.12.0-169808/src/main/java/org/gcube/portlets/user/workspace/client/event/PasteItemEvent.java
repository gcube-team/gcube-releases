package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.view.tree.CutCopyAndPaste.OperationType;

import com.google.gwt.event.shared.GwtEvent;

public class PasteItemEvent extends GwtEvent<PasteItemEventHandler> implements GuiEventInterface{
	public static Type<PasteItemEventHandler> TYPE = new Type<PasteItemEventHandler>();
	
//	private String itemId = null;
	private String folderDestinationId;

	private List<String> ids;

	private OperationType operationType;
	
	private String folderSourceId; //Used to move

	private boolean treeRefreshable;
	

//	public PasteItemEvent(String itemId, String folderDestinationId) {
//		this.itemId = itemId;
//		this.folderDestinationId = folderDestinationId;
//	}
	
	public PasteItemEvent(List<String> ids, String folderDestinationId, OperationType operation) {
		this.ids = ids;
		this.folderDestinationId = folderDestinationId;
		this.operationType = operation;
	}

	@Override
	public Type<PasteItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PasteItemEventHandler handler) {
		handler.onCutCopyAndPaste(this);
		
	}

//	public String getItemId() {
//		return itemId;
//	}

	public String getFolderDestinationId() {
		return folderDestinationId;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.PASTED_EVENT;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public String getFolderSourceId() {
		return folderSourceId;
	}

	public void setFolderSourceId(String folderSourceId) {
		this.folderSourceId = folderSourceId;
	}
	
	public void setTreeRefreshable(boolean bool){
		this.treeRefreshable = bool;
	}

	public boolean isTreeRefreshable() {
		return treeRefreshable;
	}

}
