package org.gcube.portlets.admin.wftemplates.client.event;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;

import com.google.gwt.event.shared.GwtEvent;
import com.orange.links.client.connection.Connection;
/**
 * <code> RolesAddedEvent </code>  is the event fired in case of new edge created between two steps
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class RolesAddedEvent extends GwtEvent<RolesAddedEventHandler> {
	public static Type<RolesAddedEventHandler> TYPE = new Type<RolesAddedEventHandler>();
	private final ArrayList<WfRoleDetails> roles;
	private final Connection selectedEdge;

	public RolesAddedEvent(Connection selectedEdge, ArrayList<WfRoleDetails> roles) {
		super();
		this.roles = roles;
		this.selectedEdge = selectedEdge;
	}
		
	public ArrayList<WfRoleDetails> getRoles() {
		return roles;
	}

	public Connection getSelectedEdge() {
		return selectedEdge;
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
