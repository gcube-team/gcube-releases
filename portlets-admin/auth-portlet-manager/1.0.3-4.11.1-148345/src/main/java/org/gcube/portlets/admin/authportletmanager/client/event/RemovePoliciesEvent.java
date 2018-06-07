package org.gcube.portlets.admin.authportletmanager.client.event;

import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyDeleteDialog;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class RemovePoliciesEvent extends 
GwtEvent<RemovePoliciesEvent.RemovePoliciesEventHandler> {

	public static Type<RemovePoliciesEventHandler> TYPE = new Type<RemovePoliciesEventHandler>();

	public static Type<RemovePoliciesEventHandler> getType() {
		return TYPE;
	}

	private List<Long> identifier;

	private PolicyDeleteDialog dialog;

	public interface RemovePoliciesEventHandler extends EventHandler {
		void onAdd(RemovePoliciesEvent event);
	}

	public interface HasRemovePoliciesEventHandler extends HasHandlers {
		public HandlerRegistration addRemovePoliciesEventHandler(
				RemovePoliciesEventHandler handler);
	}

	public RemovePoliciesEvent(List<Long> identifier, PolicyDeleteDialog modal) {
		super();
		this.identifier = identifier;
		this.dialog=modal;

	}
	@Override
	protected void dispatch(RemovePoliciesEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}


	@Override
	public Type<RemovePoliciesEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			RemovePoliciesEvent event) {
		source.fireEvent(event);
	}
	public List<Long> getIdentifier() {
		return identifier;
	}
	public PolicyDeleteDialog getDialog() {
		return dialog;
	}
	@Override
	public String toString() {
		return "RemovePoliciesEvent [identifier=" + identifier + ", dialog="
				+ dialog + "]";
	}

}








