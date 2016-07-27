package org.gcube.portlets.user.reportgenerator.client.events;

import java.util.HashMap;

import com.google.gwt.event.shared.GwtEvent;


public class ItemSelectionEvent extends GwtEvent<ItemSelectionEventHandler>{

	private HashMap<String,Object> map;
	
	public static Type<ItemSelectionEventHandler> TYPE = new Type<ItemSelectionEventHandler>();
	public ItemSelectionEvent(HashMap<String,Object> map) {
		this.map = map;
	}
	
	public HashMap<String, Object> getItemSelected() {
		return this.map;
	}
	
	@Override
	protected void dispatch(ItemSelectionEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onItemSelected(this);
	}

	@Override
	public Type<ItemSelectionEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
	

}
