package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jul 8, 2014
 *
 */
public class AddAdministratorEvent extends GwtEvent<AddAdministratorEventHandler> implements GuiEventInterface{
	public static Type<AddAdministratorEventHandler> TYPE = new Type<AddAdministratorEventHandler>();
	

	private FileModel folder;


	public AddAdministratorEvent(FileModel folder) {
		this.folder = folder;
	}


	@Override
	public Type<AddAdministratorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddAdministratorEventHandler handler) {
		handler.onAddAdministrator(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.ADD_ADMINISTRATOR_EVENT;
	}


	public FileModel getSelectedFolder() {
		return folder;
	}

}
