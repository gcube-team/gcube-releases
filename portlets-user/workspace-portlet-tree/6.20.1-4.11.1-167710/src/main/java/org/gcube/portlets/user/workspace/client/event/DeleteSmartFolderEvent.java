package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DeleteSmartFolderEvent extends GwtEvent<DeleteSmartFolderEventHandler> implements GuiEventInterface{
	public static Type<DeleteSmartFolderEventHandler> TYPE = new Type<DeleteSmartFolderEventHandler>();
	
	private String smartIdentifier;
	private String smartName;

	
//	public DeleteSmartFolderEvent(FileModel fileModel) {
//		this.fileTarget = fileModel;
//	}

	public DeleteSmartFolderEvent(String identifier, String name) {
		this.smartIdentifier = identifier;
		this.smartName = name;
	}

	@Override
	public Type<DeleteSmartFolderEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteSmartFolderEventHandler handler) {
		handler.onDeleteItem(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.DELETE_SMARTFOLDER_ITEM_EVENT;
	}

	public String getSmartIdentifier() {
		return smartIdentifier;
	}

	public String getSmartName() {
		return smartName;
	}

//	public FileModel getFileTarget() {
//		return fileTarget;
//	}
}
