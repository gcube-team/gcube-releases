/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceListener {
	
	/**
	 * Called when a new event is fired.
	 * @param event the event.
	 */
	public void workspaceEvent(WorkspaceEvent event);

}
