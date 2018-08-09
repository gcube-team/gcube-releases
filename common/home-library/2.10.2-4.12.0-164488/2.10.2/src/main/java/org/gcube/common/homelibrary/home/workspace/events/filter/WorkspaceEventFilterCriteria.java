/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events.filter;

import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEvent;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceEventFilterCriteria {
	
	/**
	 * Accept or reject a event.
	 * @param event the event.
	 * @return <code>true</code> if the event is accepted, <code>false</code> otherwise.
	 */
	public boolean accept(WorkspaceEvent event);

}
