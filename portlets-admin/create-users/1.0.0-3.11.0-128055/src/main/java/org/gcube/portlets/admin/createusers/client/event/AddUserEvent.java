package org.gcube.portlets.admin.createusers.client.event;
import org.gcube.portlets.admin.createusers.shared.VreUserBean;

import com.google.gwt.event.shared.GwtEvent;


public class AddUserEvent  extends GwtEvent<AddUserEventHandler> {
	public static Type<AddUserEventHandler> TYPE = new Type<AddUserEventHandler>();

	private VreUserBean addedUserBean;

	public AddUserEvent(VreUserBean addedUserBean) {
		this.addedUserBean = addedUserBean;
	}
	
	public VreUserBean getAddedUserBean() {
		return addedUserBean;
	}

	@Override
	public Type<AddUserEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddUserEventHandler handler) {
		handler.onAddUser(this);
	}
}
