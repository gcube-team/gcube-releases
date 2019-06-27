package org.gcube.portlets.admin.authportletmanager.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class ListContextEvent extends GwtEvent<ListContextEvent.ListContextEventHandler> {

	
	
	public static Type<ListContextEventHandler> TYPE = new Type<ListContextEventHandler>();

	public static Type<ListContextEventHandler> getType() {
		return TYPE;
	}
	
	@Override
	public Type<ListContextEventHandler> getAssociatedType() {
		return TYPE;
	}

	public interface ListContextEventHandler extends EventHandler {
		void onAdd(ListContextEvent event);
	}

	public interface HasListPolicyEventHandler extends HasHandlers {
		public HandlerRegistration addListContextEventHandler(
				ListContextEventHandler handler);
	}
	
	public static void fire(HasHandlers source,
		ListContextEvent event) {
			source.fireEvent(event);
	}
	
	public ListContextEvent() {
		super();
	}
	
	@Override
	protected void dispatch(ListContextEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}


	
	

}




