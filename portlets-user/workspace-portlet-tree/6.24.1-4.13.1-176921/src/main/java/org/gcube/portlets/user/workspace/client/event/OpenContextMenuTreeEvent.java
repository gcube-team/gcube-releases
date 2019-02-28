package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.constant.WorkspaceOperation;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class OpenContextMenuTreeEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 8, 2018
 */
public class OpenContextMenuTreeEvent extends GwtEvent<OpenContextMenuTreeEventHandler> {
	public static Type<OpenContextMenuTreeEventHandler> TYPE = new Type<OpenContextMenuTreeEventHandler>();

	private List<FileModel> selectedItems = null;
	private int clientX;
	private int clientY;

	private WorkspaceOperation wsOperation;

	/**
	 * Gets the ws operation.
	 *
	 * @return the ws operation
	 */
	public WorkspaceOperation getWsOperation() {
		return wsOperation;
	}


	/**
	 * Instantiates a new open context menu tree event.
	 *
	 * @param selectedItems the selected items
	 * @param clientX the client x
	 * @param clientY the client y
	 */
	public OpenContextMenuTreeEvent(List<FileModel> selectedItems, int clientX, int clientY) {
		this.selectedItems = selectedItems;
		this.clientX = clientX;
		this.clientY = clientY;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<OpenContextMenuTreeEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(OpenContextMenuTreeEventHandler handler) {
		handler.onOpenContextMenuTree(this);

	}

	/**
	 * Gets the client x.
	 *
	 * @return the client x
	 */
	public int getClientX() {
		return clientX;
	}

	/**
	 * Gets the client y.
	 *
	 * @return the client y
	 */
	public int getClientY() {
		return clientY;
	}

	/**
	 * Gets the selected items.
	 *
	 * @return the selectedItems
	 */
	public List<FileModel> getSelectedItems() {

		return selectedItems;
	}

}
