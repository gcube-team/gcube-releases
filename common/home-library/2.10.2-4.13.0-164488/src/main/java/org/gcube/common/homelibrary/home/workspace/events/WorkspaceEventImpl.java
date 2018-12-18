/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceEventImpl implements WorkspaceEvent {
	
	protected WorkspaceEventType type;
	protected WorkspaceItem target;

	/**
	 * Create a new event.
	 * @param type the event type.
	 * @param target the event target.
	 */
	public WorkspaceEventImpl(WorkspaceEventType type, WorkspaceItem target) {
		this.type = type;
		this.target = target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkspaceItem getTarget() {
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkspaceEventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return type+" "+target;
	}

}
