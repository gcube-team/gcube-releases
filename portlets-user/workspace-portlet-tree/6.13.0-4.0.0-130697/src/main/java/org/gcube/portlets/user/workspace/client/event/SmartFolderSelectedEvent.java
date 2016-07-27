package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SmartFolderSelectedEvent extends GwtEvent<SmartFolderSelectedEventHandler> implements GuiEventInterface{
	public static Type<SmartFolderSelectedEventHandler> TYPE = new Type<SmartFolderSelectedEventHandler>();

	private String idSmartFolder;
	private String smartFolderName;
	private String category;

	public SmartFolderSelectedEvent(String idSmartFolder, String smartFolderName, String category) {
		this.idSmartFolder = idSmartFolder;
		this.smartFolderName = smartFolderName;
		this.category = category;
	}

	@Override
	public Type<SmartFolderSelectedEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(SmartFolderSelectedEventHandler handler) {
		handler.onSmartFolderSelected(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.SMART_FOLDER_EVENT;
	}

	public String getIdSmartFolder() {
		return idSmartFolder;
	}

	public String getSmartFolderName() {
		return smartFolderName;
	}

	public String getCategory() {
		return category;
	}
	
}
