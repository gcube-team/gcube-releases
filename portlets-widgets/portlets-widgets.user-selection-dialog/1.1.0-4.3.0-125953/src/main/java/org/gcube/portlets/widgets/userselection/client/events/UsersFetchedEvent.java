package org.gcube.portlets.widgets.userselection.client.events;

import java.util.ArrayList;

import org.gcube.portlets.widgets.userselection.shared.ItemSelectableBean;

import com.google.gwt.event.shared.GwtEvent;



public class UsersFetchedEvent  extends GwtEvent<UsersFetchedEventHandler> {
	public static Type<UsersFetchedEventHandler> TYPE = new Type<UsersFetchedEventHandler>();
	
	private ArrayList<ItemSelectableBean> usersToShow;
	
	
	public ArrayList<ItemSelectableBean> getUsers() {
		return usersToShow;
	}
	
	public UsersFetchedEvent(ArrayList<ItemSelectableBean> usersToShow) {
		this.usersToShow = usersToShow;
	}

	@Override
	public Type<UsersFetchedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UsersFetchedEventHandler handler) {
		handler.onUsersFetched(this);
	}
}
