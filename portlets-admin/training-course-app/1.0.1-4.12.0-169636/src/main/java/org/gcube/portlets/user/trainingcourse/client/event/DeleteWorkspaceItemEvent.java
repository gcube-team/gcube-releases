package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class DeleteWorkspaceItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 16, 2018
 */
public class DeleteWorkspaceItemEvent extends GwtEvent<DeleteWorkspaceItemEventHandler> {
	
	/** The type. */
	public static Type<DeleteWorkspaceItemEventHandler> TYPE = new Type<DeleteWorkspaceItemEventHandler>();
	
	/** The workspace item id. */
	private WorkspaceItemInfo workspaceItem;

	
	/**
	 * Instantiates a new delete workspace item event.
	 *
	 * @param workspaceItemId the workspace item id
	 */
	public DeleteWorkspaceItemEvent(WorkspaceItemInfo workspaceItem) {
		this.workspaceItem = workspaceItem;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DeleteWorkspaceItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DeleteWorkspaceItemEventHandler handler) {
		handler.onRemoveWsItem(this);
	}

	public WorkspaceItemInfo getWorkspaceItem() {
		return workspaceItem;
	}
}
