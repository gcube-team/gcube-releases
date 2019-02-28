/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceEvent {
	
	/**
	 * @return the event type.
	 */
	public WorkspaceEventType getType();
	
	/**
	 * @return the event target.
	 */
	public WorkspaceItem getTarget();
	
}
