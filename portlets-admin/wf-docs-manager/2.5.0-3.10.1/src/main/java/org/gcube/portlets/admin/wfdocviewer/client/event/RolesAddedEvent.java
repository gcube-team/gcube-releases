package org.gcube.portlets.admin.wfdocviewer.client.event;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;

import com.google.gwt.event.shared.GwtEvent;
/**
 * <code> RolesAddedEvent </code>  is the event fired in case of new edge created between two steps
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class RolesAddedEvent extends GwtEvent<RolesAddedEventHandler> {
	public static Type<RolesAddedEventHandler> TYPE = new Type<RolesAddedEventHandler>();
	private final ArrayList<WfRoleDetails> roles;

	public RolesAddedEvent(ArrayList<WfRoleDetails> roles) {
		super();
		this.roles = roles;
	}
		
	public ArrayList<WfRoleDetails> getRoles() {
		return roles;
	}

	@Override
	public Type<RolesAddedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RolesAddedEventHandler handler) {
		handler.onAddRoles(this);
	}
}
