package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class MoveItemsEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 4, 2018
 */
public class MoveItemsEvent extends GwtEvent<MoveItemsEventHandler> implements GuiEventInterface{
	public static Type<MoveItemsEventHandler> TYPE = new Type<MoveItemsEventHandler>();

	private String destionationFolderId;

	private List<String> ids;

	private FileModel sourceParentFolder; //Used to move

	private boolean treeRefreshable;


	/**
	 * Instantiates a new move items event.
	 *
	 * @param ids the ids
	 * @param folderDestinationId the folder destination id
	 * @param sourceParentFolder the source parent folder
	 */
	public MoveItemsEvent(List<String> ids, String folderDestinationId, FileModel sourceParentFolder) {
		this.ids = ids;
		this.destionationFolderId = folderDestinationId;
		this.sourceParentFolder = sourceParentFolder;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<MoveItemsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(MoveItemsEventHandler handler) {
		handler.onMoveItems(this);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.MOVED_EVENT;
	}

	/**
	 * Gets the ids.
	 *
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}

	/**
	 * Sets the ids.
	 *
	 * @param ids the new ids
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	/**
	 * Sets the tree refreshable.
	 *
	 * @param bool the new tree refreshable
	 */
	public void setTreeRefreshable(boolean bool){
		this.treeRefreshable = bool;
	}

	/**
	 * Checks if is tree refreshable.
	 *
	 * @return true, if is tree refreshable
	 */
	public boolean isTreeRefreshable() {
		return treeRefreshable;
	}


	/**
	 * Gets the destionation folder id.
	 *
	 * @return the destionationFolderId
	 */
	public String getDestionationFolderId() {

		return destionationFolderId;
	}


	/**
	 * @return the sourceParentFolder
	 */
	public FileModel getSourceParentFolder() {

		return sourceParentFolder;
	}


	/**
	 * @param destionationFolderId the destionationFolderId to set
	 */
	public void setDestionationFolderId(String destionationFolderId) {

		this.destionationFolderId = destionationFolderId;
	}




}
