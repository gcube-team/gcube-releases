/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

import java.util.List;

import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceSentEventImpl extends WorkspaceEventImpl implements WorkspaceSentEvent {
	
	protected List<User> addressees;

	/**
	 * Create a new Sent event.
	 * @param target the sent item.
	 * @param addressees the addressees users.
	 */
	public WorkspaceSentEventImpl(WorkspaceItem target, List<User> addressees) {
		super(WorkspaceEventType.ITEM_SENT, target);
		this.addressees = addressees; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> getAddressees() {
		return addressees;
	}

}
