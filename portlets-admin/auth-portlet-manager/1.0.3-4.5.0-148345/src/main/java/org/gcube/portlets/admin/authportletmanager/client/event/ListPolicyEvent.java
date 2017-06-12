package org.gcube.portlets.admin.authportletmanager.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class ListPolicyEvent extends GwtEvent<ListPolicyEvent.ListPolicyEventHandler> {

	
	
	public static Type<ListPolicyEventHandler> TYPE = new Type<ListPolicyEventHandler>();

	public static Type<ListPolicyEventHandler> getType() {
		return TYPE;
	}
	
	@Override
	public Type<ListPolicyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public interface ListPolicyEventHandler extends EventHandler {
		void onAdd(ListPolicyEvent event);
	}

	public interface HasListPolicyEventHandler extends HasHandlers {
		public HandlerRegistration addListPolicyEventHandler(
				ListPolicyEventHandler handler);
	}
	
	public static void fire(HasHandlers source,
		ListPolicyEvent event) {
			source.fireEvent(event);
	}
	
	public ListPolicyEvent() {
		super();
	}
	
	@Override
	protected void dispatch(ListPolicyEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}


	
	

}




