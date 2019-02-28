package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class MoveItemsEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 4, 2018
 */
public class LoadFolderEvent extends GwtEvent<LoadFolderEventHandler> implements GuiEventInterface{
	public static Type<LoadFolderEventHandler> TYPE = new Type<LoadFolderEventHandler>();

	private FileModel targetFolder; //Used to move
	/**
	 * Instantiates a new load folder event.
	 *
	 * @param targetFolder the target folder
	 */
	public LoadFolderEvent(FileModel targetFolder) {
		this.targetFolder = targetFolder;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	/**
	 * Dispatch.
	 *
	 * @param handler the handler
	 */
	@Override
	protected void dispatch(LoadFolderEventHandler handler) {
		handler.onLoadFolder(this);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.LOAD_FOLDER_EVENT;
	}



	/**
	 * @return the targetFolder
	 */
	public FileModel getTargetFolder() {

		return targetFolder;
	}




}
