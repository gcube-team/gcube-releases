package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.constant.WorkspaceOperation;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

public class OpenContextMenuTreeEvent extends GwtEvent<OpenContextMenuTreeEventHandler> {
	public static Type<OpenContextMenuTreeEventHandler> TYPE = new Type<OpenContextMenuTreeEventHandler>();
	
	private FileModel targetFileModel = null;
	private int clientX;
	private int clientY;

	private WorkspaceOperation wsOperation;

	public WorkspaceOperation getWsOperation() {
		return wsOperation;
	}

	public OpenContextMenuTreeEvent(FileModel targetFileModel, int clientX, int clientY) {
		this.targetFileModel = targetFileModel;
		this.clientX = clientX;
		this.clientY = clientY;
	}

	@Override
	public Type<OpenContextMenuTreeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenContextMenuTreeEventHandler handler) {
		handler.onOpenContextMenuTree(this);
		
	}


	public int getClientX() {
		return clientX;
	}

	public int getClientY() {
		return clientY;
	}

	public FileModel getTargetFileModel() {
		return targetFileModel;
	}
	
}
