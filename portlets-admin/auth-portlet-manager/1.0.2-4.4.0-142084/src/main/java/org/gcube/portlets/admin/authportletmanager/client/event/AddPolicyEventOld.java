package org.gcube.portlets.admin.authportletmanager.client.event;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.PolicyAddDialog;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class AddPolicyEventOld extends 
GwtEvent<AddPolicyEventOld.AddPolicyEventHandler> {
	
	
	public static Type<AddPolicyEventHandler> TYPE = new Type<AddPolicyEventHandler>();

	public static Type<AddPolicyEventHandler> getType() {
		return TYPE;
	}

	private String caller;
	private String typeCaller;
	private String serviceClass;
	private String serviceName;
	private String serviceId;
	private String access;
	private PolicyAddDialog dialog;
	
	public interface AddPolicyEventHandler extends EventHandler {
		void onAdd(AddPolicyEventOld event);
	}

	public interface HasAddPolicyEventHandler extends HasHandlers {
		public HandlerRegistration addAddPolicyEventHandler(
				AddPolicyEventHandler handler);
	}
	
	public AddPolicyEventOld
	(String caller,String typeCaller,String serviceClass,String serviceName,String serviceId,String access,PolicyAddDialog dialog) {
		super();
		this.caller = caller;
		this.typeCaller=typeCaller;
		this.serviceClass = serviceClass;
		this.serviceName=serviceName;
		this.serviceId=serviceId;
		this.access = access;
		this.dialog=dialog;
	}
	
	@Override
	protected void dispatch(AddPolicyEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}
	
	
	@Override
	public Type<AddPolicyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			AddPolicyEventOld event) {
		source.fireEvent(event);
	}

	

	public String getCaller() {
		return caller;
	}

	public String getTypeCaller() {
		return typeCaller;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getAccess() {
		return access;
	}

	public PolicyAddDialog getModal() {
		return dialog;
	}

	@Override
	public String toString() {
		return "AddPolicyEvent [caller=" + caller + ", typeCaller="
				+ typeCaller + ", serviceClass=" + serviceClass
				+ ", serviceName=" + serviceName + ", serviceId=" + serviceId
				+ ", access=" + access + ", dialog=" + dialog + "]";
	}

	
}




