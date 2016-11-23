package org.gcube.portlets.admin.authportletmanager.client.event;


import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyAddDialog;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class AddPoliciesEvent extends 
GwtEvent<AddPoliciesEvent.AddPoliciesEventHandler> {

	
	public static Type<AddPoliciesEventHandler> TYPE = new Type<AddPoliciesEventHandler>();

	public static Type<AddPoliciesEventHandler> getType() {
		return TYPE;
	}
	
	private List<PolicyAuth> policies;
	private PolicyAddDialog dialog;
	
	public interface AddPoliciesEventHandler extends EventHandler {
		void onAdd(AddPoliciesEvent event);
	}

	public interface HasAddPoliciesEventHandler extends HasHandlers {
		public HandlerRegistration addAddPoliciesEventHandler(
				AddPoliciesEventHandler handler);
	}
	public AddPoliciesEvent(List<PolicyAuth> policies,PolicyAddDialog dialog){
		super();
		this.policies=policies;
		this.dialog=dialog;
	}
	@Override
	protected void dispatch(AddPoliciesEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}
	
	
	@Override
	public Type<AddPoliciesEventHandler> getAssociatedType() {
		return TYPE;
	}
	

	public static void fire(HasHandlers source,
			AddPoliciesEvent event) {
		source.fireEvent(event);
	}
	public List<PolicyAuth> getPolicies() {
		return policies;
	}
	
	public PolicyAddDialog getDialog() {
		return dialog;
	}
	@Override
	public String toString() {
		return "AddPoliciesEvent [policies=" + policies + ", dialog=" + dialog
				+ "]";
	}
	
}





