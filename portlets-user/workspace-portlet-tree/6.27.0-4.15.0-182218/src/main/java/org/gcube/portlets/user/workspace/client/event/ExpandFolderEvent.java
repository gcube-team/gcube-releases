package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FolderModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class ExpandFolderEvent extends GwtEvent<ExpandFolderEventHandler> implements GuiEventInterface{
	public static Type<ExpandFolderEventHandler> TYPE = new Type<ExpandFolderEventHandler>();
	
	private FolderModel folderTarget = null;
	
	public ExpandFolderEvent(FolderModel folder) {
		this.folderTarget = folder;
	}

	@Override
	public Type<ExpandFolderEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(ExpandFolderEventHandler handler) {
		handler.onExpandFolder(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.EXPANDED_FOLDER_EVENT;
	}

	public FolderModel getFolderTarget() {
		return folderTarget;
	}
}
