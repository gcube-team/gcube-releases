package org.gcube.portlets.admin.authportletmanager.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class ListQuoteEvent extends GwtEvent<ListQuoteEvent.ListQuoteEventHandler> {

	
	
	public static Type<ListQuoteEventHandler> TYPE = new Type<ListQuoteEventHandler>();

	public static Type<ListQuoteEventHandler> getType() {
		return TYPE;
	}
	
	@Override
	public Type<ListQuoteEventHandler> getAssociatedType() {
		return TYPE;
	}

	public interface ListQuoteEventHandler extends EventHandler {
		void onAdd(ListQuoteEvent event);
	}

	public interface HasListQuoteEventHandler extends HasHandlers {
		public HandlerRegistration addListQuoteEventHandler(
				ListQuoteEventHandler handler);
	}
	
	public static void fire(HasHandlers source,
		ListQuoteEvent event) {
			source.fireEvent(event);
	}
	
	public ListQuoteEvent() {
		super();
	}
	
	@Override
	protected void dispatch(ListQuoteEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}

	

	
	

}




