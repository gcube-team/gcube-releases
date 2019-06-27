package org.gcube.portlets.user.workspace.client.gridevent;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class MoveItemsGEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 5, 2018
 */
public class MoveItemsGEvent extends GwtEvent<MoveItemsGEventHandler> implements GuiEventInterface{
	public static Type<MoveItemsGEventHandler> TYPE = new Type<MoveItemsGEventHandler>();

	private String folderDestinationId;

	private List<String> ids;

	private FileModel sourceParentFolder; //Used to move

	private boolean treeRefreshable;

	/**
	 * Instantiates a new move items g event.
	 *
	 * @param ids the ids
	 * @param folderDestinationId the folder destination id
	 * @param sourceParentFolder the source parent folder
	 */
	public MoveItemsGEvent(List<String> ids, String folderDestinationId, FileModel sourceParentFolder) {
		this.ids = ids;
		this.folderDestinationId = folderDestinationId;
		this.sourceParentFolder = sourceParentFolder;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<MoveItemsGEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(MoveItemsGEventHandler handler) {
		handler.onMoveItems(this);

	}

	/**
	 * Gets the folder destination id.
	 *
	 * @return the folder destination id
	 */
	public String getFolderDestinationId() {
			return folderDestinationId;
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
	 * Gets the source parent folder.
	 *
	 * @return the sourceParentFolder
	 */
	public FileModel getSourceParentFolder() {

		return sourceParentFolder;
	}



}
