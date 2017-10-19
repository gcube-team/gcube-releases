/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceEventSource {
	
	/**
	 * Add a new listener to this source.
	 * @param listener the listener to add.
	 */
	public void addWorkspaceListener(WorkspaceListener listener);
	
	/**
	 * Remove a listener from this source.
	 * @param listener the listener to remove.
	 */
	public void removeWorkspaceListener(WorkspaceListener listener);
	

}
