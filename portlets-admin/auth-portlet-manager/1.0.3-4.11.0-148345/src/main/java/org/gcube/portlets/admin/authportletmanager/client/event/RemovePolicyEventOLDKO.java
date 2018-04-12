package org.gcube.portlets.admin.authportletmanager.client.event;




import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyDeleteDialog;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class RemovePolicyEventOLDKO extends 
GwtEvent<RemovePolicyEventOLDKO.RemovePolicyEventHandler> {

	
	public static Type<RemovePolicyEventHandler> TYPE = new Type<RemovePolicyEventHandler>();

	public static Type<RemovePolicyEventHandler> getType() {
		return TYPE;
	}
	
	private Long identifier;
	private PolicyDeleteDialog dialog;
	
	public interface RemovePolicyEventHandler extends EventHandler {
		void onAdd(RemovePolicyEventOLDKO event);
	}

	public interface HasRemovePolicyEventHandler extends HasHandlers {
		public HandlerRegistration addRemovePolicyEventHandler(
				RemovePolicyEventHandler handler);
	}
	
	public RemovePolicyEventOLDKO(Long identifier, PolicyDeleteDialog modal) {
		super();
		this.identifier = identifier;
		this.dialog=modal;
	
	}

	@Override
	protected void dispatch(RemovePolicyEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}
	
	
	@Override
	public Type<RemovePolicyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
		RemovePolicyEventOLDKO event) {
		source.fireEvent(event);
	}
	
	
	public Long getIdentifier() {
		return identifier;
	}

	public PolicyDeleteDialog getDialog() {
		return dialog;
	}

	@Override
	public String toString() {
		return "RemovePolicyEvent [identifier=" + identifier + ", dialog=" + dialog + "]";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

}



