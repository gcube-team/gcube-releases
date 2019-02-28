package org.gcube.portlets.admin.authportletmanager.client.event;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyAddDialog;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class UpdatePolicyEvent extends GwtEvent<UpdatePolicyEvent.UpdatePolicyEventHandler> {

	
	
	public static Type<UpdatePolicyEventHandler> TYPE = new Type<UpdatePolicyEventHandler>();

	public static Type<UpdatePolicyEventHandler> getType() {
		return TYPE;
	}
	
	private PolicyAuth policies;
	private PolicyAddDialog dialog;
	

	public interface UpdatePolicyEventHandler extends EventHandler {
		void onAdd(UpdatePolicyEvent event);
	}

	public interface HasUpdatePolicyEventHandler extends HasHandlers {
		public HandlerRegistration addUpdatePolicyEventHandler(
				UpdatePolicyEventHandler handler);
	}
	
	public UpdatePolicyEvent(PolicyAuth policies,PolicyAddDialog dialog){
		super();
		this.policies=policies;
		this.dialog=dialog;
	}
	
	@Override
	protected void dispatch(UpdatePolicyEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}
	
	
	@Override
	public Type<UpdatePolicyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			UpdatePolicyEvent event) {
		source.fireEvent(event);
	}

	public PolicyAuth getPolicies() {
		return policies;
	}

	public PolicyAddDialog getDialog() {
		return dialog;
	}

	@Override
	public String toString() {
		return "UpdatePolicyEvent [policies=" + policies + ", dialog=" + dialog
				+ "]";
	}


	

	

	
}
