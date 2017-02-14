package org.gcube.portlets.widgets.userselection.client.events;

import org.gcube.portlets.widgets.userselection.shared.ItemSelectableBean;

import com.google.gwt.event.shared.GwtEvent;



public class SelectedUserEvent  extends GwtEvent<SelectedUserEventHandler> {
	public static Type<SelectedUserEventHandler> TYPE = new Type<SelectedUserEventHandler>();
	
	private ItemSelectableBean user;
	
	
	public ItemSelectableBean getSelectedUser() {
		return user;
	}
	
	public SelectedUserEvent(ItemSelectableBean user) {
		this.user = user;
	}

	@Override
	public Type<SelectedUserEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedUserEventHandler handler) {
		handler.onSelectedUser(this);
	}
}
